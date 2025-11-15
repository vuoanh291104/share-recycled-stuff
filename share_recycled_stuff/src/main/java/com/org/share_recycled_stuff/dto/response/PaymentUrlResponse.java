package com.org.share_recycled_stuff.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Phản hồi chứa URL thanh toán (vd: VNPay)")
public class PaymentUrlResponse {

    @Schema(description = "Đường dẫn URL đầy đủ để chuyển hướng người dùng đến cổng thanh toán")
    private String paymentUrl;
}
