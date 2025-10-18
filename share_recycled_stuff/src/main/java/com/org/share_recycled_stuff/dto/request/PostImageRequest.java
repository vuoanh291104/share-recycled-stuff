package com.org.share_recycled_stuff.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Post image information with URL and display order")
public class PostImageRequest {
    @Schema(
            description = "Image ID (for updates)",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Image URL",
            example = "https://example.com/images/post1.jpg",
            required = true
    )
    private String imageUrl;

    @Schema(
            description = "Display order for sorting images",
            example = "1"
    )
    private Integer displayOrder;
}
