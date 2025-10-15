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
public class ReportResponse {
    private Long id;
    private ReportType reportType;
    private String violationType;
    private String content;
    private String evidenceUrl;
    private ReportStatus status;
    private LocalDateTime createdAt;
    
    // Thông tin người báo cáo
    private ReporterInfo reporter;
    
    // Thông tin đối tượng bị báo cáo
    private ReportedObjectInfo reportedObject;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReporterInfo {
        private Long id;
        private String fullName;
        private String email;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportedObjectInfo {
        private Long id;
        private String type;  // "POST" or "USER"
        private String title;  // Post title or User name
        private String description;  // Post content preview or User email
    }
}

