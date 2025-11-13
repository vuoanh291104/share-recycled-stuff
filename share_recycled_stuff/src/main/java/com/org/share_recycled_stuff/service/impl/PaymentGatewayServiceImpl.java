package com.org.share_recycled_stuff.service.impl;

import com.org.share_recycled_stuff.config.VNPayConfig;
import com.org.share_recycled_stuff.service.PaymentGatewayService;
import com.org.share_recycled_stuff.utils.VNPay;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentGatewayServiceImpl implements PaymentGatewayService {

    private final VNPayConfig vnpayConfig;

    @Override
    public String createPaymentUrl(BigDecimal totalAmount, String transactionId, String orderInfo, String ipAddress) {

        // vnp_Amount: VNPay yêu cầu nhân 100 và bỏ dấu thập phân
        String vnpAmount = totalAmount.multiply(new BigDecimal("100")).toBigInteger().toString();

        // Tạo các tham số cho VNPay
        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", vnpayConfig.getVnpTmnCode());
        vnpParams.put("vnp_Amount", vnpAmount);
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", transactionId); // Mã giao dịch của BẠN
        vnpParams.put("vnp_OrderInfo", orderInfo);
        vnpParams.put("vnp_OrderType", "other"); // Loại hàng hóa
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_ReturnUrl", vnpayConfig.getVnpReturnUrl());
        vnpParams.put("vnp_IpAddr", ipAddress);

        // Đặt thời gian tạo
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnpCreateDate = formatter.format(cld.getTime());
        vnpParams.put("vnp_CreateDate", vnpCreateDate);

        // Đặt thời gian hết hạn
        cld.add(Calendar.MINUTE, 15); // Hết hạn sau 15 phút
        String vnpExpireDate = formatter.format(cld.getTime());
        vnpParams.put("vnp_ExpireDate", vnpExpireDate);

        // Sắp xếp các tham số và tạo chuỗi hash data
        String hashData = VNPay.getHashData(vnpParams);

        // Tạo chữ ký
        String vnpSecureHash = VNPay.hmacSHA512(vnpayConfig.getVnpHashSecret(), hashData);

        // Thêm chữ ký vào tham số
        vnpParams.put("vnp_SecureHash", vnpSecureHash);

        // Xây dựng URL cuối cùng
        String queryUrl = vnpParams.entrySet().stream()
                .map(entry -> {
                    try {
                        // Mã hóa URL
                        return entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString());
                    } catch (Exception e) {
                        return "";
                    }
                })
                .collect(Collectors.joining("&"));

        return vnpayConfig.getVnpUrl() + "?" + queryUrl;
    }
}
