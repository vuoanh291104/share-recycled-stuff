package com.org.share_recycled_stuff.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User review/rating response with reviewer details and rating")
public class UserReviewResponse {
    @Schema(description = "Review ID", example = "1")
    private Long reviewId;

    @Schema(description = "Reviewer account ID", example = "5")
    private Long reviewerAccountId;

    @Schema(description = "Reviewer name", example = "Nguyễn Văn A")
    private String reviewerName;

    @Schema(description = "Rating (1-5 stars)", example = "5", minimum = "1", maximum = "5")
    private Integer rating;

    @Schema(description = "Review comment", example = "Người mua rất tốt, giao dịch nhanh chóng")
    private String comment;

    @Schema(description = "Review creation timestamp", example = "2024-01-01T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2024-01-02T11:00:00")
    private LocalDateTime updatedAt;
}
