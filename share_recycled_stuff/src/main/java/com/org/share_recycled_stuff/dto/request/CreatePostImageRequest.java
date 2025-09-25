package com.org.share_recycled_stuff.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreatePostImageRequest {
    private String imageUrl;
    private Integer displayOrder;
}
