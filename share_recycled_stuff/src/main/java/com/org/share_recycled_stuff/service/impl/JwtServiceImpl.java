package com.org.share_recycled_stuff.service.impl;

import com.org.share_recycled_stuff.config.CustomUserDetail;
import com.org.share_recycled_stuff.exception.AppException;
import com.org.share_recycled_stuff.exception.ErrorCode;
import com.org.share_recycled_stuff.security.jwt.JwtToken;
import com.org.share_recycled_stuff.service.JwtService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.UUID;
import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private static final String BLACKLIST_ACCESS_PREFIX = "blacklist:access:";
    private static final String BLACKLIST_REFRESH_PREFIX = "blacklist:refresh:";

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.access-token-expiration}")
    private Long accessTokenExpiration;

    @Value("${app.jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    private final StringRedisTemplate stringRedisTemplate;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public JwtToken generateTokens(CustomUserDetail userDetails) {
        Date now = new Date();
        Date accessTokenExpiryDate = new Date(now.getTime() + accessTokenExpiration);
        Date refreshTokenExpiryDate = new Date(now.getTime() + refreshTokenExpiration);

        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        String jti = UUID.randomUUID().toString();

        String accessToken = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("accountId", userDetails.getAccountId())
                .claim("roles", roles)
                .claim("tokenType", "ACCESS")
                .setId(jti)
                .setIssuedAt(now)
                .setExpiration(accessTokenExpiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("accountId", userDetails.getAccountId())
                .claim("tokenType", "REFRESH")
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(now)
                .setExpiration(refreshTokenExpiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();

        return JwtToken.builder().tokenType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(accessTokenExpiration / 1000)
                .roles(roles)
                .build();
    }

    public String getEmailFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.getSubject();
    }

    public Long getAccountIdFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.get("accountId", Long.class);
    }

    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.get("roles", List.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty");
        }
        return false;
    }

    public boolean isAccessToken(String token) {
        try {
            Claims claims = getClaims(token);
            return "ACCESS".equals(claims.get("tokenType", String.class));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRefreshToken(String token) {
        try {
            Claims claims = getClaims(token);
            return "REFRESH".equals(claims.get("tokenType", String.class));
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public String getJtiFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.getId();
    }

    @Override
    public Long getExpirationEpochSeconds(String token) {
        Claims claims = getClaims(token);
        Date exp = claims.getExpiration();
        return exp != null ? exp.getTime() / 1000 : null;
    }

    @Override
    public boolean isBlacklisted(String token) {
        try {
            String jtiValue = getJtiFromToken(token);
            if (jtiValue == null) {
                log.warn("Token does not have JTI claim");
                return false;
            }
            
            String tokenType = isAccessToken(token) ? "ACCESS" : "REFRESH";
            String key = buildBlacklistKey(token);
            
            long startTime = System.currentTimeMillis();
            Boolean hasKey = stringRedisTemplate.hasKey(key);
            long duration = System.currentTimeMillis() - startTime;
            
            boolean isBlacklisted = Boolean.TRUE.equals(hasKey);
            
            // Log for monitoring (debug level for normal checks, warn for blocked tokens)
            if (isBlacklisted) {
                log.warn("Blacklisted token detected - Type: {}, JTI: {}, Duration: {}ms", 
                         tokenType, jtiValue, duration);
            } else {
                log.debug("Token check - Type: {}, JTI: {}, Blacklisted: false, Duration: {}ms", 
                          tokenType, jtiValue, duration);
            }
            
            return isBlacklisted;
        } catch (RedisConnectionFailureException e) {
            log.error("Redis unavailable when checking blacklist - failing safe (allowing token)", e);
            return false; // Fail-safe: allow the token if Redis is down
        } catch (Exception e) {
            log.error("Error checking blacklist for token", e);
            return false; // Fail-safe: allow the token on error
        }
    }

    @Override
    public void blacklistToken(String token, Duration ttl) {
        if (ttl == null || ttl.isZero() || ttl.isNegative()) {
            log.debug("Skip blacklisting token due to non-positive TTL");
            return;
        }

        if (!validateToken(token)) {
            log.debug("Token is already invalid or expired, skipping blacklist");
            return;
        }

        String jtiValue = getJtiFromToken(token);
        if (jtiValue == null || jtiValue.isBlank()) {
            throw new AppException(ErrorCode.INVALID_TOKEN, "Token missing JTI");
        }

        String tokenType = isAccessToken(token) ? "ACCESS" : "REFRESH";
        String key = buildBlacklistKey(token);
        
        try {
            long startTime = System.currentTimeMillis();
            stringRedisTemplate.opsForValue().set(key, "1", ttl);
            long duration = System.currentTimeMillis() - startTime;
            
            // Enhanced metric logging for monitoring
            log.info("Token blacklisted - Type: {}, TTL: {}s, JTI: {}, Key: {}, Duration: {}ms", 
                     tokenType, ttl.getSeconds(), jtiValue, key, duration);
        } catch (RedisConnectionFailureException ex) {
            log.error("Redis unavailable when blacklisting token - Type: {}, JTI: {}", tokenType, jtiValue, ex);
            throw new AppException(ErrorCode.SERVICE_UNAVAILABLE, ex);
        } catch (Exception ex) {
            log.error("Failed to blacklist token - Type: {}, JTI: {}", tokenType, jtiValue, ex);
            throw new AppException(ErrorCode.INTERNAL_ERROR, ex);
        }
    }

    private String buildBlacklistKey(String token) {
        Claims claims = getClaims(token);
        String jtiValue = claims.getId();
        String tokenType = claims.get("tokenType", String.class);
        String prefix = "ACCESS".equals(tokenType) ? BLACKLIST_ACCESS_PREFIX : BLACKLIST_REFRESH_PREFIX;
        return prefix + jtiValue;
    }
}
