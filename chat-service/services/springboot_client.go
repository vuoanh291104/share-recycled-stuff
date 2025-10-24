package services

import (
	"bytes"
	"chat-service/config"
	"chat-service/models"
	"context"
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"time"

	"github.com/sony/gobreaker"
)

type TokenValidation struct {
	Valid     bool     `json:"valid"`
	AccountID int64    `json:"accountId"`
	Email     string   `json:"email"`
	Roles     []string `json:"roles"`
}

type SpringBootClient struct {
	baseURL string
	client  *http.Client
	breaker *gobreaker.CircuitBreaker
}

func NewSpringBootClient() *SpringBootClient {
	settings := gobreaker.Settings{
		Name:        "SpringBoot",
		MaxRequests: 3,
		Interval:    10 * time.Second,
		Timeout:     30 * time.Second,
		ReadyToTrip: func(counts gobreaker.Counts) bool {
			failureRatio := float64(counts.TotalFailures) / float64(counts.Requests)
			return counts.Requests >= 3 && failureRatio >= 0.6
		},
		OnStateChange: func(name string, from gobreaker.State, to gobreaker.State) {
			log.Printf("⚡ Circuit breaker '%s' changed from %s to %s", name, from, to)
		},
	}

	return &SpringBootClient{
		baseURL: config.AppConfig.SpringBootURL,
		client:  &http.Client{Timeout: 5 * time.Second},
		breaker: gobreaker.NewCircuitBreaker(settings),
	}
}

func (c *SpringBootClient) ValidateToken(token string) (*TokenValidation, error) {
	reqBody, _ := json.Marshal(map[string]string{"token": token})

	result, err := c.breaker.Execute(func() (interface{}, error) {
		req, err := http.NewRequest("POST", c.baseURL+"/api/v1/chat/validate-token", bytes.NewBuffer(reqBody))
		if err != nil {
			return nil, err
		}

		req.Header.Set("Content-Type", "application/json")

		resp, err := c.client.Do(req)
		if err != nil {
			return nil, err
		}
		defer resp.Body.Close()

		if resp.StatusCode != http.StatusOK {
			return nil, fmt.Errorf("invalid token: status %d", resp.StatusCode)
		}

		var validation TokenValidation
		if err := json.NewDecoder(resp.Body).Decode(&validation); err != nil {
			return nil, err
		}

		return &validation, nil
	})

	if err != nil {
		return nil, err
	}

	return result.(*TokenValidation), nil
}

func (c *SpringBootClient) NotifyMessages(ctx context.Context, messages []*models.Message) error {
	if len(messages) == 0 {
		return nil
	}

	// Convert to notification requests
	notifications := make([]map[string]interface{}, len(messages))
	for i, msg := range messages {
		// Format createdAt as LocalDateTime (without timezone) for Java
		// Java LocalDateTime format: "2025-10-22T00:19:50.123456"
		createdAtStr := msg.CreatedAt.Format("2006-01-02T15:04:05.999999")

		notifications[i] = map[string]interface{}{
			"messageId":  msg.ID,
			"senderId":   msg.SenderId,
			"receiverId": msg.ReceiverId,
			"content":    msg.Content,
			"createdAt":  createdAtStr,
		}
	}

	reqBody, _ := json.Marshal(notifications)

	_, err := c.breaker.Execute(func() (interface{}, error) {
		req, err := http.NewRequestWithContext(ctx, "POST",
			c.baseURL+"/api/v1/chat/messages/notify", bytes.NewBuffer(reqBody))
		if err != nil {
			return nil, err
		}

		req.Header.Set("Content-Type", "application/json")

		resp, err := c.client.Do(req)
		if err != nil {
			return nil, err
		}
		defer resp.Body.Close()

		if resp.StatusCode != http.StatusOK {
			return nil, fmt.Errorf("notification failed: status %d", resp.StatusCode)
		}

		return nil, nil
	})

	if err == gobreaker.ErrOpenState {
		log.Println("⚠Circuit breaker OPEN - Spring Boot may be down, skipping notification")
		return nil // Don't fail, message already saved
	}

	return err
}

func (c *SpringBootClient) HealthCheck() error {
	resp, err := c.client.Get(c.baseURL + "/api/v1/health")
	if err != nil {
		return err
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return fmt.Errorf("unhealthy: status %d", resp.StatusCode)
	}

	return nil
}
