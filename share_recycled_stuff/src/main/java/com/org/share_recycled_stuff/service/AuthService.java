package com.org.share_recycled_stuff.service;

import com.org.share_recycled_stuff.dto.request.LoginEmailRequest;
import com.org.share_recycled_stuff.dto.request.RegisterRequest;
import com.org.share_recycled_stuff.dto.response.LoginResponse;
import com.org.share_recycled_stuff.dto.response.VerificationResponse;


public interface AuthService {
    VerificationResponse register(RegisterRequest request);
    LoginResponse loginWithEmailAndPassword(LoginEmailRequest request);
    String verifyAccount(String token);
    void resendVerification(String email);
}
