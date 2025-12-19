package com.genixo.education.search.dto.supply;

import com.genixo.education.search.enumaration.Currency;
import com.genixo.education.search.enumaration.ProductStatus;
import com.genixo.education.search.enumaration.StockTrackingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductUpdateDto {

    private Long categoryId;
    private String name;
    private String sku;
    private String description;
    private String technicalSpecs;
    private ProductStatus status;
    private StockTrackingType stockTrackingType;
    private Integer stockQuantity;
    private Integer minStockLevel;
    private BigDecimal basePrice;
    private Currency currency;
    private BigDecimal taxRate;
    private Integer minOrderQuantity;
    private Integer deliveryDays;
    private String mainImageUrl;
}

