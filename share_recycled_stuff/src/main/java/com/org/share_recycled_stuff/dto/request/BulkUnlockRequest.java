package com.org.share_recycled_stuff.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to unlock multiple locked accounts at once")
public class BulkUnlockRequest {
    @Schema(
            description = "List of account IDs to unlock",
            example = "[1, 2, 3, 4, 5]",
            required = true
    )
    @NotEmpty(message = "Account IDs are required")
    private List<Long> accountIds;
}
