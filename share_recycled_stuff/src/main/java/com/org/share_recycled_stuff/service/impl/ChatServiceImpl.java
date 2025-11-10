package com.org.share_recycled_stuff.service.impl;

import com.org.share_recycled_stuff.dto.response.RecentChatUserResponse;
import com.org.share_recycled_stuff.entity.Account;
import com.org.share_recycled_stuff.exception.AppException;
import com.org.share_recycled_stuff.exception.ErrorCode;
import com.org.share_recycled_stuff.repository.AccountRepository;
import com.org.share_recycled_stuff.repository.MessagesRepository;
import com.org.share_recycled_stuff.repository.projection.RecentChatUserProjection;
import com.org.share_recycled_stuff.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final MessagesRepository messagesRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RecentChatUserResponse> getRecentChatUsers(Long currentUserId) {
        log.info("Getting recent chat users for user ID: {}", currentUserId);

        if (!accountRepository.existsById(currentUserId)) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        List<RecentChatUserProjection> projections = messagesRepository.findRecentChatUsersWithLastMessageTime(currentUserId);
        
        List<RecentChatUserResponse> responses = new ArrayList<>();
        
        for (RecentChatUserProjection projection : projections) {
            Long otherUserId = projection.getOtherUserId();

            Account chatUser = accountRepository.findById(otherUserId).orElse(null);
            
            if (chatUser != null) {
                RecentChatUserResponse response = RecentChatUserResponse.builder()
                        .id(chatUser.getId())
                        .fullName(chatUser.getUser() != null ? chatUser.getUser().getFullName() : "Unknown User")
                        .avatarUrl(chatUser.getUser() != null ? chatUser.getUser().getAvatarUrl() : null)
                        .build();
                
                responses.add(response);
            }
        }
        
        log.info("Found {} recent chat users for user ID: {}", responses.size(), currentUserId);
        return responses;
    }
}

