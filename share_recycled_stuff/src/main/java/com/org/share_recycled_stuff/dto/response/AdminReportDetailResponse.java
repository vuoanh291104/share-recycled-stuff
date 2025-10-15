package com.org.share_recycled_stuff.dto.response;

import com.org.share_recycled_stuff.entity.enums.ReportStatus;
import com.org.share_recycled_stuff.entity.enums.ReportType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminReportDetailResponse {
    private Long id;
    private ReportType reportType;
    private String violationType;
    private String content;
    private String evidenceUrl;
    private ReportStatus status;
    private String adminResponse;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    
    // Thông tin người báo cáo
    private ReporterInfo reporter;
    
    // Thông tin bài đăng bị báo cáo (nếu có)
    private ReportedPostInfo reportedPost;
    
    // Thông tin tài khoản bị báo cáo (nếu có)
    private ReportedAccountInfo reportedAccount;
    
    // Thông tin admin xử lý (nếu có)
    private ProcessedByInfo processedBy;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReporterInfo {
        private Long id;
        private String fullName;
        private String email;
        private String phoneNumber;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportedPostInfo {
        private Long id;
        private String title;
        private String content;
        private String authorName;
        private String status;
        private LocalDateTime createdAt;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportedAccountInfo {
        private Long id;
        private String fullName;
        private String email;
        private String phoneNumber;
        private Boolean isLocked;
        private LocalDateTime createdAt;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProcessedByInfo {
        private Long id;
        private String fullName;
        private String email;
    }
}

