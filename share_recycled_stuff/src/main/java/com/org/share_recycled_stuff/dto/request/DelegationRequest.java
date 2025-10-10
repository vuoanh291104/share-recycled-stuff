package com.org.share_recycled_stuff.dto.request;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DelegationRequest {
    private Long id;
    private Long proxySellerId;
    private String productDescription;
    private BigDecimal expectPrice;
    private String bankAccountNumber;
    private String bankName;
    private String accountHolderName;
    private String status;
    private String rejectionReason;
    private LocalDateTime productDeliveryDate;
    private LocalDateTime soldDate;
    private BigDecimal soldPrice;
    private BigDecimal commissionRate;
    private BigDecimal commissionFee;
    private LocalDateTime paymentToCustomerDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<String> imageUrls;
}
