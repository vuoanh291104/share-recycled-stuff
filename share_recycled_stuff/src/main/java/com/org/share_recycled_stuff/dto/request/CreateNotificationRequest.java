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
@Schema(description = "Request to create a notification for a user")
public class CreateNotificationRequest {

    @Schema(
            description = "Target account ID to receive notification",
            example = "1",
            required = true
    )
    @NotNull(message = "Account ID không được để trống")
    private Long accountId;

    @Schema(
            description = "Notification title",
            example = "Bài đăng của bạn đã được duyệt",
            required = true
    )
    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    @Schema(
            description = "Notification content/message",
            example = "Bài đăng 'Bàn học gỗ' của bạn đã được admin phê duyệt",
            required = true
    )
    @NotBlank(message = "Nội dung không được để trống")
    private String content;

    @Schema(
            description = "Notification type code (1=SYSTEM, 2=POST_STATUS, 3=COMMENT, 4=DELEGATION, etc.)",
            example = "2",
            required = true
    )
    @NotNull(message = "Loại thông báo không được để trống")
    private Integer notificationType;

    @Schema(
            description = "Delivery method (1=IN_APP, 2=EMAIL, 3=BOTH)",
            example = "1",
            defaultValue = "1"
    )
    @Builder.Default
    private Integer deliveryMethod = 1;

    @Schema(
            description = "Related entity type (e.g., POST, COMMENT, DELEGATION)",
            example = "POST"
    )
    private String relatedEntityType;

    @Schema(
            description = "Related entity ID",
            example = "123"
    )
    private Long relatedEntityId;
}

