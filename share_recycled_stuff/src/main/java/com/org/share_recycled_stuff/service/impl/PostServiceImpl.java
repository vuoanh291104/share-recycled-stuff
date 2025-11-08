package com.org.share_recycled_stuff.service.impl;

import com.org.share_recycled_stuff.dto.request.*;
import com.org.share_recycled_stuff.dto.response.*;
import com.org.share_recycled_stuff.entity.*;
import com.org.share_recycled_stuff.entity.enums.PostPurpose;
import com.org.share_recycled_stuff.entity.enums.PostStatus;
import com.org.share_recycled_stuff.exception.AppException;
import com.org.share_recycled_stuff.exception.ErrorCode;
import com.org.share_recycled_stuff.mapper.CommentMapper;
import com.org.share_recycled_stuff.mapper.PostImageMapper;
import com.org.share_recycled_stuff.mapper.PostMapper;
import com.org.share_recycled_stuff.repository.*;
import com.org.share_recycled_stuff.service.GeoIpService;
import com.org.share_recycled_stuff.service.NotificationService;
import com.org.share_recycled_stuff.service.PostService;
import com.org.share_recycled_stuff.utils.LocationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
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
    private final NotificationService notificationService;
    private final GeoIpService geoIpService;

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

    // Admin methods implementation
    @Override
    @Transactional(readOnly = true)
    public Page<AdminPostDetailResponse> getAllPostsForAdmin(
            PostStatus status,
            Long accountId,
            Long categoryId,
            String search,
            boolean includeDeleted,
            Pageable pageable) {
        log.info("Admin fetching posts with filters - status: {}, accountId: {}, categoryId: {}, search: {}, includeDeleted: {}",
                status, accountId, categoryId, search, includeDeleted);

        Page<Post> posts = postRepository.findAllWithFilters(
                status, accountId, categoryId, search, includeDeleted, pageable
        );

        return posts.map(postMapper::toAdminPostDetailResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminPostDetailResponse getPostDetailForAdmin(Long postId) {
        log.info("Admin fetching post detail for postId: {}", postId);

        Post post = postRepository.findByIdWithFullDetails(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        return postMapper.toAdminPostDetailResponse(post);
    }

    @Override
    @Transactional
    public AdminPostDetailResponse reviewPost(AdminPostReviewRequest request, Long adminId) {
        log.info("Admin {} reviewing post - postId: {}, statusCode: {}", adminId, request.getPostId(), request.getStatusCode());

        Post post = isPostExist(request.getPostId());

        // Validate status transition
        PostStatus currentStatus = post.getStatus();
        PostStatus newStatus = PostStatus.fromCode(request.getStatusCode());
        validateStatusTransition(currentStatus, newStatus);

        // Update status
        post.setStatus(newStatus);

        // Update admin review comment
        if (request.getAdminReviewComment() != null && !request.getAdminReviewComment().trim().isEmpty()) {
            post.setAdminReviewComment(request.getAdminReviewComment());
        }

        // If status is DELETED, set deletedAt
        if (newStatus == PostStatus.DELETED) {
            post.setDeletedAt(LocalDateTime.now());
        } else if (currentStatus == PostStatus.DELETED && newStatus != PostStatus.DELETED) {
            // If restoring from DELETED, clear deletedAt
            post.setDeletedAt(null);
        }

        postRepository.save(post);

        log.info("Post {} reviewed successfully with status {} by admin {}", post.getId(), newStatus, adminId);

        if (newStatus == PostStatus.ACTIVE && currentStatus != PostStatus.ACTIVE) {
            String reviewComment = request.getAdminReviewComment() != null && !request.getAdminReviewComment().trim().isEmpty()
                    ? request.getAdminReviewComment()
                    : "Bài viết của bạn đã được phê duyệt";
            notificationService.createNotification(
                    post.getAccount().getId(),
                    "Bài viết được duyệt",
                    String.format("Bài viết \"%s\" đã được phê duyệt. %s", post.getTitle(), reviewComment),
                    3,
                    1,
                    "Post",
                    post.getId()
            );
        } else if (newStatus == PostStatus.EDIT) {
            String reviewComment = request.getAdminReviewComment() != null && !request.getAdminReviewComment().trim().isEmpty()
                    ? request.getAdminReviewComment()
                    : "Bài viết cần chỉnh sửa";
            notificationService.createNotification(
                    post.getAccount().getId(),
                    "Bài viết cần chỉnh sửa",
                    String.format("Bài viết \"%s\" cần chỉnh sửa. Lý do: %s", post.getTitle(), reviewComment),
                    4,
                    1,
                    "Post",
                    post.getId()
            );
        }

        return getPostDetailForAdmin(post.getId());
    }

    private void validateStatusTransition(PostStatus currentStatus, PostStatus newStatus) {
        // DELETED posts can only transition to ACTIVE (restore), not to EDIT
        if (currentStatus == PostStatus.DELETED && newStatus == PostStatus.EDIT) {
            throw new AppException(ErrorCode.INVALID_STATUS_TRANSITION);
        }

        // Add more transition rules if needed
        log.debug("Validating status transition from {} to {}", currentStatus, newStatus);
    }

    @Override
    @Transactional
    public AdminPostDetailResponse deletePostByAdmin(Long postId, String reason, Long adminId) {
        log.info("Admin {} deleting post - postId: {}, reason: {}", adminId, postId, reason);

        Post post = isPostExist(postId);

        post.setStatus(PostStatus.DELETED);
        post.setDeletedAt(LocalDateTime.now());

        if (reason != null && !reason.trim().isEmpty()) {
            post.setAdminReviewComment(reason);
        }

        postRepository.save(post);

        log.info("Post {} deleted successfully by admin {}", postId, adminId);

        String deleteReason = reason != null && !reason.trim().isEmpty()
                ? reason
                : "Vi phạm quy định của hệ thống";
        notificationService.createNotification(
                post.getAccount().getId(),
                "Bài viết bị xóa",
                String.format("Bài viết \"%s\" đã bị xóa bởi quản trị viên. Lý do: %s", post.getTitle(), deleteReason),
                5,
                3,
                "Post",
                post.getId()
        );

        return postMapper.toAdminPostDetailResponse(post);
    }

    @Override
    @Transactional
    public BulkDeletePostResponse bulkDeletePosts(BulkDeletePostRequest request, Long adminId) {
        log.info("Admin {} bulk deleting {} posts", adminId, request.getPostIds().size());

        List<Long> successfulPostIds = new ArrayList<>();
        List<BulkDeletePostResponse.PostOperationError> errors = new ArrayList<>();
        Map<Long, String> postTitles = new HashMap<>();
        List<Post> postsToUpdate = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (Long postId : request.getPostIds()) {
            try {
                Post post = postRepository.findById(postId)
                        .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

                postTitles.put(postId, post.getTitle());

                post.setStatus(PostStatus.DELETED);
                post.setDeletedAt(now);

                if (request.getReason() != null && !request.getReason().trim().isEmpty()) {
                    post.setAdminReviewComment(request.getReason());
                }

                postsToUpdate.add(post);
                successfulPostIds.add(postId);

            } catch (Exception e) {
                log.error("Error preparing post {} for deletion: {}", postId, e.getMessage());
                errors.add(BulkDeletePostResponse.PostOperationError.builder()
                        .postId(postId)
                        .errorMessage(e.getMessage())
                        .build());
            }
        }

        if (!postsToUpdate.isEmpty()) {
            postRepository.saveAll(postsToUpdate);
        }

        log.info("Bulk delete completed - success: {}, failed: {}", successfulPostIds.size(), errors.size());

        return BulkDeletePostResponse.builder()
                .totalRequested(request.getPostIds().size())
                .successCount(successfulPostIds.size())
                .failedCount(errors.size())
                .successfulPostIds(successfulPostIds)
                .postTitles(postTitles)
                .processedAt(LocalDateTime.now())
                .errors(errors)
                .build();
    }

    @Override
    @Transactional
    public AdminPostDetailResponse restorePost(Long postId, Long adminId) {
        log.info("Admin {} restoring post - postId: {}", adminId, postId);

        Post post = isPostExist(postId);

        // Check if post is actually deleted
        if (post.getDeletedAt() == null) {
            throw new AppException(ErrorCode.POST_NOT_DELETED);
        }

        // Restore post
        post.setStatus(PostStatus.ACTIVE);
        post.setDeletedAt(null);
        post.setAdminReviewComment("Restored by admin");

        postRepository.save(post);

        log.info("Post {} restored successfully by admin {}", postId, adminId);

        return postMapper.toAdminPostDetailResponse(post);
    }

    @Override
    @Transactional(readOnly = true)
    public PostStatisticsResponse getPostStatistics(LocalDate startDate, LocalDate endDate) {
        log.info("Admin fetching post statistics - startDate: {}, endDate: {}", startDate, endDate);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        LocalDateTime startOfWeek = now.minusWeeks(1);
        LocalDateTime startOfMonth = now.minusMonths(1);

        // Convert LocalDate to LocalDateTime for filtering
        LocalDateTime filterStartDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime filterEndDateTime = endDate != null ? endDate.plusDays(1).atStartOfDay() : null;

        PostStatisticsResponse.PostStatisticsResponseBuilder builder = PostStatisticsResponse.builder()
                .totalPosts(postRepository.countAllPosts())
                .activePosts(postRepository.countByStatus(PostStatus.ACTIVE))
                .editPosts(postRepository.countByStatus(PostStatus.EDIT))
                .deletedPosts(postRepository.countDeletedPosts())
                .totalViewCount(postRepository.sumAllViewCount())
                .totalComments(postRepository.countAllComments())
                .totalReactions(postRepository.countAllReactions())
                .postsToday(postRepository.countPostsSince(startOfDay))
                .postsThisWeek(postRepository.countPostsSince(startOfWeek))
                .postsThisMonth(postRepository.countPostsSince(startOfMonth));

        // Add filtered counts if date range is provided
        if (startDate != null || endDate != null) {
            Long filteredCount = postRepository.countPostsInRange(filterStartDateTime, filterEndDateTime);
            builder.postsInDateRange(filteredCount)
                    .filterStartDate(startDate)
                    .filterEndDate(endDate);
            log.info("Posts in date range: {}", filteredCount);
        }

        return builder.build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostDetailResponse> getMainPageFeed(String clientIp, Pageable pageable) {
        log.info("Fetching main page feed for client IP: {}", clientIp);

        // Fetch all approved posts
        Page<Post> posts = postRepository.findApprovedPostsWithLocation(PostStatus.ACTIVE, pageable);

        // Get client location from IP
        GeoLocation clientLocation = geoIpService.getLocationFromIp(clientIp);

        if (clientLocation == null ||
                !LocationUtils.isValidCoordinates(clientLocation.getLatitude(), clientLocation.getLongitude())) {
            log.debug("Client location not available or invalid, returning posts sorted by createdAt");
            return posts.map(postMapper::toPostDetailResponse);
        }

        log.debug("Client location: lat={}, lon={}",
                clientLocation.getLatitude(), clientLocation.getLongitude());

        // Convert posts to responses and calculate distances
        List<PostDetailResponse> postResponses = posts.getContent().stream()
                .map(post -> {
                    PostDetailResponse response = postMapper.toPostDetailResponse(post);

                    // Calculate distance if post creator has location
                    if (post.getAccount() != null
                            && post.getAccount().getUser() != null) {
                        User postUser = post.getAccount().getUser();

                        if (LocationUtils.isValidCoordinates(postUser.getLatitude(), postUser.getLongitude())) {
                            double distance = LocationUtils.calculateDistance(
                                    clientLocation.getLatitude(),
                                    clientLocation.getLongitude(),
                                    postUser.getLatitude(),
                                    postUser.getLongitude()
                            );
                            response.setDistance(distance);
                        }
                    }

                    return response;
                })
                .sorted((p1, p2) -> {
                    // Sort by distance (nulls last), then by createdAt
                    if (p1.getDistance() == null && p2.getDistance() == null) {
                        return p2.getCreatedAt().compareTo(p1.getCreatedAt());
                    }
                    if (p1.getDistance() == null) return 1;
                    if (p2.getDistance() == null) return -1;
                    return Double.compare(p1.getDistance(), p2.getDistance());
                })
                .collect(Collectors.toList());

        return new PageImpl<>(postResponses, pageable, posts.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostSearchResponse> searchPosts(PostSearchRequest filter) {
        log.debug("Searching posts with filter: {}", filter);

        Pageable pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        PostPurpose purposeEnum = null;
        if (StringUtils.hasText(filter.getPurpose())) {
            try {
                purposeEnum = PostPurpose.valueOf(filter.getPurpose().toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid purpose string provided in filter: {}", filter.getPurpose());
            }
        }

        Page<Post> postPage = postRepository.searchPublicPosts(
                PostStatus.ACTIVE,
                filter.getKeyword(),
                filter.getCategoryId(),
                purposeEnum,
                filter.getLocation(),
                pageable
        );

        return postPage.map(postMapper::toPostSearchResponse);
    }
}
