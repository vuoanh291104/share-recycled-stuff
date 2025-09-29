package com.org.share_recycled_stuff.service.impl;

import com.org.share_recycled_stuff.dto.request.CommentRequest;
import com.org.share_recycled_stuff.dto.request.EditCommentRequest;
import com.org.share_recycled_stuff.dto.request.ReplyCommentRequest;
import com.org.share_recycled_stuff.dto.response.CommentResponse;
import com.org.share_recycled_stuff.entity.Account;
import com.org.share_recycled_stuff.entity.Comments;
import com.org.share_recycled_stuff.entity.Post;
import com.org.share_recycled_stuff.exception.AppException;
import com.org.share_recycled_stuff.exception.ErrorCode;
import com.org.share_recycled_stuff.mapper.CommentMapper;
import com.org.share_recycled_stuff.repository.AccountRepository;
import com.org.share_recycled_stuff.repository.CommentsRepository;
import com.org.share_recycled_stuff.repository.PostRepository;
import com.org.share_recycled_stuff.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentsRepository commentsRepository;
    private final PostRepository postRepository;
    private final AccountRepository accountRepository;
    private final CommentMapper commentMapper;

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

        return commentMapper.toResponse(savedComment);
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

        return commentMapper.toResponse(savedReply);
    }

    @Override
    public CommentResponse editComment(Long id, EditCommentRequest request, Long userId) {
        Comments comment = commentsRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Comment không tồn tại"));

        if (!comment.getAccount().getId().equals(userId)) {
            throw new AppException(ErrorCode.ACCESS_DENIED, "Bạn không có quyền sửa comment này");
        }

        comment.setContent(request.getContent());
        comment.setEdited(true);
        comment.setUpdatedAt(LocalDateTime.now());

        Comments updated = commentsRepository.save(comment);

        return commentMapper.toResponse(updated);
    }
    @Override
    public void deleteComment(Long id, Long userId) {
        Comments comment = commentsRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Comment không tồn tại"));

        if (!comment.getAccount().getId().equals(userId)) {
            throw new AppException(ErrorCode.ACCESS_DENIED, "Bạn không có quyền xóa comment này");
        }
        if (comment.getChildComments() != null) {
            for (Comments child : comment.getChildComments()) {
                child.setDeletedAt(LocalDateTime.now());
                if (child.getChildComments() != null && !child.getChildComments().isEmpty()) {
                    for (Comments grandChild : child.getChildComments()) {
                        grandChild.setDeletedAt(LocalDateTime.now());
                    }
                }
            }
        }
        comment.setDeletedAt(LocalDateTime.now());
        commentsRepository.save(comment);

    }
}
