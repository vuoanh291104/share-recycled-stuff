package com.org.share_recycled_stuff.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerificationResponse {
    private String email;
    private LocalDateTime expiresAt;
}
