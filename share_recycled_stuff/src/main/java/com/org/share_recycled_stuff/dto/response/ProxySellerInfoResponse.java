package com.org.share_recycled_stuff.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Thông tin cơ bản của Người bán hộ (Proxy Seller) để hiển thị trong danh sách")
public class ProxySellerInfoResponse {
    @Schema(description = "ID của tài khoản (Account) Proxy Seller", example = "5")
    private Long accountId;

    @Schema(description = "Tên đầy đủ", example = "Nguyễn Văn B")
    private String fullName;

    @Schema(description = "URL ảnh đại diện", example = "https://example.com/avatar.jpg")
    private String avatarUrl;
}
