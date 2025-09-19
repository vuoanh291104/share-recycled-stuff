package com.org.share_recycled_stuff.entity;

import com.org.share_recycled_stuff.entity.converter.PaymentMethodConverter;
import com.org.share_recycled_stuff.entity.converter.TransactionStatusConverter;
import com.org.share_recycled_stuff.entity.enums.PaymentMethod;
import com.org.share_recycled_stuff.entity.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"proxySeller", "revenueRecord"})
@ToString(exclude = {"proxySeller", "revenueRecord"})
public class CommissionPayments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proxy_seller_id", nullable = false)
    private Account proxySeller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "revenue_record_id", nullable = false)
    private ProxySellerMonthlyRevenue revenueRecord;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Convert(converter = PaymentMethodConverter.class)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Column(name = "vnpay_transaction_id", length = 100)
    private String vnpayTransactionId;

    @Column(name = "bank_transaction_code", length = 100)
    private String bankTransactionCode;

    @Column(name = "payment_proof_url", length = 500)
    private String paymentProofUrl;

    @Convert(converter = TransactionStatusConverter.class)
    @Column(nullable = false)
    private TransactionStatus status = TransactionStatus.PENDING;

    @Column(name = "paid_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime paidAt;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
