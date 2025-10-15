package com.org.share_recycled_stuff.mapper;

import com.org.share_recycled_stuff.dto.response.AdminReportDetailResponse;
import com.org.share_recycled_stuff.dto.response.ReportResponse;
import com.org.share_recycled_stuff.entity.Account;
import com.org.share_recycled_stuff.entity.Post;
import com.org.share_recycled_stuff.entity.Reports;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ReportMapper {

    // Map to ReportResponse for Customer/Proxy Seller
    @Mapping(source = "reporter", target = "reporter", qualifiedByName = "toReporterInfo")
    @Mapping(source = ".", target = "reportedObject", qualifiedByName = "toReportedObjectInfo")
    ReportResponse toReportResponse(Reports report);

    // Map to AdminReportDetailResponse for Admin
    @Mapping(source = "reporter", target = "reporter", qualifiedByName = "toAdminReporterInfo")
    @Mapping(source = "reportedPost", target = "reportedPost", qualifiedByName = "toReportedPostInfo")
    @Mapping(source = "reportedAccount", target = "reportedAccount", qualifiedByName = "toReportedAccountInfo")
    @Mapping(source = "processedBy", target = "processedBy", qualifiedByName = "toProcessedByInfo")
    AdminReportDetailResponse toAdminReportDetailResponse(Reports report);

    // Helper methods for ReportResponse
    @Named("toReporterInfo")
    default ReportResponse.ReporterInfo toReporterInfo(Account reporter) {
        if (reporter == null || reporter.getUser() == null) {
            return null;
        }
        return ReportResponse.ReporterInfo.builder()
                .id(reporter.getId())
                .fullName(reporter.getUser().getFullName())
                .email(reporter.getEmail())
                .build();
    }

    @Named("toReportedObjectInfo")
    default ReportResponse.ReportedObjectInfo toReportedObjectInfo(Reports report) {
        if (report.getReportedPost() != null) {
            Post post = report.getReportedPost();
            return ReportResponse.ReportedObjectInfo.builder()
                    .id(post.getId())
                    .type("POST")
                    .title(post.getTitle())
                    .description(post.getContent() != null && post.getContent().length() > 100
                            ? post.getContent().substring(0, 100) + "..."
                            : post.getContent())
                    .build();
        } else if (report.getReportedAccount() != null) {
            Account account = report.getReportedAccount();
            return ReportResponse.ReportedObjectInfo.builder()
                    .id(account.getId())
                    .type("USER")
                    .title(account.getUser().getFullName())
                    .description(account.getEmail())
                    .build();
        }
        return null;
    }

    // Helper methods for AdminReportDetailResponse
    @Named("toAdminReporterInfo")
    default AdminReportDetailResponse.ReporterInfo toAdminReporterInfo(Account reporter) {
        if (reporter == null || reporter.getUser() == null) {
            return null;
        }
        return AdminReportDetailResponse.ReporterInfo.builder()
                .id(reporter.getId())
                .fullName(reporter.getUser().getFullName())
                .email(reporter.getEmail())
                .phoneNumber(reporter.getUser().getPhone())
                .build();
    }

    @Named("toReportedPostInfo")
    default AdminReportDetailResponse.ReportedPostInfo toReportedPostInfo(Post post) {
        if (post == null) {
            return null;
        }
        return AdminReportDetailResponse.ReportedPostInfo.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .authorName(post.getAccount() != null && post.getAccount().getUser() != null 
                        ? post.getAccount().getUser().getFullName() : null)
                .status(post.getStatus() != null ? post.getStatus().toString() : null)
                .createdAt(post.getCreatedAt())
                .build();
    }

    @Named("toReportedAccountInfo")
    default AdminReportDetailResponse.ReportedAccountInfo toReportedAccountInfo(Account account) {
        if (account == null || account.getUser() == null) {
            return null;
        }
        return AdminReportDetailResponse.ReportedAccountInfo.builder()
                .id(account.getId())
                .fullName(account.getUser().getFullName())
                .email(account.getEmail())
                .phoneNumber(account.getUser().getPhone())
                .isLocked(account.isLocked())
                .createdAt(account.getCreatedAt())
                .build();
    }

    @Named("toProcessedByInfo")
    default AdminReportDetailResponse.ProcessedByInfo toProcessedByInfo(Account processedBy) {
        if (processedBy == null || processedBy.getUser() == null) {
            return null;
        }
        return AdminReportDetailResponse.ProcessedByInfo.builder()
                .id(processedBy.getId())
                .fullName(processedBy.getUser().getFullName())
                .email(processedBy.getEmail())
                .build();
    }
}

