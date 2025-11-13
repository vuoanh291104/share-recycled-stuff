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
@Schema(description = "DTO trả về kết quả so sánh thống kê (ví dụ: tháng này so với tháng trước)")
public class StatisticsComparisonResponse {

    @Schema(description = "Số lượng của kỳ hiện tại (ví dụ: tháng này)", example = "120")
    private long currentPeriodCount;

    @Schema(description = "Số lượng của kỳ trước đó (ví dụ: tháng trước)", example = "100")
    private long previousPeriodCount;

    @Schema(description = "Tỷ lệ phần trăm thay đổi (dương là tăng, âm là giảm)", example = "20.0")
    private double percentageChange;
}
