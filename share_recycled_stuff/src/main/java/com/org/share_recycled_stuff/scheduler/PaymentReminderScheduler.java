package com.org.share_recycled_stuff.scheduler;

import com.org.share_recycled_stuff.entity.PaymentReminders;
import com.org.share_recycled_stuff.entity.ProxySellerMonthlyRevenue;
import com.org.share_recycled_stuff.entity.enums.PaymentStatus;
import com.org.share_recycled_stuff.entity.enums.ReminderType;
import com.org.share_recycled_stuff.repository.PaymentRemindersRepository;
import com.org.share_recycled_stuff.repository.ProxySellerMonthlyRevenueRepository;
import com.org.share_recycled_stuff.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentReminderScheduler {

    private final ProxySellerMonthlyRevenueRepository revenueRepository;
    private final PaymentRemindersRepository reminderRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 9 * * ?")
    @Transactional
    public void checkAndSendPaymentReminders() {
        log.info("Starting payment reminder check...");
        
        LocalDateTime now = LocalDateTime.now();
        
        sendDueSoonReminders(now);
        
        updateOverduePayments(now);
        
        sendFinalWarnings(now);
        
        log.info("Payment reminder check completed");
    }

    private void sendDueSoonReminders(LocalDateTime now) {
        LocalDateTime threeDaysLater = now.plusDays(3);
        
        List<ProxySellerMonthlyRevenue> dueSoonRecords = 
            revenueRepository.findPendingPaymentsDueBetween(
                now, 
                threeDaysLater, 
                PaymentStatus.PENDING
            );
        
        for (ProxySellerMonthlyRevenue record : dueSoonRecords) {
            boolean alreadySent = reminderRepository.existsByRevenueRecordAndReminderType(
                record, 
                ReminderType.DUE_SOON
            );
            
            if (!alreadySent) {
                sendReminder(record, ReminderType.DUE_SOON);
            }
        }
        
        log.info("Sent {} DUE_SOON reminders", dueSoonRecords.size());
    }

    private void updateOverduePayments(LocalDateTime now) {
        List<ProxySellerMonthlyRevenue> overdueRecords = 
            revenueRepository.findOverduePayments(now, PaymentStatus.PENDING);
        
        for (ProxySellerMonthlyRevenue record : overdueRecords) {
            record.setPaymentStatus(PaymentStatus.OVERDUE);
            revenueRepository.save(record);
            
            boolean alreadySent = reminderRepository.existsByRevenueRecordAndReminderType(
                record, 
                ReminderType.OVERDUE
            );
            
            if (!alreadySent) {
                sendReminder(record, ReminderType.OVERDUE);
            }
        }
        
        log.info("Updated {} payments to OVERDUE", overdueRecords.size());
    }

    private void sendFinalWarnings(LocalDateTime now) {
        LocalDateTime sevenDaysAgo = now.minusDays(7);
        
        List<ProxySellerMonthlyRevenue> finalWarningRecords = 
            revenueRepository.findOverduePaymentsBefore(
                sevenDaysAgo, 
                PaymentStatus.OVERDUE
            );
        
        for (ProxySellerMonthlyRevenue record : finalWarningRecords) {
            boolean alreadySent = reminderRepository.existsByRevenueRecordAndReminderType(
                record, 
                ReminderType.FINAL_WARNING
            );
            
            if (!alreadySent) {
                sendReminder(record, ReminderType.FINAL_WARNING);
            }
        }
        
        log.info("Sent {} FINAL_WARNING reminders", finalWarningRecords.size());
    }

    private void sendReminder(ProxySellerMonthlyRevenue record, ReminderType type) {
        try {
            String title = getReminderTitle(type);
            String content = getReminderContent(record, type);
            
            notificationService.createNotification(
                record.getProxySeller().getId(),
                title,
                content,
                10,
                3,
                "ProxySellerMonthlyRevenue",
                record.getId()
            );
            
            PaymentReminders reminder = PaymentReminders.builder()
                .proxySeller(record.getProxySeller())
                .revenueRecord(record)
                .reminderType(type)
                .build();
            
            reminderRepository.save(reminder);
            
            log.info("Sent {} reminder to Proxy Seller ID: {} for period {}/{}",
                type, record.getProxySeller().getId(), record.getMonth(), record.getYear());
                
        } catch (Exception e) {
            log.error("Failed to send reminder for revenue record ID: {}", record.getId(), e);
        }
    }

    private String getReminderTitle(ReminderType type) {
        return switch (type) {
            case DUE_SOON -> "⏰ Nhắc nhở: Phí chiết khấu sắp đến hạn";
            case OVERDUE -> "⚠️ Cảnh báo: Phí chiết khấu đã quá hạn";
            case FINAL_WARNING -> "🚨 Cảnh báo nghiêm trọng: Vui lòng thanh toán ngay";
        };
    }

    private String getReminderContent(ProxySellerMonthlyRevenue record, ReminderType type) {
        String period = String.format("%02d/%d", record.getMonth(), record.getYear());
        String amount = record.getAdminCommissionAmount().toString();
        
        return switch (type) {
            case DUE_SOON -> String.format(
                "Phí chiết khấu lợi nhuận tháng %s (số tiền: %s VNĐ) sẽ đến hạn thanh toán vào ngày %s. " +
                "Vui lòng thanh toán trước hạn để tránh ảnh hưởng đến hoạt động kinh doanh.",
                period, amount, record.getPaymentDueDate().toLocalDate()
            );
            case OVERDUE -> String.format(
                "Phí chiết khấu lợi nhuận tháng %s (số tiền: %s VNĐ) đã quá hạn thanh toán. " +
                "Vui lòng thanh toán ngay để tránh bị khóa tài khoản.",
                period, amount
            );
            case FINAL_WARNING -> String.format(
                "Đây là cảnh báo cuối cùng! Phí chiết khấu lợi nhuận tháng %s (số tiền: %s VNĐ) " +
                "đã quá hạn thanh toán hơn 7 ngày. Tài khoản của bạn có thể bị tạm khóa nếu không thanh toán trong 24 giờ tới.",
                period, amount
            );
        };
    }
}

