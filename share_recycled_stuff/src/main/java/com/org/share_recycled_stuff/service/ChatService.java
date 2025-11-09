package com.org.share_recycled_stuff.service;

import com.org.share_recycled_stuff.dto.response.RecentChatUserResponse;

import java.util.List;

public interface ChatService {

    /**
     * Get list of users who have recently chatted with the current user
     * Ordered by most recent message first
     *
     * @param currentUserId The current user's account ID
     * @return List of recent chat users with their information
     */
    List<RecentChatUserResponse> getRecentChatUsers(Long currentUserId);
}

