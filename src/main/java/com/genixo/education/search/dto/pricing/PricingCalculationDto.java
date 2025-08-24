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
public class PricingCalculationDto {
    private Long schoolId;
    private String gradeLevel;
    private String academicYear;
    private Boolean hasSibling;
    private Boolean earlyPayment;


    private BigDecimal baseTuition;
    private BigDecimal oneTimeFees;
    private BigDecimal totalDiscounts;
    private BigDecimal finalAmount;

    private BigDecimal siblingDiscount;
    private BigDecimal earlyPaymentDiscount;

    private BigDecimal downPayment;
    private Integer installmentCount;
    private BigDecimal installmentAmount;


}
