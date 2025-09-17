package com.org.share_recycled_stuff.entity.enums;

import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum Role {
    CUSTOMER(1),
    PROXY_SELLER(2),
    ADMIN(3);

    private final int value;

    Role(int value) {
        this.value = value;
    }

    public static Role fromValue(int value) {
        return Stream.of(Role.values())
                .filter(role -> role.getValue() == value)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid role value: " + value));
    }
}
