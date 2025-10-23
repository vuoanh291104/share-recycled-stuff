package services

import (
	"chat-service/config"
	"chat-service/models"
	"context"
	"database/sql"
	"log"
	"time"

	_ "github.com/go-sql-driver/mysql"
)

type MessageService struct {
	db        *sql.DB
	publisher *RedisPublisher // Changed from buffer
}

func NewMessageService() (*MessageService, error) {
	db, err := sql.Open("mysql", config.AppConfig.MySQLDSN)
	if err != nil {
		return nil, err
	}

	db.SetMaxIdleConns(5)
	db.SetMaxOpenConns(10)
	db.SetConnMaxLifetime(time.Hour)
	db.SetConnMaxIdleTime(10 * time.Minute)

	// Test connection
	if err := db.Ping(); err != nil {
		return nil, err
	}

	log.Println("MySQL connection pool established")

	// Initialize Redis publisher
	publisher, err := NewRedisPublisher()
	if err != nil {
		return nil, err
	}

	return &MessageService{
		db:        db,
		publisher: publisher,
	}, nil
}

func (s *MessageService) SaveMessage(msg *models.Message) error {
	result, err := s.db.Exec(
		"INSERT INTO messages (sender_id, receiver_id, content, is_read, created_at) VALUES (?, ?, ?, ?, ?)",
		msg.SenderId, msg.ReceiverId, msg.Content, false, time.Now(),
	)
	if err != nil {
		return err
	}

	id, _ := result.LastInsertId()
	msg.ID = id
	msg.CreatedAt = time.Now()

	// 2. Publish to Redis for Spring Boot notification (non-blocking)
	s.publisher.PublishMessage(msg)

	log.Printf("Message saved: ID=%d, %d → %d", id, msg.SenderId, msg.ReceiverId)

	return nil
}

func (s *MessageService) GetConversationHistory(user1ID, user2ID int64, limit, offset int) ([]*models.Message, error) {
	// Use COALESCE to handle NULL values at database level
	query := `
		SELECT id, sender_id, receiver_id, content, COALESCE(is_read, 0) as is_read, created_at
		FROM messages
		WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?)
		ORDER BY created_at DESC
		LIMIT ? OFFSET ?
	`

	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()

	rows, err := s.db.QueryContext(ctx, query, user1ID, user2ID, user2ID, user1ID, limit, offset)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var messages []*models.Message
	for rows.Next() {
		msg := &models.Message{}
		var createdAt time.Time
		var isReadInt int // Use int instead of bool

		err := rows.Scan(
			&msg.ID,
			&msg.SenderId,
			&msg.ReceiverId,
			&msg.Content,
			&isReadInt, // Scan as int
			&createdAt,
		)
		if err != nil {
			return nil, err
		}

		// Convert int to bool (0 = false, 1 = true)
		msg.IsRead = isReadInt != 0
		msg.CreatedAt = createdAt
		messages = append(messages, msg)
	}

	return messages, rows.Err()
}

func (s *MessageService) GetUnreadCount(userID int64) (int64, error) {
	query := `SELECT COUNT(*) FROM messages WHERE receiver_id = ? AND is_read = false`

	ctx, cancel := context.WithTimeout(context.Background(), 2*time.Second)
	defer cancel()

	var count int64
	err := s.db.QueryRowContext(ctx, query, userID).Scan(&count)
	return count, err
}

func (s *MessageService) PingDB() error {
	ctx, cancel := context.WithTimeout(context.Background(), 2*time.Second)
	defer cancel()
	return s.db.PingContext(ctx)
}

func (s *MessageService) Close() error {
	// Close Redis publisher
	if err := s.publisher.Close(); err != nil {
		log.Printf("Error closing Redis publisher: %v", err)
	}

	// Close database
	return s.db.Close()
}
