package com.org.share_recycled_stuff.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Request DTO cho tìm kiếm và lọc bài đăng")
public class PostSearchRequest {

    @Schema(
            description = "Từ khóa tìm kiếm (theo tiêu đề, nội dung)",
            example = "bàn gỗ"
    )
    private String keyword;

    @Schema(
            description = "Lọc theo ID Phân loại (quần áo, bàn, ghế...)",
            example = "1"
    )
    private Long categoryId;

    @Schema(
            description = "Lọc theo địa điểm (Hà Nội, Hồ Chí Minh...). " +
                    "Lưu ý: Địa điểm này là của người đăng (Account)",
            example = "Hà Nội"
    )
    private String location;

    @Schema(
            description = "Lọc theo mục đích bài đăng (SALE, GIFT...)",
            example = "GIFT"
    )
    private String purpose;

    @Schema(description = "Số trang (bắt đầu từ 0)", defaultValue = "0")
    @Min(value = 0, message = "Số trang phải lớn hơn hoặc bằng 0")
    private int page = 0;

    @Schema(description = "Số lượng kết quả mỗi trang", defaultValue = "20")
    @Min(value = 1, message = "Kích thước trang phải lớn hơn 0")
    private int size = 20;
}
