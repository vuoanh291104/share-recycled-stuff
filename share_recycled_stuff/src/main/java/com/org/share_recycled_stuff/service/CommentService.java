package com.org.share_recycled_stuff.service;

import com.org.share_recycled_stuff.dto.request.CommentRequest;
import com.org.share_recycled_stuff.dto.request.ReplyCommentRequest;
import com.org.share_recycled_stuff.dto.response.CommentResponse;
import com.org.share_recycled_stuff.dto.request.EditCommentRequest;
import com.org.share_recycled_stuff.dto.response.EditCommentResponse;

public interface CommentService {

    CommentResponse createComment(CommentRequest request, Long accountId);

    CommentResponse replyToComment(ReplyCommentRequest request, Long accountId);
    EditCommentResponse editComment(Long id, EditCommentRequest request, Long userId);
}
