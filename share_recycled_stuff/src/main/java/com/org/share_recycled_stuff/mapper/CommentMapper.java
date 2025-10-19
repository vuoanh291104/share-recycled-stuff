package com.org.share_recycled_stuff.mapper;

import com.org.share_recycled_stuff.dto.response.CommentResponse;
import com.org.share_recycled_stuff.entity.Account;
import com.org.share_recycled_stuff.entity.Comments;
import com.org.share_recycled_stuff.entity.Post;
import com.org.share_recycled_stuff.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "author", expression = "java(mapUserInfo(comment.getAccount()))")
    @Mapping(source = "parentComment.id", target = "parentCommentId")
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

    default CommentResponse.UserInfo mapUserInfo(Account account) {
        if (account == null) {
            return null;
        }

        User user = account.getUser();

        return CommentResponse.UserInfo.builder()
                .id(account.getId())
                .email(account.getEmail())
                .fullName(user != null ? user.getFullName() : null)
                .avatarUrl(user != null ? user.getAvatarUrl() : null)
                .build();
    }
}
