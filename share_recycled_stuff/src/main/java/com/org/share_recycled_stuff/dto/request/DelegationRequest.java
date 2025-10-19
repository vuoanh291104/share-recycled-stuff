package com.org.share_recycled_stuff.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a delegation (customer asks proxy seller to sell their item)")
public class DelegationRequest {
    @Schema(
            description = "Proxy seller ID to delegate the sale to",
            example = "5",
            required = true
    )
    private Long proxySellerId;

    @Schema(
            description = "Product description",
            example = "Bàn học gỗ, 90% mới, kích thước 120x60cm",
            required = true
    )
    private String productDescription;

    @Schema(
            description = "Expected selling price in VND",
            example = "500000",
            required = true
    )
    private BigDecimal expectPrice;

    @Schema(
            description = "Bank account number for receiving payment",
            example = "1234567890",
            required = true
    )
    private String bankAccountNumber;

    @Schema(
            description = "Bank name",
            example = "Vietcombank",
            required = true
    )
    private String bankName;

    @Schema(
            description = "Account holder name",
            example = "NGUYEN VAN A",
            required = true
    )
    private String accountHolderName;

    @Schema(
            description = "Set of product image URLs",
            example = "[\"https://example.com/img1.jpg\", \"https://example.com/img2.jpg\"]"
    )
    private Set<String> imageUrls;
}
