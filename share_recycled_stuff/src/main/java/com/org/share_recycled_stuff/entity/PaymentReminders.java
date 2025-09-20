package com.org.share_recycled_stuff.entity;

import com.org.share_recycled_stuff.entity.converter.ReminderTypeConverter;
import com.org.share_recycled_stuff.entity.enums.ReminderType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"proxySeller", "revenueRecord"})
@ToString(exclude = {"proxySeller", "revenueRecord"})
public class PaymentReminders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proxy_seller_id", nullable = false)
    private Account proxySeller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "revenue_record_id", nullable = false)
    private ProxySellerMonthlyRevenue revenueRecord;

    @Convert(converter = ReminderTypeConverter.class)
    @Column(name = "reminder_type", nullable = false)
    private ReminderType reminderType;

    @Column(name = "sent_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime sentAt;

    @PrePersist
    protected void onCreate() {
        if (sentAt == null) {
            sentAt = LocalDateTime.now();
        }
    }
}
