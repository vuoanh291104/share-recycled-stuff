package com.org.share_recycled_stuff.service.impl;

import com.org.share_recycled_stuff.dto.request.AssignRoleRequest;
import com.org.share_recycled_stuff.dto.response.RoleOperationResponse;
import com.org.share_recycled_stuff.entity.Account;
import com.org.share_recycled_stuff.entity.User;
import com.org.share_recycled_stuff.entity.UserRole;
import com.org.share_recycled_stuff.entity.enums.Role;
import com.org.share_recycled_stuff.exception.AppException;
import com.org.share_recycled_stuff.exception.ErrorCode;
import com.org.share_recycled_stuff.repository.AccountRepository;
import com.org.share_recycled_stuff.repository.UserRepository;
import com.org.share_recycled_stuff.repository.UserRoleRepository;
import com.org.share_recycled_stuff.service.RoleManagementService;
import com.org.share_recycled_stuff.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleManagementServiceImpl implements RoleManagementService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final UserRoleRepository userRoleRepository;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public RoleOperationResponse assignRole(AssignRoleRequest request) {
        log.info("Attempting to assign role {} to user {}", request.getRole(), request.getUserId());

        Account currentAdmin = securityUtils.getCurrentAccount();

        if (currentAdmin.getUser().getId().equals(request.getUserId())) {
            throw new AppException(ErrorCode.OPERATION_NOT_ALLOWED, "Cannot assign roles to your own account");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Account account = user.getAccount();
        if (account == null) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        // Check if user already has this role
        boolean hasRole = account.getRoles().stream()
                .anyMatch(userRole -> userRole.getRoleType() == request.getRole());

        if (hasRole) {
            log.warn("User {} already has role {}", user.getId(), request.getRole());
            throw new AppException(ErrorCode.USER_ALREADY_HAS_ROLE);
        }

        // Create new UserRole
        UserRole newRole = UserRole.builder()
                .account(account)
                .roleType(request.getRole())
                .assignedBy(currentAdmin)
                .build();

        userRoleRepository.save(newRole);
        account.getRoles().add(newRole);

        log.info("Admin {} assigned role {} to user {}",
                currentAdmin.getEmail(), request.getRole(), user.getId());

        return buildResponse(user, "ASSIGNED", request.getRole(), currentAdmin.getEmail());
    }

    @Override
    @Transactional
    public RoleOperationResponse revokeRole(AssignRoleRequest request) {
        log.info("Attempting to revoke role {} from user {}", request.getRole(), request.getUserId());

        Account currentAdmin = securityUtils.getCurrentAccount();

        if (currentAdmin.getUser().getId().equals(request.getUserId())) {
            throw new AppException(ErrorCode.OPERATION_NOT_ALLOWED, "Cannot revoke roles from your own account");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Account account = user.getAccount();
        if (account == null) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        // Find the role to remove
        UserRole roleToRemove = account.getRoles().stream()
                .filter(userRole -> userRole.getRoleType() == request.getRole())
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.USER_DOES_NOT_HAVE_ROLE));

        // Prevent removing last admin
        if (request.getRole() == Role.ADMIN) {
            long adminCount = accountRepository.countByRolesRoleTypeAndStatus(Role.ADMIN, false);
            if (adminCount <= 1) {
                log.warn("Cannot remove last admin role");
                throw new AppException(ErrorCode.CANNOT_REMOVE_LAST_ADMIN);
            }
        }

        // Ensure user has at least one role
        if (account.getRoles().size() == 1) {
            log.warn("Cannot remove the only role from user {}", user.getId());
            throw new AppException(ErrorCode.USER_MUST_HAVE_AT_LEAST_ONE_ROLE);
        }

        account.getRoles().remove(roleToRemove);
        userRoleRepository.delete(roleToRemove);

        log.info("Admin {} revoked role {} from user {}",
                currentAdmin.getEmail(), request.getRole(), user.getId());

        return buildResponse(user, "REVOKED", request.getRole(), currentAdmin.getEmail());
    }

    private RoleOperationResponse buildResponse(User user, String operation, Role affectedRole, String operatedBy) {
        Set<Role> currentRoles = user.getAccount().getRoles().stream()
                .map(UserRole::getRoleType)
                .collect(Collectors.toSet());

        return RoleOperationResponse.builder()
                .userId(user.getId())
                .email(user.getAccount().getEmail())
                .currentRoles(currentRoles)
                .operation(operation)
                .affectedRole(affectedRole)
                .operationTime(LocalDateTime.now())
                .operatedBy(operatedBy)
                .build();
    }
}
