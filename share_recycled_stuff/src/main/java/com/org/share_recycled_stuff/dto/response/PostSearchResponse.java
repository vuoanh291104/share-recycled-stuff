package com.org.share_recycled_stuff.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Response DTO chứa thông tin tóm tắt của bài đăng (cho kết quả tìm kiếm)")
public class PostSearchResponse {

    @Schema(description = "ID của bài đăng", example = "101")
    private Long id;

    @Schema(description = "Tiêu đề bài đăng", example = "Bàn học gỗ cũ cần tặng")
    private String title;

    @Schema(description = "Giá (VND)", example = "0")
    private BigDecimal price;

    @Schema(description = "URL ảnh bìa (ảnh đầu tiên)", example = "https://image.com/thumbnail.jpg")
    private String thumbnailUrl;

    @Schema(description = "Địa điểm (lấy từ người đăng)", example = "Hà Nội")
    private String location;

    @Schema(description = "Tên phân loại", example = "Đồ gia dụng")
    private String categoryName;

    @Schema(description = "Tên mục đích", example = "GIFT")
    private String purposeName;

    @Schema(description = "Thời gian đăng", example = "2025-11-07T21:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Số lượt xem", example = "150")
    private Integer viewCount;

    @Schema(description = "Tổng số lượt thích", example = "10")
    private Integer reactionCount;

    @Schema(description = "Tổng số bình luận", example = "5")
    private Integer commentCount;

    @Schema(description = "Thông tin tóm tắt về người đăng")
    private AuthorSummaryResponse author;

}
