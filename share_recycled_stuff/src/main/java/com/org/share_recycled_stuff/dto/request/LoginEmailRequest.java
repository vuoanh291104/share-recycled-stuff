package com.org.share_recycled_stuff.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Login request with email and password")
public class LoginEmailRequest {
    @Schema(
            description = "User's email address",
            example = "user@example.com",
            required = true
    )
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(
            description = "User's password (minimum 6 characters)",
            example = "password123",
            required = true,
            minLength = 6
    )
    @NotBlank(message = "Password is required")
    private String password;

    @JsonIgnore
    private String clientIp;

    public LoginEmailRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
