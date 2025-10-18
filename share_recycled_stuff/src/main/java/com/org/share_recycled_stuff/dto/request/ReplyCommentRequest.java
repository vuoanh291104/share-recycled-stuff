package com.org.share_recycled_stuff.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to reply to an existing comment")
public class ReplyCommentRequest {

    @Schema(
            description = "Parent comment ID to reply to",
            example = "123",
            required = true
    )
    @NotNull(message = "Parent comment ID không được để trống")
    private Long parentCommentId;

    @Schema(
            description = "Reply content",
            example = "Cảm ơn bạn đã hỏi! Còn hàng nha",
            required = true,
            maxLength = 1000
    )
    @NotBlank(message = "Nội dung reply không được để trống")
    @Size(max = 1000, message = "Nội dung reply không được vượt quá 1000 ký tự")
    private String content;
}
