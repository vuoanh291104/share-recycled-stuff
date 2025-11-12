package com.org.share_recycled_stuff.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Request body để đánh dấu sản phẩm đã bán và nhập giá bán")
public class MarkAsSoldRequest {

    @Schema(
            description = "Giá bán thực tế của sản phẩm (ví dụ: 150000.00)",
            example = "150000",
            required = true
    )
    @NotNull(message = "Giá bán không được để trống")
    @Positive(message = "Giá bán phải là một số dương")
    private BigDecimal soldPrice;
}
