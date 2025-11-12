package com.org.share_recycled_stuff.controller.api;

import com.org.share_recycled_stuff.config.CustomUserDetail;
import com.org.share_recycled_stuff.dto.request.*;
import com.org.share_recycled_stuff.dto.response.ApiResponse;
import com.org.share_recycled_stuff.dto.response.LoginResponse;
import com.org.share_recycled_stuff.dto.response.PasswordResetResponse;
import com.org.share_recycled_stuff.dto.response.VerificationResponse;
import com.org.share_recycled_stuff.service.AuthService;
import com.org.share_recycled_stuff.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    @Operation(
            summary = "Login with email and password",
            description = "Authenticate user with email and password. Returns JWT access token and refresh token."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Login successful. Returns ApiResponse<LoginResponse> with JWT tokens in result field."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials or account not verified. Returns ApiResponse with error details."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "423",
                    description = "Account is locked. Returns ApiResponse with lock information."
            )
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Login credentials (email and password)",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginEmailRequest.class)
                    )
            )
            @Valid @RequestBody LoginEmailRequest request,
            HttpServletRequest httpRequest) {
        String clientIp = getClientIpAddress(httpRequest);
        request.setClientIp(clientIp);

        log.info("Login request from email: {} with IP: {}", request.getEmail(), clientIp);

        LoginResponse response = authService.loginWithEmailAndPassword(request);
        log.info("Login successful for email: {}", request.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @Operation(
            summary = "Refresh authentication tokens",
            description = "Exchange a valid refresh token for a new access token (and refresh token)."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Token refresh successful. Returns ApiResponse<LoginResponse> with new tokens."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid request payload. Returns ApiResponse with error details."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Refresh token invalid or expired. Returns ApiResponse with error details."
            )
    })
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Refresh token payload",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RefreshTokenRequest.class)
                    )
            )
            @Valid @RequestBody RefreshTokenRequest request,
            HttpServletRequest httpRequest) {

        log.info("Refresh token request from IP: {}", getClientIpAddress(httpRequest));

        LoginResponse response = authService.refreshToken(request);

        ApiResponse<LoginResponse> apiResponse = ApiResponse.<LoginResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Refresh token thành công")
                .path(httpRequest.getRequestURI())
                .timestamp(Instant.now().toString())
                .result(response)
                .build();

        return ResponseEntity.ok(apiResponse);
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

    @Operation(
            summary = "Register new user account",
            description = "Create a new user account with email and password. Sends verification email to activate account."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Registration successful, verification email sent. Returns ApiResponse<VerificationResponse>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input or email already exists. Returns ApiResponse with error details."
            )
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<VerificationResponse>> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Registration details including email, password, and user information",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RegisterRequest.class)
                    )
            )
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

    @Operation(
            summary = "Verify email address",
            description = "Verify user's email address using the token sent via email. Activates the account upon successful verification."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Email verified successfully. Returns ApiResponse<String>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid or expired token. Returns ApiResponse with error message."
            )
    })
    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verifyAccount(
            @Parameter(
                    description = "Verification token received via email",
                    required = true,
                    example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
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

    @Operation(
            summary = "Resend verification email",
            description = "Send verification email again to user if the previous email was not received or expired"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Verification email resent successfully. Returns ApiResponse<String>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Email not found or account already verified. Returns ApiResponse with error."
            )
    })
    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse<String>> resendVerification(
            @Parameter(description = "User email address", required = true, example = "user@example.com")
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

    @Operation(
            summary = "Forgot password",
            description = "Initiate password reset process by sending a reset token to user's email"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Password reset email sent successfully. Returns ApiResponse<PasswordResetResponse>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Email not found. Returns ApiResponse with error."
            )
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<PasswordResetResponse>> forgotPassword(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Forgot password request with user email",
                    required = true
            )
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

    @Operation(
            summary = "Validate reset password token",
            description = "Verify if a password reset token is valid before allowing user to set new password"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Token is valid. Returns ApiResponse<String>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid or expired token. Returns ApiResponse with error."
            )
    })
    @GetMapping("/reset-password/validate")
    public ResponseEntity<ApiResponse<String>> validateResetToken(
            @Parameter(description = "Password reset token", required = true, example = "eyJhbGciOiJI...")
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

    @Operation(
            summary = "Reset password",
            description = "Set a new password using a valid reset token received via email"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Password reset successfully. Returns ApiResponse<String>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid token or weak password. Returns ApiResponse with error."
            )
    })
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Reset password request with token and new password",
                    required = true
            )
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

    @Operation(
            summary = "Change password",
            description = "Change password for authenticated user (requires current password verification)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Password changed successfully. Returns ApiResponse<String>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Current password incorrect or weak new password. Returns ApiResponse with error."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token required. Returns ApiResponse with error."
            )
    })
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Change password request with current password and new password",
                    required = true
            )
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

    @Operation(
            summary = "Logout",
            description = "Logout user by blacklisting the JWT access token (requires Authorization header with Bearer token)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Logout successful, token blacklisted. Returns ApiResponse<String>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Missing/invalid Authorization header or token already invalidated. Returns ApiResponse with error."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Invalid or expired token. Returns ApiResponse with error."
            )
    })
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
