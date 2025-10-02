package com.org.share_recycled_stuff.service;

import com.org.share_recycled_stuff.config.CustomUserDetail;
import com.org.share_recycled_stuff.security.jwt.JwtToken;

import java.util.List;
import java.time.Duration;

public interface JwtService {
    JwtToken generateTokens(CustomUserDetail userDetails);

    String getEmailFromToken(String token);

    Long getAccountIdFromToken(String token);

    List<String> getRolesFromToken(String token);

    String getJtiFromToken(String token);

    Long getExpirationEpochSeconds(String token);

    boolean validateToken(String token);

    boolean isAccessToken(String token);

    boolean isRefreshToken(String token);

    boolean isBlacklisted(String token);

    void blacklistToken(String token, Duration ttl);
}
