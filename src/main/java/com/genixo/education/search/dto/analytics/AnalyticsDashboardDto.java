package com.genixo.education.search.dto.analytics;

import com.genixo.education.search.dto.institution.SchoolSummaryDto;
import com.genixo.education.search.enumaration.DeviceType;
import com.genixo.education.search.enumaration.TimePeriod;
import com.genixo.education.search.enumaration.TrafficSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsDashboardDto {
    private LocalDate dashboardDate;
    private TimePeriod timePeriod;

    // Overview metrics
    private Long totalPageViews;
    private Long totalUniqueVisitors;
    private Long totalAppointments;
    private Long totalInquiries;
    private Double overallConversionRate;

    // Growth metrics
    private Double visitorsGrowth;
    private Double appointmentsGrowth;
    private Double inquiriesGrowth;
    private Double revenueGrowth;

    // Top performers
    private List<SchoolSummaryDto> topSchoolsByViews;
    private List<SchoolSummaryDto> topSchoolsByConversions;
    private List<String> topSearchTerms;
    private List<String> topTrafficSources;

    // Geographic distribution
    private Map<String, Long> visitorsByCity;
    private Map<String, Long> visitorsByCountry;

    // Device distribution
    private Map<DeviceType, Long> visitorsByDevice;

    // Traffic sources
    private Map<TrafficSource, Long> visitorsBySource;

    // Performance indicators
    private Double averagePageLoadTime;
    private Double systemUptime;
    private Double errorRate;

    // Content performance
    private List<String> popularContent;
    private Map<String, Long> contentEngagement;

    // Campaign performance
    private Long activeCampaigns;
    private Long campaignConversions;
    private BigDecimal campaignRevenue;
}