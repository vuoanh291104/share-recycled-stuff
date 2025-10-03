package com.org.share_recycled_stuff.service;

import com.org.share_recycled_stuff.dto.request.BulkLockRequest;
import com.org.share_recycled_stuff.dto.request.BulkUnlockRequest;
import com.org.share_recycled_stuff.dto.request.LockAccountRequest;
import com.org.share_recycled_stuff.dto.request.UnlockAccountRequest;
import com.org.share_recycled_stuff.dto.response.AccountLockResponse;
import com.org.share_recycled_stuff.dto.response.BulkAccountOperationResponse;

public interface AccountManagementService {
    /**
     * Lock an account with a reason and optional duration
     */
    AccountLockResponse lockAccount(LockAccountRequest request);

    /**
     * Unlock an account
     */
    AccountLockResponse unlockAccount(UnlockAccountRequest request);

    /**
     * Bulk lock accounts
     */
    BulkAccountOperationResponse bulkLockAccounts(BulkLockRequest request);

    /**
     * Bulk unlock accounts
     */
    BulkAccountOperationResponse bulkUnlockAccounts(BulkUnlockRequest request);
}
