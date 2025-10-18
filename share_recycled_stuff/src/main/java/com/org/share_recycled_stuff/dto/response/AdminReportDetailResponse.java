package com.org.share_recycled_stuff.dto.response;

import com.org.share_recycled_stuff.entity.enums.ReportStatus;
import com.org.share_recycled_stuff.entity.enums.ReportType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Detailed report information for admin including reporter, reported entity, and resolution")
public class AdminReportDetailResponse {
    @Schema(description = "Report ID", example = "1")
    private Long id;

    @Schema(description = "Report type (USER/POST)", example = "POST")
    private ReportType reportType;

    @Schema(description = "Type of violation", example = "Spam")
    private String violationType;

    @Schema(description = "Report content", example = "Bài đăng chứa nội dung spam")
    private String content;

    @Schema(description = "Evidence URL", example = "https://example.com/evidence.jpg")
    private String evidenceUrl;

    @Schema(description = "Report status", example = "PENDING")
    private ReportStatus status;

    @Schema(description = "Admin's response", example = "Đã xử lý và khóa tài khoản")
    private String adminResponse;

    @Schema(description = "Report creation timestamp", example = "2024-01-01T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Report processed timestamp", example = "2024-01-02T09:00:00")
    private LocalDateTime processedAt;

    @Schema(description = "Reporter information")
    private ReporterInfo reporter;

    @Schema(description = "Reported post information (if applicable)")
    private ReportedPostInfo reportedPost;

    @Schema(description = "Reported account information (if applicable)")
    private ReportedAccountInfo reportedAccount;

    @Schema(description = "Admin who processed the report")
    private ProcessedByInfo processedBy;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Reporter information")
    public static class ReporterInfo {
        @Schema(description = "Reporter ID", example = "1")
        private Long id;

        @Schema(description = "Reporter full name", example = "Nguyễn Văn A")
        private String fullName;

        @Schema(description = "Reporter email", example = "reporter@example.com")
        private String email;

        @Schema(description = "Reporter phone number", example = "0123456789")
        private String phoneNumber;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Reported post information")
    public static class ReportedPostInfo {
        @Schema(description = "Post ID", example = "123")
        private Long id;

        @Schema(description = "Post title", example = "Bàn học gỗ")
        private String title;

        @Schema(description = "Post content", example = "Bàn học gỗ tốt...")
        private String content;

        @Schema(description = "Post author name", example = "Nguyễn Văn B")
        private String authorName;

        @Schema(description = "Post status", example = "ACTIVE")
        private String status;

        @Schema(description = "Post creation timestamp", example = "2024-01-01T08:00:00")
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Reported account information")
    public static class ReportedAccountInfo {
        @Schema(description = "Account ID", example = "5")
        private Long id;

        @Schema(description = "Full name", example = "Trần Văn C")
        private String fullName;

        @Schema(description = "Email", example = "reported@example.com")
        private String email;

        @Schema(description = "Phone number", example = "0987654321")
        private String phoneNumber;

        @Schema(description = "Whether account is locked", example = "true")
        private Boolean isLocked;

        @Schema(description = "Account creation timestamp", example = "2023-12-01T10:00:00")
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Admin who processed the report")
    public static class ProcessedByInfo {
        @Schema(description = "Admin ID", example = "10")
        private Long id;

        @Schema(description = "Admin full name", example = "Admin Nguyễn")
        private String fullName;

        @Schema(description = "Admin email", example = "admin@example.com")
        private String email;
    }
}
