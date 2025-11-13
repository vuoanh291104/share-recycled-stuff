package com.org.share_recycled_stuff.dto.response;

import com.org.share_recycled_stuff.entity.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema; // Thêm import
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Thông tin doanh thu của Proxy Seller (dùng cho Admin xem)") // Thêm Schema cho class
public class ProxySellerRevenueResponse {

    @Schema(description = "ID của Proxy Seller")
    private Long proxySellerId;

    @Schema(description = "Năm", example = "2025")
    private Integer year;

    @Schema(description = "Tháng", example = "10")
    private Integer month;

    @Schema(description = "Tên của Proxy Seller", example = "Nguyễn Văn A")
    private String name;

    @Schema(description = "Tổng doanh thu bán hàng (totalSalesAmount)", example = "5000000")
    private BigDecimal totalRevenue;

    @Schema(description = "Phí phải trả cho Admin (adminCommissionAmount)", example = "25000")
    private BigDecimal discountProfitPayable;

    @Schema(description = "Trạng thái thanh toán", example = "DUE")
    private PaymentStatus paymentStatus;
}
