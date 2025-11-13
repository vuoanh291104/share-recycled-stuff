package com.org.share_recycled_stuff.controller.admin;

import com.org.share_recycled_stuff.dto.request.StatisticsFilterRequest;
import com.org.share_recycled_stuff.dto.response.ApiResponse;
import com.org.share_recycled_stuff.dto.response.StatisticsComparisonResponse;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@Tag(name = "Admin Statistics", description = "Endpoints for Admin to view global or filtered statistics")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/statistics")
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
public class AdminStatisticsController {

    private final StatisticsService statisticsService;

    @Operation(
            summary = "Get system-wide statistics report",
            description = "Retrieve aggregated statistics (Posts and Delegations) for the entire system. " +
                    "Can be filtered by a specific Proxy Seller ID, or by time."
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
                    description = "Forbidden - User is not an ADMIN. Returns ApiResponse with error."
            )
    })
    @GetMapping("/report")
    public ResponseEntity<ApiResponse<StatisticsReportResponse>> getSystemStatisticsReport(
            HttpServletRequest httpRequest,

            @Parameter(description = "Filter by Proxy Seller ID (leave empty for global statistics)")
            @RequestParam(required = false) Long proxySellerId,

            @Parameter(description = "Filter by day (e.g., 25)")
            @RequestParam(required = false) Integer day,

            @Parameter(description = "Filter by month (e.g., 10)")
            @RequestParam(required = false) Integer month,

            @Parameter(description = "Filter by year (e.g., 2025)")
            @RequestParam(required = false) Integer year
    ) {
        log.info("Admin getting statistics report. Filter criteria: proxySellerId={}, year={}, month={}, day={}",
                proxySellerId, year, month, day);

        StatisticsFilterRequest filters = StatisticsFilterRequest.builder()
                .proxySellerId(proxySellerId)
                .day(day)
                .month(month)
                .year(year)
                .build();

        StatisticsReportResponse report = statisticsService.getStatistics(filters);

        return ResponseEntity.ok(
                ApiResponse.<StatisticsReportResponse>builder()
                        .code(HttpStatus.OK.value())
                        .message("Lấy thống kê hệ thống thành công")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .result(report)
                        .build()
        );
    }
    @Operation(
            summary = "So sánh bài đăng (Tháng này so với Tháng trước)",
            description = "So sánh số lượng bài đăng mới trong tháng hiện tại so với tháng trước đó."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Lấy thống kê so sánh thành công."
            )
    })
    @GetMapping("/posts/comparison")
    public ResponseEntity<ApiResponse<StatisticsComparisonResponse>> getPostComparison(
            HttpServletRequest httpRequest
    ) {
        StatisticsComparisonResponse comparison = statisticsService.getPostComparison();

        return ResponseEntity.ok(
                ApiResponse.<StatisticsComparisonResponse>builder()
                        .code(HttpStatus.OK.value())
                        .message("Lấy thống kê so sánh bài đăng thành công")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .result(comparison)
                        .build()
        );
    }
}
