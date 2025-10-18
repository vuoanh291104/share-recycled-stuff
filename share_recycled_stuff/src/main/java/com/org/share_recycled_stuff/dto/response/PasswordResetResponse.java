package com.org.share_recycled_stuff.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Password reset response with email and expiration information")
public class PasswordResetResponse {
    @Schema(description = "Email address for password reset", example = "user@example.com")
    private String email;

    @Schema(description = "Success message", example = "Password reset link has been sent to your email")
    private String message;

    @Schema(description = "Token expiration timestamp", example = "2024-01-01T11:00:00")
    private LocalDateTime expiresAt;
}
