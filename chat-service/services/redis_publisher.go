package services

import (
	"chat-service/config"
	"chat-service/models"
	"context"
	"encoding/json"
	"log"
	"time"

	"github.com/redis/go-redis/v9"
)

type RedisPublisher struct {
	client *redis.Client
	buffer chan *models.Message
}

func NewRedisPublisher() (*RedisPublisher, error) {
	client := redis.NewClient(&redis.Options{
		Addr:     config.AppConfig.RedisAddr,
		Password: config.AppConfig.RedisPassword,
		DB:       0,
	})

	ctx := context.Background()
	if err := client.Ping(ctx).Err(); err != nil {
		return nil, err
	}

	publisher := &RedisPublisher{
		client: client,
		buffer: make(chan *models.Message, 100),
	}

	go publisher.run()
	log.Println("Redis publisher started")

	return publisher, nil
}

func (p *RedisPublisher) PublishMessage(msg *models.Message) {
	go p.publishSingle(msg)
}

func (p *RedisPublisher) run() {
	batch := make([]*models.Message, 0, 10)
	ticker := time.NewTicker(2 * time.Second)
	defer ticker.Stop()

	for {
		select {
		case msg := <-p.buffer:
			batch = append(batch, msg)
			if len(batch) >= 10 {
				p.publishBatch(batch)
				batch = make([]*models.Message, 0, 10)
			}

		case <-ticker.C:
			if len(batch) > 0 {
				p.publishBatch(batch)
				batch = make([]*models.Message, 0, 10)
			}
		}
	}
}

func (p *RedisPublisher) publishBatch(messages []*models.Message) {
	ctx, cancel := context.WithTimeout(context.Background(), 2*time.Second)
	defer cancel()

	notifications := make([]map[string]interface{}, len(messages))
	for i, msg := range messages {
		notifications[i] = map[string]interface{}{
			"messageId":  msg.ID,
			"senderId":   msg.SenderId,
			"receiverId": msg.ReceiverId,
			"content":    msg.Content,
			"createdAt":  msg.CreatedAt.Format("2006-01-02T15:04:05.999999"), // Java LocalDateTime format
		}
	}

	data, err := json.Marshal(map[string]interface{}{
		"type":     "message_batch",
		"messages": notifications,
	})
	if err != nil {
		log.Printf("Failed to marshal messages: %v", err)
		return
	}

	if err := p.client.Publish(ctx, "chat:messages", data).Err(); err != nil {
		log.Printf("Failed to publish to Redis: %v", err)
	} else {
		log.Printf("Published %d messages to Redis", len(messages))
	}
}

func (p *RedisPublisher) publishSingle(msg *models.Message) {
	ctx, cancel := context.WithTimeout(context.Background(), 2*time.Second)
	defer cancel()

	data, err := json.Marshal(map[string]interface{}{
		"type": "message_single",
		"message": map[string]interface{}{
			"messageId":  msg.ID,
			"senderId":   msg.SenderId,
			"receiverId": msg.ReceiverId,
			"content":    msg.Content,
			"createdAt":  msg.CreatedAt.Format("2006-01-02T15:04:05.999999"), // Java LocalDateTime format
		},
	})
	if err != nil {
		log.Printf("Failed to marshal single message: %v", err)
		return
	}

	if err := p.client.Publish(ctx, "chat:messages", data).Err(); err != nil {
		log.Printf("Failed to publish single message to Redis: %v", err)
	}
}

func (p *RedisPublisher) Close() error {
	return p.client.Close()
}
