package com.org.share_recycled_stuff.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "User registration request with personal information")
public class RegisterRequest {

    @Schema(
            description = "User's full name",
            example = "Nguyễn Văn A",
            required = true
    )
    @NotBlank(message = "Tên không được để trống")
    private String fullName;

    @Schema(
            description = "User's email address (must be unique)",
            example = "nguyenvana@example.com",
            required = true
    )
    @NotBlank(message = "Email không được để trống")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Email không hợp lệ")
    private String email;

    @Schema(
            description = "User's password (minimum 6 characters)",
            example = "mySecurePassword123",
            required = true,
            minLength = 6
    )
    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String password;

    @Schema(
            description = "User's phone number (10-12 digits, optional + prefix)",
            example = "0123456789",
            required = true
    )
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^\\+?[0-9]{10,12}$", message = "Số điện thoại không hợp lệ")
    private String phoneNumber;

    @Schema(
            description = "Ward/Commune name (Phường/Xã)",
            example = "Phường Bách Khoa",
            required = true
    )
    @NotBlank(message = "Phường/xã không được để trống")
    private String ward;

    @Schema(
            description = "City/Province name (Tỉnh/Thành phố)",
            example = "Hà Nội",
            required = true
    )
    @NotBlank(message = "Tỉnh/thành phố không được để trống")
    private String city;

}
