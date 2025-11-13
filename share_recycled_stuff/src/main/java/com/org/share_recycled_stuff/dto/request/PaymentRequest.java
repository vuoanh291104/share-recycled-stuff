package com.org.share_recycled_stuff.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "Yêu cầu tạo thanh toán cho một hoặc nhiều hóa đơn hàng tháng")
public class PaymentRequest {

    @Schema(description = "Danh sách các ID của ProxySellerMonthlyRevenue (ID hóa đơn) cần thanh toán", required = true)
    @NotEmpty(message = "Danh sách ID hóa đơn không được để trống")
    private List<Long> monthlyRevenueIds;
}
