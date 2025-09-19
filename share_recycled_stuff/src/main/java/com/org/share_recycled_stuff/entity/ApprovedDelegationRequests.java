package com.org.share_recycled_stuff.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"delegationRequest", "approvedBy"})
@ToString(exclude = {"delegationRequest", "approvedBy"})
public class ApprovedDelegationRequests {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delegation_request_id", nullable = false, unique = true)
    private DelegationRequests delegationRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by", nullable = false)
    private Account approvedBy;

    @Column(name = "approved_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime approvedAt;

    @Column(columnDefinition = "TEXT")
    private String note;

    @PrePersist
    protected void onCreate() {
        if (approvedAt == null) {
            approvedAt = LocalDateTime.now();
        }
    }
}
