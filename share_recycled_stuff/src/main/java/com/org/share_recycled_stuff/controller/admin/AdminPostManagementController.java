package com.org.share_recycled_stuff.controller.admin;

import com.org.share_recycled_stuff.config.CustomUserDetail;
import com.org.share_recycled_stuff.dto.request.AdminPostReviewRequest;
import com.org.share_recycled_stuff.dto.request.BulkDeletePostRequest;
import com.org.share_recycled_stuff.dto.response.AdminPostDetailResponse;
import com.org.share_recycled_stuff.dto.response.ApiResponse;
import com.org.share_recycled_stuff.dto.response.BulkDeletePostResponse;
import com.org.share_recycled_stuff.dto.response.PostStatisticsResponse;
import com.org.share_recycled_stuff.entity.enums.PostStatus;
import com.org.share_recycled_stuff.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/posts")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminPostManagementController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<Page<AdminPostDetailResponse>> getAllPosts(
            @RequestParam(required = false) Integer statusCode,
            @RequestParam(required = false) Long accountId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") boolean includeDeleted,
            Pageable pageable) {
        log.info("Admin request to get all posts - statusCode: {}, accountId: {}, categoryId: {}, search: {}, includeDeleted: {}",
                statusCode, accountId, categoryId, search, includeDeleted);

        PostStatus status = null;
        if (statusCode != null) {
            status = PostStatus.fromCode(statusCode);
        }

        Page<AdminPostDetailResponse> posts = postService.getAllPostsForAdmin(
                status, accountId, categoryId, search, includeDeleted, pageable
        );

        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<AdminPostDetailResponse>> getPostDetail(@PathVariable Long postId) {
        log.info("Admin request to get post detail for postId: {}", postId);

        AdminPostDetailResponse post = postService.getPostDetailForAdmin(postId);

        return ResponseEntity.ok(
                ApiResponse.success("Post detail retrieved successfully", post)
        );
    }

    @PutMapping("/review")
    public ResponseEntity<ApiResponse<AdminPostDetailResponse>> reviewPost(
            @Valid @RequestBody AdminPostReviewRequest request,
            @AuthenticationPrincipal CustomUserDetail userDetail) {
        log.info("Admin request to review post: {}", request.getPostId());

        AdminPostDetailResponse updatedPost = postService.reviewPost(request, userDetail.getAccountId());

        return ResponseEntity.ok(
                ApiResponse.success("Post reviewed successfully", updatedPost)
        );
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<AdminPostDetailResponse>> deletePost(
            @PathVariable Long postId,
            @RequestParam(required = false) String reason,
            @AuthenticationPrincipal CustomUserDetail userDetail) {
        log.info("Admin request to delete post: {}, reason: {}", postId, reason);

        AdminPostDetailResponse deletedPost = postService.deletePostByAdmin(postId, reason, userDetail.getAccountId());

        return ResponseEntity.ok(
                ApiResponse.success("Post deleted successfully", deletedPost)
        );
    }

    @PostMapping("/bulk/delete")
    public ResponseEntity<ApiResponse<BulkDeletePostResponse>> bulkDeletePosts(
            @Valid @RequestBody BulkDeletePostRequest request,
            @AuthenticationPrincipal CustomUserDetail userDetail) {
        log.info("Admin request to bulk delete {} posts", request.getPostIds().size());

        BulkDeletePostResponse response = postService.bulkDeletePosts(request, userDetail.getAccountId());

        return ResponseEntity.ok(
                ApiResponse.success("Bulk delete operation completed", response)
        );
    }

    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<PostStatisticsResponse>> getPostStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Admin request to get post statistics - startDate: {}, endDate: {}", startDate, endDate);

        PostStatisticsResponse statistics = postService.getPostStatistics(startDate, endDate);

        return ResponseEntity.ok(
                ApiResponse.success("Statistics retrieved successfully", statistics)
        );
    }

    @PutMapping("/{postId}/restore")
    public ResponseEntity<ApiResponse<AdminPostDetailResponse>> restorePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetail userDetail) {
        log.info("Admin request to restore post: {}", postId);

        AdminPostDetailResponse restoredPost = postService.restorePost(postId, userDetail.getAccountId());

        return ResponseEntity.ok(
                ApiResponse.success("Post restored successfully", restoredPost)
        );
    }
}

