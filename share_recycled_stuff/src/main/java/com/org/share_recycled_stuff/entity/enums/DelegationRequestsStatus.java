package com.org.share_recycled_stuff.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum DelegationRequestsStatus {
    PENDING(1, "Pending"),
    APPROVED(2, "Approved"),
    REJECTED(3, "Rejected"),
    PRODUCT_RECEIVED(4, "Product Received"),
    SELLING(5, "Selling"),
    SOLD(6, "Sold"),
    PAYMENT_COMPLETED(7, "Payment Completed");

    private final int code;
    private final String displayName;

    public static DelegationRequestsStatus fromCode(int code) {
        return Arrays.stream(values())
                .filter(status -> status.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid delegation status code: " + code));
    }
}
