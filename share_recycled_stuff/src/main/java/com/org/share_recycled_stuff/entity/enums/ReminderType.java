package com.org.share_recycled_stuff.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ReminderType {
    DUE_SOON(1, "Due Soon"),
    OVERDUE(2, "Overdue"),
    FINAL_WARNING(3, "Final Warning");

    private final int code;
    private final String displayName;

    public static ReminderType fromCode(int code) {
        return Arrays.stream(values())
                .filter(type -> type.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid reminder type code: " + code));
    }
}
