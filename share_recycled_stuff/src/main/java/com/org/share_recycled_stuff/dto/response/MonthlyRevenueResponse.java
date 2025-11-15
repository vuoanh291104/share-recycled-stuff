package com.org.share_recycled_stuff.dto.response;

import com.org.share_recycled_stuff.entity.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Thông tin chi tiết về một hóa đơn doanh thu hàng tháng")
public class MonthlyRevenueResponse {

    @Schema(description = "ID của bản ghi doanh thu (ID của hóa đơn)")
    private Long id;

    @Schema(description = "ID của Proxy Seller")
    private Long proxySellerId;

    @Schema(description = "Tháng (vd: 10)")
    private Integer month;

    @Schema(description = "Năm (vd: 2025)")
    private Integer year;

    @Schema(description = "Tổng doanh thu bán hàng trong tháng")
    private BigDecimal totalSalesAmount;

    @Schema(description = "Tổng hoa hồng Proxy kiếm được trong tháng")
    private BigDecimal totalCommission;

    @Schema(description = "Số tiền phí Admin (số tiền Proxy phải trả)")
    private BigDecimal adminCommissionAmount;

    @Schema(description = "Trạng thái thanh toán phí")
    private PaymentStatus paymentStatus;

    @Schema(description = "Ngày hạn chót thanh toán")
    private LocalDateTime paymentDueDate;
}
