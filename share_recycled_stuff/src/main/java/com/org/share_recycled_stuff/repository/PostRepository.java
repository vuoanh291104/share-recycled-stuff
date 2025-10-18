package com.org.share_recycled_stuff.repository;

import com.org.share_recycled_stuff.entity.Post;
import com.org.share_recycled_stuff.entity.enums.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("""
            SELECT DISTINCT p FROM Post p 
            LEFT JOIN FETCH p.images 
            LEFT JOIN FETCH p.account a 
            LEFT JOIN FETCH a.user 
            LEFT JOIN FETCH p.category 
            WHERE p.account.id = :accountId 
            AND p.status = :status AND p.deletedAt IS NULL 
            ORDER BY p.createdAt DESC""")
    Page<Post> findByAccountIdAndStatusAndDeletedAtIsNull(
            @Param("accountId") Long accountId,
            @Param("status") PostStatus status,
            Pageable pageable
    );

    @Query("""
            SELECT DISTINCT p FROM Post p 
            LEFT JOIN FETCH p.images 
            LEFT JOIN FETCH p.account a 
            LEFT JOIN FETCH a.user
            LEFT JOIN FETCH p.category
            WHERE p.account.id = :accountId 
            AND p.deletedAt IS NULL 
            ORDER BY p.createdAt DESC""")
    Page<Post> findByAccountIdAndDeletedAtIsNull(
            @Param("accountId") Long accountId,
            Pageable pageable
    );

    @Query("SELECT p FROM Post p WHERE p.status = :status " +
            "AND p.deletedAt IS NULL " +
            "ORDER BY p.createdAt DESC")
    Page<Post> findByStatusAndDeletedAtIsNull(
            @Param("status") PostStatus status,
            Pageable pageable
    );

    // Admin queries
    @Query("""
            SELECT DISTINCT p FROM Post p 
            LEFT JOIN FETCH p.images 
            LEFT JOIN FETCH p.account a 
            LEFT JOIN FETCH a.user u
            LEFT JOIN FETCH p.category c
            WHERE (:status IS NULL OR p.status = :status)
            AND (:accountId IS NULL OR p.account.id = :accountId)
            AND (:categoryId IS NULL OR p.category.id = :categoryId)
            AND (:search IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')) 
                 OR LOWER(p.content) LIKE LOWER(CONCAT('%', :search, '%')))
            AND (:includeDeleted = true OR p.deletedAt IS NULL)
            ORDER BY p.createdAt DESC""")
    Page<Post> findAllWithFilters(
            @Param("status") PostStatus status,
            @Param("accountId") Long accountId,
            @Param("categoryId") Long categoryId,
            @Param("search") String search,
            @Param("includeDeleted") boolean includeDeleted,
            Pageable pageable
    );

    @Query("""
            SELECT p FROM Post p 
            LEFT JOIN FETCH p.images 
            LEFT JOIN FETCH p.account a 
            LEFT JOIN FETCH a.user u
            LEFT JOIN FETCH p.category c
            LEFT JOIN FETCH p.comments
            LEFT JOIN FETCH p.reactions
            WHERE p.id = :postId""")
    Optional<Post> findByIdWithFullDetails(@Param("postId") Long postId);

    // Statistics queries
    @Query("SELECT COUNT(p) FROM Post p WHERE p.status = :status")
    Long countByStatus(@Param("status") PostStatus status);

    @Query("SELECT COUNT(p) FROM Post p")
    Long countAllPosts();

    @Query("SELECT COALESCE(SUM(p.viewCount), 0) FROM Post p")
    Long sumAllViewCount();

    @Query("SELECT COUNT(c) FROM Comments c")
    Long countAllComments();

    @Query("SELECT COUNT(pr) FROM PostReactions pr")
    Long countAllReactions();

    @Query("SELECT COUNT(p) FROM Post p WHERE p.createdAt >= :date")
    Long countPostsSince(@Param("date") LocalDateTime date);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.deletedAt IS NOT NULL")
    Long countDeletedPosts();

    @Query("SELECT COUNT(p) FROM Post p WHERE " +
            "(:startDate IS NULL OR p.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR p.createdAt < :endDate)")
    Long countPostsInRange(@Param("startDate") LocalDateTime startDate,
                           @Param("endDate") LocalDateTime endDate);

    // Main page feed - fetch approved posts with user location
    @Query("""
            SELECT DISTINCT p FROM Post p
            LEFT JOIN FETCH p.images
            LEFT JOIN FETCH p.account a
            LEFT JOIN FETCH a.user u
            LEFT JOIN FETCH p.category
            WHERE p.status = :status
            AND p.deletedAt IS NULL
            ORDER BY p.createdAt DESC
            """)
    Page<Post> findApprovedPostsWithLocation(
            @Param("status") PostStatus status,
            Pageable pageable
    );
}
