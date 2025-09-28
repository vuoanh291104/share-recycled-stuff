package com.org.share_recycled_stuff.service;

import com.org.share_recycled_stuff.dto.request.CommentRequest;
import com.org.share_recycled_stuff.dto.request.ReplyCommentRequest;
import com.org.share_recycled_stuff.dto.response.CommentResponse;
import com.org.share_recycled_stuff.dto.request.EditCommentRequest;

public interface CommentService {

    CommentResponse createComment(CommentRequest request, Long accountId);

    CommentResponse replyToComment(ReplyCommentRequest request, Long accountId);
    CommentResponse editComment(Long id, EditCommentRequest request, Long userId);
}
