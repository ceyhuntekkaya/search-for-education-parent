package com.genixo.education.search.dto.pricing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketAveragesDto {
    private Integer monthlyTuition;
    private Integer annualTuition;
    private Integer registrationFee;
    private Integer totalAnnualCost;
    private Integer minMonthlyTuition;
    private Integer maxMonthlyTuition;
    private Long numberOfSchools;



}
