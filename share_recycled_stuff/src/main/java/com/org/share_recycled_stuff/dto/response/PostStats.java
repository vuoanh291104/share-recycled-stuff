package com.org.share_recycled_stuff.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Statistics related to Posts, matching the PostStatus Enum")
public class PostStats {

    @Schema(description = "Total number of posts created", example = "200")
    private long totalPosts;

    @Schema(description = "Number of posts currently active (ACTIVE)", example = "180")
    private long activePosts;

    @Schema(description = "Number of posts pending for edit (EDIT)", example = "5")
    private long editRequestPosts;

    @Schema(description = "Number of posts marked as deleted (DELETED)", example = "15")
    private long deletedPosts;
}
