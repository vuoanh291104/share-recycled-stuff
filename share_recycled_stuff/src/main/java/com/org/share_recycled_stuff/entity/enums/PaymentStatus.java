package com.org.share_recycled_stuff.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {
    NOT_DUE(1, "Not Due"),
    PENDING(2, "Pending"),
    PAID(3, "Paid"),
    OVERDUE(4, "Overdue");

    private final int code;
    private final String displayName;

    public static PaymentStatus fromCode(int code) {
        return Arrays.stream(values())
                .filter(status -> status.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid payment status code: " + code));
    }
}
