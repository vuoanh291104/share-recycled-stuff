package com.org.share_recycled_stuff.service;

import com.org.share_recycled_stuff.dto.request.BulkLockRequest;
import com.org.share_recycled_stuff.dto.request.BulkUnlockRequest;
import com.org.share_recycled_stuff.dto.request.LockAccountRequest;
import com.org.share_recycled_stuff.dto.request.UnlockAccountRequest;
import com.org.share_recycled_stuff.dto.response.AccountLockResponse;
import com.org.share_recycled_stuff.dto.response.BulkAccountOperationResponse;
import com.org.share_recycled_stuff.dto.response.UserDetailResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountManagementService {
    AccountLockResponse lockAccount(LockAccountRequest request);
    AccountLockResponse unlockAccount(UnlockAccountRequest request);
    BulkAccountOperationResponse bulkLockAccounts(BulkLockRequest request);
    BulkAccountOperationResponse bulkUnlockAccounts(BulkUnlockRequest request);
    Page<UserDetailResponse> getAllUsers(String search, String role, String status, Pageable pageable);
    UserDetailResponse getUserDetail(Long userId);
}
