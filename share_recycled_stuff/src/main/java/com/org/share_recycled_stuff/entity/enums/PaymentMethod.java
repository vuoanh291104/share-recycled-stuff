package com.org.share_recycled_stuff.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum PaymentMethod {
    VNPAY(1, "VNPay"),
    BANK_TRANSFER(2, "Bank Transfer"),
    CASH(3, "Cash");

    private final int code;
    private final String displayName;

    public static PaymentMethod fromCode(int code) {
        return Arrays.stream(values())
                .filter(method -> method.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid payment method code: " + code));
    }
}
