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
}

