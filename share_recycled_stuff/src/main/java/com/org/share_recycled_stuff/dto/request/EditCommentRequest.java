package com.org.share_recycled_stuff.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to edit an existing comment")
public class EditCommentRequest {
    @Schema(
            description = "New comment content",
            example = "Nội dung đã được cập nhật",
            required = true
    )
    @NotBlank(message = "Nội dung mới không được để trống")
    private String content;
}
