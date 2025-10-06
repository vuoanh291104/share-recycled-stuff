package com.org.share_recycled_stuff.service.impl;

import com.org.share_recycled_stuff.dto.request.BulkLockRequest;
import com.org.share_recycled_stuff.dto.request.BulkUnlockRequest;
import com.org.share_recycled_stuff.dto.request.LockAccountRequest;
import com.org.share_recycled_stuff.dto.request.UnlockAccountRequest;
import com.org.share_recycled_stuff.dto.response.AccountLockResponse;
import com.org.share_recycled_stuff.dto.response.AccountOperationError;
import com.org.share_recycled_stuff.dto.response.BulkAccountOperationResponse;
import com.org.share_recycled_stuff.dto.response.UserDetailResponse;
import com.org.share_recycled_stuff.entity.Account;
import com.org.share_recycled_stuff.entity.User;
import com.org.share_recycled_stuff.entity.enums.Role;
import com.org.share_recycled_stuff.exception.AppException;
import com.org.share_recycled_stuff.exception.ErrorCode;
import com.org.share_recycled_stuff.mapper.UserMapper;
import com.org.share_recycled_stuff.repository.AccountRepository;
import com.org.share_recycled_stuff.repository.UserRepository;
import com.org.share_recycled_stuff.service.AccountManagementService;
import com.org.share_recycled_stuff.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountManagementServiceImpl implements AccountManagementService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SecurityUtils securityUtils;
    
    private static final int MAX_LOCK_DURATION_MINUTES = 60 * 24 * 30; // 30 days
    private static final int MIN_REASON_LENGTH = 5;
    private static final int MAX_REASON_LENGTH = 255;

    @Override
    @Transactional(readOnly = true)
    public Page<UserDetailResponse> getAllUsers(String search, String role, String status, Pageable pageable) {
        log.info("Fetching users with filters - search: {}, role: {}, status: {}", search, role, status);

        Role roleEnum = null;
        if (StringUtils.hasText(role)) {
            try {
                roleEnum = Role.valueOf(role.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid role filter: {}", role);
                throw new AppException(ErrorCode.INVALID_INPUT, "Invalid role: " + role);
            }
        }

        Boolean isLocked = null;
        if (StringUtils.hasText(status)) {
            if ("LOCKED".equalsIgnoreCase(status)) {
                isLocked = true;
            } else if ("ACTIVE".equalsIgnoreCase(status)) {
                isLocked = false;
            } else {
                log.warn("Invalid status filter: {}", status);
                throw new AppException(ErrorCode.INVALID_INPUT, "Invalid status: " + status);
            }
        }

        Page<Account> accounts = accountRepository.findAllWithFilters(search, roleEnum, isLocked, pageable);
        
        return accounts.map(userMapper::toUserDetailResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetailResponse getUserDetail(Long userId) {
        log.info("Fetching user detail for userId: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Account account = user.getAccount();
        if (account == null) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        return userMapper.toUserDetailResponse(account);
    }

    @Override
    @Transactional
    public AccountLockResponse lockAccount(LockAccountRequest request) {
        log.info("Attempting to lock account with ID: {}", request.getAccountId());

        Account account = getAccountOrThrow(request.getAccountId());

        AccountLockResponse response = performLock(account, request.getReason(), request.getDurationMinutes());

        log.info("Successfully locked account: {} with reason: {}", account.getEmail(), response.getLockedReason());

        return response;
    }

    @Override
    @Transactional
    public AccountLockResponse unlockAccount(UnlockAccountRequest request) {
        log.info("Attempting to unlock account with ID: {}", request.getAccountId());

        Account account = getAccountOrThrow(request.getAccountId());

        AccountLockResponse response = performUnlock(account);

        log.info("Successfully unlocked account: {}", account.getEmail());

        return response;
    }

    @Override
    @Transactional
    public BulkAccountOperationResponse bulkLockAccounts(BulkLockRequest request) {
        log.info("Attempting bulk lock for {} accounts", request.getAccountIds().size());

        List<AccountLockResponse> successes = new ArrayList<>();
        List<AccountOperationError> failures = new ArrayList<>();

        for (Long accountId : request.getAccountIds()) {
            try {
                Account account = getAccountOrThrow(accountId);
                AccountLockResponse response = performLock(account, request.getReason(), request.getDurationMinutes());
                successes.add(response);
            } catch (AppException ex) {
                log.warn("Bulk lock failed for account {}: {}", accountId, ex.getMessage());
                failures.add(AccountOperationError.builder()
                        .accountId(accountId)
                        .errorCode(ex.getErrorCode().getCode())
                        .message(ex.getMessage())
                        .build());
            } catch (Exception ex) {
                log.error("Unexpected error when locking account {}", accountId, ex);
                failures.add(AccountOperationError.builder()
                        .accountId(accountId)
                        .errorCode(ErrorCode.INTERNAL_ERROR.getCode())
                        .message("Unexpected error: " + ex.getMessage())
                        .build());
            }
        }

        return BulkAccountOperationResponse.builder()
                .successes(successes)
                .failures(failures)
                .build();
    }

    @Override
    @Transactional
    public BulkAccountOperationResponse bulkUnlockAccounts(BulkUnlockRequest request) {
        log.info("Attempting bulk unlock for {} accounts", request.getAccountIds().size());

        List<AccountLockResponse> successes = new ArrayList<>();
        List<AccountOperationError> failures = new ArrayList<>();

        for (Long accountId : request.getAccountIds()) {
            try {
                Account account = getAccountOrThrow(accountId);
                AccountLockResponse response = performUnlock(account);
                successes.add(response);
            } catch (AppException ex) {
                log.warn("Bulk unlock failed for account {}: {}", accountId, ex.getMessage());
                failures.add(AccountOperationError.builder()
                        .accountId(accountId)
                        .errorCode(ex.getErrorCode().getCode())
                        .message(ex.getMessage())
                        .build());
            } catch (Exception ex) {
                log.error("Unexpected error when unlocking account {}", accountId, ex);
                failures.add(AccountOperationError.builder()
                        .accountId(accountId)
                        .errorCode(ErrorCode.INTERNAL_ERROR.getCode())
                        .message("Unexpected error: " + ex.getMessage())
                        .build());
            }
        }

        return BulkAccountOperationResponse.builder()
                .successes(successes)
                .failures(failures)
                .build();
    }

    private AccountLockResponse performLock(Account account, String reason, Integer durationMinutes) {
        Account currentAdmin = securityUtils.getCurrentAccount();
        
        if (currentAdmin.getId().equals(account.getId())) {
            throw new AppException(ErrorCode.OPERATION_NOT_ALLOWED, "Cannot lock your own account");
        }
        
        if (isAdminAccount(account)) {
            log.warn("Attempted to lock admin account: {}", account.getEmail());
            throw new AppException(ErrorCode.OPERATION_NOT_ALLOWED, "Cannot lock administrator accounts");
        }

        if (account.isLocked()) {
            log.warn("Account {} is already locked", account.getEmail());
            throw new AppException(ErrorCode.OPERATION_NOT_ALLOWED, "Account is already locked");
        }

        String normalizedReason = normalizeReason(reason);
        Integer sanitizedDuration = sanitizeDuration(durationMinutes);

        LocalDateTime now = LocalDateTime.now();
        account.setLocked(true);
        account.setLockedReason(normalizedReason);
        account.setLockedAt(now);
        account.setLockedUntil(sanitizedDuration != null ? now.plusMinutes(sanitizedDuration) : null);

        accountRepository.save(account);

        return buildResponse(account, "Account locked successfully");
    }

    private AccountLockResponse performUnlock(Account account) {
        Account currentAdmin = securityUtils.getCurrentAccount();
        
        if (currentAdmin.getId().equals(account.getId())) {
            throw new AppException(ErrorCode.OPERATION_NOT_ALLOWED, "Cannot unlock your own account");
        }
        
        if (!account.isLocked()) {
            log.warn("Account {} is not locked", account.getEmail());
            throw new AppException(ErrorCode.OPERATION_NOT_ALLOWED, "Account is not locked");
        }

        account.setLocked(false);
        account.setLockedReason(null);
        account.setLockedAt(null);
        account.setLockedUntil(null);
        account.setLoginAttempts(0);

        accountRepository.save(account);

        return buildResponse(account, "Account unlocked successfully");
    }

    private Account getAccountOrThrow(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> {
                    log.error("Account not found with ID: {}", accountId);
                    return new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
                });
    }

    private boolean isAdminAccount(Account account) {
        return account.getRoles().stream()
                .anyMatch(role -> role.getRoleType() == Role.ADMIN);
    }

    private String normalizeReason(String reason) {
        if (!StringUtils.hasText(reason)) {
            throw new AppException(ErrorCode.INVALID_INPUT, "Lock reason is required");
        }

        String trimmed = reason.trim();
        if (trimmed.length() < MIN_REASON_LENGTH || trimmed.length() > MAX_REASON_LENGTH) {
            throw new AppException(ErrorCode.INVALID_INPUT,
                    String.format("Lock reason must be between %d and %d characters", MIN_REASON_LENGTH, MAX_REASON_LENGTH));
        }

        return trimmed;
    }

    private Integer sanitizeDuration(Integer durationMinutes) {
        if (durationMinutes == null) {
            return null;
        }

        if (durationMinutes <= 0) {
            throw new AppException(ErrorCode.INVALID_INPUT, "Lock duration must be a positive number of minutes");
        }

        if (durationMinutes > MAX_LOCK_DURATION_MINUTES) {
            throw new AppException(ErrorCode.INVALID_INPUT,
                    String.format("Lock duration cannot exceed %d minutes", MAX_LOCK_DURATION_MINUTES));
        }

        return durationMinutes;
    }

    private AccountLockResponse buildResponse(Account account, String message) {
        return AccountLockResponse.builder()
                .accountId(account.getId())
                .email(account.getEmail())
                .isLocked(account.isLocked())
                .lockedReason(account.getLockedReason())
                .lockedAt(account.getLockedAt())
                .lockedUntil(account.getLockedUntil())
                .message(message)
                .build();
    }
}
