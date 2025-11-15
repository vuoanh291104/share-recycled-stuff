package com.org.share_recycled_stuff.repository;

import com.org.share_recycled_stuff.entity.BroadcastNotificationRead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface BroadcastNotificationReadRepository extends JpaRepository<BroadcastNotificationRead, Long> {
    
    boolean existsByAccountIdAndNotificationId(Long accountId, Long notificationId);
    
    @Query("SELECT bnr.notification.id FROM BroadcastNotificationRead bnr WHERE bnr.account.id = :accountId")
    Set<Long> findReadBroadcastNotificationIdsByAccountId(@Param("accountId") Long accountId);
    
    @Modifying
    @Query("DELETE FROM BroadcastNotificationRead bnr WHERE bnr.notification.id IN :notificationIds AND bnr.account.id = :accountId")
    void deleteByNotificationIdsAndAccountId(@Param("notificationIds") List<Long> notificationIds, @Param("accountId") Long accountId);
}

