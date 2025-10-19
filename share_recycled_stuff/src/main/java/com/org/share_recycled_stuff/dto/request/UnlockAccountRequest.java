package com.org.share_recycled_stuff.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to unlock a locked account")
public class UnlockAccountRequest {
    @Schema(
            description = "Account ID to unlock",
            example = "1",
            required = true
    )
    @NotNull(message = "Account ID is required")
    private Long accountId;
}
