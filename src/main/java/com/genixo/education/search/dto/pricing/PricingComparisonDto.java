package com.genixo.education.search.dto.pricing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricingComparisonDto {
    private String fromYear;
    private String toYear;
    private String gradeLevel;
    private BigDecimal fromTotal;
    private BigDecimal toTotal;
    private BigDecimal totalChangeAmount;
    private Double totalChangePercentage;
    private List<FeeChangeDto> feeChanges;
    private String overallTrend; // INCREASE, DECREASE, STABLE
}