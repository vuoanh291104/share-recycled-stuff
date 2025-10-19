package com.org.share_recycled_stuff.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.org.share_recycled_stuff.entity.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "User profile information including personal details, ratings, and roles")
public class UserProfileResponse {
    @Schema(description = "Account ID", example = "1")
    private Long accountId;

    @Schema(description = "User ID", example = "1")
    private Long userId;

    @Schema(description = "Email address", example = "user@example.com")
    private String email;

    @Schema(description = "Full name", example = "Nguyễn Văn A")
    private String fullName;

    @Schema(description = "Phone number", example = "0123456789")
    private String phoneNumber;

    @Schema(description = "Detailed address", example = "123 Đường ABC")
    private String address;

    @Schema(description = "Ward/Commune (Phường/Xã)", example = "Phường Bách Khoa")
    private String ward;

    @Schema(description = "City/Province (Tỉnh/Thành phố)", example = "Hà Nội")
    private String city;

    @Schema(description = "National ID card number", example = "001234567890")
    private String idCard;

    @Schema(description = "Avatar URL", example = "https://example.com/avatar.jpg")
    private String avatarUrl;

    @Schema(description = "User bio/introduction", example = "Tôi thích tái chế đồ cũ")
    private String bio;

    @Schema(description = "Average rating score", example = "4.5")
    private BigDecimal ratingAverage;

    @Schema(description = "Total number of ratings received", example = "10")
    private Integer totalRatings;

    @Schema(description = "User roles", example = "[\"CUSTOMER\"]")
    private Set<Role> roles;

    @Schema(description = "List of reviews received by this user")
    private List<UserReviewResponse> reviews;

    @Schema(description = "Account creation timestamp", example = "2024-01-01T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2024-01-02T15:00:00")
    private LocalDateTime updatedAt;
}
