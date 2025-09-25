package com.org.share_recycled_stuff.controller.api;

import com.org.share_recycled_stuff.config.CustomUserDetail;
import com.org.share_recycled_stuff.dto.request.CreatePostRequest;
import com.org.share_recycled_stuff.dto.response.ApiResponse;
import com.org.share_recycled_stuff.dto.response.PostResponse;
import com.org.share_recycled_stuff.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {
    private final PostService postService;

    @PostMapping
    ResponseEntity<ApiResponse<PostResponse>> newPost (
            @Valid
            @RequestBody CreatePostRequest request,
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
}
