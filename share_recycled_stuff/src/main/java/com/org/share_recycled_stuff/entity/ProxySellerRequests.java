package com.org.share_recycled_stuff.entity;

import com.org.share_recycled_stuff.entity.converter.RequestStatusConverter;
import com.org.share_recycled_stuff.entity.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"account", "processedBy"})
@ToString(exclude = {"account", "processedBy"})
public class ProxySellerRequests {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "id_card", nullable = false, length = 20)
    private String idCard;

    @Column(name = "id_card_front_image", length = 500)
    private String idCardFrontImage;

    @Column(name = "id_card_back_image", length = 500)
    private String idCardBackImage;

    @Convert(converter = RequestStatusConverter.class)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDING;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by")
    private Account processedBy;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
