package com.org.share_recycled_stuff.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Statistics related to Delegation Requests, matching the Enum")
public class DelegationStats {

    @Schema(example = "150")
    private long totalReceived;

    @Schema(example = "10")
    private long pending;

    @Schema(example = "5")
    private long approved;

    @Schema(example = "15")
    private long rejected;

    @Schema(example = "2")
    private long inTransit;

    @Schema(example = "3")
    private long productReceived;

    @Schema(example = "30")
    private long selling;

    @Schema(example = "15")
    private long sold;

    @Schema(example = "70")
    private long paymentCompleted;
}
