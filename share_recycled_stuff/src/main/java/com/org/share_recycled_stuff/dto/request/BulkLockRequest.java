package com.org.share_recycled_stuff.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to lock multiple accounts at once")
public class BulkLockRequest {
    @Schema(
            description = "List of account IDs to lock",
            example = "[1, 2, 3, 4, 5]",
            required = true
    )
    @NotEmpty(message = "Account IDs are required")
    private List<Long> accountIds;

    @Schema(
            description = "Common reason for locking all accounts",
            example = "Spam hàng loạt",
            required = true,
            minLength = 5,
            maxLength = 255
    )
    @NotBlank(message = "Lock reason is required")
    @Size(min = 5, max = 255, message = "Lock reason must be between 5 and 255 characters")
    private String reason;

    @Schema(
            description = "Lock duration in minutes for all accounts (null for permanent lock)",
            example = "2880"
    )
    private Integer durationMinutes;
}
