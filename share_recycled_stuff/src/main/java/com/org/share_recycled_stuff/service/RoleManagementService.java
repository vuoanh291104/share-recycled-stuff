package com.org.share_recycled_stuff.service;

import com.org.share_recycled_stuff.dto.request.AssignRoleRequest;
import com.org.share_recycled_stuff.dto.response.RoleOperationResponse;

public interface RoleManagementService {
    RoleOperationResponse assignRole(AssignRoleRequest request);

    RoleOperationResponse revokeRole(AssignRoleRequest request);
}
