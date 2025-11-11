package com.genixo.education.search.dto.analytics;

import com.genixo.education.search.enumaration.MetricType;
import com.genixo.education.search.enumaration.TimePeriod;
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
public class AnalyticsFilterDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private TimePeriod timePeriod;
    private List<MetricType> metricTypes;
    private Long brandId;
    private Long campusId;
    private Long schoolId;
    private String dataSource;
    private Boolean includeCustomMetrics;
}