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
import com.org.share_recycled_stuff.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentsRepository commentsRepository;
    private final PostRepository postRepository;
    private final AccountRepository accountRepository;
    private final CommentMapper commentMapper;
    private final NotificationService notificationService;

    @Override
    public CommentResponse createComment(CommentRequest request, Long accountId) {
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        Comments comment = commentMapper.toEntity(post, account, request.getContent(), null);
        Comments savedComment = commentsRepository.save(comment);
        
        log.info("Created new comment with ID: {} for post: {} by account: {}",
                savedComment.getId(), post.getId(), account.getId());

        if (!post.getAccount().getId().equals(accountId)) {
            String commenterName = account.getUser() != null ? account.getUser().getFullName() : account.getEmail();
            notificationService.createNotification(
                    post.getAccount().getId(),
                    "Bình luận mới",
                    String.format("%s đã bình luận trên bài viết \"%s\" của bạn", commenterName, post.getTitle()),
                    1,
                    1,
                    "Comment",
                    savedComment.getId()
            );
        }

        return commentMapper.toResponse(savedComment);
    }

    @Override
    public CommentResponse replyToComment(ReplyCommentRequest request, Long accountId) {
        Comments parentComment = commentsRepository.findParentCommentWithDetails(request.getParentCommentId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Comment"));

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        Comments reply = commentMapper.toEntity(parentComment.getPost(), account, 
                                                request.getContent(), parentComment);
        Comments savedReply = commentsRepository.save(reply);
        
        log.info("Created reply with ID: {} for comment: {} by account: {}",
                savedReply.getId(), parentComment.getId(), account.getId());

        if (!parentComment.getAccount().getId().equals(accountId)) {
            String replierName = account.getUser() != null ? account.getUser().getFullName() : account.getEmail();
            notificationService.createNotification(
                    parentComment.getAccount().getId(),
                    "Phản hồi bình luận",
                    String.format("%s đã phản hồi bình luận của bạn: \"%s\"", replierName, request.getContent()),
                    2,
                    1,
                    "Comment",
                    savedReply.getId()
            );
        }

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
        
        LocalDateTime now = LocalDateTime.now();
        List<Comments> commentsToUpdate = new ArrayList<>();
        
        collectCommentsRecursively(comment, now, commentsToUpdate);
        
        commentsRepository.saveAll(commentsToUpdate);
    }

    private void collectCommentsRecursively(Comments comment, LocalDateTime deletedAt, List<Comments> collector) {
        comment.setDeletedAt(deletedAt);
        collector.add(comment);
        
        if (comment.getChildComments() != null && !comment.getChildComments().isEmpty()) {
            for (Comments child : comment.getChildComments()) {
                collectCommentsRecursively(child, deletedAt, collector);
            }
        }
    }
}
