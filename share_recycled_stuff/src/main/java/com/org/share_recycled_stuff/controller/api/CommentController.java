package com.org.share_recycled_stuff.controller.api;

import com.org.share_recycled_stuff.config.CustomUserDetail;
import com.org.share_recycled_stuff.dto.request.CommentRequest;
import com.org.share_recycled_stuff.dto.request.ReplyCommentRequest;
import com.org.share_recycled_stuff.dto.request.EditCommentRequest;
import com.org.share_recycled_stuff.dto.response.ApiResponse;
import com.org.share_recycled_stuff.dto.response.CommentResponse;
import com.org.share_recycled_stuff.dto.response.EditCommentResponse;
import com.org.share_recycled_stuff.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {
    @Autowired
    private CommentService commentService;

    private final CommentService commentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'PROXY_SELLER', 'ADMIN')")
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal CustomUserDetail userDetail,
            HttpServletRequest httpRequest) {

        CommentResponse response = commentService.createComment(request, userDetail.getAccountId());

        return ResponseEntity.ok(
                ApiResponse.<CommentResponse>builder()
                        .code(HttpStatus.CREATED.value())
                        .message("Tạo comment thành công")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .result(response)
                        .build()
        );
    }

    @PostMapping("/reply")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'PROXY_SELLER', 'ADMIN')")
    public ResponseEntity<ApiResponse<CommentResponse>> replyToComment(
            @Valid @RequestBody ReplyCommentRequest request,
            @AuthenticationPrincipal CustomUserDetail userDetail,
            HttpServletRequest httpRequest) {

        CommentResponse response = commentService.replyToComment(request, userDetail.getAccountId());

        return ResponseEntity.ok(
                ApiResponse.<CommentResponse>builder()
                        .code(HttpStatus.CREATED.value())
                        .message("Trả lời comment thành công")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .result(response)
                        .build()
        );
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EditCommentResponse>> editComment(
            @PathVariable Long id,
            @Valid @RequestBody EditCommentRequest request,
            @RequestParam Long userId,
            HttpServletRequest httpRequest
    ) {
        EditCommentResponse response = commentService.editComment(id, request, userId);
        return ResponseEntity.ok(
                ApiResponse.<EditCommentResponse>builder()
                        .code(HttpStatus.OK.value())
                        .message("Sửa comment thành công")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .result(response)
                        .build()
        );
    }
}
