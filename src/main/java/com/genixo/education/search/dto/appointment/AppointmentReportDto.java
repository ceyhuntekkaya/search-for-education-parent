package com.genixo.education.search.dto.appointment;

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
public class AppointmentReportDto {
    private String reportId;
    private LocalDateTime generatedAt;
    private String reportType; // SUMMARY, DETAILED, STAFF_PERFORMANCE, OUTCOME_ANALYSIS
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private Long schoolId;
    private String schoolName;

    // Summary data
    private AppointmentStatisticsDto overallStatistics;
    private List<AppointmentSummaryDto> appointments;

    // Staff performance
    private List<StaffPerformanceDto> staffPerformance;

    // Outcome analysis
    private List<OutcomeAnalysisDto> outcomeAnalysis;

    // Time slot analysis
    private List<TimeSlotAnalysisDto> timeSlotAnalysis;

    // Insights and recommendations
    private List<String> keyInsights;
    private List<String> recommendations;
    private List<String> improvementOpportunities;

    // Export options
    private String csvDownloadUrl;
    private String pdfDownloadUrl;
    private String excelDownloadUrl;
}