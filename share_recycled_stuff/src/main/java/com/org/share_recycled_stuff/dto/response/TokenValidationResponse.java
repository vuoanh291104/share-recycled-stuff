package com.org.share_recycled_stuff.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenValidationResponse {
    private boolean valid;
    private Long accountId;
    private String email;

    public static TokenValidationResponse valid(Long accountId, String email) {
        return new TokenValidationResponse(true, accountId, email);
    }

    public static TokenValidationResponse invalid() {
        return new TokenValidationResponse(false, null, null);
    }
}