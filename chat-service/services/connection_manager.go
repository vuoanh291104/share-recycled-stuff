package services

import (
	"chat-service/config"
	"chat-service/models"
	"context"
	"log"
	"sync"
	"time"

	"github.com/gofiber/websocket/v2"
	"github.com/redis/go-redis/v9"
)

type Client struct {
	AccountID int64
	Email     string
	Conn      *websocket.Conn
	Send      chan *models.Message
}

type ConnectionManager struct {
	clients    map[int64]*Client
	redis      *redis.Client
	Register   chan *Client
	Unregister chan *Client
	broadcast  chan *models.Message
	mu         sync.RWMutex
}

func NewConnectionManager() (*ConnectionManager, error) {
	rdb := redis.NewClient(&redis.Options{
		Addr:     config.AppConfig.RedisAddr,
		Password: config.AppConfig.RedisPassword,
		DB:       0,
	})

	// Test connection
	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()

	if err := rdb.Ping(ctx).Err(); err != nil {
		return nil, err
	}

	log.Println("Redis connection established")

	manager := &ConnectionManager{
		clients:    make(map[int64]*Client),
		redis:      rdb,
		Register:   make(chan *Client),
		Unregister: make(chan *Client),
		broadcast:  make(chan *models.Message, 256),
	}

	return manager, nil
}

func (m *ConnectionManager) Run() {
	log.Println("Connection Manager started")

	for {
		select {
		case client := <-m.Register:
			m.registerClient(client)

		case client := <-m.Unregister:
			m.unregisterClient(client)

		case message := <-m.broadcast:
			m.broadcastMessage(message)
		}
	}
}

func (m *ConnectionManager) registerClient(client *Client) {
	m.mu.Lock()
	m.clients[client.AccountID] = client
	m.mu.Unlock()

	// Add to Redis online set
	ctx := context.Background()
	m.redis.SAdd(ctx, "chat:online:users", client.AccountID)

	log.Printf("User %d connected (total: %d)", client.AccountID, len(m.clients))

	// Broadcast online status
	m.broadcast <- &models.Message{
		Type:      models.MessageTypeOnline,
		SenderId:  client.AccountID,
		CreatedAt: time.Now(),
	}
}

func (m *ConnectionManager) unregisterClient(client *Client) {
	m.mu.Lock()
	if _, ok := m.clients[client.AccountID]; ok {
		delete(m.clients, client.AccountID)
		close(client.Send)
	}
	m.mu.Unlock()

	// Remove from Redis
	ctx := context.Background()
	m.redis.SRem(ctx, "chat:online:users", client.AccountID)

	log.Printf("User %d disconnected (total: %d)", client.AccountID, len(m.clients))

	// Broadcast offline status
	m.broadcast <- &models.Message{
		Type:      models.MessageTypeOffline,
		SenderId:  client.AccountID,
		CreatedAt: time.Now(),
	}
}

func (m *ConnectionManager) broadcastMessage(message *models.Message) {
	m.mu.RLock()
	defer m.mu.RUnlock()

	switch message.Type {
	case models.MessageTypeText:
		// Send to specific recipient
		if recipient, ok := m.clients[message.ReceiverId]; ok {
			select {
			case recipient.Send <- message:
				log.Printf("Delivered: %d → %d", message.SenderId, message.ReceiverId)
			default:
				log.Printf("Send channel full for user %d", message.ReceiverId)
			}
		}

	case models.MessageTypeTyping:
		// Send typing indicator to recipient
		if recipient, ok := m.clients[message.ReceiverId]; ok {
			select {
			case recipient.Send <- message:
			default:
			}
		}

	case models.MessageTypeOnline, models.MessageTypeOffline:
		// Broadcast to all connected clients
		for accountID, client := range m.clients {
			if accountID != message.SenderId {
				select {
				case client.Send <- message:
				default:
				}
			}
		}
	}
}

func (m *ConnectionManager) IsUserOnline(accountID int64) bool {
	m.mu.RLock()
	defer m.mu.RUnlock()
	_, ok := m.clients[accountID]
	return ok
}

func (m *ConnectionManager) GetOnlineUsers() []int64 {
	m.mu.RLock()
	defer m.mu.RUnlock()

	users := make([]int64, 0, len(m.clients))
	for accountID := range m.clients {
		users = append(users, accountID)
	}
	return users
}

func (m *ConnectionManager) GetConnectionCount() int {
	m.mu.RLock()
	defer m.mu.RUnlock()
	return len(m.clients)
}

func (m *ConnectionManager) PingRedis() error {
	ctx, cancel := context.WithTimeout(context.Background(), 2*time.Second)
	defer cancel()
	return m.redis.Ping(ctx).Err()
}

func (m *ConnectionManager) SendMessage(message *models.Message) {
	m.broadcast <- message
}
