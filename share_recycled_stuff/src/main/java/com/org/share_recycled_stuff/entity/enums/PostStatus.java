package com.org.share_recycled_stuff.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum PostStatus {
    PENDING(1, "Pending Review"),
    APPROVED(2, "Approved"),
    REJECTED(3, "Rejected");

    private final int code;
    private final String displayName;

    public static PostStatus fromCode(int code) {
        return Arrays.stream(values())
                .filter(status -> status.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid post status code: " + code));
    }
}
