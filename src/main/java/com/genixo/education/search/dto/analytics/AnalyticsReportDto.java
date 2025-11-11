package com.genixo.education.search.dto.analytics;

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
public class AnalyticsReportDto {
    private String reportId;
    private String reportTitle;
    private String reportType; // DAILY, WEEKLY, MONTHLY, CUSTOM
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime generatedAt;
    private String generatedBy;

    // Report data
    private AnalyticsDashboardDto dashboardData;
    private List<AnalyticsDto> detailedMetrics;
    private List<VisitorLogSummaryDto> visitorSummary;
    private List<SearchLogSummaryDto> searchSummary;

    // Insights and recommendations
    private List<String> keyInsights;
    private List<String> recommendations;
    private List<String> actionItems;

    // Export options
    private String pdfUrl;
    private String excelUrl;
    private String csvUrl;

    // Sharing
    private Boolean isPublic;
    private String shareUrl;
    private List<String> sharedWithEmails;
}