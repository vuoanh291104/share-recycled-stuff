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
@Schema(description = "Admin request to process/resolve a report")
public class AdminReportActionRequest {

    @Schema(
            description = "Report ID to process",
            example = "1",
            required = true
    )
    @NotNull(message = "ID báo cáo không được để trống")
    private Long reportId;

    @Schema(
            description = "New status code (2=PROCESSING, 3=RESOLVED, 4=REJECTED)",
            example = "3",
            required = true,
            allowableValues = {"2", "3", "4"}
    )
    @NotNull(message = "Trạng thái không được để trống")
    private Integer statusCode;

    @Schema(
            description = "Admin's response/resolution note",
            example = "Đã xử lý vi phạm và khóa tài khoản",
            required = true
    )
    @NotBlank(message = "Phản hồi của admin không được để trống")
    private String adminResponse;

    @Schema(
            description = "Action type taken (DELETE_POST, LOCK_ACCOUNT, NO_ACTION)",
            example = "LOCK_ACCOUNT"
    )
    private String actionType;
}

