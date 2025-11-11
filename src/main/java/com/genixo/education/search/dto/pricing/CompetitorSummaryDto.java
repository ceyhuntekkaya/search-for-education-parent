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
public class CompetitorSummaryDto {
    private Long schoolId;
    private String schoolName;
    private String campusName;
    private BigDecimal monthlyTuition;
    private BigDecimal annualTuition;
    private Double ratingAverage;
    private Long ratingCount;
    private Double distanceKm;
    private String institutionTypeName;
    private Boolean hasActiveCampaigns;
    private String priceComparison; // LOWER, SIMILAR, HIGHER
}