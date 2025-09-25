package com.org.share_recycled_stuff.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostImageResponse {
    private Long id;
    private String imageUrl;
    private Integer displayOrder;
}
