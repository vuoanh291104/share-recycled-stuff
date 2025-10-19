package com.org.share_recycled_stuff.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Registration response with user details and verification status")
public class RegisterRespone {
    @Schema(description = "User ID", example = "1")
    private Long id;

    @Schema(description = "User name", example = "Nguyễn Văn A")
    private String name;

    @Schema(description = "Email address", example = "user@example.com")
    private String email;

    @Schema(description = "Phone number", example = "0123456789")
    private String numberPhone;

    @Schema(description = "Gender (0=Male, 1=Female, 2=Other)", example = "0")
    private Integer gender;

    @Schema(description = "Whether email is verified", example = "false")
    private boolean isVerified;

    @Schema(description = "Account creation timestamp", example = "2024-01-01T10:00:00")
    private LocalDateTime createdAt;
}
