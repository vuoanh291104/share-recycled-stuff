package com.org.share_recycled_stuff.service;

import java.math.BigDecimal;

public interface PaymentGatewayService {
    String createPaymentUrl(BigDecimal totalAmount, String transactionId, String orderInfo, String ipAddress);
}
