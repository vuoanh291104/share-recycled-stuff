package com.org.share_recycled_stuff.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditCommentResponse {
    private Long id;
    private String content;
    private boolean isEdited;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long accountId;
    private Long postId;
}
