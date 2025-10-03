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
public class RoleOperationResponse {
    private Long userId;
    private String email;
    private Set<Role> currentRoles;
    private String operation; // "ASSIGNED" or "REVOKED"
    private Role affectedRole;
    private LocalDateTime operationTime;
    private String operatedBy;
}
