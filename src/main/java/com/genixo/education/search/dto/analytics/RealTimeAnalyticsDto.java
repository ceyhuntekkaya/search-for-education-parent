package com.genixo.education.search.dto.analytics;

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
public class RealTimeAnalyticsDto {
    private LocalDateTime timestamp;

    // Current activity
    private Long currentActiveUsers;
    private Long currentPageViews;
    private Long currentSessions;

    // Last hour metrics
    private Long lastHourVisitors;
    private Long lastHourAppointments;
    private Long lastHourInquiries;

    // Live events
    private List<VisitorLogSummaryDto> recentVisitors;
    private List<SearchLogSummaryDto> recentSearches;
    private List<String> recentAppointments;
    private List<String> recentInquiries;

    // System status
    private Double systemLoad;
    private Double memoryUsage;
    private Double diskUsage;
    private Boolean allSystemsOperational;

    // Alerts
    private List<String> activeAlerts;
    private Integer criticalAlertsCount;
    private Integer warningAlertsCount;
}
