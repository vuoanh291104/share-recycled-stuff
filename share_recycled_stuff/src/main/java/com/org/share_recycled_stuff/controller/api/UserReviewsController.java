package com.org.share_recycled_stuff.controller.api;

import com.org.share_recycled_stuff.config.CustomUserDetail;
import com.org.share_recycled_stuff.dto.request.ReviewRequest;
import com.org.share_recycled_stuff.dto.response.ApiResponse;
import com.org.share_recycled_stuff.dto.response.ReviewResponse;
import com.org.share_recycled_stuff.service.UserReviewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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

@Tag(name = "User Reviews", description = "Endpoints for managing user reviews")
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class UserReviewsController {

    private final UserReviewsService userReviewsService;

    @Operation(
            summary = "Create a new review",
            description = "Allows an authenticated user to post a review for another user"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Review created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input (e.g., rating out of range, already reviewed)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token required"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden (e.g., user trying to review themselves)"
            )
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'PROXY_SELLER')")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal CustomUserDetail userDetail,
            HttpServletRequest httpRequest) {

        ReviewResponse response = userReviewsService.createReview(request, userDetail.getAccountId());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<ReviewResponse>builder()
                        .code(HttpStatus.CREATED.value())
                        .message("Review created successfully")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .result(response)
                        .build()
        );
    }

    @Operation(
            summary = "Get all reviews for a specific user",
            description = "Retrieves a paginated list of reviews for a given userId (the user being reviewed)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Reviews retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getReviewsForUser(
            @Parameter(description = "ID of the user to fetch reviews for", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDir,
            HttpServletRequest httpRequest) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ReviewResponse> responsePage = userReviewsService.getReviewsForUser(userId, pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<ReviewResponse>>builder()
                        .code(HttpStatus.OK.value())
                        .message("Reviews retrieved successfully")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .result(responsePage)
                        .build()
        );
    }

    @Operation(
            summary = "Update an existing review",
            description = "Allows the original author of a review to update its content (rating and/or comment)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Review updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden (user is not the author)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Review not found"
            )
    })
    @PutMapping("/{reviewId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'PROXY_SELLER')")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @Parameter(description = "ID of the review to update", required = true, example = "1")
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal CustomUserDetail userDetail,
            HttpServletRequest httpRequest) {

        ReviewResponse response = userReviewsService.updateReview(reviewId, request, userDetail.getAccountId());

        return ResponseEntity.ok(
                ApiResponse.<ReviewResponse>builder()
                        .code(HttpStatus.OK.value())
                        .message("Review updated successfully")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .result(response)
                        .build()
        );
    }

    @Operation(
            summary = "Delete an existing review",
            description = "Allows the original author of a review to delete it"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Review deleted successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden (user is not the author)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Review not found"
            )
    })
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'PROXY_SELLER')")
    public ResponseEntity<ApiResponse<Object>> deleteReview(
            @Parameter(description = "ID of the review to delete", required = true, example = "1")
            @PathVariable Long reviewId,
            @AuthenticationPrincipal CustomUserDetail userDetail,
            HttpServletRequest httpRequest) {

        userReviewsService.deleteReview(reviewId, userDetail.getAccountId());

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .code(HttpStatus.OK.value())
                        .message("Review deleted successfully")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .result(null)
                        .build()
        );
    }
}
