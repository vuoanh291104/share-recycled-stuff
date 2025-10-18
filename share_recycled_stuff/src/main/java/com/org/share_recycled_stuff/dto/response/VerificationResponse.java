package com.org.share_recycled_stuff.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Email verification response with token and expiration")
public class VerificationResponse {
    @Schema(description = "Email address to verify", example = "user@example.com")
    private String email;

    @Schema(description = "Token expiration timestamp", example = "2024-01-01T11:00:00")
    private LocalDateTime expiresAt;

    @Schema(description = "Verification token (usually sent via email)", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String verificationToken;
}
