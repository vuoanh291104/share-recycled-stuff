package com.org.share_recycled_stuff.service;

import com.org.share_recycled_stuff.dto.request.CreateNotificationRequest;
import com.org.share_recycled_stuff.dto.response.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {

    /**
     * Create a new notification and push to user via SSE (if online) and/or email
     *
     * @param request Notification creation request
     * @return Created notification
     */
    NotificationResponse createNotification(CreateNotificationRequest request);

    /**
     * Create notification with simplified parameters (for internal service calls)
     *
     * @param accountId         User's account ID
     * @param title             Notification title
     * @param content           Notification content
     * @param notificationType  Notification type code
     * @param deliveryMethod    Delivery method code (1: IN_APP, 2: EMAIL, 3: BOTH)
     * @param relatedEntityType Related entity type (e.g., "Post", "Comment")
     * @param relatedEntityId   Related entity ID
     * @return Created notification
     */
    NotificationResponse createNotification(Long accountId, String title, String content,
                                            Integer notificationType, Integer deliveryMethod,
                                            String relatedEntityType, Long relatedEntityId);

    /**
     * Get paginated notifications for a user
     *
     * @param accountId User's account ID
     * @param pageable  Pagination parameters
     * @return Page of notifications
     */
    Page<NotificationResponse> getUserNotifications(Long accountId, Pageable pageable);

    /**
     * Get unread notifications for a user
     *
     * @param accountId User's account ID
     * @return List of unread notifications
     */
    List<NotificationResponse> getUnreadNotifications(Long accountId);

    /**
     * Get count of unread notifications for a user
     *
     * @param accountId User's account ID
     * @return Count of unread notifications
     */
    Long getUnreadCount(Long accountId);

    /**
     * Mark notifications as read
     *
     * @param notificationIds List of notification IDs
     * @param accountId       User's account ID (for security)
     * @return Number of notifications marked as read
     */
    int markAsRead(List<Long> notificationIds, Long accountId);

    /**
     * Mark all notifications as read for a user
     *
     * @param accountId User's account ID
     * @return Number of notifications marked as read
     */
    int markAllAsRead(Long accountId);

    /**
     * Delete a notification
     *
     * @param notificationId Notification ID
     * @param accountId      User's account ID (for security)
     */
    void deleteNotification(Long notificationId, Long accountId);

    /**
     * Get notifications by type
     *
     * @param accountId        User's account ID
     * @param notificationType Notification type code
     * @param pageable         Pagination parameters
     * @return Page of notifications
     */
    Page<NotificationResponse> getNotificationsByType(Long accountId, Integer notificationType, Pageable pageable);
}

