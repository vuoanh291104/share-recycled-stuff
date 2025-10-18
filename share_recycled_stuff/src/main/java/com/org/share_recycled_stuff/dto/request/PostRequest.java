package com.org.share_recycled_stuff.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Valid
@Schema(description = "Request to create or update a post")
public class PostRequest {
    @Schema(description = "Account ID (auto-filled from JWT token)", hidden = true)
    private Long accountId;

    @Schema(
            description = "Post title",
            example = "Bàn học gỗ cũ cần tặng",
            required = true
    )
    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    @Schema(
            description = "Post content/description",
            example = "Bàn học gỗ còn tốt, 90% mới. Có thể lấy miễn phí tại Hà Nội"
    )
    private String content;

    @Schema(
            description = "Category ID",
            example = "1",
            required = true
    )
    @NotNull(message = "Phải chọn phân loại sản phẩm")
    private Long categoryId;

    @Schema(
            description = "Price in VND (set to 0 or null for free items)",
            example = "50000",
            minimum = "0"
    )
    @Min(value = 0, message = "Giá phải lớn hơn hoặc bằng 0")
    private BigDecimal price;

    @Schema(
            description = "Post purpose code (1=SELL, 2=GIFT, 3=EXCHANGE)",
            example = "2",
            allowableValues = {"1", "2", "3"}
    )
    private Integer purposeCode;

    @Schema(description = "List of post images (URLs)")
    private List<PostImageRequest> images;
}
