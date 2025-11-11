package com.genixo.education.search.dto.appointment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentMetricsDto {
    private String metricName;
    private String metricDisplayName;
    private String metricType; // COUNT, PERCENTAGE, AVERAGE, RATIO
    private Double currentValue;
    private Double previousValue;
    private Double changeAmount;
    private Double changePercentage;
    private String changeDirection; // UP, DOWN, STABLE
    private String trendDirection; // IMPROVING, DECLINING, STABLE
    private String benchmark; // INDUSTRY_AVERAGE, TARGET, COMPETITOR
    private Double benchmarkValue;
    private String performanceLevel; // EXCELLENT, GOOD, AVERAGE, BELOW_AVERAGE, POOR
    private List<String> insights;
    private List<String> recommendations;


    public AppointmentMetricsDto(String metricName, String metricDisplayName, String metricType,
                                 Double currentValue) {
        this.metricName = metricName;
        this.metricDisplayName = metricDisplayName;
        this.metricType = metricType;
        this.currentValue = currentValue;
        this.previousValue = 0.0;
        this.changeAmount = 0.0;
        this.changePercentage = 0.0;
        this.changeDirection = "STABLE";
        this.trendDirection = "STABLE";
        this.benchmark = "TARGET";
        this.benchmarkValue = 0.0;
        this.performanceLevel = "AVERAGE";

        // List'ler service'te set edilecek
        this.insights = new ArrayList<>();
        this.recommendations = new ArrayList<>();
    }

}