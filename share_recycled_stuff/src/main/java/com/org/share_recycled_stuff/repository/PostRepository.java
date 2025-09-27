package com.org.share_recycled_stuff.repository;

import com.org.share_recycled_stuff.entity.Post;
import com.org.share_recycled_stuff.entity.enums.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
