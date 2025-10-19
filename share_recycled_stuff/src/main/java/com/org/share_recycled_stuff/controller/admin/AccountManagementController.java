package com.org.share_recycled_stuff.controller.admin;

import com.org.share_recycled_stuff.dto.request.BulkLockRequest;
import com.org.share_recycled_stuff.dto.request.BulkUnlockRequest;
import com.org.share_recycled_stuff.dto.request.LockAccountRequest;
import com.org.share_recycled_stuff.dto.request.UnlockAccountRequest;
import com.org.share_recycled_stuff.dto.response.AccountLockResponse;
import com.org.share_recycled_stuff.dto.response.ApiResponse;
import com.org.share_recycled_stuff.dto.response.BulkAccountOperationResponse;
import com.org.share_recycled_stuff.dto.response.UserDetailResponse;
import com.org.share_recycled_stuff.service.AccountManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin - Account Management", description = "Admin endpoints for managing user accounts (lock/unlock/view)")
@RestController
@RequestMapping("/api/admin/accounts")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AccountManagementController {

    private final AccountManagementService accountManagementService;

    @Operation(
            summary = "Get all users",
            description = "Retrieve all users with optional filters including search by name/email, filter by role (CUSTOMER/PROXY_SELLER/ADMIN), " +
                    "and filter by account status (ACTIVE/LOCKED/PENDING)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved users. Returns Page<UserDetailResponse> directly (no ApiResponse wrapper for this endpoint)."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Admin role required."
            )
    })
    @GetMapping("/users")
    public ResponseEntity<Page<UserDetailResponse>> getAllUsers(
            @Parameter(
                    description = "Search term for filtering by user name or email",
                    example = "john@example.com"
            )
            @RequestParam(required = false) String search,
            @Parameter(
                    description = "Filter by user role (CUSTOMER, PROXY_SELLER, ADMIN)",
                    example = "CUSTOMER"
            )
            @RequestParam(required = false) String role,
            @Parameter(
                    description = "Filter by account status (ACTIVE, LOCKED, PENDING)",
                    example = "ACTIVE"
            )
            @RequestParam(required = false) String status,
            Pageable pageable) {
        log.info("Admin request to get all users - search: {}, role: {}, status: {}", search, role, status);

        Page<UserDetailResponse> users = accountManagementService.getAllUsers(search, role, status, pageable);
        return ResponseEntity.ok(users);
    }

    @Operation(
            summary = "Get user details",
            description = "Retrieve detailed information about a specific user including profile, roles, and account status"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved user details. Returns ApiResponse<UserDetailResponse>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "User not found. Returns ApiResponse with error."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Admin role required."
            )
    })
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserDetailResponse>> getUserDetail(
            @Parameter(description = "User ID to retrieve details for", required = true, example = "1")
            @PathVariable Long userId) {
        log.info("Admin request to get user detail for userId: {}", userId);

        UserDetailResponse user = accountManagementService.getUserDetail(userId);
        return ResponseEntity.ok(ApiResponse.success("User detail retrieved successfully", user));
    }

    @Operation(
            summary = "Lock user account",
            description = "Lock a user account with specified reason and optional duration. " +
                    "If duration is provided, account will be automatically unlocked after the period expires."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Account locked successfully. Returns ApiResponse<AccountLockResponse>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Account not found. Returns ApiResponse with error."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Admin role required. Returns ApiResponse with error."
            )
    })
    @PostMapping("/lock")
    public ResponseEntity<ApiResponse<AccountLockResponse>> lockAccount(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Lock account request with account ID, reason, and optional duration in days",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LockAccountRequest.class)
                    )
            )
            @Valid @RequestBody LockAccountRequest request) {
        log.info("Admin request to lock account: {}", request.getAccountId());

        AccountLockResponse response = accountManagementService.lockAccount(request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("Account locked successfully", response));
    }

    @Operation(
            summary = "Unlock user account",
            description = "Unlock a previously locked user account"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Account unlocked successfully. Returns ApiResponse<AccountLockResponse>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Account not found or not locked. Returns ApiResponse with error."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Admin role required. Returns ApiResponse with error."
            )
    })
    @PostMapping("/unlock")
    public ResponseEntity<ApiResponse<AccountLockResponse>> unlockAccount(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Unlock account request with account ID and optional reason",
                    required = true
            )
            @Valid @RequestBody UnlockAccountRequest request) {
        log.info("Admin request to unlock account: {}", request.getAccountId());

        AccountLockResponse response = accountManagementService.unlockAccount(request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("Account unlocked successfully", response));
    }

    @Operation(
            summary = "Bulk lock accounts",
            description = "Lock multiple user accounts at once with a common reason and optional duration"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Bulk lock operation completed. Returns ApiResponse<BulkAccountOperationResponse> with summary."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Admin role required. Returns ApiResponse with error."
            )
    })
    @PostMapping("/bulk/lock")
    public ResponseEntity<ApiResponse<BulkAccountOperationResponse>> bulkLock(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Bulk lock request with a list of account IDs, reason, and optional duration",
                    required = true
            )
            @Valid @RequestBody BulkLockRequest request) {
        log.info("Admin request to bulk lock {} accounts", request.getAccountIds().size());

        BulkAccountOperationResponse response = accountManagementService.bulkLockAccounts(request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("Bulk lock operation completed", response));
    }

    @Operation(
            summary = "Bulk unlock accounts",
            description = "Unlock multiple user accounts at once"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Bulk unlock operation completed. Returns ApiResponse<BulkAccountOperationResponse> with summary."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Admin role required. Returns ApiResponse with error."
            )
    })
    @PostMapping("/bulk/unlock")
    public ResponseEntity<ApiResponse<BulkAccountOperationResponse>> bulkUnlock(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Bulk unlock request with a list of account IDs and optional reason",
                    required = true
            )
            @Valid @RequestBody BulkUnlockRequest request) {
        log.info("Admin request to bulk unlock {} accounts", request.getAccountIds().size());

        BulkAccountOperationResponse response = accountManagementService.bulkUnlockAccounts(request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("Bulk unlock operation completed", response));
    }
}
