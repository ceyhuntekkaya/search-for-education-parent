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
public class PricingCalculationRequestDto {
    private Long schoolId;
    private String gradeLevel;
    private String academicYear;

    private Boolean hasSibling;
    private Boolean earlyPayment;
    private String installmentPlan;


}

