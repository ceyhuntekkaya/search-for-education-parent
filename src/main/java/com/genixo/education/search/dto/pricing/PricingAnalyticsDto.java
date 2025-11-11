package com.genixo.education.search.dto.pricing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricingAnalyticsDto {

    private Long totalPricingEntries;
    private Double averageMonthlyTuition;
    private Double minimumMonthlyTuition;
    private Double maximumMonthlyTuition;
    private Double averageAnnualTuition;
    private Double minimumAnnualTuition;
    private Double maximumAnnualTuition;
    private Double averageRegistrationFee;
    private Double averageTotalAnnualCost;
    private Long distinctGradeLevels;
    private Long distinctAcademicYears;





}
