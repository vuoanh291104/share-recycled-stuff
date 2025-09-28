package com.org.share_recycled_stuff.controller.api;

import com.org.share_recycled_stuff.dto.request.EditCommentRequest;
import com.org.share_recycled_stuff.dto.response.ApiResponse;
import com.org.share_recycled_stuff.dto.response.EditCommentResponse;
import com.org.share_recycled_stuff.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

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
    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long id,
            @RequestParam Long userId,
            HttpServletRequest httpRequest
    ){
        commentService.deleteComment(id, userId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .code(HttpStatus.OK.value())
                        .message("Xoá comment thành công")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .build()
        );
    }
}
