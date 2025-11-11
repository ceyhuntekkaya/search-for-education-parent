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
public class MarketComparisonDto {
    private Long schoolId;
    private String schoolName;
    private String districtName;
    private String institutionTypeName;

    // School's pricing
    private BigDecimal schoolMonthlyTuition;
    private BigDecimal schoolAnnualTuition;

    // Market data
    private BigDecimal marketAverageMonthly;
    private BigDecimal marketMedianMonthly;
    private BigDecimal marketMinMonthly;
    private BigDecimal marketMaxMonthly;

    // Positioning
    private String marketPosition; // BUDGET, COMPETITIVE, PREMIUM, LUXURY
    private Double percentileRank; // 0-100
    private Integer competitorsCount;
    private Integer schoolsAbovePrice;
    private Integer schoolsBelowPrice;

    // Recommendations
    private List<String> recommendations;
    private String competitiveAdvantage;
    private List<CompetitorSummaryDto> nearbyCompetitors;
}
