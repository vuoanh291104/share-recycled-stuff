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
public class ReportRequest {
    
    @NotNull(message = "Loại báo cáo không được để trống")
    private Integer reportTypeCode;  // 2: POST_VIOLATION, 3: USER_VIOLATION
    
    private Long reportedPostId;  // Required if reportTypeCode = 2
    
    private Long reportedAccountId;  // Required if reportTypeCode = 3
    
    @NotBlank(message = "Loại vi phạm không được để trống")
    private String violationType;  // Spam, Nội dung không phù hợp, Lừa đảo, etc.
    
    @NotBlank(message = "Nội dung báo cáo không được để trống")
    private String content;
    
    private String evidenceUrl;  // Optional
}

