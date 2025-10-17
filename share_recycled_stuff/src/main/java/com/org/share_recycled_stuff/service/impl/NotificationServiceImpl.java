package com.org.share_recycled_stuff.service.impl;

import com.org.share_recycled_stuff.dto.request.CreateNotificationRequest;
import com.org.share_recycled_stuff.dto.response.NotificationResponse;
import com.org.share_recycled_stuff.entity.Account;
import com.org.share_recycled_stuff.entity.Notifications;
import com.org.share_recycled_stuff.exception.AppException;
import com.org.share_recycled_stuff.exception.ErrorCode;
import com.org.share_recycled_stuff.mapper.NotificationMapper;
import com.org.share_recycled_stuff.repository.AccountRepository;
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

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final AccountRepository accountRepository;
    private final SseEmitterManager sseEmitterManager;
    private final EmailService emailService;
    private final NotificationMapper notificationMapper;

    @Override
    @Transactional
    public NotificationResponse createNotification(CreateNotificationRequest request) {
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

    @Override
    public Page<NotificationResponse> getUserNotifications(Long accountId, Pageable pageable) {
        Page<Notifications> notifications = notificationRepository
                .findByAccountIdOrderByCreatedAtDesc(accountId, pageable);
        return notifications.map(notificationMapper::toResponse);
    }

    @Override
    public List<NotificationResponse> getUnreadNotifications(Long accountId) {
        List<Notifications> notifications = notificationRepository
                .findByAccountIdAndIsReadFalseOrderByCreatedAtDesc(accountId);
        return notifications.stream()
                .map(notificationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Long getUnreadCount(Long accountId) {
        return notificationRepository.countByAccountIdAndIsReadFalse(accountId);
    }

    @Override
    @Transactional
    public int markAsRead(List<Long> notificationIds, Long accountId) {
        if (notificationIds == null || notificationIds.isEmpty()) {
            return 0;
        }
        int count = notificationRepository.markAsRead(notificationIds, accountId);
        log.info("Marked {} notifications as read for account ID: {}", count, accountId);
        return count;
    }

    @Override
    @Transactional
    public int markAllAsRead(Long accountId) {
        List<Notifications> unreadNotifications = notificationRepository
                .findByAccountIdAndIsReadFalseOrderByCreatedAtDesc(accountId);
        
        if (unreadNotifications.isEmpty()) {
            return 0;
        }

        List<Long> ids = unreadNotifications.stream()
                .map(Notifications::getId)
                .collect(Collectors.toList());
        
        int count = notificationRepository.markAsRead(ids, accountId);
        log.info("Marked all {} notifications as read for account ID: {}", count, accountId);
        return count;
    }

    @Override
    @Transactional
    public void deleteNotification(Long notificationId, Long accountId) {
        Notifications notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if (!notification.getAccount().getId().equals(accountId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        notificationRepository.deleteByIdAndAccountId(notificationId, accountId);
        log.info("Deleted notification ID: {} for account ID: {}", notificationId, accountId);
    }

    @Override
    public Page<NotificationResponse> getNotificationsByType(Long accountId, Integer notificationType, Pageable pageable) {
        Page<Notifications> notifications = notificationRepository
                .findByAccountIdAndNotificationTypeOrderByCreatedAtDesc(accountId, notificationType, pageable);
        return notifications.map(notificationMapper::toResponse);
    }
}

