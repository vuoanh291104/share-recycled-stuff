package com.org.share_recycled_stuff.utils;

import com.org.share_recycled_stuff.dto.response.NotificationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Component
public class SseEmitterManager {

    private final Map<Long, Set<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public void addEmitter(Long accountId, SseEmitter emitter) {
        emitters.computeIfAbsent(accountId, k -> new CopyOnWriteArraySet<>()).add(emitter);
        log.info("Added SSE emitter for account ID: {}", accountId);
    }

    public void removeEmitter(Long accountId, SseEmitter emitter) {
        Set<SseEmitter> userEmitters = emitters.get(accountId);
        if (userEmitters != null) {
            userEmitters.remove(emitter);
            log.info("Removed SSE emitter for account ID: {}", accountId);
            
            if (userEmitters.isEmpty()) {
                emitters.remove(accountId);
            }
        }
    }

    public void sendToUser(Long accountId, NotificationResponse notification) {
        Set<SseEmitter> userEmitters = emitters.get(accountId);
        
        if (userEmitters == null || userEmitters.isEmpty()) {
            log.debug("No active SSE connections for account ID: {}", accountId);
            return;
        }

        log.info("Sending notification to account ID: {}", accountId);

        userEmitters.removeIf(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(notification));
                return false;
            } catch (IOException e) {
                log.warn("Failed to send notification to account ID: {}", accountId, e);
                return true;
            }
        });

        if (userEmitters.isEmpty()) {
            emitters.remove(accountId);
        }
    }

    public boolean hasConnection(Long accountId) {
        Set<SseEmitter> userEmitters = emitters.get(accountId);
        return userEmitters != null && !userEmitters.isEmpty();
    }

    public int getConnectionCount(Long accountId) {
        Set<SseEmitter> userEmitters = emitters.get(accountId);
        return userEmitters != null ? userEmitters.size() : 0;
    }

    public int getTotalConnectionCount() {
        return emitters.values().stream()
                .mapToInt(Set::size)
                .sum();
    }

    public void removeAllEmitters(Long accountId) {
        Set<SseEmitter> userEmitters = emitters.remove(accountId);
        if (userEmitters != null) {
            userEmitters.forEach(emitter -> {
                try {
                    emitter.complete();
                } catch (Exception e) {
                    log.warn("Error completing emitter for account ID: {}", accountId, e);
                }
            });
            log.info("Removed all SSE emitters for account ID: {}", accountId);
        }
    }
}

