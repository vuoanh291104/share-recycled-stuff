package com.org.share_recycled_stuff.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
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
            description = "Target account ID to receive notification. Bỏ trống nếu gửi toàn hệ thống.",
            example = "1"
    )
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
            description = "Delivery method (1=IN_APP, 2=EMAIL, 3=BOTH). Khi broadcastToAll=true, chỉ cho phép 1 (IN_APP).",
            example = "1",
            defaultValue = "1"
    )
    @Builder.Default
    private Integer deliveryMethod = 1;

    @Schema(
            description = "Set true để gửi thông báo tới toàn bộ tài khoản trong hệ thống",
            example = "false",
            defaultValue = "false"
    )
    @Builder.Default
    private Boolean broadcastToAll = false;

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

    @JsonIgnore
    @AssertTrue(message = "Account ID không được để trống khi không gửi toàn hệ thống")
    public boolean isValidTargetSelection() {
        return Boolean.TRUE.equals(broadcastToAll) || accountId != null;
    }

    @JsonIgnore
    @AssertTrue(message = "Khi gửi broadcast toàn hệ thống, chỉ cho phép deliveryMethod = 1 (IN_APP), không hỗ trợ gửi email")
    public boolean isValidDeliveryMethodForBroadcast() {
        // Nếu không phải broadcast, cho phép tất cả delivery methods
        if (!Boolean.TRUE.equals(broadcastToAll)) {
            return true;
        }
        // Nếu là broadcast, chỉ cho phép deliveryMethod = 1 (IN_APP) hoặc null (sẽ default thành 1)
        return deliveryMethod == null || deliveryMethod == 1;
    }
}

