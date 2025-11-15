package com.org.share_recycled_stuff.entity;

import com.org.share_recycled_stuff.entity.converter.PaymentStatusConverter;
import com.org.share_recycled_stuff.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "proxy_seller_monthly_revenue",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"proxy_seller_id", "month", "year"})
        })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"proxySeller", "commissionPayments"})
@ToString(exclude = {"proxySeller", "commissionPayments"})
public class ProxySellerMonthlyRevenue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proxy_seller_id", nullable = false)
    private Account proxySeller;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Integer year;

    @Column(name = "total_consignments")
    private Integer totalConsignments = 0;

    @Column(name = "completed_consignments")
    private Integer completedConsignments = 0;

    @Column(name = "total_sales_amount", precision = 10, scale = 2)
    private BigDecimal totalSalesAmount = BigDecimal.ZERO;

    @Column(name = "total_commission", precision = 10, scale = 2)
    private BigDecimal totalCommission = BigDecimal.ZERO;

    @Column(name = "admin_commission_rate", precision = 5, scale = 2)
    private BigDecimal adminCommissionRate = new BigDecimal("5.00");

    @Column(name = "admin_commission_amount", precision = 10, scale = 2)
    private BigDecimal adminCommissionAmount = BigDecimal.ZERO;

    @Convert(converter = PaymentStatusConverter.class)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.NOT_DUE;

    @Column(name = "payment_due_date")
    private LocalDateTime paymentDueDate;

    @OneToMany(mappedBy = "revenueRecord", fetch = FetchType.LAZY)
    @BatchSize(size = 10)
    private Set<CommissionPayments> commissionPayments = new HashSet<>();

    @OneToMany(mappedBy = "revenueRecord", fetch = FetchType.LAZY)
    @BatchSize(size = 10)
    private Set<PaymentReminders> paymentReminders = new HashSet<>();

    @Column(name = "payment_txn_ref")
    private String paymentTxnRef;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculateAdminCommission();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateAdminCommission();
    }

    private void calculateAdminCommission() {
        if (totalCommission != null && adminCommissionRate != null) {
            adminCommissionAmount = totalCommission.multiply(adminCommissionRate).divide(new BigDecimal("100"));
        }
    }
}
