package com.org.share_recycled_stuff.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum TransactionStatus {
    PENDING(1, "Pending"),
    SUCCESS(2, "Success"),
    FAILED(3, "Failed");

    private final int code;
    private final String displayName;

    public static TransactionStatus fromCode(int code) {
        return Arrays.stream(values())
                .filter(status -> status.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid transaction status code: " + code));
    }
}
