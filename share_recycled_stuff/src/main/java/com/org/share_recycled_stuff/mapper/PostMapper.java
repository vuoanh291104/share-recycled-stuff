package com.org.share_recycled_stuff.mapper;

import com.org.share_recycled_stuff.dto.request.PostRequest;
import com.org.share_recycled_stuff.dto.response.PostDetailResponse;
import com.org.share_recycled_stuff.dto.response.PostResponse;
import com.org.share_recycled_stuff.entity.Account;
import com.org.share_recycled_stuff.entity.Category;
import com.org.share_recycled_stuff.entity.Post;
import com.org.share_recycled_stuff.entity.User;
import com.org.share_recycled_stuff.entity.enums.PostPurpose;
import com.org.share_recycled_stuff.exception.AppException;
import com.org.share_recycled_stuff.exception.ErrorCode;
import org.mapstruct.*;


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

    @Mapping(source = "account", target = "author", qualifiedByName = "toUserInfo")
    @Mapping(source = "images", target = "images")
    @Mapping(source = "category.name", target = "category")
    PostDetailResponse toPostDetailResponse(Post post);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "id", target = "id")
    @Mapping(source = "account.id", target = "accountId")
    @Mapping(source = "status", target = "status")
    PostResponse toDeletedPost(Post post);
}
