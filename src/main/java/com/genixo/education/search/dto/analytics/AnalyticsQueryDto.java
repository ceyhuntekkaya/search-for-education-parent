package com.genixo.education.search.dto.analytics;

import com.genixo.education.search.enumaration.TimePeriod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsQueryDto {
    private String queryName;
    private String queryDescription;
    private LocalDate startDate;
    private LocalDate endDate;
    private TimePeriod groupBy;
    private List<String> metrics;
    private List<String> dimensions;
    private Map<String, Object> filters;
    private String orderBy;
    private String orderDirection;
    private Integer limit;
    private Integer offset;

    // Advanced options
    private Boolean includeComparisons;
    private Boolean includeTrends;
    private Boolean includeForecasts;
    private String segmentation;
}
