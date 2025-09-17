package com.org.share_recycled_stuff.entity;

import com.org.share_recycled_stuff.entity.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_roles",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"account_id", "role_type"})})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "role_type", nullable = false)
    private Role roleType;

    @CreationTimestamp
    @Column(name = "assigned_at", updatable = false)
    private LocalDateTime assignedAt;

    @ManyToOne
    @JoinColumn(name = "assigned_by")
    private Account assignedBy;
}
