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
public class PostDetailResponse {
    private Long id;
    private String title;
    private String content;
    private String category;
    private BigDecimal price;
    private PostPurpose purpose;
    private PostStatus status;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Thông tin người đăng
    private UserInfo author;
    
    // Hình ảnh bài đăng
    private List<PostImageResponse> images;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String fullName;
        private String avatarUrl;
        private String email;
    }
}
