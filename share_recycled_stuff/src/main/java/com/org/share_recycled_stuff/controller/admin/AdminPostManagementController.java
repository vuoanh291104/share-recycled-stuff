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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Admin - Post Management", description = "Admin endpoints for reviewing, deleting, and managing posts")
@RestController
@RequestMapping("/api/admin/posts")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminPostManagementController {

    private final PostService postService;

    @Operation(
            summary = "Get all posts (Admin)",
            description = "Retrieve all posts with filters for admin review and management including status, account, category, search term, and soft-deleted posts"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved posts. Returns Page<AdminPostDetailResponse> directly (no ApiResponse wrapper)."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Admin role required."
            )
    })
    @GetMapping
    public ResponseEntity<Page<AdminPostDetailResponse>> getAllPosts(
            @Parameter(description = "Filter by post status code (0=PENDING, 1=ACTIVE, 2=REJECTED, etc.)", example = "1")
            @RequestParam(required = false) Integer statusCode,
            @Parameter(description = "Filter by account ID (post author)", example = "1")
            @RequestParam(required = false) Long accountId,
            @Parameter(description = "Filter by category ID", example = "1")
            @RequestParam(required = false) Long categoryId,
            @Parameter(description = "Search term for post title/content", example = "bàn học")
            @RequestParam(required = false) String search,
            @Parameter(description = "Include soft-deleted posts", example = "false")
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

    @Operation(
            summary = "Get post details (Admin)",
            description = "Retrieve detailed information about a specific post including author info, images, comments count, etc."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved post details. Returns ApiResponse<AdminPostDetailResponse>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Post not found. Returns ApiResponse with error."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Admin role required."
            )
    })
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<AdminPostDetailResponse>> getPostDetail(
            @Parameter(description = "Post ID to retrieve", required = true, example = "1")
            @PathVariable Long postId) {
        log.info("Admin request to get post detail for postId: {}", postId);

        AdminPostDetailResponse post = postService.getPostDetailForAdmin(postId);

        return ResponseEntity.ok(
                ApiResponse.success("Post detail retrieved successfully", post)
        );
    }

    @Operation(
            summary = "Review post (Admin)",
            description = "Admin reviews a post and updates its status (approve/reject)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Post reviewed successfully. Returns ApiResponse<AdminPostDetailResponse>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Post not found. Returns ApiResponse with error."
            )
    })
    @PutMapping("/review")
    public ResponseEntity<ApiResponse<AdminPostDetailResponse>> reviewPost(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Post review details including post ID, status, and optional feedback",
                    required = true
            )
            @Valid @RequestBody AdminPostReviewRequest request,
            @AuthenticationPrincipal CustomUserDetail userDetail) {
        log.info("Admin request to review post: {}", request.getPostId());

        AdminPostDetailResponse updatedPost = postService.reviewPost(request, userDetail.getAccountId());

        return ResponseEntity.ok(
                ApiResponse.success("Post reviewed successfully", updatedPost)
        );
    }

    @Operation(
            summary = "Delete post (Admin)",
            description = "Admin soft-deletes a post with optional reason. Post will be marked as deleted."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Post deleted successfully. Returns ApiResponse<AdminPostDetailResponse>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Post not found. Returns ApiResponse with error."
            )
    })
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<AdminPostDetailResponse>> deletePost(
            @Parameter(description = "Post ID to delete", required = true, example = "1")
            @PathVariable Long postId,
            @Parameter(description = "Reason for deletion (optional)", example = "Violates terms of service")
            @RequestParam(required = false) String reason,
            @AuthenticationPrincipal CustomUserDetail userDetail) {
        log.info("Admin request to delete post: {}, reason: {}", postId, reason);

        AdminPostDetailResponse deletedPost = postService.deletePostByAdmin(postId, reason, userDetail.getAccountId());

        return ResponseEntity.ok(
                ApiResponse.success("Post deleted successfully", deletedPost)
        );
    }

    @Operation(
            summary = "Bulk delete posts (Admin)",
            description = "Admin bulk-deletes multiple posts at once. Returns summary of successful and failed operations."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Bulk delete operation completed. Returns ApiResponse<BulkDeletePostResponse> with success/failure count."
            )
    })
    @PostMapping("/bulk/delete")
    public ResponseEntity<ApiResponse<BulkDeletePostResponse>> bulkDeletePosts(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "List of post IDs to delete and optional reason",
                    required = true
            )
            @Valid @RequestBody BulkDeletePostRequest request,
            @AuthenticationPrincipal CustomUserDetail userDetail) {
        log.info("Admin request to bulk delete {} posts", request.getPostIds().size());

        BulkDeletePostResponse response = postService.bulkDeletePosts(request, userDetail.getAccountId());

        return ResponseEntity.ok(
                ApiResponse.success("Bulk delete operation completed", response)
        );
    }

    @Operation(
            summary = "Get post statistics (Admin)",
            description = "Retrieve post statistics including counts by status, date range filtering, and trends"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved statistics. Returns ApiResponse<PostStatisticsResponse>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Admin role required."
            )
    })
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<PostStatisticsResponse>> getPostStatistics(
            @Parameter(description = "Start date for statistics (ISO format: YYYY-MM-DD)", example = "2024-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date for statistics (ISO format: YYYY-MM-DD)", example = "2024-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Admin request to get post statistics - startDate: {}, endDate: {}", startDate, endDate);

        PostStatisticsResponse statistics = postService.getPostStatistics(startDate, endDate);

        return ResponseEntity.ok(
                ApiResponse.success("Statistics retrieved successfully", statistics)
        );
    }

    @Operation(
            summary = "Restore deleted post (Admin)",
            description = "Restore a soft-deleted post, making it visible again"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Post restored successfully. Returns ApiResponse<AdminPostDetailResponse>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Post not found or not deleted. Returns ApiResponse with error."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Admin role required."
            )
    })
    @PutMapping("/{postId}/restore")
    public ResponseEntity<ApiResponse<AdminPostDetailResponse>> restorePost(
            @Parameter(description = "Post ID to restore", required = true, example = "1")
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetail userDetail) {
        log.info("Admin request to restore post: {}", postId);

        AdminPostDetailResponse restoredPost = postService.restorePost(postId, userDetail.getAccountId());

        return ResponseEntity.ok(
                ApiResponse.success("Post restored successfully", restoredPost)
        );
    }
}

