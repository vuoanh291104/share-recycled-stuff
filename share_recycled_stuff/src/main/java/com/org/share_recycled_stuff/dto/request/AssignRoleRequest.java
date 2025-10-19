package com.org.share_recycled_stuff.dto.request;

import com.org.share_recycled_stuff.entity.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to assign or revoke a role from user")
public class AssignRoleRequest {

    @Schema(
            description = "User ID to assign/revoke role",
            example = "1",
            required = true
    )
    @NotNull(message = "User ID is required")
    private Long userId;

    @Schema(
            description = "Role to assign/revoke (CUSTOMER, PROXY_SELLER, ADMIN)",
            example = "PROXY_SELLER",
            required = true,
            allowableValues = {"CUSTOMER", "PROXY_SELLER", "ADMIN"}
    )
    @NotNull(message = "Role is required")
    private Role role;
}
