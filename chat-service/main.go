package main

import (
	"chat-service/config"
	"chat-service/handlers"
	"chat-service/middlewares"
	"chat-service/services"
	"log"
	"os"
	"os/signal"
	"syscall"
	"time"

	"github.com/gofiber/fiber/v2"
	"github.com/gofiber/fiber/v2/middleware/cors"
	"github.com/gofiber/fiber/v2/middleware/logger"
	"github.com/gofiber/fiber/v2/middleware/recover"
	"github.com/gofiber/websocket/v2"
)

func main() {
	// Load configuration
	config.LoadConfig()

	// Initialize Spring Boot client
	springBootClient := services.NewSpringBootClient()

	// Initialize services
	messageService, err := services.NewMessageService()
	if err != nil {
		log.Fatalf("Failed to initialize message service: %v", err)
	}
	defer messageService.Close()

	authService := services.NewAuthService(springBootClient)

	connManager, err := services.NewConnectionManager()
	if err != nil {
		log.Fatalf("Failed to initialize connection manager: %v", err)
	}

	// Start connection manager
	go connManager.Run()

	// Initialize handlers
	wsHandler := handlers.NewWebSocketHandler(connManager, authService, messageService)
	restHandler := handlers.NewRestHandler(connManager, messageService, springBootClient)

	// Initialize Fiber
	app := fiber.New(fiber.Config{
		Prefork:      false,
		AppName:      "Chat Service v1.0",
		ReadTimeout:  10 * time.Second,
		WriteTimeout: 10 * time.Second,
	})

	// Middleware
	app.Use(recover.New())
	app.Use(logger.New(logger.Config{
		Format: "[${time}] ${status} - ${method} ${path} (${latency})\n",
	}))
	app.Use(cors.New(cors.Config{
		AllowOrigins: "*",
		AllowHeaders: "Origin, Content-Type, Accept, Authorization, Sec-WebSocket-Protocol",
	}))

	// REST endpoints
	api := app.Group("/api/v1")

	// Public endpoints
	api.Get("/health", restHandler.HealthCheck)

	// Protected endpoints (require JWT authentication)
	authMiddleware := middlewares.AuthMiddleware(authService)
	api.Get("/messages/history/:withUserId", authMiddleware, restHandler.GetMessageHistory)
	api.Get("/messages/unread-count", authMiddleware, restHandler.GetUnreadCount)
	api.Get("/users/online", authMiddleware, restHandler.GetOnlineUsers)
	api.Get("/users/:userId/online", authMiddleware, restHandler.CheckUserOnline)

	// Admin endpoints (called by Spring Boot, no auth needed)
	api.Post("/admin/disconnect/:accountId", restHandler.ForceDisconnectUser)

	// WebSocket upgrade middleware
	app.Use("/ws", func(c *fiber.Ctx) error {
		if websocket.IsWebSocketUpgrade(c) {
			return c.Next()
		}
		return fiber.ErrUpgradeRequired
	})

	// WebSocket endpoint
	app.Get("/ws", websocket.New(wsHandler.HandleWebSocket))

	// Graceful shutdown
	go func() {
		sigint := make(chan os.Signal, 1)
		signal.Notify(sigint, os.Interrupt, syscall.SIGTERM)
		<-sigint

		log.Println("Shutting down chat service...")
		app.Shutdown()
	}()

	// Start server
	port := ":" + config.AppConfig.ServerPort
	log.Printf("Chat service starting on http://localhost%s", port)
	log.Printf("WebSocket endpoint: ws://localhost%s/ws", config.AppConfig.ServerPort)
	log.Printf("Health check: http://localhost%s/api/v1/health", config.AppConfig.ServerPort)

	if err := app.Listen(port); err != nil {
		log.Fatalf("Failed to start: %v", err)
	}
}
