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
@Schema(description = "Detailed post information including author and images")
public class PostDetailResponse {
    @Schema(description = "Post ID", example = "1")
    private Long id;

    @Schema(description = "Post title", example = "Bàn học gỗ cũ")
    private String title;

    @Schema(description = "Post content/description", example = "Bàn học gỗ tốt, còn mới 90%")
    private String content;

    @Schema(description = "Category name", example = "Đồ nội thất")
    private String category;

    @Schema(description = "Price in VND (nullable for free items)", example = "500000")
    private BigDecimal price;

    @Schema(description = "Post purpose", example = "SELL")
    private PostPurpose purpose;

    @Schema(description = "Post status", example = "ACTIVE")
    private PostStatus status;

    @Schema(description = "Number of views", example = "150")
    private Integer viewCount;

    @Schema(description = "Post creation timestamp", example = "2024-01-01T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2024-01-02T15:30:00")
    private LocalDateTime updatedAt;

    @Schema(
            description = "Distance from viewer in kilometers (calculated using GeoIP)",
            example = "5.2",
            nullable = true
    )
    private Double distance;

    @Schema(description = "Post author information")
    private UserInfo author;

    @Schema(description = "List of post images")
    private List<PostImageResponse> images;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Post author information")
    public static class UserInfo {
        @Schema(description = "Author's user ID", example = "1")
        private Long id;

        @Schema(description = "Author's full name", example = "Nguyễn Văn A")
        private String fullName;

        @Schema(description = "Author's avatar URL", example = "https://example.com/avatar.jpg")
        private String avatarUrl;

        @Schema(description = "Author's email", example = "nguyenvana@example.com")
        private String email;
    }
}
