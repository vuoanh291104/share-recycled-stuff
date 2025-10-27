package com.org.share_recycled_stuff.service;

public interface PostReactionService {
    boolean toggleReaction(Long postId, Long currentUserId);
}
