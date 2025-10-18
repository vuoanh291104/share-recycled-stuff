package com.org.share_recycled_stuff.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Result of bulk delete operation with success/failure counts and details")
public class BulkDeletePostResponse {
    @Schema(description = "Total number of posts requested to delete", example = "10")
    private Integer totalRequested;

    @Schema(description = "Number of successfully deleted posts", example = "8")
    private Integer successCount;

    @Schema(description = "Number of failed deletions", example = "2")
    private Integer failedCount;

    @Schema(description = "List of successfully deleted post IDs", example = "[1, 2, 3, 4, 5, 6, 7, 8]")
    private List<Long> successfulPostIds;

    @Schema(description = "List of errors for failed deletions")
    private List<PostOperationError> errors;

    @Schema(description = "Map of post IDs to titles for reference")
    private Map<Long, String> postTitles;

    @Schema(description = "Processing timestamp", example = "2024-01-01T10:00:00")
    private LocalDateTime processedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Error details for a failed post deletion")
    public static class PostOperationError {
        @Schema(description = "Post ID that failed to delete", example = "9")
        private Long postId;

        @Schema(description = "Error message", example = "Post not found or already deleted")
        private String errorMessage;
    }
}
