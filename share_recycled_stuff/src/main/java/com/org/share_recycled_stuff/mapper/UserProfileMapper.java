package com.org.share_recycled_stuff.mapper;

import com.org.share_recycled_stuff.dto.response.UserProfileResponse;
import com.org.share_recycled_stuff.dto.response.UserReviewResponse;
import com.org.share_recycled_stuff.entity.Account;
import com.org.share_recycled_stuff.entity.User;
import com.org.share_recycled_stuff.entity.UserReviews;
import com.org.share_recycled_stuff.entity.UserRole;
import com.org.share_recycled_stuff.entity.enums.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    @Mapping(source = "account.id", target = "accountId")
    @Mapping(source = "id", target = "userId")
    @Mapping(source = "account.email", target = "email")
    @Mapping(source = "fullName", target = "fullName")
    @Mapping(source = "phone", target = "phoneNumber")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "ward", target = "ward")
    @Mapping(source = "district", target = "district")
    @Mapping(source = "city", target = "city")
    @Mapping(source = "idCard", target = "idCard")
    @Mapping(source = "avatarUrl", target = "avatarUrl")
    @Mapping(source = "bio", target = "bio")
    @Mapping(source = "ratingAverage", target = "ratingAverage")
    @Mapping(source = "totalRatings", target = "totalRatings")
    @Mapping(target = "roles", expression = "java(mapRoles(user.getAccount()))")
    @Mapping(target = "reviews", expression = "java(mapReviews(user))")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    UserProfileResponse toUserProfileResponse(User user);

    default Set<Role> mapRoles(Account account) {
        if (account == null || account.getRoles() == null) {
            return null;
        }
        return account.getRoles().stream()
                .map(UserRole::getRoleType)
                .collect(Collectors.toSet());
    }

    default List<UserReviewResponse> mapReviews(User user) {
        if (user == null || user.getUserReviews() == null) {
            return null;
        }
        return user.getUserReviews().stream()
                .map(this::mapReview)
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .toList();
    }

    default UserReviewResponse mapReview(UserReviews review) {
        if (review == null) {
            return null;
        }

        Account reviewerAccount = review.getReviewer();
        return UserReviewResponse.builder()
                .reviewId(review.getId())
                .reviewerAccountId(reviewerAccount != null ? reviewerAccount.getId() : null)
                .reviewerName(reviewerAccount != null && reviewerAccount.getUser() != null
                        ? reviewerAccount.getUser().getFullName()
                        : null)
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
