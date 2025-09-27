package com.org.share_recycled_stuff.service.impl;

import com.org.share_recycled_stuff.dto.request.CommentRequest;
import com.org.share_recycled_stuff.dto.request.ReplyCommentRequest;
import com.org.share_recycled_stuff.dto.response.CommentResponse;
import com.org.share_recycled_stuff.entity.Account;
import com.org.share_recycled_stuff.entity.Comments;
import com.org.share_recycled_stuff.entity.Post;
import com.org.share_recycled_stuff.exception.AppException;
import com.org.share_recycled_stuff.exception.ErrorCode;
import com.org.share_recycled_stuff.repository.AccountRepository;
import com.org.share_recycled_stuff.repository.CommentsRepository;
import com.org.share_recycled_stuff.repository.PostRepository;
import com.org.share_recycled_stuff.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentsRepository commentsRepository;
    private final PostRepository postRepository;
    private final AccountRepository accountRepository;

    @Override
    public CommentResponse createComment(CommentRequest request, Long accountId) {
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        Comments comment = Comments.builder()
                .post(post)
                .account(account)
                .content(request.getContent())
                .parentComment(null)
                .build();

        Comments savedComment = commentsRepository.save(comment);
        log.info("Created new comment with ID: {} for post: {} by account: {}",
                savedComment.getId(), post.getId(), account.getId());

        return mapToCommentResponse(savedComment);
    }

    @Override
    public CommentResponse replyToComment(ReplyCommentRequest request, Long accountId) {
        Comments parentComment = commentsRepository.findParentCommentWithDetails(request.getParentCommentId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Comment"));

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        Comments reply = Comments.builder()
                .post(parentComment.getPost())
                .account(account)
                .content(request.getContent())
                .parentComment(parentComment)
                .build();

        Comments savedReply = commentsRepository.save(reply);
        log.info("Created reply with ID: {} for comment: {} by account: {}",
                savedReply.getId(), parentComment.getId(), account.getId());

        return mapToCommentResponse(savedReply);
    }

    private CommentResponse mapToCommentResponse(Comments comment) {
        String commenterName = comment.getAccount().getUser() != null ?
                comment.getAccount().getUser().getFullName() :
                comment.getAccount().getEmail();

        return CommentResponse.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .content(comment.getContent())
                .isEdited(comment.isEdited())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .accountId(comment.getAccount().getId())
                .commenterName(commenterName)
                .parentCommentId(comment.getParentComment() != null ?
                        comment.getParentComment().getId() : null)
                .build();
    }
}
