package com.org.share_recycled_stuff.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Column(name = "google_id")
    private String googleId;

    @Column(name = "is_verified")
    private boolean isVerified = false;

    @Column(name = "is_locked")
    private boolean isLocked = false;

    @Column(name = "locked_reason", columnDefinition = "TEXT")
    private String lockedReason;

    @Column(name = "locked_at")
    private LocalDateTime lockedAt;

    @Column(name = "verification_token")
    private String verificationToken;

    @Column(name = "verification_expiry")
    private LocalDateTime verificationExpiry;

    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    @Column(name = "reset_token_expires")
    private LocalDateTime resetTokenExpires;

    @Column(name = "login_attempts")
    private int loginAttempts = 0;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "last_login_ip", length = 45)
    private String lastLoginIp;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
