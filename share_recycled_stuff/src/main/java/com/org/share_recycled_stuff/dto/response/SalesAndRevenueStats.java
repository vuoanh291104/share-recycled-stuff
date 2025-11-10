package com.org.share_recycled_stuff.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
@Schema(description = "Thống kê chi tiết về doanh số và lợi nhuận")
public class SalesAndRevenueStats {
    @Schema(description = "Tổng số đơn hàng đã bán thành công (lượt bán)")
    private long totalCompletedOrders;

    @Schema(description = "Tổng doanh thu (tổng tiền bán được - soldPrice)")
    private BigDecimal totalRevenue;

    @Schema(description = "Tổng lợi nhuận (tiền chiết khấu - commissionFee)")
    private BigDecimal totalProfit;
}
