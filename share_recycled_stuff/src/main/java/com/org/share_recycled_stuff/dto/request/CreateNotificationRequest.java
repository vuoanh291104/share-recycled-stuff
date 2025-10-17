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
public class CreateNotificationRequest {

    @NotNull(message = "Account ID không được để trống")
    private Long accountId;

    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    @NotBlank(message = "Nội dung không được để trống")
    private String content;

    @NotNull(message = "Loại thông báo không được để trống")
    private Integer notificationType;

    @Builder.Default
    private Integer deliveryMethod = 1;

    private String relatedEntityType;

    private Long relatedEntityId;
}

