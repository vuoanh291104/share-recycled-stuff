package com.org.share_recycled_stuff.dto.response;

import com.org.share_recycled_stuff.entity.enums.PostPurpose;
import com.org.share_recycled_stuff.entity.enums.PostStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Basic post information without author details")
public class PostResponse {
    @Schema(description = "Post ID", example = "1")
    private Long id;

    @Schema(description = "Author account ID", example = "1")
    private Long accountId;

    @Schema(description = "Post title", example = "Bàn học gỗ cũ")
    private String title;

    @Schema(description = "Post content", example = "Bàn học gỗ tốt...")
    private String content;

    @Schema(description = "Category name", example = "Đồ nội thất")
    private String category;

    @Schema(description = "Price in VND", example = "500000")
    private BigDecimal price;

    @Schema(description = "Post purpose (SELL/GIFT/EXCHANGE)", example = "SELL")
    private PostPurpose purpose;

    @Schema(description = "Post status (ACTIVE/PENDING/INACTIVE)", example = "ACTIVE")
    private PostStatus status;

    @Schema(description = "Post images")
    private List<PostImageResponse> images;
}
