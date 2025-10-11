package com.org.share_recycled_stuff.dto.response;

import com.org.share_recycled_stuff.entity.enums.PostPurpose;
import com.org.share_recycled_stuff.entity.enums.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminPostDetailResponse {
    private Long id;
    private String title;
    private String content;
    private String category;
    private Long categoryId;
    private BigDecimal price;
    private PostPurpose purpose;
    private PostStatus status;
    private String adminReviewComment;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    // Thông tin người đăng
    private AuthorInfo author;

    // Hình ảnh bài đăng
    private List<PostImageResponse> images;

    // Thống kê
    private Integer commentCount;
    private Integer reactionCount;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthorInfo {
        private Long userId;
        private Long accountId;
        private String fullName;
        private String email;
        private String phone;
        private String avatarUrl;
        private Boolean isLocked;
    }
}

