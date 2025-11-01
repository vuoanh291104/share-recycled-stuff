package com.org.share_recycled_stuff.controller.api;

import com.org.share_recycled_stuff.config.CustomUserDetail;
import com.org.share_recycled_stuff.dto.request.StatisticsFilterRequest;
import com.org.share_recycled_stuff.dto.response.ApiResponse;
import com.org.share_recycled_stuff.dto.response.StatisticsReportResponse;
import com.org.share_recycled_stuff.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@Tag(name = "Proxy Seller Statistics", description = "Endpoints for Proxy Sellers to view their own statistics")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/proxy-seller/statistics")
@PreAuthorize("hasRole('PROXY_SELLER')")
@Slf4j
public class ProxySellerStatisticsController {

    private final StatisticsService statisticsService;

    @Operation(
            summary = "Get my statistics report",
            description = "Retrieve aggregated statistics (Posts and Delegations) for the currently logged-in Proxy Seller. " +
                    "Data can be filtered by day, month, and/or year."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved statistics. Returns ApiResponse<StatisticsReportResponse>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token required. Returns ApiResponse with error."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - User is not a PROXY_SELLER. Returns ApiResponse with error."
            )
    })
    @GetMapping("/my-report")
    public ResponseEntity<ApiResponse<StatisticsReportResponse>> getMyStatisticsReport(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            HttpServletRequest httpRequest,

            @Parameter(description = "Filter by day (e.g., 25)")
            @RequestParam(required = false) Integer day,

            @Parameter(description = "Filter by month (e.g., 10)")
            @RequestParam(required = false) Integer month,

            @Parameter(description = "Filter by year (e.g., 2025)")
            @RequestParam(required = false) Integer year
    ) {
        log.info("Proxy Seller {} getting their statistics report", userDetail.getAccountId());

        StatisticsFilterRequest filters = StatisticsFilterRequest.builder()
                .proxySellerId(userDetail.getAccountId())
                .day(day)
                .month(month)
                .year(year)
                .build();

        StatisticsReportResponse report = statisticsService.getStatistics(filters);

        return ResponseEntity.ok(
                ApiResponse.<StatisticsReportResponse>builder()
                        .code(HttpStatus.OK.value())
                        .message("Lấy thống kê thành công")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .result(report)
                        .build()
        );
    }
}
