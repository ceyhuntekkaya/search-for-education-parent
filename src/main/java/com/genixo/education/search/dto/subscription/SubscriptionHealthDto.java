package com.genixo.education.search.dto.subscription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionHealthDto {
    private Long subscriptionId;
    private String campusName;
    private String healthScore; // EXCELLENT, GOOD, FAIR, POOR, CRITICAL
    private Integer healthScoreValue; // 0-100

    // Health indicators
    private String paymentHealth; // GOOD, WARNING, CRITICAL
    private String usageHealth; // ACTIVE, MODERATE, LOW, INACTIVE
    private String supportHealth; // SATISFIED, NEUTRAL, UNSATISFIED
    private String engagementHealth; // HIGH, MEDIUM, LOW

    // Risk factors
    private List<String> riskFactors;
    private String churnRisk; // LOW, MEDIUM, HIGH, CRITICAL
    private Double churnProbability;

    // Recommendations
    private List<String> healthRecommendations;
    private List<String> actionItems;

    // Trends
    private String healthTrend; // IMPROVING, STABLE, DECLINING
    private LocalDateTime lastHealthCheck;
}
