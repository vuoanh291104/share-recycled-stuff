package com.org.share_recycled_stuff.dto.response;

import com.org.share_recycled_stuff.entity.enums.PostPurpose;
import com.org.share_recycled_stuff.entity.enums.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PostResponse {
    private Long id;
    private Long accountId;
    private String title;
    private String content;
    private String category;
    private BigDecimal price;
    private PostPurpose purpose;
    private PostStatus status;
    private List<PostImageResponse> images;
}
