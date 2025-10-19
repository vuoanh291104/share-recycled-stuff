package com.org.share_recycled_stuff.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to update user profile information (all fields are optional)")
public class UpdateUserProfileRequest {

    @Schema(
            description = "Full name",
            example = "Nguyễn Văn A",
            maxLength = 255
    )
    @Size(max = 255, message = "Full name must not exceed 255 characters")
    private String fullName;

    @Schema(
            description = "Phone number (8-15 digits)",
            example = "0123456789",
            pattern = "^[0-9]{8,15}$"
    )
    @Pattern(regexp = "^$|^[0-9]{8,15}$", message = "Phone number must contain 8-15 digits")
    private String phoneNumber;

    @Schema(
            description = "Detailed address",
            example = "123 Đường ABC",
            maxLength = 500
    )
    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    @Schema(
            description = "Ward/Commune (Phường/Xã)",
            example = "Phường Bách Khoa",
            maxLength = 100
    )
    @Size(max = 100, message = "Ward must not exceed 100 characters")
    private String ward;

    @Schema(
            description = "City/Province (Tỉnh/Thành phố)",
            example = "Hà Nội",
            maxLength = 100
    )
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @Schema(
            description = "National ID card number (CCCD/CMND)",
            example = "001234567890",
            maxLength = 20
    )
    @Size(max = 20, message = "ID card must not exceed 20 characters")
    private String idCard;

    @Schema(
            description = "Avatar image URL",
            example = "https://example.com/avatar.jpg",
            maxLength = 500
    )
    @Size(max = 500, message = "Avatar URL must not exceed 500 characters")
    private String avatarUrl;

    @Schema(
            description = "User biography/introduction",
            example = "Tôi là người thích tái chế đồ cũ",
            maxLength = 1000
    )
    @Size(max = 1000, message = "Bio must not exceed 1000 characters")
    private String bio;
}
