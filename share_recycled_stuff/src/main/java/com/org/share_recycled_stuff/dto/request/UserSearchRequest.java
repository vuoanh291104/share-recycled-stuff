package com.org.share_recycled_stuff.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Request DTO cho tìm kiếm và lọc người dùng")
public class UserSearchRequest {

    @Schema(
            description = "Từ khóa tìm kiếm (theo tên hiển thị, email...)",
            example = "Nguyễn Văn A"
    )
    private String keyword;

    @Schema(
            description = "Lọc theo tỉnh/thành phố (dùng tên chính xác trong User.city)",
            example = "Hồ Chí Minh"
    )
    private String location;

    @Schema(
            description = "Lọc tài khoản là Proxy Seller. (true = chỉ lấy Proxy Seller, false = chỉ lấy user thường, null = lấy cả hai)",
            example = "true"
    )
    private Boolean isProxySeller;

    @Schema(description = "Số trang (bắt đầu từ 0)", defaultValue = "0")
    @Min(value = 0, message = "Số trang phải lớn hơn hoặc bằng 0")
    private int page = 0;

    @Schema(description = "Số lượng kết quả mỗi trang", defaultValue = "20")
    @Min(value = 1, message = "Kích thước trang phải lớn hơn 0")
    private int size = 20;
}
