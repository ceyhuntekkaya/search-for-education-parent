package com.genixo.education.search.dto.campaign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComparisonMetricDto {
    private String metricName;
    private String metricDisplayName;
    private Double currentValue;
    private Double previousValue;
    private Double changeAmount;
    private Double changePercentage;
    private String changeDirection; // INCREASE, DECREASE, NO_CHANGE
    private String changeSignificance; // SIGNIFICANT, MODERATE, MINOR
}
