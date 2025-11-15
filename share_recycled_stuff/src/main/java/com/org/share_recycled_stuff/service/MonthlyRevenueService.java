package com.org.share_recycled_stuff.service;

import com.org.share_recycled_stuff.dto.request.PaymentRequest;
import com.org.share_recycled_stuff.dto.response.MonthlyRevenueResponse;
import com.org.share_recycled_stuff.dto.response.PaymentUrlResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

public interface MonthlyRevenueService {

    List<MonthlyRevenueResponse> getMyRevenueRecords(Long proxySellerId);

    List<MonthlyRevenueResponse> getMyUnpaidRevenueRecords(Long proxySellerId);

    PaymentUrlResponse createPaymentRequest(PaymentRequest request, Long proxySellerId, HttpServletRequest httpRequest);

    Map<String, String> processVNPayCallback(HttpServletRequest request);
}
