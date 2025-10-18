package com.org.share_recycled_stuff.service.impl;

import com.org.share_recycled_stuff.dto.request.UpdateUserProfileRequest;
import com.org.share_recycled_stuff.dto.response.UserProfileResponse;
import com.org.share_recycled_stuff.entity.Account;
import com.org.share_recycled_stuff.entity.User;
import com.org.share_recycled_stuff.exception.AppException;
import com.org.share_recycled_stuff.exception.ErrorCode;
import com.org.share_recycled_stuff.mapper.UserProfileMapper;
import com.org.share_recycled_stuff.repository.AccountRepository;
import com.org.share_recycled_stuff.repository.UserRepository;
import com.org.share_recycled_stuff.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileServiceImpl implements UserProfileService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final UserProfileMapper userProfileMapper;

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile(Long accountId) {
        Account account = getAccount(accountId);
        User user = fetchUser(account);

        log.info("Fetching profile for account {}", account.getEmail());
        return userProfileMapper.toUserProfileResponse(user);
    }

    @Override
    @Transactional
    public UserProfileResponse updateCurrentUserProfile(Long accountId, UpdateUserProfileRequest request) {
        Account account = getAccount(accountId);
        User user = fetchUser(account);

        log.info("Updating profile for account {}", account.getEmail());

        applyUpdates(user, request);
        userRepository.save(user);

        return userProfileMapper.toUserProfileResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(Long userId) {
        log.info("Fetching profile for userId {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.getAccount() == null) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        return userProfileMapper.toUserProfileResponse(user);
    }

    private User fetchUser(Account account) {
        User user = account.getUser();
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        return user;
    }

    private void applyUpdates(User user, UpdateUserProfileRequest request) {
        if (request.getFullName() != null) {
            String fullName = sanitizeRequired(request.getFullName(), "fullName");
            user.setFullName(fullName);
        }

        if (request.getPhoneNumber() != null) {
            String phone = sanitizeOptional(request.getPhoneNumber());
            if (StringUtils.hasText(phone)) {
                validatePhoneNumber(phone);
                validateUniquePhone(user, phone);
            }
            user.setPhone(StringUtils.hasText(phone) ? phone : null);
        }

        if (request.getAddress() != null) {
            user.setAddress(sanitizeOptional(request.getAddress()));
        }

        if (request.getWard() != null) {
            user.setWard(sanitizeOptional(request.getWard()));
        }

        if (request.getCity() != null) {
            user.setCity(sanitizeOptional(request.getCity()));
        }

        if (request.getIdCard() != null) {
            user.setIdCard(sanitizeOptional(request.getIdCard()));
        }

        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(sanitizeOptional(request.getAvatarUrl()));
        }

        if (request.getBio() != null) {
            user.setBio(sanitizeOptional(request.getBio()));
        }
    }

    private void validatePhoneNumber(String phone) {
        if (!phone.matches("^[0-9]{8,15}$")) {
            throw new AppException(ErrorCode.INVALID_INPUT, "Phone number must contain 8-15 digits");
        }
    }

    private void validateUniquePhone(User currentUser, String phone) {
        userRepository.findByPhone(phone)
                .filter(existing -> !Objects.equals(existing.getId(), currentUser.getId()))
                .ifPresent(existing -> {
                    throw new AppException(ErrorCode.INVALID_INPUT, "Phone number is already in use");
                });
    }

    private String sanitizeOptional(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String sanitizeRequired(String value, String fieldName) {
        String sanitized = sanitizeOptional(value);
        if (!StringUtils.hasText(sanitized)) {
            throw new AppException(ErrorCode.MISSING_REQUIRED_FIELD, fieldName);
        }
        return sanitized;
    }

    private Account getAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
    }
}
