package com.org.share_recycled_stuff.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request to change password for authenticated user")
public class ChangePasswordRequest {
    @Schema(
            description = "Current password",
            example = "currentPassword123",
            required = true
    )
    @NotBlank(message = "Mật khẩu hiện tại không được để trống")
    private String currentPassword;

    @Schema(
            description = "New password (minimum 6 characters)",
            example = "newSecurePassword123",
            required = true,
            minLength = 6
    )
    @NotBlank(message = "Mật khẩu mới không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String newPassword;

    @Schema(
            description = "Confirm new password (must match newPassword)",
            example = "newSecurePassword123",
            required = true
    )
    @NotBlank(message = "Xác nhận mật khẩu không được để trống")
    private String confirmPassword;
}
