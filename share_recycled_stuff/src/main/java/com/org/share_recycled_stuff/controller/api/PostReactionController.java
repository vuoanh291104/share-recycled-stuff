package com.org.share_recycled_stuff.controller.api;

import com.org.share_recycled_stuff.config.CustomUserDetail;
import com.org.share_recycled_stuff.dto.response.ApiResponse;
import com.org.share_recycled_stuff.service.PostReactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@Tag(name = "Reactions", description = "Post reaction (like/unlike) endpoints")
@RestController
@RequestMapping("/api/reactions")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAnyRole('CUSTOMER', 'PROXY_SELLER')")
public class PostReactionController {

    private final PostReactionService postReactionService;

    @Operation(
            summary = "Toggle post reaction",
            description = "Like or unlike a specific post. Toggles the current user's reaction state."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Reaction toggled successfully. Returns ApiResponse<Map<String, Object>>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Post not found. Returns ApiResponse."
            )
    })
    @PostMapping("/post/{postId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> reactToPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetail userDetail,
            HttpServletRequest request
    ) {
        Long currentUserId = userDetail.getAccountId();
        log.info("User {} toggling reaction for post {}", currentUserId, postId);

        boolean newReactionState = postReactionService.toggleReaction(postId, currentUserId);

        String message = newReactionState ? "Đã thích bài viết" : "Đã bỏ thích bài viết";
        Map<String, Object> responseData = Map.of(
                "postId", postId,
                "liked", newReactionState
        );

        return ResponseEntity.ok(
                ApiResponse.<Map<String, Object>>builder()
                        .code(HttpStatus.OK.value())
                        .message(message)
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now().toString())
                        .result(responseData)
                        .build()
        );
    }
}
