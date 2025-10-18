package com.org.share_recycled_stuff.controller.api;

import com.org.share_recycled_stuff.config.CustomUserDetail;
import com.org.share_recycled_stuff.dto.request.CommentRequest;
import com.org.share_recycled_stuff.dto.request.EditCommentRequest;
import com.org.share_recycled_stuff.dto.request.ReplyCommentRequest;
import com.org.share_recycled_stuff.dto.response.ApiResponse;
import com.org.share_recycled_stuff.dto.response.CommentResponse;
import com.org.share_recycled_stuff.service.CommentService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@Tag(name = "Comments", description = "Comment management endpoints")
@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(
            summary = "Create comment",
            description = "Create a new top-level comment on a post"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Comment created successfully. Returns ApiResponse<CommentResponse>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Post not found. Returns ApiResponse with error."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token required. Returns ApiResponse with error."
            )
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'PROXY_SELLER', 'ADMIN')")
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Comment details including post ID and content",
                    required = true
            )
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

    @Operation(
            summary = "Reply to comment",
            description = "Create a reply (child comment) to an existing comment"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Reply created successfully. Returns ApiResponse<CommentResponse>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Parent comment not found. Returns ApiResponse with error."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token required. Returns ApiResponse with error."
            )
    })
    @PostMapping("/reply")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'PROXY_SELLER', 'ADMIN')")
    public ResponseEntity<ApiResponse<CommentResponse>> replyToComment(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Reply details including parent comment ID and content",
                    required = true
            )
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

    @Operation(
            summary = "Edit comment",
            description = "Update the content of an existing comment (only by the comment owner)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Comment updated successfully. Returns ApiResponse<CommentResponse>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Comment not found. Returns ApiResponse with error."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Not the comment owner. Returns ApiResponse with error."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token required. Returns ApiResponse with error."
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CommentResponse>> editComment(
            @Parameter(description = "Comment ID to edit", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated comment content",
                    required = true
            )
            @Valid @RequestBody EditCommentRequest request,
            @AuthenticationPrincipal CustomUserDetail userDetail,
            HttpServletRequest httpRequest
    ) {
        CommentResponse response = commentService.editComment(id, request, userDetail.getAccountId());
        return ResponseEntity.ok(
                ApiResponse.<CommentResponse>builder()
                        .code(HttpStatus.OK.value())
                        .message("Sửa comment thành công")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .result(response)
                        .build()
        );
    }

    @Operation(
            summary = "Delete comment",
            description = "Delete a comment (only by the comment owner or admin)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Comment deleted successfully. Returns ApiResponse<Void>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Comment not found. Returns ApiResponse with error."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Not the comment owner. Returns ApiResponse with error."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token required. Returns ApiResponse with error."
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @Parameter(description = "Comment ID to delete", required = true, example = "1")
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetail userDetail,
            HttpServletRequest httpRequest
    ) {
        commentService.deleteComment(id, userDetail.getAccountId());

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .code(HttpStatus.OK.value())
                        .message("Xoá comment thành công")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .build()
        );
    }

    @Operation(
            summary = "Get comment replies",
            description = "Retrieve paginated list of all replies (child comments) for a specific parent comment. " +
                    "Results are sorted by creation date (oldest first)."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved comment replies. Returns ApiResponse<Page<CommentResponse>>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Parent comment not found. Returns ApiResponse with error."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token required. Returns ApiResponse with error."
            )
    })
    @GetMapping("/{parentCommentId}/replies")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'PROXY_SELLER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> getCommentReplies(
            @Parameter(
                    description = "ID of the parent comment to get replies for",
                    required = true,
                    example = "123"
            )
            @PathVariable Long parentCommentId,
            @Parameter(
                    description = "Page number (0-indexed)",
                    example = "0"
            )
            @RequestParam(defaultValue = "0") int page,
            @Parameter(
                    description = "Number of replies per page",
                    example = "10"
            )
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest httpRequest
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CommentResponse> replies = commentService.getCommentReplies(parentCommentId, pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<CommentResponse>>builder()
                        .code(HttpStatus.OK.value())
                        .message("Lấy danh sách trả lời thành công")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .result(replies)
                        .build()
        );
    }
}
