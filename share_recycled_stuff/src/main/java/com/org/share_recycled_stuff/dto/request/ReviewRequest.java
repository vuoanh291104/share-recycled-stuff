package com.org.share_recycled_stuff.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload để tạo hoặc cập nhật một đánh giá người dùng")
public class ReviewRequest {

    @Schema(
            description = "ID của người dùng được đánh giá",
            example = "123",
            required = true
    )
    @NotNull(message = "ID người được đánh giá không được để trống")
    private Long reviewedUserId;

    @Schema(
            description = "Điểm đánh giá (từ 1 đến 5 sao)",
            example = "5",
            required = true
    )
    @NotNull(message = "Rating không được để trống")
    @Min(value = 1, message = "Rating phải từ 1 đến 5")
    @Max(value = 5, message = "Rating phải từ 1 đến 5")
    private Integer rating;

    @Schema(
            description = "Nội dung bình luận (không bắt buộc)",
            example = "Người dùng này rất thân thiện!"
    )
    private String comment;
}
