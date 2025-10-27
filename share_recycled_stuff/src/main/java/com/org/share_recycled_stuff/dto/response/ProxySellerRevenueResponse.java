package com.org.share_recycled_stuff.dto.response;

import com.org.share_recycled_stuff.entity.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProxySellerRevenueResponse {
    private Long proxySellerId;
    private Integer year;
    private Integer month;
    private String name;
    private BigDecimal totalRevenue;
    private BigDecimal discountProfitPayable;
    private PaymentStatus paymentStatus;
}
