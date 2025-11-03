package com.org.share_recycled_stuff.controller.api;

import com.org.share_recycled_stuff.dto.request.ValidateTokenRequest;
import com.org.share_recycled_stuff.dto.response.TokenValidationResponse;
import com.org.share_recycled_stuff.entity.Account;
import com.org.share_recycled_stuff.exception.AppException;
import com.org.share_recycled_stuff.exception.ErrorCode;
import com.org.share_recycled_stuff.repository.AccountRepository;
import com.org.share_recycled_stuff.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final JwtService jwtService;
    private final AccountRepository accountRepository;

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
}