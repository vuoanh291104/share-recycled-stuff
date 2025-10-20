package com.org.share_recycled_stuff.dto.response;

import com.org.share_recycled_stuff.entity.enums.RequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Proxy seller upgrade request response with status and details")
public class UpgradeRequestResponse {
    @Schema(description = "Request ID", example = "1")
    private Long requestId;

    @Schema(description = "User full name", example = "Nguyễn Văn A")
    private String fullName;

    @Schema(description = "User email", example = "user@example.com")
    private String email;

    @Schema(description = "National ID card number", example = "001234567890")
    private String idCard;

    @Schema(description = "National ID card front image URL", example = "https://example.com/images/front.png")
    private String idCardFrontImage;

    @Schema(description = "National ID card back image URL", example = "https://example.com/images/back.png")
    private String idCardBackImage;

    @Schema(description = "Detailed address", example = "123 Đường ABC, Quận 1, TP.HCM")
    private String addressDetail;

    @Schema(description = "Request status (PENDING/APPROVED/REJECTED)", example = "PENDING")
    private RequestStatus status;

    @Schema(description = "Request creation timestamp", example = "2024-01-01T10:00:00")
    private LocalDateTime createdAt;
}
