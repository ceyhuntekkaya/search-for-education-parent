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
public class MarketAveragesDto {
    private Double monthlyTuition;        // AVG BigDecimal döner
    private Double annualTuition;         // AVG BigDecimal döner
    private Double registrationFee;       // AVG BigDecimal döner
    private Double totalAnnualCost;       // AVG BigDecimal döner
    private Double minMonthlyTuition;        // MIN Integer döner
    private Double maxMonthlyTuition;        // MAX Integer döner
    private Long numberOfSchools;


}
