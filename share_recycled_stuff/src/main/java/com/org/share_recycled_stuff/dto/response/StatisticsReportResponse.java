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
@Schema(description = "Main response DTO, aggregating Post and Delegation statistics")
public class StatisticsReportResponse {

    @Schema(description = "Detailed statistics for delegation requests (orders)")
    private DelegationStats delegationStats;

    @Schema(description = "Detailed statistics for posts")
    private PostStats postStats;

    @Schema(description = "Detailed statistics for Sales and Revenue")
    private SalesAndRevenueStats salesAndRevenueStats;
}
