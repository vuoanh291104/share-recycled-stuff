package com.org.share_recycled_stuff.controller.api;

import com.org.share_recycled_stuff.dto.request.LoginEmailRequest;
import com.org.share_recycled_stuff.dto.request.RegisterRequest;
import com.org.share_recycled_stuff.dto.response.ApiResponse;
import com.org.share_recycled_stuff.dto.response.LoginResponse;
import com.org.share_recycled_stuff.dto.response.VerificationResponse;
import com.org.share_recycled_stuff.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginEmailRequest request, 
                                                           HttpServletRequest httpRequest) {
        String clientIp = getClientIpAddress(httpRequest);
        request.setClientIp(clientIp);
        
        log.info("Login request from email: {} with IP: {}", request.getEmail(), clientIp);
        
        LoginResponse response = authService.loginWithEmailAndPassword(request);
        log.info("Login successful for email: {}", request.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<VerificationResponse>> register(
            @Valid @RequestBody RegisterRequest request, HttpServletRequest httpRequest) {



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
