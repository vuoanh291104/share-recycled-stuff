package com.org.share_recycled_stuff.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ReportStatus {
    PENDING(1, "Pending"),
    PROCESSING(2, "Processing"),
    RESOLVED(3, "Resolved");

    private final int code;
    private final String displayName;

    public static ReportStatus fromCode(int code) {
        return Arrays.stream(values())
                .filter(status -> status.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid report status code: " + code));
    }
}
