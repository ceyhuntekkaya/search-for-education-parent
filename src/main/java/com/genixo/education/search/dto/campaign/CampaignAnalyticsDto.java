package com.genixo.education.search.dto.campaign;

import com.genixo.education.search.enumaration.CampaignType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignAnalyticsDto {
    private Long campaignId;
    private String campaignTitle;
    private CampaignType campaignType;
    private LocalDate startDate;
    private LocalDate endDate;

    // Performance metrics
    private Long totalViews;
    private Long totalClicks;
    private Long totalApplications;
    private Long totalConversions;
    private Double clickThroughRate;
    private Double conversionRate;
    private Double applicationToConversionRate;

    // Financial metrics
    private BigDecimal totalRevenueGenerated;
    private BigDecimal totalDiscountGiven;
    private BigDecimal averageOrderValue;
    private Double returnOnInvestment;

    // Usage metrics
    private Integer totalUsages;
    private Integer uniqueUsers;
    private Integer repeatUsers;
    private Double averageUsagePerUser;

    // School metrics
    private Integer totalSchools;
    private Integer activeSchools;
    private Long topPerformingSchoolId;
    private String topPerformingSchoolName;

    // Time-based analytics
    private List<DailyAnalyticsDto> dailyAnalytics;
    private List<SchoolPerformanceDto> schoolPerformance;

    // Geographic analytics
    private List<LocationAnalyticsDto> locationAnalytics;

    // Device and source analytics
    private List<SourceAnalyticsDto> sourceAnalytics;
}