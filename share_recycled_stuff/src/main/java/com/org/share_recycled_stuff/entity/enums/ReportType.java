package com.org.share_recycled_stuff.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ReportType {
    COMPLAINT(1, "General Complaint"),
    POST_VIOLATION(2, "Post Violation"),
    USER_VIOLATION(3, "User Violation");

    private final int code;
    private final String displayName;

    public static ReportType fromCode(int code) {
        return Arrays.stream(values())
                .filter(type -> type.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid report type code: " + code));
    }
}
