package com.org.share_recycled_stuff.dto.response;

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
@Schema(description = "Comment response with author information")
public class CommentResponse {
    @Schema(description = "Comment ID", example = "1")
    private Long id;

    @Schema(description = "Post ID that this comment belongs to", example = "1")
    private Long postId;

    @Schema(description = "Comment content", example = "Đồ này còn không ạ?")
    private String content;

    @Schema(description = "Whether the comment has been edited", example = "false")
    private boolean isEdited;

    @Schema(description = "Comment creation timestamp", example = "2024-01-01T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2024-01-01T10:30:00")
    private LocalDateTime updatedAt;

    @Schema(description = "Comment author information")
    private UserInfo author;

    @Schema(description = "Parent comment ID if this is a reply (null for top-level comments)", example = "null", nullable = true)
    private Long parentCommentId;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Comment author information")
    public static class UserInfo {
        @Schema(description = "User account ID", example = "1")
        private Long id;

        @Schema(description = "User full name", example = "Nguyễn Văn A")
        private String fullName;

        @Schema(description = "User avatar URL", example = "https://example.com/avatar.jpg")
        private String avatarUrl;

        @Schema(description = "User email", example = "nguyenvana@example.com")
        private String email;
    }
}
