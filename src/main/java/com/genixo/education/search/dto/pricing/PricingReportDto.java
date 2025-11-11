package com.genixo.education.search.dto.pricing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricingReportDto {
    private String reportName;
    private LocalDate generatedAt;
    private String generatedBy;
    private List<Long> filterSchoolIds;
    private List<String> filterGradeLevels;
    private List<String> filterAcademicYears;
    private LocalDate filterStartDate;
    private LocalDate filterEndDate;
    private List<SchoolPricingDto> pricingDetails;
    private PricingStatisticsDto statistics;


}
