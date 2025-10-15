package com.org.share_recycled_stuff.service;

import com.org.share_recycled_stuff.dto.request.AdminReportActionRequest;
import com.org.share_recycled_stuff.dto.request.ReportRequest;
import com.org.share_recycled_stuff.dto.response.AdminReportDetailResponse;
import com.org.share_recycled_stuff.dto.response.ReportResponse;
import com.org.share_recycled_stuff.entity.enums.ReportStatus;
import com.org.share_recycled_stuff.entity.enums.ReportType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReportService {
    
    // Customer/Proxy Seller methods
    ReportResponse createReport(ReportRequest request, Long reporterId);
    
    Page<ReportResponse> getMyReports(Long reporterId, Pageable pageable);
    
    // Admin methods
    Page<AdminReportDetailResponse> getAllReports(
            ReportType reportType,
            ReportStatus status,
            Pageable pageable
    );
    
    AdminReportDetailResponse getReportDetail(Long reportId);
    
    AdminReportDetailResponse processReport(AdminReportActionRequest request, Long adminId);
}

