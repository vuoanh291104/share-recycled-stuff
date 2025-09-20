package com.org.share_recycled_stuff.entity;

import com.org.share_recycled_stuff.entity.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"account", "assignedBy"})
@ToString(exclude = {"account", "assignedBy"})
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "role_type", nullable = false)
    private Role roleType;

    @Column(name = "assigned_at", updatable = false)
    private LocalDateTime assignedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by")
    private Account assignedBy;

    @PrePersist
    protected void onCreate() {
        assignedAt = LocalDateTime.now();
    }
}
