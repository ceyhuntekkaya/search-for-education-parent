package com.genixo.education.search.dto.supply;

import com.genixo.education.search.enumaration.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EffectivePriceDto {

    private BigDecimal basePrice;
    private BigDecimal discountAmount;
    private BigDecimal finalPrice;
    private BigDecimal taxAmount;
    private BigDecimal totalPrice;
    private Currency currency;
    private Long appliedDiscountId;
    private String appliedDiscountName;
}

