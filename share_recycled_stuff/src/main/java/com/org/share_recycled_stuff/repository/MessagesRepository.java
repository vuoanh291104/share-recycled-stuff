package com.org.share_recycled_stuff.repository;

import com.org.share_recycled_stuff.entity.Messages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessagesRepository extends JpaRepository<Messages, Long> {

    /**
     * Get list of users who have recently chatted with the given user
     * Returns the most recent conversation partner first
     * 
     * @param userId The current user's account ID
     * @return List of account IDs of users who have chatted with this user, ordered by most recent message
     */
    @Query("""
            SELECT DISTINCT CASE 
                WHEN m.sender.id = :userId THEN m.receiver.id 
                ELSE m.sender.id 
            END as otherUserId
            FROM Messages m
            WHERE m.sender.id = :userId OR m.receiver.id = :userId
            ORDER BY MAX(m.createdAt) DESC
            """)
    List<Long> findRecentChatUserIds(@Param("userId") Long userId);

    /**
     * Get detailed list of recent chat conversations with the last message
     * This returns a projection with user ID and the timestamp of the most recent message
     */
    @Query("""
            SELECT CASE 
                WHEN m.sender.id = :userId THEN m.receiver 
                ELSE m.sender 
            END as chatUser,
            MAX(m.createdAt) as lastMessageTime
            FROM Messages m
            WHERE m.sender.id = :userId OR m.receiver.id = :userId
            GROUP BY CASE 
                WHEN m.sender.id = :userId THEN m.receiver.id 
                ELSE m.sender.id 
            END
            ORDER BY lastMessageTime DESC
            """)
    List<Object[]> findRecentChatUsersWithLastMessageTime(@Param("userId") Long userId);

    /**
     * Count unread messages from a specific sender to the current user
     */
    @Query("SELECT COUNT(m) FROM Messages m WHERE m.receiver.id = :receiverId AND m.sender.id = :senderId AND m.isRead = false")
    Long countUnreadMessages(@Param("receiverId") Long receiverId, @Param("senderId") Long senderId);
}

