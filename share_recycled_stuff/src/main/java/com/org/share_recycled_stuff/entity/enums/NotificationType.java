package com.org.share_recycled_stuff.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    SYSTEM(1, "System"),
    CONSIGNMENT_STATUS(2, "Consignment Status"),
    NEW_REVIEW(3, "New Review"),
    POST_INTERACTION(4, "Post Interaction"),
    ACCOUNT_UPGRADE(5, "Account Upgrade"),
    PAYMENT_REMINDER(6, "Payment Reminder"),
    ADMIN_MESSAGE(7, "Admin Message"),
    POST_REVIEW(8, "Post Review");

    private final int code;
    private final String displayName;

    public static NotificationType fromCode(int code) {
        return Arrays.stream(values())
                .filter(type -> type.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid notification type code: " + code));
    }
}
