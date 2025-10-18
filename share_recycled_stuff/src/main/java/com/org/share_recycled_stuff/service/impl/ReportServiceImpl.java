package com.org.share_recycled_stuff.service.impl;

import com.org.share_recycled_stuff.dto.request.AdminReportActionRequest;
import com.org.share_recycled_stuff.dto.request.LockAccountRequest;
import com.org.share_recycled_stuff.dto.request.ReportRequest;
import com.org.share_recycled_stuff.dto.response.AdminReportDetailResponse;
import com.org.share_recycled_stuff.dto.response.ReportResponse;
import com.org.share_recycled_stuff.entity.Account;
import com.org.share_recycled_stuff.entity.Post;
import com.org.share_recycled_stuff.entity.Reports;
import com.org.share_recycled_stuff.entity.enums.ReportStatus;
import com.org.share_recycled_stuff.entity.enums.ReportType;
import com.org.share_recycled_stuff.exception.AppException;
import com.org.share_recycled_stuff.exception.ErrorCode;
import com.org.share_recycled_stuff.mapper.ReportMapper;
import com.org.share_recycled_stuff.repository.AccountRepository;
import com.org.share_recycled_stuff.repository.PostRepository;
import com.org.share_recycled_stuff.repository.ReportRepository;
import com.org.share_recycled_stuff.service.AccountManagementService;
import com.org.share_recycled_stuff.service.NotificationService;
import com.org.share_recycled_stuff.service.PostService;
import com.org.share_recycled_stuff.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final AccountRepository accountRepository;
    private final PostRepository postRepository;
    private final ReportMapper reportMapper;
    private final NotificationService notificationService;

    @Lazy
    private final PostService postService;

    @Lazy
    private final AccountManagementService accountManagementService;

    @Override
    @Transactional
    public ReportResponse createReport(ReportRequest request, Long reporterId) {
        log.info("Creating report - type: {}, reporter: {}", request.getReportTypeCode(), reporterId);

        // Validate reporter exists
        Account reporter = accountRepository.findById(reporterId)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        ReportType reportType = ReportType.fromCode(request.getReportTypeCode());

        Reports report = reportMapper.toEntity(reporter, reportType, request.getViolationType(),
                request.getContent(), request.getEvidenceUrl());

        // Handle POST_VIOLATION
        if (reportType == ReportType.POST_VIOLATION) {
            if (request.getReportedPostId() == null) {
                throw new AppException(ErrorCode.INVALID_REPORT_TARGET);
            }

            Post post = postRepository.findById(request.getReportedPostId())
                    .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

            // Check if reporting own post
            if (post.getAccount().getId().equals(reporterId)) {
                throw new AppException(ErrorCode.CANNOT_REPORT_OWN_POST);
            }

            // Check if already reported
            if (reportRepository.existsByReporterAndPost(reporterId, request.getReportedPostId())) {
                throw new AppException(ErrorCode.ALREADY_REPORTED_POST);
            }

            report.setReportedPost(post);
        }
        // Handle USER_VIOLATION
        else if (reportType == ReportType.USER_VIOLATION) {
            if (request.getReportedAccountId() == null) {
                throw new AppException(ErrorCode.INVALID_REPORT_TARGET);
            }

            Account reportedAccount = accountRepository.findById(request.getReportedAccountId())
                    .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

            // Check if reporting yourself
            if (reportedAccount.getId().equals(reporterId)) {
                throw new AppException(ErrorCode.CANNOT_REPORT_YOURSELF);
            }

            // Check if already reported
            if (reportRepository.existsByReporterAndAccount(reporterId, request.getReportedAccountId())) {
                throw new AppException(ErrorCode.ALREADY_REPORTED_USER);
            }

            report.setReportedAccount(reportedAccount);
        } else {
            throw new AppException(ErrorCode.INVALID_REPORT_TYPE);
        }

        Reports savedReport = reportRepository.save(report);
        log.info("Report created successfully - ID: {}", savedReport.getId());

        return reportMapper.toReportResponse(savedReport);
    }

    @Override
    public Page<ReportResponse> getMyReports(Long reporterId, Pageable pageable) {
        log.info("Getting reports for reporter: {}", reporterId);

        Page<Reports> reports = reportRepository.findAllWithFilters(null, null, reporterId, pageable);
        return reports.map(reportMapper::toReportResponse);
    }

    @Override
    public Page<AdminReportDetailResponse> getAllReports(
            ReportType reportType,
            ReportStatus status,
            Pageable pageable) {
        log.info("Admin getting all reports - type: {}, status: {}", reportType, status);

        Page<Reports> reports = reportRepository.findAllWithFilters(reportType, status, null, pageable);
        return reports.map(reportMapper::toAdminReportDetailResponse);
    }

    @Override
    public AdminReportDetailResponse getReportDetail(Long reportId) {
        log.info("Getting report detail - ID: {}", reportId);

        Reports report = reportRepository.findByIdWithDetails(reportId)
                .orElseThrow(() -> new AppException(ErrorCode.REPORT_NOT_FOUND));

        return reportMapper.toAdminReportDetailResponse(report);
    }

    @Override
    @Transactional
    public AdminReportDetailResponse processReport(AdminReportActionRequest request, Long adminId) {
        log.info("Processing report - ID: {}, admin: {}, action: {}",
                request.getReportId(), adminId, request.getActionType());

        Reports report = reportRepository.findByIdWithDetails(request.getReportId())
                .orElseThrow(() -> new AppException(ErrorCode.REPORT_NOT_FOUND));

        Account admin = accountRepository.findById(adminId)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        // Execute action based on action type
        if (request.getActionType() != null) {
            executeReportAction(report, request, adminId);
        }

        // Update report status
        ReportStatus newStatus = ReportStatus.fromCode(request.getStatusCode());
        report.setStatus(newStatus);
        report.setAdminResponse(request.getAdminResponse());
        report.setProcessedBy(admin);
        report.setProcessedAt(LocalDateTime.now());

        Reports updatedReport = reportRepository.save(report);
        log.info("Report processed successfully - ID: {}", updatedReport.getId());

        String reportedObjectType = updatedReport.getReportedPost() != null ? "bài viết" : "tài khoản";
        String adminResponseText = request.getAdminResponse() != null && !request.getAdminResponse().trim().isEmpty()
                ? request.getAdminResponse()
                : "Báo cáo của bạn đã được xử lý";

        notificationService.createNotification(
                updatedReport.getReporter().getId(),
                "Báo cáo được xử lý",
                String.format("Báo cáo %s của bạn đã được xử lý. Phản hồi: %s", reportedObjectType, adminResponseText),
                13,
                3,
                "Report",
                updatedReport.getId()
        );

        return reportMapper.toAdminReportDetailResponse(updatedReport);
    }

    private void executeReportAction(Reports report, AdminReportActionRequest request, Long adminId) {
        String actionType = request.getActionType();

        if ("DELETE_POST".equals(actionType)) {
            if (report.getReportedPost() != null) {
                Long postId = report.getReportedPost().getId();
                String reason = "Bài đăng vi phạm quy định: " + request.getAdminResponse();
                log.info("Executing DELETE_POST action for postId: {}", postId);
                postService.deletePostByAdmin(postId, reason, adminId);
            } else {
                log.warn("Cannot DELETE_POST - report does not have a reported post");
            }
        } else if ("LOCK_ACCOUNT".equals(actionType)) {
            if (report.getReportedAccount() != null) {
                Long accountId = report.getReportedAccount().getId();
                String reason = "Tài khoản vi phạm quy định: " + request.getAdminResponse();
                log.info("Executing LOCK_ACCOUNT action for accountId: {}", accountId);

                LockAccountRequest lockRequest = LockAccountRequest.builder()
                        .accountId(accountId)
                        .reason(reason)
                        .durationMinutes(null)  // Permanent lock
                        .build();
                accountManagementService.lockAccount(lockRequest);
            } else {
                log.warn("Cannot LOCK_ACCOUNT - report does not have a reported account");
            }
        } else if ("NO_ACTION".equals(actionType)) {
            log.info("No action taken for report: {}", report.getId());
        } else if (actionType != null) {
            log.warn("Unknown action type: {}", actionType);
        }
    }
}

