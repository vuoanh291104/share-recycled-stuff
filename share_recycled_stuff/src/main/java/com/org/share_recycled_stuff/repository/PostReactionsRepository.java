package com.org.share_recycled_stuff.repository;

import com.org.share_recycled_stuff.entity.PostReactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostReactionsRepository extends JpaRepository<PostReactions, Long> {

    Optional<PostReactions> findByPostIdAndAccountId(Long postId, Long accountId);
}
