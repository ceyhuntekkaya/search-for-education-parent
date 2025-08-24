package com.genixo.education.search.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsResultDto {
    private String queryId;
    private LocalDateTime executedAt;
    private Long executionTimeMs;
    private Integer totalRows;

    // Result data
    private List<Map<String, Object>> data;
    private Map<String, Object> summary;
    private Map<String, Object> totals;

    // Metadata
    private List<String> columns;
    private Map<String, String> columnTypes;
    private String nextPageToken;

    // Insights
    private List<String> insights;
    private Map<String, Double> trends;
    private Map<String, Object> forecasts;
}