package com.org.share_recycled_stuff.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String tokenType;
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private UserInfo userInfo;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserInfo {
        private Long accountId;
        private String email;
        private String fullName;
        private String avatarUrl;
        private String role;
    }
}
