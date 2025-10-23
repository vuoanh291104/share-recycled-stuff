package com.org.share_recycled_stuff.controller.api;

import com.org.share_recycled_stuff.dto.request.ValidateTokenRequest;
import com.org.share_recycled_stuff.dto.response.TokenValidationResponse;
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

    @PostMapping("/validate-token")
    public ResponseEntity<TokenValidationResponse> validateToken(@RequestBody ValidateTokenRequest request) {
        try {
            String token = request.getToken();
            boolean isValid = jwtService.validateToken(token);
            
            if (isValid) {
                // Extract user info from token
                String email = jwtService.getEmailFromToken(token);
                Long accountId = jwtService.getAccountIdFromToken(token);
                
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