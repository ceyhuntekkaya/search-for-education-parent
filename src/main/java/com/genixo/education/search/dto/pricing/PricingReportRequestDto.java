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
public class PricingReportRequestDto {

    private List<Long> schoolIds;
    private List<String> gradeLevels;
    private List<String> academicYears;
    private LocalDate startDate; // ISO 8601 format: YYYY-MM-DD
    private LocalDate endDate;   // ISO 8601 format: YYYY-MM-DD


}
