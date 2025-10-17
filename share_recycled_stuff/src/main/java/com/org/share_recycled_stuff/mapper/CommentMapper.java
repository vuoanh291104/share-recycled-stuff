package com.org.share_recycled_stuff.mapper;

import com.org.share_recycled_stuff.dto.response.CommentResponse;
import com.org.share_recycled_stuff.entity.Account;
import com.org.share_recycled_stuff.entity.Comments;
import com.org.share_recycled_stuff.entity.Post;
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

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", source = "post")
    @Mapping(target = "account", source = "account")
    @Mapping(target = "content", source = "content")
    @Mapping(target = "parentComment", source = "parentComment")
    @Mapping(target = "childComments", ignore = true)
    @Mapping(target = "isEdited", constant = "false")
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Comments toEntity(Post post, Account account, String content, Comments parentComment);
}
