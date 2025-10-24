package com.org.share_recycled_stuff.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageNotifyRequest {
    private Long messageId;
    private Long senderId;
    private String senderName;
    private Long receiverId;
    private String content;
    private LocalDateTime createdAt;
}

