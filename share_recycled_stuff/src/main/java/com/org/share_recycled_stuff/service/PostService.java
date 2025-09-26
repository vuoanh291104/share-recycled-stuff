package com.org.share_recycled_stuff.service;

import com.org.share_recycled_stuff.dto.request.CreatePostRequest;
import com.org.share_recycled_stuff.dto.response.PostDetailResponse;
import com.org.share_recycled_stuff.dto.response.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
    PostResponse createPost(CreatePostRequest postRequest, Long accountId);
    Page<PostDetailResponse> getUserPosts(Long userId, Pageable pageable);
    Page<PostDetailResponse> getMyPosts(Long accountId, Pageable pageable);
}
