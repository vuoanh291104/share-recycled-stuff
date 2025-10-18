package com.org.share_recycled_stuff.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Account lock/unlock operation response")
public class AccountLockResponse {
    @Schema(description = "Account ID", example = "1")
    private Long accountId;

    @Schema(description = "Account email", example = "user@example.com")
    private String email;

    @Schema(description = "Whether account is currently locked", example = "true")
    private boolean isLocked;

    @Schema(description = "Lock reason", example = "Vi phạm chính sách cộng đồng")
    private String lockedReason;

    @Schema(description = "Locked timestamp", example = "2024-01-05T10:00:00")
    private LocalDateTime lockedAt;

    @Schema(description = "Lock expiration timestamp (null for permanent)", example = "2024-01-06T10:00:00")
    private LocalDateTime lockedUntil;

    @Schema(description = "Operation message", example = "Account locked successfully")
    private String message;
}
