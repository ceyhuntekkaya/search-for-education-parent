package com.genixo.education.search.dto.supply;

import com.genixo.education.search.enumaration.Currency;
import com.genixo.education.search.enumaration.ProductStatus;
import com.genixo.education.search.enumaration.StockTrackingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCreateDto {

    @NotNull(message = "Supplier ID is required")
    private Long supplierId;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotBlank(message = "Product name is required")
    private String name;

    private String sku;

    private String description;

    private String technicalSpecs;

    @Builder.Default
    private ProductStatus status = ProductStatus.ACTIVE;

    @Builder.Default
    private StockTrackingType stockTrackingType = StockTrackingType.LIMITED;

    private Integer stockQuantity = 0;

    private Integer minStockLevel = 0;

    @NotNull(message = "Base price is required")
    @Positive(message = "Base price must be positive")
    private BigDecimal basePrice;

    @Builder.Default
    private Currency currency = Currency.TRY;

    private BigDecimal taxRate;

    @Builder.Default
    private Integer minOrderQuantity = 1;

    @Builder.Default
    private Integer deliveryDays = 7;

    private String mainImageUrl;
}

