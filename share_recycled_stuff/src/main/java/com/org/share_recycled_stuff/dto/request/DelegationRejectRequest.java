package com.org.share_recycled_stuff.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request to reject a delegation request")
public class DelegationRejectRequest {
    @Schema(
            description = "Reason for rejection",
            example = "Không thể bán sản phẩm này do vi phạm chính sách",
            required = true
    )
    private String reason;
}
