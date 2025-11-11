package com.genixo.education.search.dto.campaign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignReportDto {
    private String reportId;
    private LocalDateTime generatedAt;
    private String reportType; // SUMMARY, DETAILED, COMPARISON
    private LocalDate periodStart;
    private LocalDate periodEnd;

    // Campaign summary
    private List<CampaignSummaryDto> campaigns;
    private CampaignAnalyticsDto overallAnalytics;

    // Insights and recommendations
    private List<String> keyInsights;
    private List<String> recommendations;
    private List<String> bestPerformingCampaigns;
    private List<String> improvementOpportunities;

    // Comparison data (if comparison report)
    private CampaignAnalyticsDto previousPeriodAnalytics;
    private List<ComparisonMetricDto> comparisonMetrics;
}