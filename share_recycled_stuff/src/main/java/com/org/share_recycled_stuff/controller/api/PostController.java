package com.org.share_recycled_stuff.controller.api;

import com.org.share_recycled_stuff.config.CustomUserDetail;
import com.org.share_recycled_stuff.dto.request.PostRequest;
import com.org.share_recycled_stuff.dto.response.ApiResponse;
import com.org.share_recycled_stuff.dto.response.CommentResponse;
import com.org.share_recycled_stuff.dto.response.PostDetailResponse;
import com.org.share_recycled_stuff.dto.response.PostResponse;
import com.org.share_recycled_stuff.service.PostService;
import com.org.share_recycled_stuff.utils.IpUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@Tag(name = "Posts", description = "Post management endpoints")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {
    private final PostService postService;

    @Operation(
            summary = "Create new post",
            description = "Create a new post with title, content, category, price, purpose, and images"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Post created successfully. Returns ApiResponse<PostResponse>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid post data. Returns ApiResponse with validation errors."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token required. Returns ApiResponse with error."
            )
    })
    @PreAuthorize("hasAnyRole('CUSTOMER', 'PROXY_SELLER')")
    @PostMapping
    ResponseEntity<ApiResponse<PostResponse>> newPost(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Post details including title, content, category, price, and images",
                    required = true
            )
            @Valid
            @RequestBody PostRequest request,
            @AuthenticationPrincipal CustomUserDetail userDetail,
            HttpServletRequest httpRequest
    ) {
        PostResponse postResponse = postService.createPost(request, userDetail.getAccountId());
        return ResponseEntity.ok(
                ApiResponse.<PostResponse>builder()
                        .code(HttpStatus.CREATED.value())
                        .message("Đăng bài thành công")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .result(postResponse)
                        .build()
        );
    }

    @Operation(
            summary = "Get user's posts",
            description = "Retrieve all posts created by a specific user with pagination and sorting"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved user posts. Returns ApiResponse<Page<PostDetailResponse>>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "User not found. Returns ApiResponse with error."
            )
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<PostDetailResponse>>> getUserPosts(
            @Parameter(
                    description = "User ID to get posts from",
                    required = true,
                    example = "1"
            )
            @PathVariable Long userId,
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDir,
            HttpServletRequest httpRequest
    ) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<PostDetailResponse> posts = postService.getUserPosts(userId, pageable);
        return ResponseEntity.ok(
                ApiResponse.<Page<PostDetailResponse>>builder()
                        .code(HttpStatus.OK.value())
                        .message("Lấy danh sách bài đăng thành công")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .result(posts)
                        .build()
        );
    }

    @Operation(
            summary = "Get my posts",
            description = "Retrieve all posts created by the authenticated user with pagination and sorting"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved current user's posts. Returns ApiResponse<Page<PostDetailResponse>>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token required. Returns ApiResponse with error."
            )
    })
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<PostDetailResponse>>> getMyPosts(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDir,
            @AuthenticationPrincipal CustomUserDetail userDetail,
            HttpServletRequest httpRequest
    ) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Long accountId = userDetail.getAccountId();
        Page<PostDetailResponse> posts = postService.getMyPosts(accountId, pageable);
        return ResponseEntity.ok(
                ApiResponse.<Page<PostDetailResponse>>builder()
                        .code(HttpStatus.OK.value())
                        .message("Lấy danh sách bài đăng của bạn thành công")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .result(posts)
                        .build()
        );
    }

    @Operation(
            summary = "Update post",
            description = "Update an existing post (title, content, category, price, images, etc.)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Post updated successfully. Returns ApiResponse<PostResponse>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Post not found. Returns ApiResponse with error."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Not the post owner. Returns ApiResponse with error."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token required. Returns ApiResponse with error."
            )
    })
    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<PostResponse>> updatePost(
            @Parameter(description = "Post ID to update", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated post details",
                    required = true
            )
            @Valid
            @RequestBody PostRequest postRequest,
            @AuthenticationPrincipal CustomUserDetail userDetail,
            HttpServletRequest httpRequest
    ) {

        PostResponse postResponse = postService.updatePost(postRequest, userDetail.getAccountId(), id);
        return ResponseEntity.ok(
                ApiResponse.<PostResponse>builder()
                        .code(HttpStatus.OK.value())
                        .message("Cập nhật bài đăng thành công")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .result(postResponse)
                        .build());
    }

    @Operation(
            summary = "Delete post",
            description = "Soft delete a post (mark as deleted, not permanently removed)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Post deleted successfully. Returns ApiResponse<PostResponse>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Post not found. Returns ApiResponse with error."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Not the post owner. Returns ApiResponse with error."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token required. Returns ApiResponse with error."
            )
    })
    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<PostResponse>> deletePost(
            @Parameter(description = "Post ID to delete", required = true, example = "1")
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetail userDetail,
            HttpServletRequest httpRequest
    ) {

        PostResponse postResponse = postService.softDelete(userDetail.getAccountId(), id);

        return ResponseEntity.ok(
                ApiResponse.<PostResponse>builder()
                        .code(HttpStatus.OK.value())
                        .message("Xóa thành công")
                        .path(httpRequest.getRequestURI())
                        .result(postResponse)
                        .build()
        );

    }

    @Operation(
            summary = "Get post comments",
            description = "Retrieve all top-level comments for a specific post (replies are accessible via /api/comments/{parentCommentId}/replies)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved comments. Returns ApiResponse<List<CommentResponse>>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Post not found. Returns ApiResponse with error."
            )
    })
    @GetMapping("/{id}/comments")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getPostComments(
            @Parameter(description = "Post ID to get comments for", required = true, example = "1")
            @PathVariable Long id,
            HttpServletRequest httpRequest
    ) {
        List<CommentResponse> comments = postService.getPostComments(id);

        return ResponseEntity.ok(
                ApiResponse.<List<CommentResponse>>builder()
                        .code(HttpStatus.OK.value())
                        .message("Lấy danh sách bình luận thành công")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .result(comments)
                        .build()
        );
    }

    @Operation(
            summary = "Get main page feed",
            description = "Get personalized post feed based on user's location using GeoIP lookup from client IP address. " +
                    "Posts are automatically sorted by distance from user (nearest first). " +
                    "If location cannot be determined, posts are sorted by creation date."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved post feed. Returns ApiResponse<Page<PostDetailResponse>> with posts sorted by distance."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token required. Returns ApiResponse with error."
            )
    })
    @GetMapping("/main-page")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'PROXY_SELLER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<PostDetailResponse>>> getMainPageFeed(
            @Parameter(
                    description = "Page number (0-indexed)",
                    example = "0"
            )
            @RequestParam(defaultValue = "0") int page,
            @Parameter(
                    description = "Number of posts per page",
                    example = "10"
            )
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest httpRequest
    ) {
        String clientIp = IpUtils.extractClientIp(httpRequest);
        Pageable pageable = PageRequest.of(page, size);
        Page<PostDetailResponse> posts = postService.getMainPageFeed(clientIp, pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<PostDetailResponse>>builder()
                        .code(HttpStatus.OK.value())
                        .message("Lấy trang chủ thành công")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .result(posts)
                        .build()
        );
    }
}
