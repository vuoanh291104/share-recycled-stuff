package models

import "time"

type MessageType string

const (
	MessageTypeText    MessageType = "text"
	MessageTypeTyping  MessageType = "typing"
	MessageTypeOnline  MessageType = "online"
	MessageTypeOffline MessageType = "offline"
	MessageTypeRead    MessageType = "read"
)

// Message for WebSocket communication
type Message struct {
	ID         int64       `json:"id,omitempty"`
	Type       MessageType `json:"type"`
	SenderId   int64       `json:"sender_id,omitempty"`
	ReceiverId int64       `json:"receiver_id,omitempty"`
	Content    string      `json:"content,omitempty"`
	IsRead     bool        `json:"is_read"`
	CreatedAt  time.Time   `json:"created_at,omitempty"`
}

// DBMessage for MySQL operations
type DBMessage struct {
	ID         int64
	SenderID   int64
	ReceiverID int64
	Content    string
	IsRead     bool
	CreatedAt  time.Time
}
