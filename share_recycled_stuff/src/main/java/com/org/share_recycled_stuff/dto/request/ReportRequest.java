package com.org.share_recycled_stuff.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to report a user or post for policy violations")
public class ReportRequest {

    @Schema(
            description = "Report type code (1=USER, 2=POST)",
            example = "2",
            required = true,
            allowableValues = {"1", "2"}
    )
    @NotNull(message = "Loại báo cáo không được để trống")
    private Integer reportTypeCode;

    @Schema(
            description = "Reported post ID (required if reportTypeCode = 2)",
            example = "123"
    )
    private Long reportedPostId;

    @Schema(
            description = "Reported user account ID (required if reportTypeCode = 1)",
            example = "45"
    )
    private Long reportedAccountId;

    @Schema(
            description = "Type of violation (e.g., Spam, Inappropriate Content, Fraud)",
            example = "Nội dung không phù hợp",
            required = true
    )
    @NotBlank(message = "Loại vi phạm không được để trống")
    private String violationType;

    @Schema(
            description = "Detailed report content explaining the violation",
            example = "Bài đăng chứa nội dung vi phạm chính sách cộng đồng",
            required = true
    )
    @NotBlank(message = "Nội dung báo cáo không được để trống")
    private String content;

    @Schema(
            description = "URL to evidence (screenshot, etc.)",
            example = "https://example.com/evidence.jpg"
    )
    private String evidenceUrl;
}

