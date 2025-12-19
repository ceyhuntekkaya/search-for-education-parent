package com.genixo.education.search.dto.supply;

import com.genixo.education.search.enumaration.Currency;
import com.genixo.education.search.enumaration.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSummaryDto {

    private Long id;
    private String name;
    private String sku;
    private ProductStatus status;
    private BigDecimal basePrice;
    private Currency currency;
    private String mainImageUrl;
    private Integer stockQuantity;
}

