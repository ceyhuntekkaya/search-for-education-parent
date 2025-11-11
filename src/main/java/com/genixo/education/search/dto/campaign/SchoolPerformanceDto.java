package com.genixo.education.search.dto.campaign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolPerformanceDto {
    private Long schoolId;
    private String schoolName;
    private String campusName;
    private Long views;
    private Long clicks;
    private Long applications;
    private Long conversions;
    private Double conversionRate;
    private BigDecimal revenueGenerated;
    private Integer usageCount;
    private String performanceRank;
}
