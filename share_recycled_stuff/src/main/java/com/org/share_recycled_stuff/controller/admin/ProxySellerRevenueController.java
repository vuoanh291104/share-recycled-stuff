package com.org.share_recycled_stuff.controller.admin;

import com.org.share_recycled_stuff.dto.response.ProxySellerRevenueResponse;
import com.org.share_recycled_stuff.dto.response.ApiResponse;
import com.org.share_recycled_stuff.service.ProxySellerRevenueService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/admin/revenue")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ProxySellerRevenueController {

    private final ProxySellerRevenueService revenueService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProxySellerRevenueResponse>>> getMonthlyRevenue(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest httpRequest) {

        Page<ProxySellerRevenueResponse> revenues =
                revenueService.getRevenues(month, year, page, size);

        return ResponseEntity.ok(
                ApiResponse.<Page<ProxySellerRevenueResponse>>builder()
                        .code(HttpStatus.OK.value())
                        .message("Lấy danh sách doanh thu thành công")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .result(revenues)
                        .build()
        );
    }
}
