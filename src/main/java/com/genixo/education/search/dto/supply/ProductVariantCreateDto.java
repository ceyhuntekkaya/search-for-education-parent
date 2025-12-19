package com.genixo.education.search.dto.supply;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantCreateDto {

    @NotBlank(message = "Variant name is required")
    private String variantName;

    private String sku;

    private BigDecimal priceAdjustment;

    @Builder.Default
    private Integer stockQuantity = 0;

    @Builder.Default
    private Boolean isActive = true;
}

