package com.org.share_recycled_stuff.service;


import com.org.share_recycled_stuff.dto.request.CreatePostImageRequest;
import com.org.share_recycled_stuff.dto.response.PostImageResponse;
import com.org.share_recycled_stuff.entity.Post;

public interface PostImageService {
    PostImageResponse createImage(CreatePostImageRequest createPostImageRequest, Post post);
}
