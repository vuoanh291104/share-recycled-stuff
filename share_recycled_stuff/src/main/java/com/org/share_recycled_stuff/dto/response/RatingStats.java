package com.org.share_recycled_stuff.dto.response;

import java.math.BigDecimal;

public class RatingStats {

    private Double average;
    private Long count;

    public RatingStats(Double average, Long count) {
        this.average = average;
        this.count = count;
    }

    public BigDecimal getAverage() {
        return average != null ? BigDecimal.valueOf(average) : BigDecimal.ZERO;
    }

    public Long getCount() {
        return count != null ? count : 0L;
    }
}
