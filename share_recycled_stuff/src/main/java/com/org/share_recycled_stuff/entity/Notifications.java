package com.org.share_recycled_stuff.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_notification_account_read", columnList = "account_id,is_read"),
        @Index(name = "idx_notification_type", columnList = "notification_type")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"account", "createdBy"})
@ToString(exclude = {"account", "createdBy"})
public class Notifications {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "notification_type")
    private Integer notificationType;
    // 1: SYSTEM, 2: CONSIGNMENT_STATUS, 3: NEW_REVIEW, etc.

    @Column(name = "delivery_method")
    private Integer deliveryMethod = 1;
    // 1: IN_APP, 2: EMAIL, 3: BOTH

    @Column(name = "is_read")
    private boolean isRead = false;

    @Column(name = "related_entity_type", length = 50)
    private String relatedEntityType;

    @Column(name = "related_entity_id")
    private Long relatedEntityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private Account createdBy;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
