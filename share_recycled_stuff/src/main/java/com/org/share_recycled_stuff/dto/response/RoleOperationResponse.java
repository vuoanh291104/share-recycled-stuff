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
@Schema(description = "Role operation response with user's current roles and operation details")
public class RoleOperationResponse {
    @Schema(description = "User ID", example = "1")
    private Long userId;

    @Schema(description = "User email", example = "user@example.com")
    private String email;

    @Schema(description = "Current roles after operation", example = "[\"CUSTOMER\", \"PROXY_SELLER\"]")
    private Set<Role> currentRoles;

    @Schema(description = "Operation type (ASSIGNED/REVOKED)", example = "ASSIGNED")
    private String operation;

    @Schema(description = "Role that was assigned or revoked", example = "PROXY_SELLER")
    private Role affectedRole;

    @Schema(description = "Operation timestamp", example = "2024-01-01T10:00:00")
    private LocalDateTime operationTime;

    @Schema(description = "Admin who performed the operation", example = "admin@example.com")
    private String operatedBy;
}
