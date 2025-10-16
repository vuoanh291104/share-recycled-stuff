package com.org.share_recycled_stuff.dto.request;

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
public class AdminReportActionRequest {
    
    @NotNull(message = "ID báo cáo không được để trống")
    private Long reportId;
    
    @NotNull(message = "Trạng thái không được để trống")
    private Integer statusCode;  // 2: PROCESSING, 3: RESOLVED
    
    @NotBlank(message = "Phản hồi của admin không được để trống")
    private String adminResponse;
    
    private String actionType;  // "DELETE_POST", "LOCK_ACCOUNT", "NO_ACTION"
}

