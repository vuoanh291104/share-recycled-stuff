package com.org.share_recycled_stuff.service.impl;

import com.org.share_recycled_stuff.dto.request.CreateNotificationRequest;
import com.org.share_recycled_stuff.dto.response.NotificationResponse;
import com.org.share_recycled_stuff.entity.Account;
import com.org.share_recycled_stuff.entity.BroadcastNotificationRead;
import com.org.share_recycled_stuff.entity.Notifications;
import com.org.share_recycled_stuff.exception.AppException;
import com.org.share_recycled_stuff.exception.ErrorCode;
import com.org.share_recycled_stuff.mapper.NotificationMapper;
import com.org.share_recycled_stuff.repository.AccountRepository;
import com.org.share_recycled_stuff.repository.BroadcastNotificationReadRepository;
import com.org.share_recycled_stuff.repository.NotificationRepository;
import com.org.share_recycled_stuff.service.EmailService;
import com.org.share_recycled_stuff.service.NotificationService;
import com.org.share_recycled_stuff.utils.SseEmitterManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final AccountRepository accountRepository;
    private final BroadcastNotificationReadRepository broadcastNotificationReadRepository;
    private final SseEmitterManager sseEmitterManager;
    private final EmailService emailService;
    private final NotificationMapper notificationMapper;

    @Override
    @Transactional
    public NotificationResponse createNotification(CreateNotificationRequest request) {
        if (Boolean.TRUE.equals(request.getBroadcastToAll())) {
            return createBroadcastNotification(
                    request.getTitle(),
                    request.getContent(),
                    request.getNotificationType(),
                    request.getDeliveryMethod(),
                    request.getRelatedEntityType(),
                    request.getRelatedEntityId()
            );
        }

        return createNotification(
                request.getAccountId(),
                request.getTitle(),
                request.getContent(),
                request.getNotificationType(),
                request.getDeliveryMethod(),
                request.getRelatedEntityType(),
                request.getRelatedEntityId()
        );
    }

    @Override
    @Transactional
    public NotificationResponse createNotification(Long accountId, String title, String content,
                                                   Integer notificationType, Integer deliveryMethod,
                                                   String relatedEntityType, Long relatedEntityId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        Notifications notification = notificationMapper.toEntity(title, content, notificationType,
                deliveryMethod, relatedEntityType,
                relatedEntityId, account);

        notification = notificationRepository.save(notification);
        log.info("Created notification ID: {} for account ID: {}", notification.getId(), accountId);

        NotificationResponse response = notificationMapper.toResponse(notification);

        if (deliveryMethod == null || deliveryMethod == 1 || deliveryMethod == 3) {
            if (sseEmitterManager.hasConnection(accountId)) {
                sseEmitterManager.sendToUser(accountId, response);
                log.info("Pushed notification via SSE to account ID: {}", accountId);
            } else {
                log.debug("Account ID: {} has no active SSE connection", accountId);
            }
        }

        if (deliveryMethod != null && (deliveryMethod == 2 || deliveryMethod == 3)) {
            try {
                emailService.sendNotificationEmail(account.getEmail(), title, content);
                log.info("Sent notification email to: {}", account.getEmail());
            } catch (Exception e) {
                log.error("Failed to send notification email to: {}", account.getEmail(), e);
            }
        }

        return response;
    }

    private NotificationResponse createBroadcastNotification(String title,
                                                             String content,
                                                             Integer notificationType,
                                                             Integer deliveryMethod,
                                                             String relatedEntityType,
                                                             Long relatedEntityId) {
        // Chỉ lưu 1 notification với account = null để đánh dấu là broadcast
        Notifications broadcastNotification = notificationMapper.toEntity(
                title,
                content,
                notificationType,
                deliveryMethod,
                relatedEntityType,
                relatedEntityId,
                null // account = null để đánh dấu là broadcast
        );

        broadcastNotification = notificationRepository.save(broadcastNotification);
        log.info("Created broadcast notification ID: {}", broadcastNotification.getId());

        NotificationResponse response = notificationMapper.toResponse(broadcastNotification);

        // Lấy tất cả accounts để gửi SSE
        List<Account> accounts = accountRepository.findAll();
        long recipientCount = accounts.size();
        long broadcastSseDeliveries = 0L;

        boolean shouldSendSse = deliveryMethod == null || deliveryMethod == 1 || deliveryMethod == 3;

        if (shouldSendSse) {
            // Gửi SSE cho tất cả users đang online
            for (Account account : accounts) {
                int connectionCount = sseEmitterManager.getConnectionCount(account.getId());
                if (connectionCount > 0) {
                    sseEmitterManager.sendToUser(account.getId(), response);
                    broadcastSseDeliveries += connectionCount;
                }
            }
        }

        log.info("Broadcasted notification '{}' to {} accounts (SSE deliveries: {}, emails skipped)",
                title, recipientCount, broadcastSseDeliveries);

        return NotificationResponse.builder()
                .id(response.getId())
                .title(response.getTitle())
                .content(response.getContent())
                .notificationType(response.getNotificationType())
                .notificationTypeName(response.getNotificationTypeName())
                .deliveryMethod(response.getDeliveryMethod())
                .relatedEntityType(response.getRelatedEntityType())
                .relatedEntityId(response.getRelatedEntityId())
                .createdAt(response.getCreatedAt())
                .broadcastRecipientCount(recipientCount)
                .broadcastSseDeliveredCount(shouldSendSse ? broadcastSseDeliveries : 0L)
                .broadcastEmailSentCount(0L)
                .isRead(false) // Broadcast notification mặc định chưa đọc cho mỗi user
                .build();
    }

    @Override
    public Page<NotificationResponse> getUserNotifications(Long accountId, Pageable pageable) {
        Page<Notifications> notifications = notificationRepository
                .findByAccountIdOrBroadcastOrderByCreatedAtDesc(accountId, pageable);
        
        // Lấy danh sách broadcast notifications đã đọc của user
        Set<Long> readBroadcastIds = broadcastNotificationReadRepository
                .findReadBroadcastNotificationIdsByAccountId(accountId);
        
        return notifications.map(notification -> {
            NotificationResponse response = notificationMapper.toResponse(notification);
            // Nếu là broadcast notification, kiểm tra read status từ bảng BroadcastNotificationRead
            if (notification.getAccount() == null) {
                response.setRead(readBroadcastIds.contains(notification.getId()));
            }
            return response;
        });
    }

    @Override
    public List<NotificationResponse> getUnreadNotifications(Long accountId) {
        List<Notifications> notifications = notificationRepository
                .findUnreadNotificationsIncludingBroadcast(accountId);
        
        // Lấy danh sách broadcast notifications đã đọc của user
        Set<Long> readBroadcastIds = broadcastNotificationReadRepository
                .findReadBroadcastNotificationIdsByAccountId(accountId);
        
        return notifications.stream()
                .map(notification -> {
                    NotificationResponse response = notificationMapper.toResponse(notification);
                    // Nếu là broadcast notification, kiểm tra read status từ bảng BroadcastNotificationRead
                    if (notification.getAccount() == null) {
                        response.setRead(readBroadcastIds.contains(notification.getId()));
                    }
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Long getUnreadCount(Long accountId) {
        return notificationRepository.countUnreadNotificationsIncludingBroadcast(accountId);
    }

    @Override
    @Transactional
    public int markAsRead(List<Long> notificationIds, Long accountId) {
        if (notificationIds == null || notificationIds.isEmpty()) {
            return 0;
        }
        
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
        
        int count = 0;
        
        // Lấy tất cả notifications để phân biệt broadcast và normal
        List<Notifications> notifications = notificationRepository.findAllById(notificationIds);
        
        List<Long> normalNotificationIds = new ArrayList<>();
        List<Long> broadcastNotificationIds = new ArrayList<>();
        
        for (Notifications notification : notifications) {
            if (notification.getAccount() == null) {
                // Broadcast notification
                broadcastNotificationIds.add(notification.getId());
            } else if (notification.getAccount().getId().equals(accountId)) {
                // Normal notification của user
                normalNotificationIds.add(notification.getId());
            }
        }
        
        // Đánh dấu đọc normal notifications
        if (!normalNotificationIds.isEmpty()) {
            count += notificationRepository.markAsRead(normalNotificationIds, accountId);
        }
        
        // Đánh dấu đọc broadcast notifications
        for (Long broadcastId : broadcastNotificationIds) {
            if (!broadcastNotificationReadRepository.existsByAccountIdAndNotificationId(accountId, broadcastId)) {
                Notifications broadcastNotification = notificationRepository.findById(broadcastId)
                        .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));
                
                BroadcastNotificationRead broadcastRead = BroadcastNotificationRead.builder()
                        .account(account)
                        .notification(broadcastNotification)
                        .build();
                broadcastNotificationReadRepository.save(broadcastRead);
                count++;
            }
        }
        
        log.info("Marked {} notifications as read for account ID: {}", count, accountId);
        return count;
    }

    @Override
    @Transactional
    public int markAllAsRead(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
        
        // Lấy tất cả unread notifications (bao gồm cả broadcast)
        List<Notifications> unreadNotifications = notificationRepository
                .findUnreadNotificationsIncludingBroadcast(accountId);

        if (unreadNotifications.isEmpty()) {
            return 0;
        }

        List<Long> normalNotificationIds = new ArrayList<>();
        List<Notifications> broadcastNotifications = new ArrayList<>();
        
        for (Notifications notification : unreadNotifications) {
            if (notification.getAccount() == null) {
                // Broadcast notification
                broadcastNotifications.add(notification);
            } else {
                // Normal notification
                normalNotificationIds.add(notification.getId());
            }
        }
        
        int count = 0;
        
        // Đánh dấu đọc normal notifications
        if (!normalNotificationIds.isEmpty()) {
            count += notificationRepository.markAsRead(normalNotificationIds, accountId);
        }
        
        // Đánh dấu đọc broadcast notifications
        Set<Long> readBroadcastIds = broadcastNotificationReadRepository
                .findReadBroadcastNotificationIdsByAccountId(accountId);
        
        for (Notifications broadcastNotification : broadcastNotifications) {
            if (!readBroadcastIds.contains(broadcastNotification.getId())) {
                BroadcastNotificationRead broadcastRead = BroadcastNotificationRead.builder()
                        .account(account)
                        .notification(broadcastNotification)
                        .build();
                broadcastNotificationReadRepository.save(broadcastRead);
                count++;
            }
        }
        
        log.info("Marked all {} notifications as read for account ID: {}", count, accountId);
        return count;
    }

    @Override
    @Transactional
    public void deleteNotification(Long notificationId, Long accountId) {
        Notifications notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));

        // Không cho phép xóa broadcast notifications
        if (notification.getAccount() == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (!notification.getAccount().getId().equals(accountId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        notificationRepository.deleteByIdAndAccountId(notificationId, accountId);
        log.info("Deleted notification ID: {} for account ID: {}", notificationId, accountId);
    }

    @Override
    public Page<NotificationResponse> getNotificationsByType(Long accountId, Integer notificationType, Pageable pageable) {
        Page<Notifications> notifications = notificationRepository
                .findByAccountIdOrBroadcastAndNotificationTypeOrderByCreatedAtDesc(accountId, notificationType, pageable);
        
        // Lấy danh sách broadcast notifications đã đọc của user
        Set<Long> readBroadcastIds = broadcastNotificationReadRepository
                .findReadBroadcastNotificationIdsByAccountId(accountId);
        
        return notifications.map(notification -> {
            NotificationResponse response = notificationMapper.toResponse(notification);
            // Nếu là broadcast notification, kiểm tra read status từ bảng BroadcastNotificationRead
            if (notification.getAccount() == null) {
                response.setRead(readBroadcastIds.contains(notification.getId()));
            }
            return response;
        });
    }
}

