package config

import (
	"log"
	"os"

	"github.com/joho/godotenv"
)

type Config struct {
	ServerPort    string
	RedisAddr     string
	RedisPassword string
	MySQLDSN      string
	SpringBootURL string
}

var AppConfig *Config

func LoadConfig() {
	if err := godotenv.Load(); err != nil {
		log.Println("No .env file found, using environment variables")
	}

	AppConfig = &Config{
		ServerPort:    getEnv("SERVER_PORT", "3003"),
		RedisAddr:     getEnv("REDIS_ADDR", "localhost:6379"),
		RedisPassword: os.Getenv("REDIS_PASSWORD"),
		MySQLDSN:      getEnv("MYSQL_DSN", "root:password@tcp(localhost:33306)/share_recycled_stuff_db?parseTime=true"),
		SpringBootURL: getEnv("SPRING_BOOT_URL", "http://localhost:8080"),
	}

	log.Println("Chat service configuration loaded")
}

func getEnv(key, defaultValue string) string {
	value := os.Getenv(key)
	if value == "" {
		return defaultValue
	}
	return value
}
