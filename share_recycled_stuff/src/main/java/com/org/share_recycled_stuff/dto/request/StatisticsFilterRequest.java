package com.org.share_recycled_stuff.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO containing filter parameters for the statistics API")
public class StatisticsFilterRequest {

    @Schema(description = "Filter by Proxy Seller ID (leave empty for global statistics)",
            example = "123")
    private Long proxySellerId;

    @Schema(description = "Filter by day", example = "25")
    private Integer day;

    @Schema(description = "Filter by month", example = "10")
    private Integer month;

    @Schema(description = "Filter by year", example = "2025")
    private Integer year;
}
