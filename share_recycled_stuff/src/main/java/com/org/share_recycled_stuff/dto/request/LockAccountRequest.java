package com.org.share_recycled_stuff.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to lock an account with specified reason and optional duration")
public class LockAccountRequest {
    @Schema(
            description = "Account ID to lock",
            example = "1",
            required = true
    )
    @NotNull(message = "Account ID is required")
    private Long accountId;

    @Schema(
            description = "Reason for locking the account",
            example = "Vi phạm chính sách cộng đồng",
            required = true,
            minLength = 5,
            maxLength = 255
    )
    @NotBlank(message = "Lock reason is required")
    @Size(min = 5, max = 255, message = "Lock reason must be between 5 and 255 characters")
    private String reason;

    @Schema(
            description = "Lock duration in minutes (null for permanent lock)",
            example = "1440"
    )
    private Integer durationMinutes;
}
