package com.org.share_recycled_stuff.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.org.share_recycled_stuff.config.CustomUserDetail;
import com.org.share_recycled_stuff.dto.response.ApiResponse;
import com.org.share_recycled_stuff.entity.Account;
import com.org.share_recycled_stuff.repository.AccountRepository;
import com.org.share_recycled_stuff.security.CustomUserDetailsService;
import com.org.share_recycled_stuff.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService tokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final AccountRepository accountRepository;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt)) {
                // Validate token first to avoid unnecessary blacklist check
                if (!tokenProvider.validateToken(jwt)) {
                    filterChain.doFilter(request, response);
                    return;
                }

                // Check blacklist for valid tokens
                if (tokenProvider.isBlacklisted(jwt)) {
                    log.warn("Blocked blacklisted token for request: {}", request.getRequestURI());
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                            "Token has been revoked",
                            "Please login again",
                            request.getRequestURI());
                    return;
                }

                // Check if account tokens are blacklisted
                Long accountId = tokenProvider.getAccountIdFromToken(jwt);
                if (accountId != null && tokenProvider.isAccountTokenBlacklisted(accountId)) {
                    log.warn("Blocked token for blacklisted account: {} for request: {}", accountId, request.getRequestURI());
                    sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN,
                            "Account locked",
                            "Your account has been locked",
                            request.getRequestURI());
                    return;
                }

                // Only check access token type after validating it's not blacklisted
                if (tokenProvider.isAccessToken(jwt)) {
                    String email = tokenProvider.getEmailFromToken(jwt);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                    // Check if account is currently locked
                    if (userDetails instanceof CustomUserDetail) {
                        CustomUserDetail customUserDetail = (CustomUserDetail) userDetails;
                        Account account = accountRepository.findById(customUserDetail.getAccountId()).orElse(null);
                        if (account != null && account.isCurrentlyLocked()) {
                            log.warn("Account is locked: {} for request: {}", email, request.getRequestURI());
                            sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN,
                                    "Account locked",
                                    "Your account has been locked",
                                    request.getRequestURI());
                            return;
                        }
                    }

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void sendErrorResponse(HttpServletResponse response, int status,
                                   String error, String message, String path) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");

        ApiResponse<Void> errorResponse = ApiResponse.<Void>builder()
                .code(status)
                .message(error + ": " + message)
                .path(path)
                .timestamp(java.time.Instant.now().toString())
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
