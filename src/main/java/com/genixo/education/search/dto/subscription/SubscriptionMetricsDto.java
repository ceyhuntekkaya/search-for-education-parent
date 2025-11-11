package com.genixo.education.search.dto.subscription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionMetricsDto {
    private String metricType; // MRR, ARR, CHURN, LTV, CAC
    private String period; // DAILY, WEEKLY, MONTHLY, QUARTERLY, YEARLY
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // Core metrics
    private BigDecimal monthlyRecurringRevenue;
    private BigDecimal annualRecurringRevenue;
    private Double churnRate;
    private Double retentionRate;
    private BigDecimal averageRevenuePerUser;
    private BigDecimal customerLifetimeValue;
    private BigDecimal customerAcquisitionCost;

    // Growth metrics
    private Double netRevenueRetention;
    private Double grossRevenueRetention;
    private Integer netNewCustomers;
    private BigDecimal netNewMrr;

    // Cohort analysis
    private Map<String, Object> cohortData;

    // Trends
    private List<Double> trendData;
    private String trendDirection;
    private Double confidenceScore;
}
