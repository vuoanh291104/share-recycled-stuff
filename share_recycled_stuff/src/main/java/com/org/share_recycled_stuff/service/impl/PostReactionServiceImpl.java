package com.org.share_recycled_stuff.service.impl;

import com.org.share_recycled_stuff.entity.Account;
import com.org.share_recycled_stuff.entity.Post;
import com.org.share_recycled_stuff.entity.PostReactions;
import com.org.share_recycled_stuff.exception.AppException;
import com.org.share_recycled_stuff.exception.ErrorCode;
import com.org.share_recycled_stuff.repository.PostReactionsRepository;
import com.org.share_recycled_stuff.repository.PostRepository;
import com.org.share_recycled_stuff.service.PostReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostReactionServiceImpl implements PostReactionService {

    private final PostReactionsRepository postReactionsRepository;
    private final PostRepository postRepository;

    @Override
    public boolean toggleReaction(Long postId, Long currentUserId) {
        if (currentUserId == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (postId == null || postId <= 0) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        Optional<PostReactions> existingReaction = postReactionsRepository.findByPostIdAndAccountId(postId, currentUserId);

        if (existingReaction.isPresent()) {
            PostReactions reaction = existingReaction.get();
            reaction.setReactionType(!reaction.getReactionType());
            postReactionsRepository.save(reaction);
            return reaction.getReactionType();
        } else {
            Account accountReference = new Account();
            accountReference.setId(currentUserId);

            PostReactions newReaction = PostReactions.builder()
                    .post(post)
                    .account(accountReference)
                    .reactionType(true)
                    .build();

            postReactionsRepository.save(newReaction);
            return true;
        }
    }
}
