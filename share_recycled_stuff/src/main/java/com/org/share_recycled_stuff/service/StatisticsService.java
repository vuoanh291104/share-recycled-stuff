package com.org.share_recycled_stuff.service;

import com.org.share_recycled_stuff.dto.request.StatisticsFilterRequest;
import com.org.share_recycled_stuff.dto.response.StatisticsComparisonResponse;
import com.org.share_recycled_stuff.dto.response.StatisticsReportResponse;

public interface StatisticsService {
    StatisticsReportResponse getStatistics(StatisticsFilterRequest filters);

    StatisticsComparisonResponse getPostComparison();
}
