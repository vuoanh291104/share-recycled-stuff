package com.org.share_recycled_stuff.repository;

import com.org.share_recycled_stuff.entity.PaymentReminders;
import com.org.share_recycled_stuff.entity.ProxySellerMonthlyRevenue;
import com.org.share_recycled_stuff.entity.enums.ReminderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRemindersRepository extends JpaRepository<PaymentReminders, Long> {
    
    boolean existsByRevenueRecordAndReminderType(
        ProxySellerMonthlyRevenue revenueRecord, 
        ReminderType reminderType
    );
}

