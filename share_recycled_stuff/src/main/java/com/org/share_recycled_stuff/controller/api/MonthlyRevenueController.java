package com.org.share_recycled_stuff.controller.api;

import com.org.share_recycled_stuff.config.CustomUserDetail;
import com.org.share_recycled_stuff.dto.request.PaymentRequest;
import com.org.share_recycled_stuff.dto.response.ApiResponse;
import com.org.share_recycled_stuff.dto.response.MonthlyRevenueResponse;
import com.org.share_recycled_stuff.dto.response.PaymentUrlResponse;
import com.org.share_recycled_stuff.service.MonthlyRevenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@Tag(name = "Proxy Seller - Monthly Revenue", description = "Quản lý doanh thu và thanh toán phí hàng tháng cho Admin")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/proxy-seller/revenue")
@PreAuthorize("hasRole('PROXY_SELLER')")
@Slf4j
public class MonthlyRevenueController {

    private final MonthlyRevenueService revenueService;

    @Operation(
            summary = "Lấy tất cả lịch sử hóa đơn (đã trả và chưa trả)",
            description = "Lấy danh sách tất cả các bản ghi doanh thu hàng tháng của Proxy Seller."
    )
    @GetMapping("/my-history")
    public ResponseEntity<ApiResponse<List<MonthlyRevenueResponse>>> getMyRevenueHistory(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            HttpServletRequest httpRequest
    ) {
        List<MonthlyRevenueResponse> records = revenueService.getMyRevenueRecords(userDetail.getAccountId());
        return ResponseEntity.ok(
                ApiResponse.<List<MonthlyRevenueResponse>>builder()
                        .code(HttpStatus.OK.value())
                        .message("Lấy lịch sử doanh thu thành công")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .result(records)
                        .build()
        );
    }

    @Operation(
            summary = "Lấy các hóa đơn CHƯA thanh toán (Quá hạn)",
            description = "Chỉ lấy các hóa đơn đang ở trạng thái Quá hạn (OVERDUE)."
    )
    @GetMapping("/my-unpaid")
    public ResponseEntity<ApiResponse<List<MonthlyRevenueResponse>>> getMyUnpaidInvoices(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            HttpServletRequest httpRequest
    ) {
        List<MonthlyRevenueResponse> records = revenueService.getMyUnpaidRevenueRecords(userDetail.getAccountId());
        return ResponseEntity.ok(
                ApiResponse.<List<MonthlyRevenueResponse>>builder()
                        .code(HttpStatus.OK.value())
                        .message("Lấy hóa đơn chưa thanh toán thành công")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .result(records)
                        .build()
        );
    }

    @Operation(
            summary = "Tạo yêu cầu thanh toán cho các hóa đơn đã chọn",
            description = "Gửi một danh sách các ID hóa đơn (revenueId) để tạo link thanh toán (VNPAY...)."
    )
    @PostMapping("/create-payment")
    public ResponseEntity<ApiResponse<PaymentUrlResponse>> createPaymentRequest(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @Valid @RequestBody PaymentRequest paymentRequest,
            HttpServletRequest httpRequest // Thêm HttpServletRequest để lấy IP
    ) {
        log.info("Proxy Seller {} requests payment for revenue IDs: {}", userDetail.getAccountId(), paymentRequest.getMonthlyRevenueIds());

        // Truyền httpRequest vào service
        PaymentUrlResponse paymentUrl = revenueService.createPaymentRequest(paymentRequest, userDetail.getAccountId(), httpRequest);

        return ResponseEntity.ok(
                ApiResponse.<PaymentUrlResponse>builder()
                        .code(HttpStatus.OK.value())
                        .message("Tạo link thanh toán thành công")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .result(paymentUrl)
                        .build()
        );
    }
}
