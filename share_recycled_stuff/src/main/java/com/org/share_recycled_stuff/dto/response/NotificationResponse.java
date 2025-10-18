package com.org.share_recycled_stuff.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Notification response with details and read status")
public class NotificationResponse {

    @Schema(description = "Notification ID", example = "1")
    private Long id;

    @Schema(description = "Notification title", example = "Bài đăng đã được duyệt")
    private String title;

    @Schema(description = "Notification content", example = "Bài đăng 'Bàn học gỗ' đã được admin phê duyệt")
    private String content;

    @Schema(description = "Notification type code", example = "2")
    private Integer notificationType;

    @Schema(description = "Notification type name", example = "POST_STATUS")
    private String notificationTypeName;

    @Schema(description = "Delivery method code (1=IN_APP, 2=EMAIL, 3=BOTH)", example = "1")
    private Integer deliveryMethod;

    @Schema(description = "Whether notification has been read", example = "false")
    private boolean isRead;

    @Schema(description = "Related entity type (e.g., POST, COMMENT)", example = "POST")
    private String relatedEntityType;

    @Schema(description = "Related entity ID", example = "123")
    private Long relatedEntityId;

    @Schema(description = "Creation timestamp", example = "2024-01-01T10:00:00")
    private LocalDateTime createdAt;
}

