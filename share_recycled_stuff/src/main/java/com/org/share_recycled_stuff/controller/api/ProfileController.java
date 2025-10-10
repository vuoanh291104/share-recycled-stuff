package com.org.share_recycled_stuff.controller.api;

import com.org.share_recycled_stuff.config.CustomUserDetail;
import com.org.share_recycled_stuff.dto.request.UpdateUserProfileRequest;
import com.org.share_recycled_stuff.dto.response.ApiResponse;
import com.org.share_recycled_stuff.dto.response.UserProfileResponse;
import com.org.share_recycled_stuff.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAnyRole('ADMIN','CUSTOMER','PROXY_SELLER')")
public class ProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(
            @AuthenticationPrincipal CustomUserDetail userDetail) {
        log.info("Request to fetch current user profile for account {}", userDetail.getAccountId());

        UserProfileResponse response = userProfileService.getCurrentUserProfile(userDetail.getAccountId());
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", response));
    }

    @GetMapping("/{userId:\\d+}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile(@PathVariable Long userId) {
        log.info("Request to fetch profile for userId {}", userId);

        UserProfileResponse response = userProfileService.getUserProfile(userId);
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", response));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @Valid @RequestBody UpdateUserProfileRequest request) {
        log.info("Request to update current user profile for account {}", userDetail.getAccountId());

        UserProfileResponse response = userProfileService.updateCurrentUserProfile(userDetail.getAccountId(), request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", response));
    }
}
