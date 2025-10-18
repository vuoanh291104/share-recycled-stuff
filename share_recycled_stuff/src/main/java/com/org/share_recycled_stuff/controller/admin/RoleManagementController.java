package com.org.share_recycled_stuff.controller.admin;

import com.org.share_recycled_stuff.dto.request.AssignRoleRequest;
import com.org.share_recycled_stuff.dto.response.ApiResponse;
import com.org.share_recycled_stuff.dto.response.RoleOperationResponse;
import com.org.share_recycled_stuff.service.RoleManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin - Role Management", description = "Admin endpoints for assigning and revoking user roles")
@RestController
@RequestMapping("/api/admin/roles")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class RoleManagementController {

    private final RoleManagementService roleManagementService;

    @Operation(
            summary = "Assign role to user",
            description = "Assign a specific role (CUSTOMER, PROXY_SELLER, ADMIN) to a user account"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Role assigned successfully. Returns ApiResponse<RoleOperationResponse>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid role or user ID. Returns ApiResponse with error."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Admin role required. Returns ApiResponse with error."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "User not found. Returns ApiResponse with error."
            )
    })
    @PostMapping("/assign")
    public ResponseEntity<ApiResponse<RoleOperationResponse>> assignRole(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Role assignment request with user ID and role",
                    required = true
            )
            @Valid @RequestBody AssignRoleRequest request) {
        log.info("Admin request to assign role {} to user {}", request.getRole(), request.getUserId());

        RoleOperationResponse response = roleManagementService.assignRole(request);
        return ResponseEntity.ok(ApiResponse.success("Role assigned successfully", response));
    }

    @Operation(
            summary = "Revoke role from user",
            description = "Remove a specific role from a user account"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Role revoked successfully. Returns ApiResponse<RoleOperationResponse>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid role or user ID. Returns ApiResponse with error."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Admin role required. Returns ApiResponse with error."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "User not found or does not have the role. Returns ApiResponse with error."
            )
    })
    @PostMapping("/revoke")
    public ResponseEntity<ApiResponse<RoleOperationResponse>> revokeRole(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Role revocation request with user ID and role",
                    required = true
            )
            @Valid @RequestBody AssignRoleRequest request) {
        log.info("Admin request to revoke role {} from user {}", request.getRole(), request.getUserId());

        RoleOperationResponse response = roleManagementService.revokeRole(request);
        return ResponseEntity.ok(ApiResponse.success("Role revoked successfully", response));
    }
}
