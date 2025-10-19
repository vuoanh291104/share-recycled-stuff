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
@Schema(description = "Report response with reporter and reported object information")
public class ReportResponse {
    @Schema(description = "Report ID", example = "1")
    private Long id;

    @Schema(description = "Report type (USER/POST)", example = "POST")
    private ReportType reportType;

    @Schema(description = "Type of violation", example = "Spam")
    private String violationType;

    @Schema(description = "Report content/details", example = "Bài đăng chứa nội dung spam")
    private String content;

    @Schema(description = "Evidence URL", example = "https://example.com/evidence.jpg")
    private String evidenceUrl;

    @Schema(description = "Report status", example = "PENDING")
    private ReportStatus status;

    @Schema(description = "Report creation timestamp", example = "2024-01-01T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Reporter information")
    private ReporterInfo reporter;

    @Schema(description = "Reported object information")
    private ReportedObjectInfo reportedObject;

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
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Reported object information")
    public static class ReportedObjectInfo {
        @Schema(description = "Reported object ID", example = "123")
        private Long id;

        @Schema(description = "Object type (POST/USER)", example = "POST")
        private String type;

        @Schema(description = "Object title or name", example = "Bàn học gỗ")
        private String title;

        @Schema(description = "Object description or email", example = "Bàn học gỗ tốt...")
        private String description;
    }
}

