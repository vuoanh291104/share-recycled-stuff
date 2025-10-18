package com.org.share_recycled_stuff.controller.admin;

import com.org.share_recycled_stuff.config.CustomUserDetail;
import com.org.share_recycled_stuff.dto.request.AdminReportActionRequest;
import com.org.share_recycled_stuff.dto.response.AdminReportDetailResponse;
import com.org.share_recycled_stuff.dto.response.ApiResponse;
import com.org.share_recycled_stuff.entity.enums.ReportStatus;
import com.org.share_recycled_stuff.entity.enums.ReportType;
import com.org.share_recycled_stuff.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin - Report Management", description = "Admin endpoints for handling user and post reports")
@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminReportController {

    private final ReportService reportService;

    @Operation(
            summary = "Get all reports",
            description = "Retrieve all reports with optional filters by type (USER/POST) and status (PENDING/PROCESSING/RESOLVED/REJECTED)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved reports. Returns ApiResponse<Page<AdminReportDetailResponse>>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Admin role required. Returns ApiResponse with error."
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<Page<AdminReportDetailResponse>>> getAllReports(
            @Parameter(
                    description = "Report type code (1=USER, 2=POST)",
                    example = "1"
            )
            @RequestParam(required = false) Integer reportTypeCode,
            @Parameter(
                    description = "Status code (1=PENDING, 2=PROCESSING, 3=RESOLVED, 4=REJECTED)",
                    example = "1"
            )
            @RequestParam(required = false) Integer statusCode,
            Pageable pageable) {
        log.info("Admin getting all reports - typeCode: {}, statusCode: {}", reportTypeCode, statusCode);

        ReportType reportType = null;
        if (reportTypeCode != null) {
            reportType = ReportType.fromCode(reportTypeCode);
        }

        ReportStatus status = null;
        if (statusCode != null) {
            status = ReportStatus.fromCode(statusCode);
        }

        Page<AdminReportDetailResponse> reports = reportService.getAllReports(reportType, status, pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Lấy danh sách báo cáo thành công", reports)
        );
    }

    @Operation(
            summary = "Get report detail",
            description = "Retrieve detailed information about a specific report"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved report detail. Returns ApiResponse<AdminReportDetailResponse>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Admin role required. Returns ApiResponse with error."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Report not found. Returns ApiResponse with error."
            )
    })
    @GetMapping("/{reportId}")
    public ResponseEntity<ApiResponse<AdminReportDetailResponse>> getReportDetail(
            @Parameter(
                    description = "Report ID",
                    required = true,
                    example = "1"
            )
            @PathVariable Long reportId) {
        log.info("Admin getting report detail - ID: {}", reportId);

        AdminReportDetailResponse report = reportService.getReportDetail(reportId);

        return ResponseEntity.ok(
                ApiResponse.success("Lấy chi tiết báo cáo thành công", report)
        );
    }

    @Operation(
            summary = "Process report",
            description = "Admin processes a report by changing its status and optionally providing a resolution note"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Report processed successfully. Returns ApiResponse<AdminReportDetailResponse>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data. Returns ApiResponse with error."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Admin role required. Returns ApiResponse with error."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Report not found. Returns ApiResponse with error."
            )
    })
    @PutMapping("/process")
    public ResponseEntity<ApiResponse<AdminReportDetailResponse>> processReport(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Report action request with report ID, new status, and optional resolution note",
                    required = true
            )
            @Valid @RequestBody AdminReportActionRequest request,
            @AuthenticationPrincipal CustomUserDetail userDetail) {
        log.info("Admin {} processing report - ID: {}", userDetail.getAccountId(), request.getReportId());

        AdminReportDetailResponse processedReport = reportService.processReport(request, userDetail.getAccountId());

        return ResponseEntity.ok(
                ApiResponse.success("Xử lý báo cáo thành công", processedReport)
        );
    }
}

