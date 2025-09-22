package com.org.share_recycled_stuff.controller;

import com.org.share_recycled_stuff.dto.request.RegisterRequest;
import com.org.share_recycled_stuff.dto.response.ApiResponse;
import com.org.share_recycled_stuff.dto.response.RegisterRespone;
import com.org.share_recycled_stuff.dto.response.VerificationResponse;
import com.org.share_recycled_stuff.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<ApiResponse<VerificationResponse>> register(
            @Valid @RequestBody RegisterRequest request,HttpServletRequest httpRequest) {



        VerificationResponse response = authService.register(request);

        ApiResponse<VerificationResponse> apiResponse = ApiResponse.<VerificationResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Đăng ký thành công, vui lòng xác thực email.")
                .path(httpRequest.getRequestURI())
                .timestamp(Instant.now().toString())
                .result(response)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verifyAccount(
            @RequestParam("token") String token,
            HttpServletRequest httpRequest) {

        String response = authService.verifyAccount(token);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(HttpStatus.OK.value())
                .message("Xác thực tài khoản thành công.")
                .path(httpRequest.getRequestURI())
                .timestamp(Instant.now().toString())
                .result(response)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
