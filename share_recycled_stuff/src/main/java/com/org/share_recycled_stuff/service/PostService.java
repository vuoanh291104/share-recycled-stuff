package com.org.share_recycled_stuff.service;

import com.org.share_recycled_stuff.dto.request.CreatePostRequest;
import com.org.share_recycled_stuff.dto.response.PostResponse;

public interface PostService {
    PostResponse createPost(CreatePostRequest postRequest, Long accountId);
}
