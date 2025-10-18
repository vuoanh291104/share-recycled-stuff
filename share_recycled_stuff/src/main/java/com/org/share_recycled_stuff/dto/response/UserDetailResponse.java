package com.org.share_recycled_stuff.dto.response;

import com.org.share_recycled_stuff.entity.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Detailed user information including status, roles, and lock information")
public class UserDetailResponse {
    @Schema(description = "User ID", example = "1")
    private Long userId;

    @Schema(description = "Email address", example = "user@example.com")
    private String email;

    @Schema(description = "Full name", example = "Nguyễn Văn A")
    private String fullName;

    @Schema(description = "Phone number", example = "0123456789")
    private String phoneNumber;

    @Schema(description = "Address", example = "123 Đường ABC")
    private String address;

    @Schema(description = "Avatar URL", example = "https://example.com/avatar.jpg")
    private String avatarUrl;

    @Schema(description = "Account status (ACTIVE/INACTIVE/PENDING)", example = "ACTIVE")
    private String status;

    @Schema(description = "User roles", example = "[\"CUSTOMER\"]")
    private Set<Role> roles;

    @Schema(description = "Whether account is locked", example = "false")
    private boolean isLocked;

    @Schema(description = "Lock reason", example = "Vi phạm chính sách")
    private String lockReason;

    @Schema(description = "Locked timestamp", example = "2024-01-05T10:00:00")
    private LocalDateTime lockedAt;

    @Schema(description = "Admin who locked the account", example = "Admin Nguyễn")
    private String lockedBy;

    @Schema(description = "Account creation timestamp", example = "2024-01-01T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2024-01-02T15:00:00")
    private LocalDateTime updatedAt;
}
