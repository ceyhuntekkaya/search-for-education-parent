package com.genixo.education.search.dto.pricing;

import com.genixo.education.search.enumaration.PaymentFrequency;
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
public class PricingStatisticsDto {
    private Long schoolId;
    private String schoolName;
    private Integer totalGradeLevels;
    private BigDecimal minMonthlyTuition;
    private BigDecimal maxMonthlyTuition;
    private BigDecimal minAnnualTuition;
    private BigDecimal maxAnnualTuition;
    private BigDecimal averageMonthlyTuition;
    private BigDecimal averageAnnualTuition;

    // Service availability
    private Integer transportationAvailableCount;
    private Integer cafeteriaAvailableCount;
    private Integer extendedDayAvailableCount;
    private Integer needBasedAidCount;
    private Integer meritBasedAidCount;

    // Payment options
    private List<PaymentFrequency> availableFrequencies;
    private Integer maxInstallmentCount;
    private Double averageDownPaymentPercentage;

    // Market position
    private String overallMarketPosition;
    private Integer competitorsAnalyzed;
    private Double marketPercentileRank;
}
