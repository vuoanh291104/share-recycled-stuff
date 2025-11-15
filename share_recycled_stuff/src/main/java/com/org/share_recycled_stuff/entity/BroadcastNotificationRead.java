package com.org.share_recycled_stuff.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "broadcast_notification_read", indexes = {
        @Index(name = "idx_broadcast_read_account_notification", columnList = "account_id,notification_id"),
        @Index(name = "idx_broadcast_read_account", columnList = "account_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"account", "notification"})
@ToString(exclude = {"account", "notification"})
public class BroadcastNotificationRead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id", nullable = false)
    private Notifications notification;

    @Column(name = "read_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime readAt;

    @PrePersist
    protected void onCreate() {
        readAt = LocalDateTime.now();
    }
}

