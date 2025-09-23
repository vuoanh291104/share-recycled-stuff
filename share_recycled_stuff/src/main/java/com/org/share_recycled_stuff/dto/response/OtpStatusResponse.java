package com.org.share_recycled_stuff.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OtpStatusResponse {
    
    private String email;
    private String message;
    private boolean isNewOtp;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expiresAt;
    
    private Integer expiresInMinutes;
    
    // Helper method to calculate minutes until expiry
    public void calculateExpiryMinutes() {
        if (expiresAt != null) {
            long minutesUntilExpiry = java.time.Duration.between(LocalDateTime.now(), expiresAt).toMinutes();
            this.expiresInMinutes = Math.max(0, (int) minutesUntilExpiry);
        }
    }
}
