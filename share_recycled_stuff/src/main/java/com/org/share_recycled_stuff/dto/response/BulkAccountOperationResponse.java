package com.org.share_recycled_stuff.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Bulk account operation response with successes and failures")
public class BulkAccountOperationResponse {
    @Schema(description = "List of successful operations")
    @Builder.Default
    private List<AccountLockResponse> successes = new ArrayList<>();

    @Schema(description = "List of failed operations with error details")
    @Builder.Default
    private List<AccountOperationError> failures = new ArrayList<>();
}
