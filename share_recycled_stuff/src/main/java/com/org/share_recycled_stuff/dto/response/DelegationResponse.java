package com.org.share_recycled_stuff.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DelegationResponse {
    private Long id;
    private Long customerId;
    private Long proxySellerId;
    private String productDescription;
    private BigDecimal expectPrice;
    private String bankAccountNumber;
    private String bankName;
    private String accountHolderName;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<String> imageUrls;
}
