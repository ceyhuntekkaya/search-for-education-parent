package com.genixo.education.search.dto.analytics;

import com.genixo.education.search.dto.institution.BrandSummaryDto;
import com.genixo.education.search.dto.institution.CampusSummaryDto;
import com.genixo.education.search.dto.institution.SchoolSummaryDto;
import com.genixo.education.search.enumaration.MetricType;
import com.genixo.education.search.enumaration.TimePeriod;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// ========================= ANALYTICS DTO =========================
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsDto {
    private Long id;
    private LocalDate date;
    private MetricType metricType;
    private TimePeriod timePeriod;

    // Visitor metrics
    private Long pageViews;
    private Long uniqueVisitors;
    private Long newVisitors;
    private Long returningVisitors;
    private Double bounceRate;
    private Integer averageSessionDurationSeconds;
    private Double pagesPerSession;

    // Traffic sources
    private Long directTraffic;
    private Long organicSearchTraffic;
    private Long paidSearchTraffic;
    private Long socialMediaTraffic;
    private Long referralTraffic;
    private Long emailTraffic;

    // Device analytics
    private Long mobileVisitors;
    private Long desktopVisitors;
    private Long tabletVisitors;

    // Geographic data
    private Long localVisitors;
    private Long nationalVisitors;
    private Long internationalVisitors;

    // Engagement metrics
    private Long appointmentRequests;
    private Long appointmentConfirmations;
    private Long appointmentCompletions;
    private Long messageInquiries;
    private Long phoneClicks;
    private Long emailClicks;
    private Long directionClicks;
    private Long brochureDownloads;
    private Long galleryViews;
    private Long videoViews;
    private Long socialMediaClicks;

    // Content engagement
    private Long postViews;
    private Long postLikes;
    private Long postComments;
    private Long postShares;

    // Search analytics
    private Long internalSearches;
    private Long zeroResultSearches;
    private Double searchToAppointmentRate;

    // Conversion metrics
    private Double conversionRate;
    private Double appointmentConversionRate;
    private Double inquiryConversionRate;
    private Double enrollmentConversionRate;

    // Campaign metrics
    private Long campaignViews;
    private Long campaignClicks;
    private Long campaignApplications;
    private Long campaignConversions;
    private Long promoCodeUsage;
    private BigDecimal discountAmountUsed;

    // Rating and feedback
    private Double averageRating;
    private Long totalRatings;
    private Long surveyResponses;
    private Double surveyCompletionRate;

    // Performance metrics
    private Integer pageLoadTimeMs;
    private Integer serverResponseTimeMs;
    private Double errorRate;
    private Double uptimePercentage;

    // Financial metrics
    private BigDecimal revenue;
    private Long newSubscriptions;
    private Long canceledSubscriptions;
    private Long subscriptionRenewals;
    private Double churnRate;

    // Growth metrics
    private Double visitorsGrowthRate;
    private Double appointmentsGrowthRate;
    private Double inquiriesGrowthRate;
    private Double ratingChange;

    // Custom metrics
    private String customMetrics; // JSON

    // Metadata
    private String dataSource;
    private LocalDateTime lastCalculatedAt;
    private Long calculationDurationMs;

    // Relationships
    private BrandSummaryDto brand;
    private CampusSummaryDto campus;
    private SchoolSummaryDto school;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
