package com.org.share_recycled_stuff.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Post statistics including counts by status, views, and time periods")
public class PostStatisticsResponse {
    @Schema(description = "Total number of posts", example = "1000")
    private Long totalPosts;

    @Schema(description = "Number of active posts", example = "750")
    private Long activePosts;

    @Schema(description = "Number of posts pending edit", example = "50")
    private Long editPosts;

    @Schema(description = "Number of deleted posts", example = "200")
    private Long deletedPosts;

    @Schema(description = "Total view count across all posts", example = "50000")
    private Long totalViewCount;

    @Schema(description = "Total comments across all posts", example = "5000")
    private Long totalComments;

    @Schema(description = "Total reactions across all posts", example = "10000")
    private Long totalReactions;

    @Schema(description = "Posts created today", example = "15")
    private Long postsToday;

    @Schema(description = "Posts created this week", example = "100")
    private Long postsThisWeek;

    @Schema(description = "Posts created this month", example = "350")
    private Long postsThisMonth;

    @Schema(description = "Posts in custom date range (if filtered)", example = "250")
    private Long postsInDateRange;

    @Schema(description = "Filter start date", example = "2024-01-01")
    private LocalDate filterStartDate;

    @Schema(description = "Filter end date", example = "2024-01-31")
    private LocalDate filterEndDate;
}
