package com.org.share_recycled_stuff.service.impl;

import com.org.share_recycled_stuff.dto.request.PostImageRequest;
import com.org.share_recycled_stuff.dto.request.PostRequest;
import com.org.share_recycled_stuff.dto.response.CommentResponse;
import com.org.share_recycled_stuff.dto.response.PostDetailResponse;
import com.org.share_recycled_stuff.dto.response.PostResponse;
import com.org.share_recycled_stuff.entity.*;
import com.org.share_recycled_stuff.entity.enums.PostStatus;
import com.org.share_recycled_stuff.exception.AppException;
import com.org.share_recycled_stuff.exception.ErrorCode;
import com.org.share_recycled_stuff.mapper.CommentMapper;
import com.org.share_recycled_stuff.mapper.PostImageMapper;
import com.org.share_recycled_stuff.mapper.PostMapper;
import com.org.share_recycled_stuff.repository.*;
import com.org.share_recycled_stuff.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CommentsRepository commentsRepository;
    private final PostMapper postMapper;
    private final PostImageMapper postImageMapper;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public PostResponse createPost(PostRequest postRequest, Long accountId) {
        isAccountExist(accountId);
        postRequest.setAccountId(accountId);
        Category category = getCategoryExist(postRequest.getCategoryId());

        Post post = postMapper.toEntity(postRequest);

        if (post.getImages() != null) {
            post.getImages().forEach(img -> img.setPost(post));
        }
        post.setCategory(category);

        Post savedPost = postRepository.save(post);

        return postMapper.toResponse(savedPost);
    }

    @Override
    @Transactional
    public PostResponse updatePost(PostRequest postRequest, Long accountId, Long postId) {
        Post post = isPostExist(postId);

        isOwner(accountId, post);
        isDeleted(post);

        Set<PostImages> oldImageList = new HashSet<>(post.getImages());
        List<PostImageRequest> newImageList = postRequest.getImages();

        Category category = getCategoryExist(postRequest.getCategoryId());

        for (PostImageRequest newImg : newImageList) {
            if (newImg.getId() == null) {
                PostImages postImages = postImageMapper.toEntity(newImg);
                postImages.setPost(post);
                oldImageList.add(postImages);
            } else {
                PostImages existing = oldImageList.stream()
                        .filter(old -> old.getId().equals(newImg.getId()))
                        .findFirst()
                        .orElseThrow(() -> new AppException(ErrorCode.IMAGE_NOT_FOUND));
                postImageMapper.updateImage(newImg, existing);
            }
        }

        Set<Long> newIds = newImageList.stream()
                .map(PostImageRequest::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        oldImageList.removeIf(old -> {
            boolean shouldRemove = old.getId() != null && !newIds.contains(old.getId());
            if (shouldRemove) {
                postImageRepository.delete(old);
            }
            return shouldRemove;
        });

        postMapper.updatePost(postRequest, post);
        post.setImages(oldImageList);
        post.setCategory(category);

        postRepository.save(post);
        return postMapper.toResponse(post);
    }

    @Override
    public PostResponse softDelete(Long accountID, Long postId) {
        Post post = isPostExist(postId);

        isDeleted(post);
        isOwner(accountID, post);

        post.setDeletedAt(LocalDateTime.now());
        post.setStatus(PostStatus.DELETED);

        postRepository.save(post);

        PostResponse postResponse = postMapper.toDeletedPost(post);
        return postResponse;
    }


    @Override
    public Page<PostDetailResponse> getUserPosts(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (user.getAccount() == null) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        Long accountId = user.getAccount().getId();
        Page<Post> posts = postRepository.findByAccountIdAndStatusAndDeletedAtIsNull(
                accountId, PostStatus.ACTIVE, pageable);
        return posts.map(postMapper::toPostDetailResponse);
    }

    @Override
    public Page<PostDetailResponse> getMyPosts(Long accountId, Pageable pageable) {
        isAccountExist(accountId);
        Page<Post> posts = postRepository.findByAccountIdAndDeletedAtIsNull(accountId, pageable);

        return posts.map(postMapper::toPostDetailResponse);
    }

    private void isAccountExist(Long accountId) {
        accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
    }

    private Category getCategoryExist(Long categoryID) {
        return categoryRepository.findById(categoryID)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    private Post isPostExist(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
    }

    private void isOwner(Long accountId, Post post) {
        if (!post.getAccount().getId().equals(accountId)) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }
    }

    private void isDeleted(Post post) {
        if (post.getDeletedAt() != null || post.getStatus().equals(PostStatus.DELETED)) {
            throw new AppException(ErrorCode.POST_ALREADY_DELETED);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getPostComments(Long postId) {

        isPostExist(postId);

        List<Comments> parentComments = commentsRepository.findByPostIdOrderByCreatedAtAsc(postId);

        return parentComments.stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

}
