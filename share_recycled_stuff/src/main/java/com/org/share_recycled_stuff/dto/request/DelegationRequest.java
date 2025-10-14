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
    private Long proxySellerId;
    private String productDescription;
    private BigDecimal expectPrice;
    private String bankAccountNumber;
    private String bankName;
    private String accountHolderName;
    private Set<String> imageUrls;
}
