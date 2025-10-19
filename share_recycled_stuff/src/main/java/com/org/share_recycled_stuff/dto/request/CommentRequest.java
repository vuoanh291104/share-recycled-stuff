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
@Schema(description = "Request to create a comment on a post")
public class CommentRequest {

    @Schema(
            description = "Post ID to comment on",
            example = "1",
            required = true
    )
    @NotNull(message = "Post ID không được để trống")
    private Long postId;

    @Schema(
            description = "Comment content",
            example = "Món đồ này còn không ạ?",
            required = true,
            maxLength = 1000
    )
    @NotBlank(message = "Nội dung comment không được để trống")
    @Size(max = 1000, message = "Nội dung comment không được vượt quá 1000 ký tự")
    private String content;
}
