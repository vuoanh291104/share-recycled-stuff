package com.org.share_recycled_stuff.repository;

import com.org.share_recycled_stuff.entity.Notifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notifications, Long> {

    Page<Notifications> findByAccountIdOrderByCreatedAtDesc(Long accountId, Pageable pageable);

    List<Notifications> findByAccountIdAndIsReadFalseOrderByCreatedAtDesc(Long accountId);

    Long countByAccountIdAndIsReadFalse(Long accountId);

    @Modifying
    @Query("UPDATE Notifications n SET n.isRead = true WHERE n.id IN :ids AND n.account.id = :accountId")
    int markAsRead(@Param("ids") List<Long> ids, @Param("accountId") Long accountId);

    void deleteByIdAndAccountId(Long id, Long accountId);

    Page<Notifications> findByAccountIdAndNotificationTypeOrderByCreatedAtDesc(
            Long accountId, Integer notificationType, Pageable pageable);

    // Query để lấy cả notifications của user và broadcast notifications (account IS NULL)
    @Query("SELECT n FROM Notifications n WHERE (n.account.id = :accountId OR n.account IS NULL) ORDER BY n.createdAt DESC")
    Page<Notifications> findByAccountIdOrBroadcastOrderByCreatedAtDesc(@Param("accountId") Long accountId, Pageable pageable);

    // Query để lấy unread notifications (bao gồm cả broadcast chưa đọc)
    @Query("SELECT n FROM Notifications n WHERE " +
           "(n.account.id = :accountId AND n.isRead = false) OR " +
           "(n.account IS NULL AND NOT EXISTS " +
           "(SELECT 1 FROM BroadcastNotificationRead bnr WHERE bnr.notification.id = n.id AND bnr.account.id = :accountId)) " +
           "ORDER BY n.createdAt DESC")
    List<Notifications> findUnreadNotificationsIncludingBroadcast(@Param("accountId") Long accountId);

    // Query để đếm unread notifications (bao gồm cả broadcast chưa đọc)
    @Query("SELECT COUNT(n) FROM Notifications n WHERE " +
           "(n.account.id = :accountId AND n.isRead = false) OR " +
           "(n.account IS NULL AND NOT EXISTS " +
           "(SELECT 1 FROM BroadcastNotificationRead bnr WHERE bnr.notification.id = n.id AND bnr.account.id = :accountId))")
    Long countUnreadNotificationsIncludingBroadcast(@Param("accountId") Long accountId);

    // Query để lấy notifications theo type (bao gồm cả broadcast)
    @Query("SELECT n FROM Notifications n WHERE (n.account.id = :accountId OR n.account IS NULL) " +
           "AND n.notificationType = :notificationType ORDER BY n.createdAt DESC")
    Page<Notifications> findByAccountIdOrBroadcastAndNotificationTypeOrderByCreatedAtDesc(
            @Param("accountId") Long accountId, @Param("notificationType") Integer notificationType, Pageable pageable);

    // Query để lấy broadcast notifications (account IS NULL)
    @Query("SELECT n FROM Notifications n WHERE n.account IS NULL ORDER BY n.createdAt DESC")
    List<Notifications> findBroadcastNotifications();
}

