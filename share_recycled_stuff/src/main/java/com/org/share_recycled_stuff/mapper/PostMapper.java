package com.org.share_recycled_stuff.mapper;

import com.org.share_recycled_stuff.dto.request.PostRequest;
import com.org.share_recycled_stuff.dto.response.*;
import com.org.share_recycled_stuff.entity.*;
import com.org.share_recycled_stuff.entity.enums.PostPurpose;
import com.org.share_recycled_stuff.exception.AppException;
import com.org.share_recycled_stuff.exception.ErrorCode;
import org.mapstruct.*;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring", uses = {PostImageMapper.class})
public interface PostMapper {
    @Mapping(source = "accountId", target = "account")
    @Mapping(source = "categoryId", target = "category")
    @Mapping(source = "purposeCode", target = "purpose")
    @Mapping(target = "status", expression = "java(com.org.share_recycled_stuff.entity.enums.PostStatus.ACTIVE)")
    Post toEntity(PostRequest postRequest);

    @Mapping(source = "account.id", target = "accountId")
    @Mapping(source = "category.name", target = "category")
    PostResponse toResponse(Post post);

    @Mapping(source = "categoryId", target = "category")
    @Mapping(source = "purposeCode", target = "purpose")
    void updatePost(PostRequest postRequest, @MappingTarget Post post);

    default Account mapAccount(Long accountId) {
        if (accountId == null) return null;
        Account account = new Account();
        account.setId(accountId);
        return account;
    }

    default Category mapCategory(Long categoryId) {
        if (categoryId == null) return null;
        Category category = new Category();
        category.setId(categoryId);
        return category;
    }

    default PostPurpose mapPostPurpose(Integer value) {
        return value == null ? null : PostPurpose.fromCode(value);
    }

    @Named("toUserInfo")
    default PostDetailResponse.UserInfo toUserInfo(Account account) {
        if (account == null || account.getUser() == null) {
            throw new AppException(ErrorCode.DATA_INTEGRITY_ERROR);
        }

        User user = account.getUser();

        return PostDetailResponse.UserInfo.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .email(account.getEmail())
                .build();
    }

    @Named("toLikedUserIDs")
    default Set<Long> mapReactionsToUserIDs(Post post) {
        if (post.getReactions() == null) return Set.of();
        return post.getReactions().stream()
                .filter(r -> Boolean.TRUE.equals(r.getReactionType())) // chỉ type=true
                .map(r -> r.getAccount().getUser().getId())
                .collect(Collectors.toSet());
    }
    @Mapping(source = "account", target = "author", qualifiedByName = "toUserInfo")
    @Mapping(source = "images", target = "images")
    @Mapping(source = "category.name", target = "category")
    @Mapping(source = ".", target = "likedUserIDs", qualifiedByName = "toLikedUserIDs")
    PostDetailResponse toPostDetailResponse(Post post);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "id", target = "id")
    @Mapping(source = "account.id", target = "accountId")
    @Mapping(source = "status", target = "status")
    PostResponse toDeletedPost(Post post);

    // Admin mapping methods
    @Named("toAuthorInfo")
    default AdminPostDetailResponse.AuthorInfo toAuthorInfo(Account account) {
        if (account == null || account.getUser() == null) {
            throw new AppException(ErrorCode.DATA_INTEGRITY_ERROR);
        }

        User user = account.getUser();

        return AdminPostDetailResponse.AuthorInfo.builder()
                .userId(user.getId())
                .accountId(account.getId())
                .fullName(user.getFullName())
                .email(account.getEmail())
                .phone(user.getPhone())
                .avatarUrl(user.getAvatarUrl())
                .isLocked(account.isLocked())
                .build();
    }

    @Mapping(source = "account", target = "author", qualifiedByName = "toAuthorInfo")
    @Mapping(source = "images", target = "images")
    @Mapping(source = "category.name", target = "category")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "comments", target = "commentCount", qualifiedByName = "commentsSize")
    @Mapping(source = "reactions", target = "reactionCount", qualifiedByName = "reactionsSize")
    AdminPostDetailResponse toAdminPostDetailResponse(Post post);

    @Named("commentsSize")
    default Integer commentsSize(java.util.Set<?> comments) {
        return comments == null ? 0 : comments.size();
    }

    @Named("reactionsSize")
    default Integer reactionsSize(java.util.Set<?> reactions) {
        return reactions == null ? 0 : reactions.size();
    }

    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "purpose", target = "purposeName")
    @Mapping(source = "account.user.city", target = "location")
    @Mapping(source = "account", target = "author", qualifiedByName = "toAuthorSummary")
    @Mapping(source = "images", target = "thumbnailUrl", qualifiedByName = "getThumbnailUrl")
    @Mapping(source = "comments", target = "commentCount", qualifiedByName = "commentsSize")
    @Mapping(source = "reactions", target = "reactionCount", qualifiedByName = "reactionsSize")
    PostSearchResponse toPostSearchResponse(Post post);

    @Named("toAuthorSummary")
    default AuthorSummaryResponse toAuthorSummary(Account account) {
        if (account == null || account.getUser() == null) {
            return null;
        }
        User user = account.getUser();
        AuthorSummaryResponse author = new AuthorSummaryResponse();
        author.setId(user.getId());
        author.setDisplayName(user.getFullName());
        author.setAvatarUrl(user.getAvatarUrl());
        return author;
    }

    @Named("getThumbnailUrl")
    default String getThumbnailUrl(java.util.Set<PostImages> images) {
        if (images == null || images.isEmpty()) {
            return null;
        }
        Optional<PostImages> firstImage = images.stream().findFirst();
        return firstImage.map(PostImages::getImageUrl).orElse(null);
    }
}
