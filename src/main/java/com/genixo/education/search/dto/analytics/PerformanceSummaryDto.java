package com.genixo.education.search.dto.analytics;

import com.genixo.education.search.enumaration.PerformanceMetricCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceSummaryDto {
    private LocalDateTime timestamp;
    private PerformanceMetricCategory metricCategory;
    private String endpointUrl;
    private Integer responseTimeMs;
    private Boolean success;
    private Double memoryUsagePercentage;
    private Double cpuUsagePercentage;
    private Boolean cacheHit;
    private Boolean thresholdExceeded;
}
