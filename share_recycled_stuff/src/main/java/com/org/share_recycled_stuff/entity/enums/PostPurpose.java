package com.org.share_recycled_stuff.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum PostPurpose {
    FREE(1, "Free Giveaway"),
    SALE(2, "For Sale"),
    NEWS(3, "News/Information");

    private final int code;
    private final String displayName;

    public static PostPurpose fromCode(int code) {
        return Arrays.stream(values())
                .filter(purpose -> purpose.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid post purpose code: " + code));
    }
}
