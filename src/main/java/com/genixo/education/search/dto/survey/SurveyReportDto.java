package com.genixo.education.search.dto.survey;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyReportDto {
    private String reportId;
    private LocalDateTime generatedAt;
    private String reportType; // SUMMARY, DETAILED, COMPARISON, TREND_ANALYSIS
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private Long surveyId;
    private String surveyTitle;

    // Summary data
    private SurveyAnalyticsDto analytics;
    private List<SurveyRatingDto> topRatings;
    private List<SurveyRatingDto> bottomRatings;

    // Detailed responses
    private List<SurveyResponseDto> responses;

    // School performance
    private List<SchoolSurveyPerformanceDto> schoolPerformance;

    // Insights and recommendations
    private List<String> keyInsights;
    private List<String> recommendations;
    private List<String> actionItems;
    private List<String> strengthsIdentified;
    private List<String> improvementAreas;

    // Benchmarking
    private Map<String, Double> industryBenchmarks;
    private Map<String, Double> performanceComparison;

    // Export options
    private String csvDownloadUrl;
    private String pdfDownloadUrl;
    private String excelDownloadUrl;
}
