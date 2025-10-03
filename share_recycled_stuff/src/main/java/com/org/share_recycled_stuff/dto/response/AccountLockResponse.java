package com.org.share_recycled_stuff.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountLockResponse {
    private Long accountId;
    private String email;
    private boolean isLocked;
    private String lockedReason;
    private LocalDateTime lockedAt;
    private LocalDateTime lockedUntil;
    private String message;
}
