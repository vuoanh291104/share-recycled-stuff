package com.org.share_recycled_stuff.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkLockRequest {
    @NotEmpty(message = "Account IDs are required")
    private List<Long> accountIds;

    @NotBlank(message = "Lock reason is required")
    @Size(min = 5, max = 255, message = "Lock reason must be between 5 and 255 characters")
    private String reason;

    /**
     * Duration in minutes. If null, the accounts will be locked permanently.
     */
    private Integer durationMinutes;
}
