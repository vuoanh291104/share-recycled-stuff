package com.org.share_recycled_stuff.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Delegation response with customer, proxy seller, and product information")
public class DelegationResponse {
    @Schema(description = "Delegation ID", example = "1")
    private Long id;

    @Schema(description = "Customer ID", example = "2")
    private Long customerId;

    @Schema(description = "Proxy seller ID", example = "5")
    private Long proxySellerId;
    private String customerName;
    private String proxySellerName;

    @Schema(description = "Product description", example = "Bàn học gỗ, 90% mới")
    private String productDescription;

    @Schema(description = "Expected selling price in VND", example = "500000")
    private BigDecimal expectPrice;

    @Schema(description = "Bank account number", example = "1234567890")
    private String bankAccountNumber;

    @Schema(description = "Bank name", example = "Vietcombank")
    private String bankName;

    @Schema(description = "Account holder name", example = "NGUYEN VAN A")
    private String accountHolderName;

    @Schema(description = "Delegation status (PENDING/APPROVED/REJECTED)", example = "PENDING")
    private String status;

    @Schema(description = "Creation timestamp", example = "2024-01-01T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2024-01-02T15:00:00")
    private LocalDateTime updatedAt;

    @Schema(description = "Set of product image URLs")
    private Set<String> imageUrls;
}
