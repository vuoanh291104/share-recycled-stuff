package com.org.share_recycled_stuff.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostStatisticsResponse {
    private Long totalPosts;
    private Long activePosts;
    private Long editPosts;
    private Long deletedPosts;
    private Long totalViewCount;
    private Long totalComments;
    private Long totalReactions;
    private Long postsToday;
    private Long postsThisWeek;
    private Long postsThisMonth;

    private Long postsInDateRange;
    private LocalDate filterStartDate;
    private LocalDate filterEndDate;
}

