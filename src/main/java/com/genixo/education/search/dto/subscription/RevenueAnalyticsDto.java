package com.genixo.education.search.dto.subscription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevenueAnalyticsDto {
    private String period; // DAILY, WEEKLY, MONTHLY, YEARLY
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;

    // Revenue breakdown
    private BigDecimal totalRevenue;
    private BigDecimal newCustomerRevenue;
    private BigDecimal existingCustomerRevenue;
    private BigDecimal upgradeRevenue;
    private BigDecimal downgradeRevenue;

    // Customer metrics
    private Integer newCustomers;
    private Integer churnedCustomers;
    private Integer upgradedCustomers;
    private Integer downgradedCustomers;

    // Plan performance
    private List<PlanRevenueDto> planRevenues;

    // Predictions
    private BigDecimal projectedRevenue;
    private Double confidenceInterval;

    // Trends
    private Double revenueGrowthRate;
    private Double customerGrowthRate;
    private String trendDirection; // UP, DOWN, STABLE
}