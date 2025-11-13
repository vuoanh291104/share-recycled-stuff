package com.org.share_recycled_stuff.service.impl;

import com.org.share_recycled_stuff.dto.request.PaymentRequest;
import com.org.share_recycled_stuff.dto.response.MonthlyRevenueResponse;
import com.org.share_recycled_stuff.dto.response.PaymentUrlResponse;
import com.org.share_recycled_stuff.entity.ProxySellerMonthlyRevenue;
import com.org.share_recycled_stuff.entity.enums.PaymentStatus; // File Enum của bạn
import com.org.share_recycled_stuff.exception.AppException;
import com.org.share_recycled_stuff.exception.ErrorCode;
import com.org.share_recycled_stuff.repository.ProxySellerMonthlyRevenueRepository;
import com.org.share_recycled_stuff.service.MonthlyRevenueService;
import com.org.share_recycled_stuff.service.PaymentGatewayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MonthlyRevenueServiceImpl implements MonthlyRevenueService {

    private final ProxySellerMonthlyRevenueRepository revenueRepository;

    // private final PaymentGatewayService paymentGatewayService;

    @Override
    @Transactional(readOnly = true)
    public List<MonthlyRevenueResponse> getMyRevenueRecords(Long proxySellerId) {
        log.info("Fetching all revenue records for Proxy Seller ID: {}", proxySellerId);
        List<ProxySellerMonthlyRevenue> records = revenueRepository.findByProxySellerIdOrderByYearDescMonthDesc(proxySellerId);
        return records.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MonthlyRevenueResponse> getMyUnpaidRevenueRecords(Long proxySellerId) {
        // === SỬA TẠI ĐÂY ===
        // Chỉ lấy các hóa đơn Quá hạn (OVERDUE),
        // vì 'DUE' không có trong Enum của bạn.
        List<PaymentStatus> unpaidStatuses = Arrays.asList(PaymentStatus.OVERDUE);

        log.info("Fetching unpaid (OVERDUE) records for Proxy Seller ID: {}", proxySellerId);
        List<ProxySellerMonthlyRevenue> records = revenueRepository.findByProxySellerIdAndPaymentStatusIn(proxySellerId, unpaidStatuses);

        return records.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PaymentUrlResponse createPaymentRequest(PaymentRequest request, Long proxySellerId, HttpServletRequest httpRequest) {

        // === SỬA TẠI ĐÂY ===
        // Chỉ cho phép thanh toán các hóa đơn Quá hạn (OVERDUE)
        List<PaymentStatus> payableStatuses = Arrays.asList(PaymentStatus.OVERDUE);

        // 1. Lấy và xác thực các hóa đơn
        List<ProxySellerMonthlyRevenue> recordsToPay = revenueRepository.findByIdsAndProxySellerAndStatusIn(
                proxySellerId,
                request.getMonthlyRevenueIds(),
                payableStatuses
        );

        // 2. Kiểm tra lỗi
        if (recordsToPay.isEmpty()) {
            log.warn("No valid records (OVERDUE) found for payment request. Proxy ID: {}, Requested IDs: {}", proxySellerId, request.getMonthlyRevenueIds());
            throw new AppException(ErrorCode.INVALID_REQUEST, "Không tìm thấy hóa đơn (Quá hạn) hợp lệ để thanh toán.");
        }

        if(recordsToPay.size() != request.getMonthlyRevenueIds().size()){
            log.warn("Payment request contains invalid IDs. Proxy ID: {}, Requested: {}, Found: {}",
                    proxySellerId, request.getMonthlyRevenueIds().size(), recordsToPay.size());
            throw new AppException(ErrorCode.INVALID_REQUEST, "Một số hóa đơn không hợp lệ hoặc không thuộc về bạn.");
        }

        // 3. Tính tổng số tiền cần thanh toán
        BigDecimal totalAmount = recordsToPay.stream()
                .map(ProxySellerMonthlyRevenue::getAdminCommissionAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Total amount is zero or less. Proxy ID: {}", proxySellerId);
            throw new AppException(ErrorCode.INVALID_REQUEST, "Tổng tiền thanh toán phải lớn hơn 0.");
        }

        // 4. (Cần làm) Tạo một bản ghi Giao dịch (CommissionPayments)
        String transactionId = UUID.randomUUID().toString().replaceAll("-", "");
        log.info("Generated Transaction ID: {} for total amount: {}", transactionId, totalAmount);

        // 5. Cập nhật trạng thái các hóa đơn sang PENDING
        // (Trạng thái PENDING của bạn là đúng)
        for(ProxySellerMonthlyRevenue record : recordsToPay) {
            record.setPaymentStatus(PaymentStatus.PENDING);
        }
        revenueRepository.saveAll(recordsToPay);

        // 6. (GIẢ LẬP GỌI VNPAY)
        String paymentUrl = "https://sandbox.vnpayment.vn/pay?amount=" + totalAmount + "&txnRef=" + transactionId;
        log.info("Payment URL created for Proxy ID: {}", proxySellerId);

        return PaymentUrlResponse.builder()
                .paymentUrl(paymentUrl)
                .build();
    }

    // Hàm helper để chuyển Entity sang DTO (Giữ nguyên)
    private MonthlyRevenueResponse mapToResponse(ProxySellerMonthlyRevenue entity) {
        return MonthlyRevenueResponse.builder()
                .id(entity.getId())
                .proxySellerId(entity.getProxySeller().getId())
                .month(entity.getMonth())
                .year(entity.getYear())
                .totalSalesAmount(entity.getTotalSalesAmount())
                .totalCommission(entity.getTotalCommission())
                .adminCommissionAmount(entity.getAdminCommissionAmount())
                .paymentStatus(entity.getPaymentStatus())
                .paymentDueDate(entity.getPaymentDueDate())
                .build();
    }
}
