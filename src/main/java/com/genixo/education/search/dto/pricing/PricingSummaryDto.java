package com.genixo.education.search.dto.pricing;

import com.genixo.education.search.enumaration.Currency;
import com.genixo.education.search.enumaration.PaymentFrequency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricingSummaryDto {
    private Long schoolId;
    private String schoolName;
    private String academicYear;
    private String gradeLevel;
    private BigDecimal monthlyTuition;
    private BigDecimal annualTuition;
    private BigDecimal totalAnnualCost;
    private Currency currency;
    private Boolean hasTransportation;
    private Boolean hasCafeteria;
    private Boolean hasExtendedDay;
    private Boolean hasFinancialAid;
    private PaymentFrequency paymentFrequency;
    private Integer installmentCount;
    private String formattedMonthlyFee;
    private String formattedAnnualFee;
    private String marketPosition;
}