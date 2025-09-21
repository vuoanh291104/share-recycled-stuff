package com.org.share_recycled_stuff.service;
import com.org.share_recycled_stuff.dto.request.RegisterRequest;
import com.org.share_recycled_stuff.dto.response.VerificationResponse;

public interface AuthService {
    VerificationResponse register(RegisterRequest request);
}
