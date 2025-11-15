package com.org.share_recycled_stuff.service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

public interface VNPayService {

    String createPaymentUrl(String transactionId, long amount, String orderInfo, HttpServletRequest request);

    Map<String, String> getAndValidateVNPayParams(HttpServletRequest request);
}
