package com.org.share_recycled_stuff.dto.request;

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
public class UpdateUserProfileRequest {

    @Size(max = 255, message = "Full name must not exceed 255 characters")
    private String fullName;

    @Pattern(regexp = "^$|^[0-9]{8,15}$", message = "Phone number must contain 8-15 digits")
    private String phoneNumber;

    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    @Size(max = 100, message = "Ward must not exceed 100 characters")
    private String ward;

    @Size(max = 100, message = "District must not exceed 100 characters")
    private String district;

    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @Size(max = 20, message = "ID card must not exceed 20 characters")
    private String idCard;

    @Size(max = 500, message = "Avatar URL must not exceed 500 characters")
    private String avatarUrl;

    @Size(max = 1000, message = "Bio must not exceed 1000 characters")
    private String bio;
}
