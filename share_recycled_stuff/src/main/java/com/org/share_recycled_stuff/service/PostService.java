package com.org.share_recycled_stuff.service;

import com.org.share_recycled_stuff.dto.request.AdminPostReviewRequest;
import com.org.share_recycled_stuff.dto.request.BulkDeletePostRequest;
import com.org.share_recycled_stuff.dto.request.PostRequest;
import com.org.share_recycled_stuff.dto.response.*;
import com.org.share_recycled_stuff.entity.enums.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface PostService {
    Page<PostDetailResponse> getUserPosts(Long userId, Pageable pageable);

    Page<PostDetailResponse> getMyPosts(Long accountId, Pageable pageable);

    PostResponse createPost(PostRequest postRequest, Long accountId);

    PostResponse updatePost(PostRequest postRequest, Long accountId, Long postId);

    PostResponse softDelete(Long accountID, Long postId);

    List<CommentResponse> getPostComments(Long postId);

    // Admin methods
    Page<AdminPostDetailResponse> getAllPostsForAdmin(
            PostStatus status,
            Long accountId,
            Long categoryId,
            String search,
            boolean includeDeleted,
            Pageable pageable
    );

    AdminPostDetailResponse getPostDetailForAdmin(Long postId);

    AdminPostDetailResponse reviewPost(AdminPostReviewRequest request, Long adminId);

    AdminPostDetailResponse deletePostByAdmin(Long postId, String reason, Long adminId);

    BulkDeletePostResponse bulkDeletePosts(BulkDeletePostRequest request, Long adminId);

    AdminPostDetailResponse restorePost(Long postId, Long adminId);

    PostStatisticsResponse getPostStatistics(LocalDate startDate, LocalDate endDate);
}
