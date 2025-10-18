package com.org.share_recycled_stuff.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "OTP status response with expiration and new OTP flag")
public class OtpStatusResponse {

    @Schema(description = "Email address for OTP", example = "user@example.com")
    private String email;

    @Schema(description = "Status message", example = "OTP sent successfully")
    private String message;

    @Schema(description = "Whether a new OTP was generated", example = "true")
    private boolean isNewOtp;

    @Schema(description = "OTP expiration timestamp", example = "2024-01-01T10:15:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expiresAt;

    @Schema(description = "Minutes until OTP expires", example = "15")
    private Integer expiresInMinutes;

    // Helper method to calculate minutes until expiry
    public void calculateExpiryMinutes() {
        if (expiresAt != null) {
            long minutesUntilExpiry = java.time.Duration.between(LocalDateTime.now(), expiresAt).toMinutes();
            this.expiresInMinutes = Math.max(0, (int) minutesUntilExpiry);
        }
    }
}
