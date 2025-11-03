package com.org.share_recycled_stuff.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "accounts", indexes = {
        @Index(name = "idx_account_email", columnList = "email"),
        @Index(name = "idx_account_google_id", columnList = "google_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"user", "roles", "posts", "sentMessages", "receivedMessages", "notifications"})
@ToString(exclude = {"user", "roles", "password", "posts", "sentMessages", "receivedMessages", "notifications"})
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 255)
    private String email;

    @Column(length = 255)
    private String password;

    @Column(name = "google_id", length = 255)
    private String googleId;

    @Column(name = "is_verified")
    private boolean isVerified = false;

    @Column(name = "is_locked")
    private boolean isLocked = false;

    @Column(name = "locked_reason", columnDefinition = "TEXT")
    private String lockedReason;

    @Column(name = "locked_at")
    private LocalDateTime lockedAt;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    @Column(name = "verification_token", length = 255)
    private String verificationToken;

    @Column(name = "verification_expires")
    private LocalDateTime verificationExpiry;

    @Column(name = "reset_password_token", length = 255)
    private String resetPasswordToken;

    @Column(name = "reset_token_expires")
    private LocalDateTime resetTokenExpires;

    @Column(name = "login_attempts")
    private Integer loginAttempts = 0;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "last_login_ip", length = 45)
    private String lastLoginIp;

    @JsonManagedReference("account-user")
    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private User user;

    @JsonManagedReference("account-roles")
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @BatchSize(size = 10)
    private Set<UserRole> roles = new HashSet<>();

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private Set<Post> posts = new HashSet<>();

    @OneToMany(mappedBy = "sender", fetch = FetchType.LAZY)
    private Set<Messages> sentMessages = new HashSet<>();

    @OneToMany(mappedBy = "receiver", fetch = FetchType.LAZY)
    private Set<Messages> receivedMessages = new HashSet<>();

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private Set<Notifications> notifications = new HashSet<>();

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isCurrentlyLocked() {
        if (!this.isLocked) {
            return false;
        }

        // Permanent lock (lockedUntil = null) → vẫn bị lock
        if (this.lockedUntil == null) {
            return true;
        }

        // Temporary lock → check expiry
        if (this.lockedUntil.isBefore(LocalDateTime.now())) {
            // Lock đã hết hạn → coi như không bị lock
            return false;
        }

        // Vẫn còn bị lock
        return true;
    }
}
