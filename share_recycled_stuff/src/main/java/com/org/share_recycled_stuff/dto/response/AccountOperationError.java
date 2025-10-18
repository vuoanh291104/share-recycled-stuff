package com.org.share_recycled_stuff.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Account operation error details")
public class AccountOperationError {
    @Schema(description = "Account ID that failed", example = "5")
    private Long accountId;

    @Schema(description = "Error code", example = "ACCOUNT_NOT_FOUND")
    private String errorCode;

    @Schema(description = "Error message", example = "Account not found or already locked")
    private String message;
}
