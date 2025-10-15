package com.org.share_recycled_stuff.controller.admin;

import com.org.share_recycled_stuff.config.CustomUserDetail;
import com.org.share_recycled_stuff.dto.request.AdminReportActionRequest;
import com.org.share_recycled_stuff.dto.response.AdminReportDetailResponse;
import com.org.share_recycled_stuff.dto.response.ApiResponse;
import com.org.share_recycled_stuff.entity.enums.ReportStatus;
import com.org.share_recycled_stuff.entity.enums.ReportType;
import com.org.share_recycled_stuff.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminReportController {

    private final ReportService reportService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AdminReportDetailResponse>>> getAllReports(
            @RequestParam(required = false) Integer reportTypeCode,
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

    @GetMapping("/{reportId}")
    public ResponseEntity<ApiResponse<AdminReportDetailResponse>> getReportDetail(
            @PathVariable Long reportId) {
        log.info("Admin getting report detail - ID: {}", reportId);

        AdminReportDetailResponse report = reportService.getReportDetail(reportId);

        return ResponseEntity.ok(
                ApiResponse.success("Lấy chi tiết báo cáo thành công", report)
        );
    }

    @PutMapping("/process")
    public ResponseEntity<ApiResponse<AdminReportDetailResponse>> processReport(
            @Valid @RequestBody AdminReportActionRequest request,
            @AuthenticationPrincipal CustomUserDetail userDetail) {
        log.info("Admin {} processing report - ID: {}", userDetail.getAccountId(), request.getReportId());

        AdminReportDetailResponse processedReport = reportService.processReport(request, userDetail.getAccountId());

        return ResponseEntity.ok(
                ApiResponse.success("Xử lý báo cáo thành công", processedReport)
        );
    }
}

