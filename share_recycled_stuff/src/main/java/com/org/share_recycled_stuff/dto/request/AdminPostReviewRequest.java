package com.org.share_recycled_stuff.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminPostReviewRequest {
    @NotNull(message = "Post ID không được để trống")
    private Long postId;

    @NotNull(message = "Status không được để trống")
    private Integer statusCode; // 1: ACTIVE, 2: EDIT, 3: DELETED

    private String adminReviewComment;
}

