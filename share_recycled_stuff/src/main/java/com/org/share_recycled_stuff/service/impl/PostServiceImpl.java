package com.org.share_recycled_stuff.service.impl;

import com.org.share_recycled_stuff.dto.request.CreatePostRequest;
import com.org.share_recycled_stuff.dto.response.PostImageResponse;
import com.org.share_recycled_stuff.dto.response.PostResponse;
import com.org.share_recycled_stuff.entity.Category;
import com.org.share_recycled_stuff.entity.Post;
import com.org.share_recycled_stuff.exception.AppException;
import com.org.share_recycled_stuff.exception.ErrorCode;
import com.org.share_recycled_stuff.mapper.PostMapper;
import com.org.share_recycled_stuff.repository.AccountRepository;
import com.org.share_recycled_stuff.repository.CategoryRepository;
import com.org.share_recycled_stuff.repository.PostRepository;
import com.org.share_recycled_stuff.service.PostImageService;
import com.org.share_recycled_stuff.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final PostImageService postImageService;
    private final PostMapper postMapper;

    @Override
    @Transactional
    public PostResponse createPost(CreatePostRequest createPostRequest, Long accountId) {
        isAccountExist(accountId);
        createPostRequest.setAccountId(accountId);
        Category category = getCategoryExist(createPostRequest.getCategoryId());
        Post post = postMapper.toEntity(createPostRequest);
        post.setCategory(category);
        postRepository.save(post);
        List<PostImageResponse> images = createPostRequest.getImages() == null
                ? List.of()
                : createPostRequest.getImages().stream()
                .map(imgReq -> postImageService.createImage(imgReq, post))
                .toList();
        PostResponse postResponse = postMapper.toResponse(post);
        postResponse.setImages(images);
        return postResponse;
    }

    private void isAccountExist (Long accountId) {
        accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
    }

    private Category getCategoryExist (Long categoryID) {
        return categoryRepository.findById(categoryID)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
    }
}
