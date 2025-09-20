package com.org.share_recycled_stuff.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum DeliveryMethod {
    IN_APP(1, "In-App"),
    EMAIL(2, "Email"),
    BOTH(3, "Both");

    private final int code;
    private final String displayName;

    public static DeliveryMethod fromCode(int code) {
        return Arrays.stream(values())
                .filter(method -> method.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid delivery method code: " + code));
    }
}
