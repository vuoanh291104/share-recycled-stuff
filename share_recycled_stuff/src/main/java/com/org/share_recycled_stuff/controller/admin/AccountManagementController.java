package com.org.share_recycled_stuff.controller.admin;

import com.org.share_recycled_stuff.dto.request.BulkLockRequest;
import com.org.share_recycled_stuff.dto.request.BulkUnlockRequest;
import com.org.share_recycled_stuff.dto.request.LockAccountRequest;
import com.org.share_recycled_stuff.dto.request.UnlockAccountRequest;
import com.org.share_recycled_stuff.dto.response.AccountLockResponse;
import com.org.share_recycled_stuff.dto.response.BulkAccountOperationResponse;
import com.org.share_recycled_stuff.dto.response.ApiResponse;
import com.org.share_recycled_stuff.service.AccountManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/accounts")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AccountManagementController {

    private final AccountManagementService accountManagementService;

    /**
     * Lock an account
     * POST /api/admin/accounts/lock
     */
    @PostMapping("/lock")
    public ResponseEntity<ApiResponse<AccountLockResponse>> lockAccount(
            @Valid @RequestBody LockAccountRequest request) {
        log.info("Admin request to lock account: {}", request.getAccountId());

        AccountLockResponse response = accountManagementService.lockAccount(request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("Account locked successfully", response));
    }

    /**
     * Unlock an account
     * POST /api/admin/accounts/unlock
     */
    @PostMapping("/unlock")
    public ResponseEntity<ApiResponse<AccountLockResponse>> unlockAccount(
        @Valid @RequestBody UnlockAccountRequest request) {
    log.info("Admin request to unlock account: {}", request.getAccountId());

    AccountLockResponse response = accountManagementService.unlockAccount(request);

    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.success("Account unlocked successfully", response));
    }

    /**
     * Bulk lock accounts
     * POST /api/admin/accounts/bulk/lock
     */
    @PostMapping("/bulk/lock")
    public ResponseEntity<ApiResponse<BulkAccountOperationResponse>> bulkLock(
        @Valid @RequestBody BulkLockRequest request) {
    log.info("Admin request to bulk lock {} accounts", request.getAccountIds().size());

    BulkAccountOperationResponse response = accountManagementService.bulkLockAccounts(request);

    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.success("Bulk lock operation completed", response));
    }

    /**
     * Bulk unlock accounts
     * POST /api/admin/accounts/bulk/unlock
     */
    @PostMapping("/bulk/unlock")
    public ResponseEntity<ApiResponse<BulkAccountOperationResponse>> bulkUnlock(
        @Valid @RequestBody BulkUnlockRequest request) {
    log.info("Admin request to bulk unlock {} accounts", request.getAccountIds().size());

    BulkAccountOperationResponse response = accountManagementService.bulkUnlockAccounts(request);

    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.success("Bulk unlock operation completed", response));
    }
}
