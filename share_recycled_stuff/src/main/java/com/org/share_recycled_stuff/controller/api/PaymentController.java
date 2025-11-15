package com.org.share_recycled_stuff.controller.api;

import com.org.share_recycled_stuff.dto.response.ApiResponse;
import com.org.share_recycled_stuff.service.MonthlyRevenueService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;


@Tag(name = "Payment", description = "Endpoints for handling payment callbacks")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentController {

    private final MonthlyRevenueService monthlyRevenueService;

    @GetMapping("/transaction")
    public ResponseEntity<ApiResponse<Map<String, String>>> vnPayCallback(HttpServletRequest request) {

        Map<String, String> vnPayResponse = monthlyRevenueService.processVNPayCallback(request);

        String rspCode = vnPayResponse.get("RspCode");

        if ("00".equals(rspCode)) {
            return ResponseEntity.ok(
                    ApiResponse.<Map<String, String>>builder()
                            .code(HttpStatus.OK.value())
                            .message("Giao dịch thành công!")
                            .path(request.getRequestURI())
                            .timestamp(LocalDateTime.now().toString())
                            .result(vnPayResponse)
                            .build()
            );
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<Map<String, String>>builder()
                            .code(HttpStatus.BAD_REQUEST.value())
                            .message("Thanh toán thất bại. Lỗi: " + vnPayResponse.get("vnp_Message"))
                            .path(request.getRequestURI())
                            .timestamp(LocalDateTime.now().toString())
                            .result(vnPayResponse)
                            .build()
                    );

        }
    }

}
