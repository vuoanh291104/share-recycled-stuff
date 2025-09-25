package com.org.share_recycled_stuff.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Valid
public class CreatePostRequest {
    private Long id;
    private Long accountId;

    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    private String content;

    @NotNull(message = "Phải chọn phân loại sản phẩm")
    private Long categoryId;

    @Min(value = 0, message = "Giá phải lớn hơn hoặc bằng 0")
    private BigDecimal price;

    private Integer purposeCode;

    private List<CreatePostImageRequest> images;
}
