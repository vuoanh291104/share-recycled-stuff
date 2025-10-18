package com.org.share_recycled_stuff.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Admin request to review and change post status")
public class AdminPostReviewRequest {
    @Schema(
            description = "Post ID to review",
            example = "1",
            required = true
    )
    @NotNull(message = "Post ID không được để trống")
    private Long postId;

    @Schema(
            description = "New status code (1=ACTIVE, 2=PENDING, 3=INACTIVE)",
            example = "1",
            required = true,
            allowableValues = {"1", "2", "3"}
    )
    @NotNull(message = "Status không được để trống")
    private Integer statusCode;

    @Schema(
            description = "Admin's review comment",
            example = "Bài đăng đã được duyệt"
    )
    private String adminReviewComment;
}

