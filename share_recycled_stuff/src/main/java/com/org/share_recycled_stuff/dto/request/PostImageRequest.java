package com.org.share_recycled_stuff.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostImageRequest {
    private Long id;
    private String imageUrl;
    private Integer displayOrder;
}
