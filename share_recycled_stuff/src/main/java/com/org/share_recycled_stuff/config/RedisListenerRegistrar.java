package com.org.share_recycled_stuff.config;

import com.org.share_recycled_stuff.service.redis.ChatMessageSubscriber;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisListenerRegistrar {

    private final RedisMessageListenerContainer container;
    private final ChatMessageSubscriber subscriber;
    private final PatternTopic chatMessagesTopic;

    @PostConstruct
    public void registerListeners() {
        container.addMessageListener(subscriber, chatMessagesTopic);
        System.out.println("✅ Redis message listener registered for chat:messages");
    }
}
