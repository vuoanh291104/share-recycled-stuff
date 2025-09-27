package com.org.share_recycled_stuff.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum PostStatus {
    ACTIVE(1, "Active"),
    EDIT(2, "Request for Edit"),
    DELETED(3, "Deleted");

    private final int code;
    private final String displayName;

    public static PostStatus fromCode(int code) {
        return Arrays.stream(values())
                .filter(status -> status.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid post status code: " + code));
    }
}
