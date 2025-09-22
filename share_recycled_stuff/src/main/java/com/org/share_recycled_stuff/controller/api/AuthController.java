package com.org.share_recycled_stuff.controller.api;

import com.org.share_recycled_stuff.dto.request.LoginEmailRequest;
import com.org.share_recycled_stuff.dto.response.ApiResponse;
import com.org.share_recycled_stuff.dto.response.LoginResponse;
import com.org.share_recycled_stuff.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
