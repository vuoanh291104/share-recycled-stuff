package com.org.share_recycled_stuff.service.impl;

import com.org.share_recycled_stuff.dto.request.EditCommentRequest;
import com.org.share_recycled_stuff.dto.response.EditCommentResponse;
import com.org.share_recycled_stuff.entity.Comments;
import com.org.share_recycled_stuff.exception.AppException;
import com.org.share_recycled_stuff.exception.ErrorCode;
import com.org.share_recycled_stuff.repository.CommentRepository;
import com.org.share_recycled_stuff.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Override
    public EditCommentResponse editComment(Long id, EditCommentRequest request, Long userId) {
        Comments comment = commentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Comment không tồn tại"));

        if (!comment.getAccount().getId().equals(userId)) {
            throw new AppException(ErrorCode.ACCESS_DENIED, "Bạn không có quyền sửa comment này");
        }

        comment.setContent(request.getContent());

        Comments updated = commentRepository.save(comment);

        return EditCommentResponse.builder()
                .id(updated.getId())
                .content(updated.getContent())
                .isEdited(updated.isEdited())
                .createdAt(updated.getCreatedAt())
                .updatedAt(updated.getUpdatedAt())
                .accountId(updated.getAccount().getId())
                .postId(updated.getPost().getId())
                .build();
    }
    @Override
    public void deleteComment(Long id, Long userId) {
        Comments comment = commentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Comment không tồn tại"));

        if (!comment.getAccount().getId().equals(userId)) {
            throw new AppException(ErrorCode.ACCESS_DENIED, "Bạn không có quyền xóa comment này");
        }

        comment.setDeletedAt(LocalDateTime.now());
        commentRepository.save(comment);

    }
}
