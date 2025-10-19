package com.org.share_recycled_stuff.controller.api;

import com.org.share_recycled_stuff.config.CustomUserDetail;
import com.org.share_recycled_stuff.dto.request.UpdateUserProfileRequest;
import com.org.share_recycled_stuff.dto.response.ApiResponse;
import com.org.share_recycled_stuff.dto.response.UserProfileResponse;
import com.org.share_recycled_stuff.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Profile", description = "User profile management endpoints")
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAnyRole('ADMIN','CUSTOMER','PROXY_SELLER')")
public class ProfileController {

    private final UserProfileService userProfileService;

    @Operation(
            summary = "Get current user profile",
            description = "Retrieve the authenticated user's profile information including personal details, contact info, and location"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved user profile. Returns ApiResponse<UserProfileResponse>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token required. Returns ApiResponse with error."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "User profile not found. Returns ApiResponse with error."
            )
    })
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(
            @AuthenticationPrincipal CustomUserDetail userDetail) {
        log.info("Request to fetch current user profile for account {}", userDetail.getAccountId());

        UserProfileResponse response = userProfileService.getCurrentUserProfile(userDetail.getAccountId());
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", response));
    }

    @Operation(
            summary = "Get user profile by ID",
            description = "Retrieve any user's public profile information by their user ID"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved user profile. Returns ApiResponse<UserProfileResponse>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "User not found. Returns ApiResponse with error."
            )
    })
    @GetMapping("/{userId:\\d+}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile(
            @Parameter(
                    description = "User ID to retrieve profile for",
                    required = true,
                    example = "1"
            )
            @PathVariable Long userId) {
        log.info("Request to fetch profile for userId {}", userId);

        UserProfileResponse response = userProfileService.getUserProfile(userId);
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", response));
    }

    @Operation(
            summary = "Update current user profile",
            description = "Update authenticated user's profile information including full name, phone number, address (ward, city), " +
                    "date of birth, avatar URL, and location coordinates (latitude/longitude)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Profile updated successfully. Returns ApiResponse<UserProfileResponse>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data. Returns ApiResponse with validation errors."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token required. Returns ApiResponse with error."
            )
    })
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Profile update request with all updatable fields (all fields are optional)",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UpdateUserProfileRequest.class)
                    )
            )
            @Valid @RequestBody UpdateUserProfileRequest request) {
        log.info("Request to update current user profile for account {}", userDetail.getAccountId());

        UserProfileResponse response = userProfileService.updateCurrentUserProfile(userDetail.getAccountId(), request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", response));
    }
}
