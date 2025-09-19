package com.org.share_recycled_stuff.service;
import com.org.share_recycled_stuff.dto.request.RegisterRequest;
import com.org.share_recycled_stuff.dto.response.ApiResponse;


import java.util.Optional;

public interface RegisterService {
    ApiResponse<Void> register(RegisterRequest request);
}
