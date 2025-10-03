package com.org.share_recycled_stuff.controller.admin;

import com.org.share_recycled_stuff.dto.request.AssignRoleRequest;
import com.org.share_recycled_stuff.dto.response.ApiResponse;
import com.org.share_recycled_stuff.dto.response.RoleOperationResponse;
import com.org.share_recycled_stuff.service.RoleManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/roles")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class RoleManagementController {

    private final RoleManagementService roleManagementService;

    @PostMapping("/assign")
    public ResponseEntity<ApiResponse<RoleOperationResponse>> assignRole(@Valid @RequestBody AssignRoleRequest request) {
        log.info("Admin request to assign role {} to user {}", request.getRole(), request.getUserId());
        
        RoleOperationResponse response = roleManagementService.assignRole(request);
        return ResponseEntity.ok(ApiResponse.success("Role assigned successfully", response));
    }

    @PostMapping("/revoke")
    public ResponseEntity<ApiResponse<RoleOperationResponse>> revokeRole(@Valid @RequestBody AssignRoleRequest request) {
        log.info("Admin request to revoke role {} from user {}", request.getRole(), request.getUserId());
        
        RoleOperationResponse response = roleManagementService.revokeRole(request);
        return ResponseEntity.ok(ApiResponse.success("Role revoked successfully", response));
    }
}
