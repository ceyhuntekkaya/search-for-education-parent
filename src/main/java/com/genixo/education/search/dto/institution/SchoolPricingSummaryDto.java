package com.genixo.education.search.dto.institution;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolPricingSummaryDto {
    private Long id;
    private String academicYear;
    private String gradeLevel;
    private Double monthlyTuition;
    private Double annualTuition;
    private Double registrationFee;
    private String currency;
    private Boolean isCurrent;
}
