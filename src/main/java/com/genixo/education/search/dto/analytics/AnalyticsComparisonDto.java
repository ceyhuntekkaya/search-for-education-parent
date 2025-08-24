package com.genixo.education.search.dto.analytics;

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
public class AnalyticsComparisonDto {
    private String comparisonType; // PERIOD, INSTITUTION, METRIC
    private LocalDate startDate;
    private LocalDate endDate;

    // Period comparison
    private AnalyticsSummaryDto currentPeriod;
    private AnalyticsSummaryDto previousPeriod;
    private Map<String, Double> changePercentages;

    // Institution comparison
    private List<AnalyticsSummaryDto> institutionMetrics;
    private Map<String, Object> benchmarkData;

    // Metric comparison
    private List<String> comparedMetrics;
    private Map<String, List<Double>> metricTrends;

    // Insights
    private List<String> significantChanges;
    private List<String> trendAnalysis;
    private String overallAssessment;
}