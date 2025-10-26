package com.org.share_recycled_stuff.service;

import com.org.share_recycled_stuff.dto.request.ReviewRequest;
import com.org.share_recycled_stuff.dto.response.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserReviewsService {

    ReviewResponse createReview(ReviewRequest request, Long accountId);

    Page<ReviewResponse> getReviewsForUser(Long userId, Pageable pageable);

    ReviewResponse updateReview(Long reviewId, ReviewRequest request, Long accountId);

    void deleteReview(Long reviewId, Long accountId);
}
