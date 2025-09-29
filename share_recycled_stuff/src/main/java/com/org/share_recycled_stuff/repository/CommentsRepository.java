package com.org.share_recycled_stuff.repository;

import com.org.share_recycled_stuff.entity.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentsRepository extends JpaRepository<Comments, Long> {

    // Tìm comment với đầy đủ thông tin account và user
    @Query("""
            SELECT c FROM Comments c
            LEFT JOIN FETCH c.account a
            LEFT JOIN FETCH a.user u
            WHERE c.id = :commentId
            """)
    Optional<Comments> findByIdWithAccount(@Param("commentId") Long commentId);

    // Tìm parent comment 
    @Query("""
            SELECT c FROM Comments c
            LEFT JOIN FETCH c.account a
            LEFT JOIN FETCH a.user u
            LEFT JOIN FETCH c.post p
            WHERE c.id = :parentCommentId
            """)
    Optional<Comments> findParentCommentWithDetails(@Param("parentCommentId") Long parentCommentId);

    // Lấy tất cả comments của một post (chỉ parent comments, không bao gồm replies)
    @Query("""
            SELECT c FROM Comments c
            LEFT JOIN FETCH c.account a
            LEFT JOIN FETCH a.user u
            WHERE c.post.id = :postId
            AND c.parentComment IS NULL
            ORDER BY c.createdAt ASC
           """)
    List<Comments> findByPostIdOrderByCreatedAtAsc(@Param("postId") Long postId);

    // Lấy tất cả comments của một post (bao gồm cả parent và child comments)
    @Query("""
            SELECT c FROM Comments c
            LEFT JOIN FETCH c.account a
            LEFT JOIN FETCH a.user u
            WHERE c.post.id = :postId
            ORDER BY c.createdAt ASC
            """)
    List<Comments> findAllByPostIdOrderByCreatedAtAsc(@Param("postId") Long postId);
}
