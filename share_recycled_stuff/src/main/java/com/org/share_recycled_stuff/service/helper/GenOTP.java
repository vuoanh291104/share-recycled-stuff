package com.org.share_recycled_stuff.service.helper;

import java.security.SecureRandom;

public class GenOTP {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int OTP_LENGTH = 6;
    private static final int MAX_OTP_VALUE = 1000000; // 10^6 for 6 digits

    public static String generateOTP() {
        int otp = SECURE_RANDOM.nextInt(MAX_OTP_VALUE);
        return String.format("%0" + OTP_LENGTH + "d", otp);
    }
}
