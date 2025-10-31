# Stage 1: Build the Go binary
FROM golang:1.23-alpine AS builder

WORKDIR /app

# Copy go.mod and go.sum, download dependencies
COPY go.mod go.sum ./
RUN go mod download

# Copy the rest of the source code
COPY . .

# Build the Go app
RUN go build -o chat-service .

# Stage 2: Run the binary in a minimal image
FROM alpine:latest

WORKDIR /app

# Copy the built binary from builder
COPY --from=builder /app/chat-service .

# Expose the port (change if needed)
EXPOSE 3003

# Run the binary
CMD ["./chat-service"]
