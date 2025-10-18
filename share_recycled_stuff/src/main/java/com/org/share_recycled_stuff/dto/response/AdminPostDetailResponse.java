package com.org.share_recycled_stuff.dto.response;

import com.org.share_recycled_stuff.entity.enums.PostPurpose;
import com.org.share_recycled_stuff.entity.enums.PostStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Detailed post information for admin including author details and statistics")
public class AdminPostDetailResponse {
    @Schema(description = "Post ID", example = "1")
    private Long id;

    @Schema(description = "Post title", example = "Bàn học gỗ")
    private String title;

    @Schema(description = "Post content", example = "Bàn học gỗ tốt...")
    private String content;

    @Schema(description = "Category name", example = "Đồ nội thất")
    private String category;

    @Schema(description = "Category ID", example = "1")
    private Long categoryId;

    @Schema(description = "Price in VND", example = "500000")
    private BigDecimal price;

    @Schema(description = "Post purpose (SELL/GIFT/EXCHANGE)", example = "SELL")
    private PostPurpose purpose;

    @Schema(description = "Post status (ACTIVE/PENDING/INACTIVE)", example = "ACTIVE")
    private PostStatus status;

    @Schema(description = "Admin review comment", example = "Đã duyệt")
    private String adminReviewComment;

    @Schema(description = "View count", example = "150")
    private Integer viewCount;

    @Schema(description = "Creation timestamp", example = "2024-01-01T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2024-01-02T15:00:00")
    private LocalDateTime updatedAt;

    @Schema(description = "Deletion timestamp", example = "2024-01-10T09:00:00")
    private LocalDateTime deletedAt;

    @Schema(description = "Post author information")
    private AuthorInfo author;

    @Schema(description = "Post images")
    private List<PostImageResponse> images;

    @Schema(description = "Number of comments", example = "25")
    private Integer commentCount;

    @Schema(description = "Number of reactions", example = "50")
    private Integer reactionCount;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Post author information")
    public static class AuthorInfo {
        @Schema(description = "User ID", example = "1")
        private Long userId;

        @Schema(description = "Account ID", example = "1")
        private Long accountId;

        @Schema(description = "Full name", example = "Nguyễn Văn A")
        private String fullName;

        @Schema(description = "Email", example = "user@example.com")
        private String email;

        @Schema(description = "Phone number", example = "0123456789")
        private String phone;

        @Schema(description = "Avatar URL", example = "https://example.com/avatar.jpg")
        private String avatarUrl;

        @Schema(description = "Whether account is locked", example = "false")
        private Boolean isLocked;
    }
}
