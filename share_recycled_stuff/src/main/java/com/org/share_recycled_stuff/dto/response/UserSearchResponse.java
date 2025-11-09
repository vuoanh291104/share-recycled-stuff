package com.org.share_recycled_stuff.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Response DTO chứa thông tin tóm tắt của người dùng (cho kết quả tìm kiếm)")
public class UserSearchResponse {

    @Schema(description = "ID của User (không phải Account ID)", example = "12")
    private Long id;

    @Schema(description = "Tên hiển thị (fullName từ User)", example = "Nguyễn Văn A")
    private String displayName;

    @Schema(description = "URL ảnh đại diện", example = "https.image.com/avatar.jpg")
    private String avatarUrl;

    @Schema(description = "Địa điểm (city từ User)", example = "Hồ Chí Minh")
    private String location;

    @Schema(description = "Là Proxy Seller hay không", example = "true")
    private boolean isProxySeller;

    @Schema(description = "Điểm đánh giá trung bình", example = "4.5")
    private BigDecimal averageRating;
}
