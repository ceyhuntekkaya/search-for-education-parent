package com.genixo.education.search.dto.supply;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantUpdateDto {

    private String variantName;
    private String sku;
    private BigDecimal priceAdjustment;
    private Integer stockQuantity;
    private Boolean isActive;
}

