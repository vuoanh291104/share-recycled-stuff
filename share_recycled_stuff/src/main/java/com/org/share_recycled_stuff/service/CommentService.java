package com.org.share_recycled_stuff.service;

import com.org.share_recycled_stuff.dto.request.EditCommentRequest;
import com.org.share_recycled_stuff.dto.response.EditCommentResponse;

public interface CommentService {
    EditCommentResponse editComment(Long id, EditCommentRequest request, Long userId);
    void deleteComment(Long id, Long userId);
}
