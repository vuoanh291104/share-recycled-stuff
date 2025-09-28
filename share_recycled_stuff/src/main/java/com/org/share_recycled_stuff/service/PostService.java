package com.org.share_recycled_stuff.service;

import com.org.share_recycled_stuff.dto.request.PostRequest;
import com.org.share_recycled_stuff.dto.response.PostDetailResponse;
import com.org.share_recycled_stuff.dto.response.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
    Page<PostDetailResponse> getUserPosts(Long userId, Pageable pageable);

    Page<PostDetailResponse> getMyPosts(Long accountId, Pageable pageable);

    PostResponse createPost(PostRequest postRequest, Long accountId);

    PostResponse updatePost(PostRequest postRequest, Long accountId, Long postId);

    PostResponse softDelete (Long accountID, Long postId);
}
