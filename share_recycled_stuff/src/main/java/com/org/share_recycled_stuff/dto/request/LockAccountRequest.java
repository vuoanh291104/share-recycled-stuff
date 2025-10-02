package com.org.share_recycled_stuff.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LockAccountRequest {
    @NotNull(message = "Account ID is required")
    private Long accountId;

    @NotBlank(message = "Lock reason is required")
    @Size(min = 5, max = 255, message = "Lock reason must be between 5 and 255 characters")
    private String reason;

    /**
     * Duration in minutes. If null, the account will be locked permanently.
     */
    private Integer durationMinutes;
}
