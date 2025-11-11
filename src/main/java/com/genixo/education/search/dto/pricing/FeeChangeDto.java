package com.genixo.education.search.dto.pricing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeChangeDto {
    private String fieldName;
    private String displayName;
    private BigDecimal fromAmount;
    private BigDecimal toAmount;
    private BigDecimal changeAmount;
    private Double changePercentage;
    private String changeType; // INCREASE, DECREASE, NEW, REMOVED, NO_CHANGE
    private String formattedFromAmount;
    private String formattedToAmount;
    private String formattedChangeAmount;
}