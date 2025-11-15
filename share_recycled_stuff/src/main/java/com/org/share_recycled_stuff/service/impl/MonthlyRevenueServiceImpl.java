package com.org.share_recycled_stuff.service.impl;

import com.org.share_recycled_stuff.dto.request.PaymentRequest;
import com.org.share_recycled_stuff.dto.response.MonthlyRevenueResponse;
import com.org.share_recycled_stuff.dto.response.PaymentUrlResponse;
import com.org.share_recycled_stuff.entity.ProxySellerMonthlyRevenue;
import com.org.share_recycled_stuff.entity.enums.PaymentStatus;
import com.org.share_recycled_stuff.exception.AppException;
import com.org.share_recycled_stuff.exception.ErrorCode;
import com.org.share_recycled_stuff.repository.ProxySellerMonthlyRevenueRepository;
import com.org.share_recycled_stuff.service.MonthlyRevenueService;
import com.org.share_recycled_stuff.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MonthlyRevenueServiceImpl implements MonthlyRevenueService {

    private final ProxySellerMonthlyRevenueRepository revenueRepository;
    private final VNPayService vnPayService;

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

        List<PaymentStatus> payableStatuses = Arrays.asList(PaymentStatus.OVERDUE);

        List<ProxySellerMonthlyRevenue> recordsToPay = revenueRepository.findByIdsAndProxySellerAndStatusIn(
                proxySellerId,
                request.getMonthlyRevenueIds(),
                payableStatuses
        );

        if (recordsToPay.isEmpty()) {
            log.warn("No valid records (OVERDUE) found for payment request. Proxy ID: {}, Requested IDs: {}", proxySellerId, request.getMonthlyRevenueIds());
            throw new AppException(ErrorCode.INVALID_REQUEST, "Không tìm thấy hóa đơn (Quá hạn) hợp lệ để thanh toán.");
        }
        if(recordsToPay.size() != request.getMonthlyRevenueIds().size()){
            log.warn("Payment request contains invalid IDs. Proxy ID: {}, Requested: {}, Found: {}",
                    proxySellerId, request.getMonthlyRevenueIds().size(), recordsToPay.size());
            throw new AppException(ErrorCode.INVALID_REQUEST, "Một số hóa đơn không hợp lệ hoặc không thuộc về bạn.");
        }

        BigDecimal totalAmount = recordsToPay.stream()
                .map(ProxySellerMonthlyRevenue::getAdminCommissionAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Total amount is zero or less. Proxy ID: {}", proxySellerId);
            throw new AppException(ErrorCode.INVALID_REQUEST, "Tổng tiền thanh toán phải lớn hơn 0.");
        }

        String transactionId = UUID.randomUUID().toString().replaceAll("-", "");
        log.info("Generated Transaction ID: {} for total amount: {}", transactionId, totalAmount);

        for(ProxySellerMonthlyRevenue record : recordsToPay) {
            record.setPaymentStatus(PaymentStatus.PENDING);
            record.setPaymentTxnRef(transactionId);
        }
        revenueRepository.saveAll(recordsToPay);

        long amountInVnPayFormat = totalAmount.multiply(new BigDecimal(100)).longValue();
        String orderInfo = "Thanh toan phi hoa hong voi ma GD " + transactionId;

        String paymentUrl = vnPayService.createPaymentUrl(
                transactionId,
                amountInVnPayFormat,
                orderInfo,
                httpRequest
        );

        log.info("Payment URL created for Proxy ID: {}", proxySellerId);

        return PaymentUrlResponse.builder()
                .paymentUrl(paymentUrl)
                .build();
    }

    @Override
    @Transactional
    public Map<String, String> processVNPayCallback(HttpServletRequest request) {
        Map<String, String> response = new HashMap<>();
        try {

            Map<String, String> vnp_Params = vnPayService.getAndValidateVNPayParams(request);

            String vnp_TxnRef = vnp_Params.get("vnp_TxnRef");
            String vnp_ResponseCode = vnp_Params.get("vnp_ResponseCode");

            List<ProxySellerMonthlyRevenue> records = revenueRepository.findByPaymentTxnRef(vnp_TxnRef);

            if (records.isEmpty()) {
                log.warn("VNPay Callback: No records found for TxnRef: {}", vnp_TxnRef);
                response.put("RspCode", "01");
                response.put("Message", "Order Not Found");
                return response;
            }

            boolean isPending = records.stream().allMatch(r -> r.getPaymentStatus() == PaymentStatus.PENDING);
            if (!isPending) {
                log.warn("VNPay Callback: Records for TxnRef {} are not in PENDING state", vnp_TxnRef);
                response.put("RspCode", "02");
                response.put("Message", "Order already confirmed");
                return response;
            }

            if ("00".equals(vnp_ResponseCode)) {
                log.info("VNPay payment SUCCESS for TxnRef: {}", vnp_TxnRef);
                for (ProxySellerMonthlyRevenue record : records) {
                    record.setPaymentStatus(PaymentStatus.PAID);
                }
            } else {

                log.warn("VNPay payment FAILED for TxnRef: {}. ResponseCode: {}", vnp_TxnRef, vnp_ResponseCode);
                for (ProxySellerMonthlyRevenue record : records) {
                    record.setPaymentStatus(PaymentStatus.OVERDUE);
                    record.setPaymentTxnRef(null);
                }
            }

            revenueRepository.saveAll(records);

            response.put("RspCode", "00");
            response.put("Message", "Confirm Success");

        } catch (AppException e) {
            log.error("Error processing VNPay callback", e);

            response.put("RspCode", "97");
            response.put("Message", e.getMessage());
        } catch (Exception e) {
            log.error("Unknown error processing VNPay callback", e);
            response.put("RspCode", "99");
            response.put("Message", "Unknown error");
        }

        return response;
    }

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
