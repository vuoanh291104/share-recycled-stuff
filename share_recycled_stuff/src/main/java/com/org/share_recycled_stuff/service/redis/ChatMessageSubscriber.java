package com.org.share_recycled_stuff.service.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.org.share_recycled_stuff.dto.request.MessageNotifyRequest;
import com.org.share_recycled_stuff.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatMessageSubscriber implements MessageListener {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

    // Define DTOs for type safety
    private record RedisMessagePayload(String type, MessageData message) {}

    private record MessageData(
            Long messageId,
            Long senderId,
            Long receiverId,
            String content,
            String createdAt
    ) {}

    @Override
    public void onMessage(@NonNull Message message, byte[] pattern) {
        String data = new String(message.getBody());
        log.debug("Received message from Redis: {}", data);

        try {
            RedisMessagePayload payload = objectMapper.readValue(data, RedisMessagePayload.class);

            if ("message_single".equals(payload.type())) {
                handleSingleMessage(payload.message());
            } else {
                log.warn("Unknown message type from Redis: {}", payload.type());
            }

        } catch (JsonProcessingException e) {
            log.error("Failed to parse Redis message: {}", data, e);
        } catch (Exception e) {
            log.error("Unexpected error processing Redis message", e);
        }
    }

    private void handleSingleMessage(MessageData messageData) {
        if (messageData == null) {
            log.error("Received null message data");
            return;
        }

        try {
            MessageNotifyRequest request = mapToNotifyRequest(messageData);

            // Validate request data
            if (!isValidRequest(request)) {
                log.error("Invalid message request: {}", request);
                return;
            }

            notificationService.createNotification(
                    request.getReceiverId(),
                    "New Message",
                    String.format("You have a new message from user %d: %s",
                            request.getSenderId(),
                            truncateContent(request.getContent(), 100)),
                    1,
                    1,
                    "MESSAGE", // Use constant
                    request.getMessageId()
            );

            log.info("Successfully processed message {} from sender {} to receiver {}",
                    request.getMessageId(), request.getSenderId(), request.getReceiverId());

        } catch (Exception e) {
            log.error("Failed to handle single message: {}", messageData, e);
        }
    }

    private MessageNotifyRequest mapToNotifyRequest(MessageData messageData) {
        LocalDateTime createdAt;
        try {
            createdAt = LocalDateTime.parse(messageData.createdAt(), FORMATTER);
        } catch (DateTimeParseException e) {
            log.warn("Failed to parse date: {}, using current time", messageData.createdAt());
            createdAt = LocalDateTime.now();
        }

        return MessageNotifyRequest.builder()
                .messageId(messageData.messageId())
                .senderId(messageData.senderId())
                .receiverId(messageData.receiverId())
                .content(messageData.content())
                .createdAt(createdAt)
                .build();
    }

    private boolean isValidRequest(MessageNotifyRequest request) {
        return request != null
                && request.getMessageId() != null
                && request.getSenderId() != null
                && request.getReceiverId() != null
                && request.getContent() != null
                && !request.getSenderId().equals(request.getReceiverId());
    }

    private String truncateContent(String content, int maxLength) {
        if (content == null || content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "...";
    }
}
