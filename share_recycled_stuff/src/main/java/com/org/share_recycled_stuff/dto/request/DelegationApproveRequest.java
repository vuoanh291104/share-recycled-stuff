package com.org.share_recycled_stuff.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request to approve a delegation request")
public class DelegationApproveRequest {
    @Schema(
            description = "Optional note for approval",
            example = "Sẽ xử lý bán sản phẩm trong vòng 7 ngày"
    )
    private String note;
}
