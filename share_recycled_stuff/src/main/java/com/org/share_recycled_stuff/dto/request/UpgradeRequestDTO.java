package com.org.share_recycled_stuff.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request to upgrade from CUSTOMER to PROXY_SELLER role")
public class UpgradeRequestDTO {
    @Schema(
            description = "National ID card number (CCCD/CMND)",
            example = "001234567890",
            required = true
    )
    @NotBlank(message = "CCCD không được để trống")
    private String idCard;

    @Schema(
            description = "URL to front side of ID card image",
            example = "https://example.com/id-front.jpg",
            required = true
    )
    @NotBlank(message = "Ảnh không được để trống")
    private String idCardFrontImage;

    @Schema(
            description = "URL to back side of ID card image",
            example = "https://example.com/id-back.jpg",
            required = true
    )
    @NotBlank(message = "Ảnh không được để trống")
    private String idCardBackImage;

    @Schema(
            description = "Detailed address for business operations",
            example = "123 Đường ABC, Phường XYZ, Quận 1, TP.HCM",
            required = true
    )
    @NotBlank(message = "Địa chỉ không được để trống")
    private String addressDetail;

}
