package com.org.share_recycled_stuff.service.impl;

import com.org.share_recycled_stuff.config.CustomUserDetail;
import com.org.share_recycled_stuff.security.jwt.JwtToken;
import com.org.share_recycled_stuff.service.JwtService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JwtServiceImpl implements JwtService {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.access-token-expiration}")
    private Long accessTokenExpiration;

    @Value("${app.jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;

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

        String accessToken = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("accountId", userDetails.getAccountId())
                .claim("roles", roles)
                .claim("tokenType", "ACCESS")
                .setIssuedAt(now)
                .setExpiration(accessTokenExpiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("accountId", userDetails.getAccountId())
                .claim("tokenType", "REFRESH")
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
}
