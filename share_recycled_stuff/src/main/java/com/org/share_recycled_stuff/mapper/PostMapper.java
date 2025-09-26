package com.org.share_recycled_stuff.mapper;

import com.org.share_recycled_stuff.dto.request.CreatePostRequest;
import com.org.share_recycled_stuff.dto.response.PostDetailResponse;
import com.org.share_recycled_stuff.dto.response.PostImageResponse;
import com.org.share_recycled_stuff.dto.response.PostResponse;
import com.org.share_recycled_stuff.entity.Account;
import com.org.share_recycled_stuff.entity.Category;
import com.org.share_recycled_stuff.entity.Post;
import com.org.share_recycled_stuff.entity.PostImages;
import com.org.share_recycled_stuff.entity.User;
import com.org.share_recycled_stuff.entity.enums.PostPurpose;
import com.org.share_recycled_stuff.exception.AppException;
import com.org.share_recycled_stuff.exception.ErrorCode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PostMapper {
    @Mapping(source = "accountId", target = "account")
    @Mapping(source = "categoryId", target = "category")
    @Mapping(source = "purposeCode", target = "purpose")
    @Mapping(target = "status", expression = "java(com.org.share_recycled_stuff.entity.enums.PostStatus.ACTIVE)")
    Post toEntity (CreatePostRequest createPostRequest);

    @Mapping(source = "account.id", target = "accountId")
    @Mapping(source = "category.name", target = "category")
    PostResponse toResponse (Post post);

    default Account mapAccount (Long accountId) {
        if(accountId == null) return null;
        Account account = new Account();
        account.setId(accountId);
        return account;
    }

    default Category mapCategory (Long categoryId) {
        if(categoryId == null) return  null;
        Category category = new Category();
        category.setId(categoryId);
        return category;
    }

    default PostPurpose mapPostPurpose (Integer value) {
        return value == null ? null : PostPurpose.fromCode(value);
    }

    @Mapping(source = "imageUrl", target = "imageUrl")
    @Mapping(source = "displayOrder", target = "displayOrder")
    PostImageResponse toImageResponse(PostImages postImage);

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

    default List<PostImageResponse> toImageResponseList(Set<PostImages> images) {
        return images != null
                ? images.stream()
                    .map(this::toImageResponse)
                    .collect(Collectors.toList())
                : List.of();
    }

    default PostDetailResponse toPostDetailResponse(Post post) {
        if (post == null) return null;
        PostDetailResponse.UserInfo userInfo = toUserInfo(post.getAccount());
        List<PostImageResponse> images = toImageResponseList(post.getImages());
        return PostDetailResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .category(post.getCategory() != null ? post.getCategory().getName() : null)
                .price(post.getPrice())
                .purpose(post.getPurpose())
                .status(post.getStatus())
                .viewCount(post.getViewCount())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .author(userInfo)
                .images(images)
                .build();
    }
}
