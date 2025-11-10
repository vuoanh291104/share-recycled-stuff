package com.org.share_recycled_stuff.repository.projection;

import java.time.LocalDateTime;

/**
 * Projection interface for recent chat user query results
 */
public interface RecentChatUserProjection {
    Long getOtherUserId();
    LocalDateTime getLastMessageTime();
}

