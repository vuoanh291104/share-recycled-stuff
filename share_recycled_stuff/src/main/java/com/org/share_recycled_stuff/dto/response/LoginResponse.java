package com.org.share_recycled_stuff.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Login response containing JWT tokens and user information")
public class LoginResponse {
    @Schema(
            description = "Token type (always 'Bearer')",
            example = "Bearer"
    )
    private String tokenType;

    @Schema(
            description = "JWT access token for authentication",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    private String accessToken;

    @Schema(
            description = "JWT refresh token for obtaining new access tokens",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    private String refreshToken;

    @Schema(
            description = "Token expiration time in seconds",
            example = "3600"
    )
    private Long expiresIn;

    @Schema(description = "Authenticated user's basic information")
    private UserInfo userInfo;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "User information")
    public static class UserInfo {
        @Schema(description = "User's account ID", example = "1")
        private Long accountId;

        @Schema(description = "User's email address", example = "user@example.com")
        private String email;

        @Schema(description = "User's full name", example = "Nguyễn Văn A")
        private String fullName;

        @Schema(description = "URL to user's avatar image", example = "https://example.com/avatar.jpg")
        private String avatarUrl;

        @Schema(description = "User's role", example = "CUSTOMER", allowableValues = {"CUSTOMER", "PROXY_SELLER", "ADMIN"})
        private String role;
    }
}
