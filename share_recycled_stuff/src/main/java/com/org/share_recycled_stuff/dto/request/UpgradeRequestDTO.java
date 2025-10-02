package com.org.share_recycled_stuff.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpgradeRequestDTO {
    @NotBlank(message = "CCCD không được để trống")
    private String idCard;

    @NotBlank(message = "Ảnh không được để trống")
    private String idCardFrontImage;

    @NotBlank(message = "Ảnh không được để trống")
    private String idCardBackImage;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String addressDetail;

}
