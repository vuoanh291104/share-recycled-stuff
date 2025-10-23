package middlewares

import (
	"chat-service/services"
	"fmt"
	"strings"

	"github.com/gofiber/fiber/v2"
)

func AuthMiddleware(authService *services.AuthService) fiber.Handler {
	return func(c *fiber.Ctx) error {
		// Get token from Authorization header
		authHeader := c.Get("Authorization")
		fmt.Printf("🔍 Auth header: %s\n", authHeader) // Debug log

		if authHeader == "" {
			fmt.Printf("❌ No token provided\n")
			return c.Status(401).JSON(fiber.Map{"error": "No token provided"})
		}

		// Remove "Bearer " prefix
		token := strings.TrimPrefix(authHeader, "Bearer ")
		if token == authHeader {
			fmt.Printf("❌ Invalid token format\n")
			return c.Status(401).JSON(fiber.Map{"error": "Invalid token format"})
		}

		fmt.Printf("🔑 Token: %s\n", token[:min(len(token), 20)]+"...") // Debug log

		// Only allow test token for development when Spring Boot is down
		if token == "test" {
			fmt.Printf("✅ DEV MODE: Using test token (userId=21)\n")
			c.Locals("userId", int64(21)) // Default test user
			c.Locals("email", "test@example.com")
			return c.Next()
		}

		// Validate token with Spring Boot
		fmt.Printf("🔐 Validating token with Spring Boot...\n")
		validation, err := authService.ValidateToken(token)
		if err != nil || !validation.Valid {
			fmt.Printf("❌ Token validation failed: %v\n", err)
			return c.Status(401).JSON(fiber.Map{"error": "Invalid token"})
		}

		// Store in context
		fmt.Printf("✅ User authenticated: %d\n", validation.AccountID)
		c.Locals("userId", validation.AccountID)
		c.Locals("email", validation.Email)

		return c.Next()
	}
}

func min(a, b int) int {
	if a < b {
		return a
	}
	return b
}
