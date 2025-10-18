package com.org.share_recycled_stuff.controller.api;

import com.org.share_recycled_stuff.config.CustomUserDetail;
import com.org.share_recycled_stuff.dto.request.ReportRequest;
import com.org.share_recycled_stuff.dto.response.ApiResponse;
import com.org.share_recycled_stuff.dto.response.ReportResponse;
import com.org.share_recycled_stuff.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Reports", description = "User and post reporting endpoints")
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAnyRole('CUSTOMER', 'PROXY_SELLER')")
public class ReportController {

    private final ReportService reportService;

    @Operation(
            summary = "Create report",
            description = "Report a user or post for policy violations. Report will be reviewed by admin."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Report created successfully. Returns ApiResponse<ReportResponse>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid report data. Returns ApiResponse with validation errors."
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<ReportResponse>> createReport(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Report details including type, target ID, and reason",
                    required = true
            )
            @Valid @RequestBody ReportRequest request,
            @AuthenticationPrincipal CustomUserDetail userDetail) {
        log.info("User {} creating report - type: {}", userDetail.getAccountId(), request.getReportTypeCode());

        ReportResponse response = reportService.createReport(request, userDetail.getAccountId());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Báo cáo đã được gửi thành công", response)
        );
    }

    @Operation(
            summary = "Get my reports",
            description = "Retrieve all reports created by current user with pagination"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved reports. Returns ApiResponse<Page<ReportResponse>>."
            )
    })
    @GetMapping("/my-reports")
    public ResponseEntity<ApiResponse<Page<ReportResponse>>> getMyReports(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            Pageable pageable) {
        log.info("User {} getting their reports", userDetail.getAccountId());

        Page<ReportResponse> reports = reportService.getMyReports(userDetail.getAccountId(), pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Lấy danh sách báo cáo thành công", reports)
        );
    }
}

