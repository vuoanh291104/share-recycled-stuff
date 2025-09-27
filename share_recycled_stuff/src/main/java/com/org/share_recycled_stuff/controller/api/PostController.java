package com.org.share_recycled_stuff.controller.api;

import com.org.share_recycled_stuff.config.CustomUserDetail;
import com.org.share_recycled_stuff.dto.request.PostRequest;
import com.org.share_recycled_stuff.dto.response.ApiResponse;
import com.org.share_recycled_stuff.dto.response.PostResponse;
import com.org.share_recycled_stuff.dto.response.PostDetailResponse;
import com.org.share_recycled_stuff.service.PostService;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {
    private final PostService postService;

    @PreAuthorize("hasAnyRole('CUSTOMER', 'PROXY_SELLER')")
    @PostMapping
    ResponseEntity<ApiResponse<PostResponse>> newPost (
            @Valid
            @RequestBody PostRequest request,
            @AuthenticationPrincipal CustomUserDetail userDetail,
            HttpServletRequest httpRequest
            ){
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

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<PostDetailResponse>>> getUserPosts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
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

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<PostDetailResponse>>> getMyPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
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

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<PostResponse>> updatePost (
            @PathVariable Long id,
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
}
