package com.org.share_recycled_stuff.service.impl;

import com.org.share_recycled_stuff.dto.request.CreatePostImageRequest;
import com.org.share_recycled_stuff.dto.response.PostImageResponse;
import com.org.share_recycled_stuff.entity.Post;
import com.org.share_recycled_stuff.entity.PostImages;
import com.org.share_recycled_stuff.mapper.PostImageMapper;
import com.org.share_recycled_stuff.repository.PostImageRepository;
import com.org.share_recycled_stuff.service.PostImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostImageServiceImpl implements PostImageService {
    private final PostImageRepository postImageRepository;
    private final PostImageMapper postImageMapper;
    @Override
    @Transactional
    public PostImageResponse createImage(CreatePostImageRequest createPostImageRequest, Post post) {
        PostImages entity = postImageMapper.toEntity(createPostImageRequest);
        entity.setPost(post);
        entity = postImageRepository.save(entity);
        return postImageMapper.toResponse(entity);
    }
}
