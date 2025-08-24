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

    private Long schoolPricingCount;
    private Integer averageMonthlyTuition;
    private Integer minimumMonthlyTuition;
    private Integer maximumMonthlyTuition;
    private Integer averageAnnualTuition;
    private Integer minimumAnnualTuition;


    private Integer maximumAnnualTuition;
    private Integer averageRegistrationFee;
    private Integer averageTotalAnnualCost;
    private Long distinctGradeLevels;
    private Long distinctAcademicYears;



}
