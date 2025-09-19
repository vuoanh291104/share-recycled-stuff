package com.org.share_recycled_stuff.entity;

import com.org.share_recycled_stuff.entity.converter.DelegationRequestsStatusConverter;
import com.org.share_recycled_stuff.entity.enums.DelegationRequestsStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"customer", "proxySeller", "images", "approvedDelegation"})
@ToString(exclude = {"customer", "proxySeller", "images", "approvedDelegation"})
public class DelegationRequests {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Account customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proxy_seller_id", nullable = false)
    private Account proxySeller;

    @Column(name = "product_description", columnDefinition = "TEXT", nullable = false)
    private String productDescription;

    @Column(name = "expect_price", precision = 10, scale = 2)
    private BigDecimal expectPrice;

    @Column(name = "bank_account_number", length = 50)
    private String bankAccountNumber;

    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Column(name = "account_holder_name", length = 255)
    private String accountHolderName;

    @Convert(converter = DelegationRequestsStatusConverter.class)
    @Column(nullable = false)
    private DelegationRequestsStatus status = DelegationRequestsStatus.PENDING;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "product_delivery_date")
    private LocalDateTime productDeliveryDate;

    @Column(name = "sold_date")
    private LocalDateTime soldDate;

    @Column(name = "sold_price", precision = 10, scale = 2)
    private BigDecimal soldPrice;

    @Column(name = "commission_rate", precision = 5, scale = 2)
    private BigDecimal commissionRate = new BigDecimal("10.00");

    @Column(name = "commission_fee", precision = 10, scale = 2)
    private BigDecimal commissionFee;

    @Column(name = "payment_to_customer_date")
    private LocalDateTime paymentToCustomerDate;

    @OneToMany(mappedBy = "delegationRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    @OrderBy("displayOrder ASC")
    private Set<DelegationImages> images = new HashSet<>();

    @OneToOne(mappedBy = "delegationRequest")
    private ApprovedDelegationRequests approvedDelegation;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (soldPrice != null && commissionRate != null) {
            commissionFee = soldPrice.multiply(commissionRate).divide(new BigDecimal("100"));
        }
    }
}
