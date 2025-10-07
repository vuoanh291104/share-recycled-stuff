package com.org.share_recycled_stuff.service;

public interface EmailService {
    void sendVerificationEmail(String to, String token);

    void sendPasswordResetEmail(String to, String token);
}
