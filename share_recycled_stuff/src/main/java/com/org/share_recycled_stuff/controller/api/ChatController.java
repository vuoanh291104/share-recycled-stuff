package com.org.share_recycled_stuff.controller.api;

import com.org.share_recycled_stuff.config.CustomUserDetail;
import com.org.share_recycled_stuff.dto.request.ValidateTokenRequest;
import com.org.share_recycled_stuff.dto.response.ApiResponse;
import com.org.share_recycled_stuff.dto.response.RecentChatUserResponse;
import com.org.share_recycled_stuff.dto.response.TokenValidationResponse;
import com.org.share_recycled_stuff.entity.Account;
import com.org.share_recycled_stuff.exception.AppException;
import com.org.share_recycled_stuff.exception.ErrorCode;
import com.org.share_recycled_stuff.repository.AccountRepository;
import com.org.share_recycled_stuff.service.ChatService;
import com.org.share_recycled_stuff.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;

@Tag(name = "Chat", description = "Chat and messaging endpoints")
@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final JwtService jwtService;
    private final AccountRepository accountRepository;
    private final ChatService chatService;

    @Operation(
            summary = "Validate JWT token",
            description = "Validates JWT token for WebSocket authentication (used by chat-service)"
    )
    @PostMapping("/validate-token")
    public ResponseEntity<TokenValidationResponse> validateToken(@RequestBody ValidateTokenRequest request) {
        try {
            String token = request.getToken();
            boolean isValid = jwtService.validateToken(token);
            
            if (isValid) {
                // Extract user info from token
                String email = jwtService.getEmailFromToken(token);
                Long accountId = jwtService.getAccountIdFromToken(token);
                
                // Check account lock status
                Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
                
                if (account.isCurrentlyLocked()) {
                    log.warn("Locked account attempting chat connection: {}", email);
                    return ResponseEntity.ok(TokenValidationResponse.invalid());
                }
                
                log.info("Token validated successfully for user: {}", email);
                return ResponseEntity.ok(TokenValidationResponse.valid(accountId, email));
            } else {
                log.warn("Invalid token provided");
                return ResponseEntity.ok(TokenValidationResponse.invalid());
            }
        } catch (Exception e) {
            log.error("Error validating token", e);
            return ResponseEntity.ok(TokenValidationResponse.invalid());
        }
    }

    @Operation(
            summary = "Get recent chat users",
            description = "Retrieve list of users who have recently exchanged messages with the current user, ordered by most recent message first"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved recent chat users list. Returns ApiResponse<List<RecentChatUserResponse>>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token required. Returns ApiResponse with error."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "User account not found. Returns ApiResponse with error."
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER','PROXY_SELLER')")
    @GetMapping("/recent-users")
    public ResponseEntity<ApiResponse<List<RecentChatUserResponse>>> getRecentChatUsers(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetail userDetail,
            HttpServletRequest httpRequest
    ) {
        log.info("Fetching recent chat users for account ID: {}", userDetail.getAccountId());

        List<RecentChatUserResponse> recentUsers = chatService.getRecentChatUsers(userDetail.getAccountId());

        return ResponseEntity.ok(
                ApiResponse.<List<RecentChatUserResponse>>builder()
                        .code(HttpStatus.OK.value())
                        .message("Lấy danh sách người dùng nhắn tin gần đây thành công")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .result(recentUsers)
                        .build()
        );
    }
}