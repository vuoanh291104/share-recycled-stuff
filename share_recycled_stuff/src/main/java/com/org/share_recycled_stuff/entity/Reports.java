package com.org.share_recycled_stuff.entity;

import com.org.share_recycled_stuff.entity.converter.ReportStatusConverter;
import com.org.share_recycled_stuff.entity.converter.ReportTypeConverter;
import com.org.share_recycled_stuff.entity.enums.ReportStatus;
import com.org.share_recycled_stuff.entity.enums.ReportType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"reporter", "reportedPost", "reportedAccount", "processedBy"})
@ToString(exclude = {"reporter", "reportedPost", "reportedAccount", "processedBy"})
public class Reports {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private Account reporter;

    @Convert(converter = ReportTypeConverter.class)
    @Column(name = "report_type", nullable = false)
    private ReportType reportType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_post_id")
    private Post reportedPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_account_id")
    private Account reportedAccount;

    @Column(length = 500)
    private String title;

    @Column(name = "violation_type", length = 100)
    private String violationType;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "evidence_url", length = 500)
    private String evidenceUrl;

    @Convert(converter = ReportStatusConverter.class)
    @Column(nullable = false)
    private ReportStatus status = ReportStatus.PENDING;

    @Column(name = "admin_response", columnDefinition = "TEXT")
    private String adminResponse;

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
