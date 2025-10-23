package services

import (
	"chat-service/models"
	"context"
	"log"
	"time"
)

type MessageBuffer struct {
	messages         chan *models.Message
	batchSize        int
	flushInterval    time.Duration
	springBootClient *SpringBootClient
}

func NewMessageBuffer(client *SpringBootClient) *MessageBuffer {
	buffer := &MessageBuffer{
		messages:         make(chan *models.Message, 100),
		batchSize:        10,
		flushInterval:    2 * time.Second,
		springBootClient: client,
	}

	go buffer.run()
	log.Println("✅ Message buffer started (batch: 10, interval: 2s)")

	return buffer
}

func (b *MessageBuffer) Add(msg *models.Message) {
	select {
	case b.messages <- msg:
		// Added to buffer
	default:
		// Buffer full, notify immediately
		log.Println("Buffer full, notifying immediately")
		go func() {
			ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
			defer cancel()
			b.springBootClient.NotifyMessages(ctx, []*models.Message{msg})
		}()
	}
}

func (b *MessageBuffer) run() {
	batch := make([]*models.Message, 0, b.batchSize)
	ticker := time.NewTicker(b.flushInterval)
	defer ticker.Stop()

	for {
		select {
		case msg := <-b.messages:
			batch = append(batch, msg)
			if len(batch) >= b.batchSize {
				b.flush(batch)
				batch = make([]*models.Message, 0, b.batchSize)
			}

		case <-ticker.C:
			if len(batch) > 0 {
				b.flush(batch)
				batch = make([]*models.Message, 0, b.batchSize)
			}
		}
	}
}

func (b *MessageBuffer) flush(batch []*models.Message) {
	defer func() {
		if r := recover(); r != nil {
			log.Printf("Panic in message buffer flush: %v", r)
		}
	}()

	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()

	if err := b.springBootClient.NotifyMessages(ctx, batch); err != nil {
		// Only log in debug mode to reduce noise in dev
		// log.Printf(" Failed to notify Spring Boot (batch size: %d): %v", len(batch), err)
		// Messages already saved to MySQL, notification can be retried later if needed
	} else {
		log.Printf("Notified Spring Boot: %d messages", len(batch))
	}
}
