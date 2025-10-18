package com.org.share_recycled_stuff.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(description = "Post image information with URL and display order")
public class PostImageResponse {
    @Schema(description = "Image ID", example = "1")
    private Long id;

    @Schema(description = "Image URL", example = "https://example.com/images/post1.jpg")
    private String imageUrl;

    @Schema(description = "Display order", example = "1")
    private Integer displayOrder;

}
