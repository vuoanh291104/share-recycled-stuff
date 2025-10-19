package com.org.share_recycled_stuff.service;

import com.org.share_recycled_stuff.dto.request.UpdateUserProfileRequest;
import com.org.share_recycled_stuff.dto.response.UserProfileResponse;

public interface UserProfileService {
    UserProfileResponse getCurrentUserProfile(Long accountId);

    UserProfileResponse updateCurrentUserProfile(Long accountId, UpdateUserProfileRequest request);

    UserProfileResponse getUserProfile(Long userId);
}
