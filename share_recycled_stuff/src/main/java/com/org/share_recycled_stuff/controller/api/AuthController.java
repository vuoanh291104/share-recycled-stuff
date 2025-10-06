package com.org.share_recycled_stuff.controller.api;

import com.org.share_recycled_stuff.config.CustomUserDetail;
import com.org.share_recycled_stuff.dto.request.ChangePasswordRequest;
import com.org.share_recycled_stuff.dto.request.ForgotPasswordRequest;
import com.org.share_recycled_stuff.dto.request.LoginEmailRequest;
import com.org.share_recycled_stuff.dto.request.RegisterRequest;
import com.org.share_recycled_stuff.dto.request.ResetPasswordRequest;
import com.org.share_recycled_stuff.dto.response.ApiResponse;
import com.org.share_recycled_stuff.dto.response.LoginResponse;
import com.org.share_recycled_stuff.dto.response.PasswordResetResponse;
import com.org.share_recycled_stuff.dto.response.VerificationResponse;
import com.org.share_recycled_stuff.service.AuthService;
import com.org.share_recycled_stuff.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

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

    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse<String>> resendVerification(
            @RequestParam("email") String email,
            HttpServletRequest httpRequest) {

        authService.resendVerification(email);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(HttpStatus.OK.value())
                .message("Email xác thực đã được gửi lại.")
                .path(httpRequest.getRequestURI())
                .timestamp(Instant.now().toString())
                .result("OK")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<PasswordResetResponse>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request,
            HttpServletRequest httpRequest) {

        log.info("Forgot password request for email: {}", request.getEmail());

        PasswordResetResponse response = authService.forgotPassword(request);

        ApiResponse<PasswordResetResponse> apiResponse = ApiResponse.<PasswordResetResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Email đặt lại mật khẩu đã được gửi.")
                .path(httpRequest.getRequestURI())
                .timestamp(Instant.now().toString())
                .result(response)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/reset-password/validate")
    public ResponseEntity<ApiResponse<String>> validateResetToken(
            @RequestParam("token") String token,
            HttpServletRequest httpRequest) {

        log.info("Validating reset password token");

        String result = authService.validateResetToken(token);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(HttpStatus.OK.value())
                .message("Token hợp lệ. Vui lòng nhập mật khẩu mới.")
                .path(httpRequest.getRequestURI())
                .timestamp(Instant.now().toString())
                .result(result)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request,
            HttpServletRequest httpRequest) {

        log.info("Reset password request");

        String result = authService.resetPassword(request);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(HttpStatus.OK.value())
                .message("Đặt lại mật khẩu thành công.")
                .path(httpRequest.getRequestURI())
                .timestamp(Instant.now().toString())
                .result(result)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal CustomUserDetail userDetail,
            HttpServletRequest httpRequest) {

        log.info("Change password request for user: {}", userDetail.getUsername());

        String result = authService.changePassword(request);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(HttpStatus.OK.value())
                .message("Đổi mật khẩu thành công.")
                .path(httpRequest.getRequestURI())
                .timestamp(Instant.now().toString())
                .result(result)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest httpRequest) {
        String bearerToken = httpRequest.getHeader("Authorization");
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Missing or invalid Authorization header", httpRequest.getRequestURI()));
        }

        String token = bearerToken.substring(7);

        // Validate token first
        if (!jwtService.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Invalid or expired token", httpRequest.getRequestURI()));
        }

        // Check if already blacklisted
        if (jwtService.isBlacklisted(token)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Token is already invalidated", httpRequest.getRequestURI()));
        }

        // Calculate remaining TTL
        Long expEpoch = jwtService.getExpirationEpochSeconds(token);
        long nowEpoch = Instant.now().getEpochSecond();
        long secondsLeft = expEpoch != null ? Math.max(0, expEpoch - nowEpoch) : 0L;

        if (secondsLeft > 0) {
            jwtService.blacklistToken(token, java.time.Duration.ofSeconds(secondsLeft));
            log.info("User logged out successfully, token blacklisted for {} seconds", secondsLeft);
        } else {
            log.info("User logged out, token already expired");
        }

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(HttpStatus.OK.value())
                .message("Đăng xuất thành công")
                .path(httpRequest.getRequestURI())
                .timestamp(Instant.now().toString())
                .result("OK")
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
