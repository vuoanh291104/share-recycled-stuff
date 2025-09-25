package com.org.share_recycled_stuff.mapper;

import com.org.share_recycled_stuff.dto.request.CreatePostRequest;
import com.org.share_recycled_stuff.dto.response.PostResponse;
import com.org.share_recycled_stuff.entity.Account;
import com.org.share_recycled_stuff.entity.Category;
import com.org.share_recycled_stuff.entity.Post;
import com.org.share_recycled_stuff.entity.enums.PostPurpose;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {
    @Mapping(source = "accountId", target = "account")
    @Mapping(source = "categoryId", target = "category")
    @Mapping(source = "purposeCode", target = "purpose")
    @Mapping(target = "status", expression = "java(com.org.share_recycled_stuff.entity.enums.PostStatus.ACTIVE)")
    @Mapping(target = "images",ignore = true)
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
}
