package com.org.share_recycled_stuff.mapper;

import com.org.share_recycled_stuff.dto.response.CommentResponse;
import com.org.share_recycled_stuff.entity.Comments;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "commenterName",
            expression = "java(comment.getAccount().getUser() != null ? comment.getAccount().getUser().getFullName() : comment.getAccount().getEmail())")
    @Mapping(source = "parentComment.id", target = "parentCommentId")
    @Mapping(source = "account.id", target = "accountId")
    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "edited", target = "isEdited")
    CommentResponse toResponse(Comments comment);
}
