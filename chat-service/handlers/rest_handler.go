package handlers

import (
	"chat-service/services"
	"fmt"
	"time"

	"github.com/gofiber/fiber/v2"
)

type RestHandler struct {
	manager          *services.ConnectionManager
	messageService   *services.MessageService
	springBootClient *services.SpringBootClient
}

func NewRestHandler(
	manager *services.ConnectionManager,
	messageService *services.MessageService,
	springBootClient *services.SpringBootClient,
) *RestHandler {
	return &RestHandler{
		manager:          manager,
		messageService:   messageService,
		springBootClient: springBootClient,
	}
}

func (h *RestHandler) GetOnlineUsers(c *fiber.Ctx) error {
	users := h.manager.GetOnlineUsers()
	return c.JSON(fiber.Map{
		"success": true,
		"count":   len(users),
		"users":   users,
	})
}

func (h *RestHandler) CheckUserOnline(c *fiber.Ctx) error {
	userID, err := c.ParamsInt("userId")
	if err != nil {
		return c.Status(400).JSON(fiber.Map{"error": "Invalid user ID"})
	}

	online := h.manager.IsUserOnline(int64(userID))

	return c.JSON(fiber.Map{
		"success": true,
		"user_id": userID,
		"online":  online,
	})
}

func (h *RestHandler) GetMessageHistory(c *fiber.Ctx) error {
	// Get current user from JWT (set by auth middleware)
	currentUserID, ok := c.Locals("userId").(int64)
	if !ok {
		fmt.Printf("GetMessageHistory: No userId in context\n")
		return c.Status(401).JSON(fiber.Map{"error": "Unauthorized"})
	}

	// Get conversation partner
	withUserID, err := c.ParamsInt("withUserId")
	if err != nil {
		fmt.Printf("GetMessageHistory: Invalid withUserId: %v\n", err)
		return c.Status(400).JSON(fiber.Map{"error": "Invalid user ID"})
	}

	fmt.Printf("GetMessageHistory: user=%d, with=%d\n", currentUserID, withUserID)

	// Pagination
	page := c.QueryInt("page", 0)
	size := c.QueryInt("size", 50)
	if size > 100 {
		size = 100 // Max 100 per page
	}

	offset := page * size

	fmt.Printf("Pagination: page=%d, size=%d, offset=%d\n", page, size, offset)

	messages, err := h.messageService.GetConversationHistory(
		currentUserID,
		int64(withUserID),
		size,
		offset,
	)
	if err != nil {
		fmt.Printf("GetMessageHistory: Database error: %v\n", err)
		return c.Status(500).JSON(fiber.Map{"error": "Failed to load history"})
	}

	fmt.Printf("✅ GetMessageHistory: Found %d messages\n", len(messages))

	return c.JSON(fiber.Map{
		"success":  true,
		"messages": messages,
		"page":     page,
		"size":     len(messages),
	})
}

func (h *RestHandler) GetUnreadCount(c *fiber.Ctx) error {
	// Get current user from JWT (set by auth middleware)
	currentUserID, ok := c.Locals("userId").(int64)
	if !ok {
		return c.Status(401).JSON(fiber.Map{"error": "Unauthorized"})
	}

	count, err := h.messageService.GetUnreadCount(currentUserID)
	if err != nil {
		return c.Status(500).JSON(fiber.Map{"error": "Failed to get unread count"})
	}

	return c.JSON(fiber.Map{
		"success": true,
		"count":   count,
	})
}

func (h *RestHandler) HealthCheck(c *fiber.Ctx) error {
	health := fiber.Map{
		"status":    "ok",
		"service":   "chat-service",
		"timestamp": time.Now().Unix(),
	}

	// Check MySQL
	if err := h.messageService.PingDB(); err != nil {
		health["mysql"] = "down"
		health["status"] = "degraded"
	} else {
		health["mysql"] = "ok"
	}

	// Check Redis
	if err := h.manager.PingRedis(); err != nil {
		health["redis"] = "down"
		health["status"] = "degraded"
	} else {
		health["redis"] = "ok"
	}

	// Check Spring Boot
	if err := h.springBootClient.HealthCheck(); err != nil {
		health["springboot"] = "down"
		health["notifications"] = "degraded"
	} else {
		health["springboot"] = "ok"
	}

	// Active connections
	health["active_connections"] = h.manager.GetConnectionCount()

	statusCode := fiber.StatusOK
	if health["status"] == "degraded" {
		statusCode = fiber.StatusServiceUnavailable
	}

	return c.Status(statusCode).JSON(health)
}
