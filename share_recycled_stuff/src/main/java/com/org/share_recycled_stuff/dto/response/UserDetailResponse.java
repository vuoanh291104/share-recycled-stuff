package com.org.share_recycled_stuff.dto.response;

import com.org.share_recycled_stuff.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailResponse {
    private Long userId;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String address;
    private String avatarUrl;
    private String status;
    private Set<Role> roles;
    private boolean isLocked;
    private String lockReason;
    private LocalDateTime lockedAt;
    private String lockedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
