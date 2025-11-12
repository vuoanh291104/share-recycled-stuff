package com.org.share_recycled_stuff.repository;

import com.org.share_recycled_stuff.dto.response.RatingStats;
import com.org.share_recycled_stuff.entity.User;
import com.org.share_recycled_stuff.entity.UserReviews;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserReviewsRepository extends JpaRepository<UserReviews, Long> {
    Page<UserReviews> findByReviewedUser_Id(Long reviewedUserId, Pageable pageable);
    Optional<UserReviews> findByReviewer_IdAndReviewedUser_Id(Long reviewerId, Long reviewedUserId);

    @Query(value = "SELECT * FROM user_reviews WHERE reviewer_id = :reviewerId AND reviewed_user_id = :reviewedUserId",
            nativeQuery = true)
    Optional<UserReviews> findWithDeleted(
            @Param("reviewerId") Long reviewerId,
            @Param("reviewedUserId") Long reviewedUserId
    );

    @Query("SELECT new com.org.share_recycled_stuff.dto.response.RatingStats(AVG(r.rating), COUNT(r)) " +
            "FROM UserReviews r WHERE r.reviewedUser.id = :userId")
    RatingStats getRatingStatsForUser(@Param("userId") Long userId);

    boolean existsByReviewerAndReviewedUser(User reviewer, User reviewedUser);
}
