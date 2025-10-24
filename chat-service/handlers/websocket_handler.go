package handlers

import (
	"chat-service/models"
	"chat-service/services"
	"context"
	"encoding/json"
	"log"
	"time"

	"github.com/gofiber/websocket/v2"
)

type WebSocketHandler struct {
	manager        *services.ConnectionManager
	authService    *services.AuthService
	messageService *services.MessageService
}

func NewWebSocketHandler(
	manager *services.ConnectionManager,
	authService *services.AuthService,
	messageService *services.MessageService,
) *WebSocketHandler {
	return &WebSocketHandler{
		manager:        manager,
		authService:    authService,
		messageService: messageService,
	}
}

func (h *WebSocketHandler) HandleWebSocket(c *websocket.Conn) {
	// Extract token from query parameter as fallback since Sec-WebSocket-Protocol
	// doesn't work well with JWT tokens in browser
	var token string

	// Try to get from Locals (set by middleware if available)
	if t := c.Locals("token"); t != nil {
		token = t.(string)
	}

	// Try query parameter
	if token == "" {
		token = c.Query("token")
	}

	if token == "" {
		log.Println("No token provided (use ?token=<JWT> in URL)")
		c.Close()
		return
	}

	// Validate token via Spring Boot
	validation, err := h.authService.ValidateToken(token)
	if err != nil || !validation.Valid {
		// DEV MODE: Allow test connection with "test" token
		if token == "test" {
			log.Println("DEV MODE: Using test token (accountId=1)")
			client := &services.Client{
				AccountID: 1,
				Email:     "test@example.com",
				Conn:      c,
				Send:      make(chan *models.Message, 256),
			}
			h.manager.Register <- client
			go h.writePump(client)
			h.readPump(client)
			return
		}

		log.Printf("Invalid token: %v", err)
		c.Close()
		return
	}

	// Create client
	client := &services.Client{
		AccountID: validation.AccountID,
		Email:     validation.Email,
		Conn:      c,
		Send:      make(chan *models.Message, 256),
	}

	// Register client
	h.manager.Register <- client

	// Start goroutines
	go h.writePump(client)
	h.readPump(client)
}

// extractToken is no longer needed - Subprotocol() returns the selected protocol directly

func (h *WebSocketHandler) readPump(client *services.Client) {
	defer func() {
		h.manager.Unregister <- client
		client.Conn.Close()
	}()

	// Set read deadline and pong handler
	client.Conn.SetReadDeadline(time.Now().Add(60 * time.Second))
	client.Conn.SetPongHandler(func(string) error {
		client.Conn.SetReadDeadline(time.Now().Add(60 * time.Second))
		return nil
	})

	// Context with max connection time (24 hours)
	ctx, cancel := context.WithTimeout(context.Background(), 24*time.Hour)
	defer cancel()

	for {
		select {
		case <-ctx.Done():
			log.Printf("Connection timeout for user %d", client.AccountID)
			return

		default:
			_, messageData, err := client.Conn.ReadMessage()
			if err != nil {
				if websocket.IsUnexpectedCloseError(err, websocket.CloseGoingAway, websocket.CloseAbnormalClosure) {
					log.Printf("WebSocket error for user %d: %v", client.AccountID, err)
				}
				return
			}

			var message models.Message
			if err := json.Unmarshal(messageData, &message); err != nil {
				log.Printf("Invalid message format from user %d: %v", client.AccountID, err)
				continue
			}

			// Set sender
			message.SenderId = client.AccountID

			// Handle different message types
			switch message.Type {
			case models.MessageTypeText:
				if message.ReceiverId == 0 {
					log.Printf("Message missing receiver from user %d", client.AccountID)
					continue
				}

				// Save to MySQL
				if err := h.messageService.SaveMessage(&message); err != nil {
					log.Printf("Failed to save message: %v", err)
					continue
				}

				// Broadcast to recipient if online
				h.manager.SendMessage(&message)

			case models.MessageTypeTyping:
				// Forward typing indicator
				h.manager.SendMessage(&message)

			case models.MessageTypeRead:
				// TODO: Mark message as read in database
				h.manager.SendMessage(&message)
			}
		}
	}
}

func (h *WebSocketHandler) writePump(client *services.Client) {
	ticker := time.NewTicker(54 * time.Second)
	defer func() {
		ticker.Stop()
		client.Conn.Close()
	}()

	for {
		select {
		case message, ok := <-client.Send:
			client.Conn.SetWriteDeadline(time.Now().Add(10 * time.Second))

			if !ok {
				// Channel closed
				client.Conn.WriteMessage(websocket.CloseMessage, []byte{})
				return
			}

			// Send message as JSON
			if err := client.Conn.WriteJSON(message); err != nil {
				log.Printf("Write error for user %d: %v", client.AccountID, err)
				return
			}

		case <-ticker.C:
			// Send ping
			client.Conn.SetWriteDeadline(time.Now().Add(10 * time.Second))
			if err := client.Conn.WriteMessage(websocket.PingMessage, nil); err != nil {
				return
			}
		}
	}
}
