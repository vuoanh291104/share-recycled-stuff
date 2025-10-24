package com.org.share_recycled_stuff.service.impl;

import com.org.share_recycled_stuff.dto.request.ReviewRequest;
import com.org.share_recycled_stuff.dto.response.ReviewResponse;
import com.org.share_recycled_stuff.entity.Account;
import com.org.share_recycled_stuff.entity.User;
import com.org.share_recycled_stuff.entity.UserReviews;
import com.org.share_recycled_stuff.exception.AppException;
import com.org.share_recycled_stuff.exception.ErrorCode;
import com.org.share_recycled_stuff.repository.AccountRepository;
import com.org.share_recycled_stuff.repository.UserRepository;
import com.org.share_recycled_stuff.repository.UserReviewsRepository;
import com.org.share_recycled_stuff.service.UserReviewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserReviewsServiceImpl implements UserReviewsService {

    private final UserReviewsRepository userReviewsRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    @Override
    @Transactional
    public ReviewResponse createReview(ReviewRequest request, Long accountId) {

        Account currentAccount = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        User reviewedUser = userRepository.findById(request.getReviewedUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy người dùng với ID: " + request.getReviewedUserId()));

        if (currentAccount.getId().equals(reviewedUser.getId())) {
            throw new AppException(ErrorCode.CANNOT_DELETE_REVIEW);
        }
        Optional<UserReviews> existingReviewOpt = userReviewsRepository
                .findWithDeleted(currentAccount.getId(), reviewedUser.getId());

        UserReviews reviewToSave;

        if (existingReviewOpt.isPresent()) {
            reviewToSave = existingReviewOpt.get();
            reviewToSave.setRating(request.getRating());
            reviewToSave.setComment(request.getComment());
            reviewToSave.setDeletedAt(null);
        } else {
            reviewToSave = UserReviews.builder()
                    .reviewer(currentAccount)
                    .reviewedUser(reviewedUser)
                    .rating(request.getRating())
                    .comment(request.getComment())
                    .build();
        }

        UserReviews savedReview = userReviewsRepository.save(reviewToSave);
        return ReviewResponse.fromEntity(savedReview);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsForUser(Long userId, Pageable pageable) {

        if (!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy người dùng với ID: " + userId);
        }

        Page<UserReviews> reviewsPage = userReviewsRepository.findByReviewedUser_Id(userId, pageable);
        return reviewsPage.map(ReviewResponse::fromEntity);
    }

    @Override
    @Transactional
    public ReviewResponse updateReview(Long reviewId, ReviewRequest request, Long accountId) {

        Account currentAccount = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        UserReviews existingReview = userReviewsRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND, "Không tìm thấy đánh giá với ID: " + reviewId));

        if (!existingReview.getReviewer().getId().equals(currentAccount.getId())) {
            throw new AppException(ErrorCode.CANNOT_DELETE_REVIEW);
        }

        existingReview.setRating(request.getRating());
        existingReview.setComment(request.getComment());

        UserReviews updatedReview = userReviewsRepository.save(existingReview);
        return ReviewResponse.fromEntity(updatedReview);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId, Long accountId) {

        Account currentAccount = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        UserReviews existingReview = userReviewsRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND, "Không tìm thấy đánh giá với ID: " + reviewId));

        if (!existingReview.getReviewer().getId().equals(currentAccount.getId())) {
            throw new AppException(ErrorCode.CANNOT_DELETE_REVIEW);
        }

        userReviewsRepository.delete(existingReview);
    }
}
