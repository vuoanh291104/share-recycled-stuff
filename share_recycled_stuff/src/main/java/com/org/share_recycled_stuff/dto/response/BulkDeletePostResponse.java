package com.org.share_recycled_stuff.dto.response;

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
public class BulkDeletePostResponse {
    private Integer totalRequested;
    private Integer successCount;
    private Integer failedCount;
    private List<Long> successfulPostIds;
    private List<PostOperationError> errors;
    private Map<Long, String> postTitles;
    private LocalDateTime processedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostOperationError {
        private Long postId;
        private String errorMessage;
    }
}

