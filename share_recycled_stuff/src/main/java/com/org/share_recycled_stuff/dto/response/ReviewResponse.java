package com.org.share_recycled_stuff.dto.response;

import com.org.share_recycled_stuff.entity.Account;
import com.org.share_recycled_stuff.entity.UserReviews;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Thông tin chi tiết của một đánh giá")
public class ReviewResponse {

    @Schema(description = "ID của đánh giá", example = "1")
    private Long id;

    @Schema(description = "Điểm đánh giá", example = "5")
    private Integer rating;

    @Schema(description = "Nội dung bình luận", example = "Tuyệt vời!")
    private String comment;

    @Schema(description = "Thời điểm tạo đánh giá")
    private LocalDateTime createdAt;

    @Schema(description = "Thời điểm cập nhật đánh giá lần cuối")
    private LocalDateTime updatedAt;

    @Schema(description = "Thông tin người viết đánh giá")
    private ReviewerInfo reviewer;

    public static ReviewResponse fromEntity(UserReviews review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .reviewer(ReviewerInfo.fromAccount(review.getReviewer()))
                .build();
    }

    @Data
    @Builder
    @Schema(description = "Thông tin cơ bản của người viết đánh giá")
    public static class ReviewerInfo {

        @Schema(description = "ID của người viết đánh giá", example = "456")
        private Long id;

        @Schema(
                description = "URL avatar của người đánh giá. (Lưu ý: Thông thường trường này nên được lấy từ server)",
                example = "https://example.com/avatar/user456.png"
        )
        private String reviewerAvatarUrl;

        @Schema(description = "Tên đầy đủ của người viết", example = "Nguyễn Văn A")
        private String fullName;

        public static ReviewerInfo fromAccount(Account account) {
            return ReviewerInfo.builder()
                    .id(account.getId())
                    .reviewerAvatarUrl(account.getUser().getAvatarUrl())
                    .fullName(account.getUser().getFullName())
                    .build();
        }
    }
}
