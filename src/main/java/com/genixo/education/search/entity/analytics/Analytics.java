package com.genixo.education.search.entity.analytics;

import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.enumaration.MetricType;
import com.genixo.education.search.enumaration.TimePeriod;
import com.genixo.education.search.entity.institution.Brand;
import com.genixo.education.search.entity.institution.Campus;
import com.genixo.education.search.entity.institution.School;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

// ========================= ANALYTICS =========================


@Entity
@Table(name = "analytics")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Analytics extends BaseEntity {

    // Entity associations (nullable - for system-wide analytics)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campus_id")
    private Campus campus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    private School school;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "metric_type", nullable = false)
    private MetricType metricType;

    @Enumerated(EnumType.STRING)
    @Column(name = "time_period", nullable = false)
    private TimePeriod timePeriod;

    // Visitor metrics
    @Column(name = "page_views")
    private Long pageViews = 0L;

    @Column(name = "unique_visitors")
    private Long uniqueVisitors = 0L;

    @Column(name = "new_visitors")
    private Long newVisitors = 0L;

    @Column(name = "returning_visitors")
    private Long returningVisitors = 0L;

    @Column(name = "bounce_rate")
    private Double bounceRate = 0.0;

    @Column(name = "average_session_duration_seconds")
    private Integer averageSessionDurationSeconds = 0;

    @Column(name = "pages_per_session")
    private Double pagesPerSession = 0.0;

    // Traffic sources
    @Column(name = "direct_traffic")
    private Long directTraffic = 0L;

    @Column(name = "organic_search_traffic")
    private Long organicSearchTraffic = 0L;

    @Column(name = "paid_search_traffic")
    private Long paidSearchTraffic = 0L;

    @Column(name = "social_media_traffic")
    private Long socialMediaTraffic = 0L;

    @Column(name = "referral_traffic")
    private Long referralTraffic = 0L;

    @Column(name = "email_traffic")
    private Long emailTraffic = 0L;

    // Device analytics
    @Column(name = "mobile_visitors")
    private Long mobileVisitors = 0L;

    @Column(name = "desktop_visitors")
    private Long desktopVisitors = 0L;

    @Column(name = "tablet_visitors")
    private Long tabletVisitors = 0L;

    // Geographic data
    @Column(name = "local_visitors")
    private Long localVisitors = 0L; // Same city

    @Column(name = "national_visitors")
    private Long nationalVisitors = 0L; // Same country

    @Column(name = "international_visitors")
    private Long internationalVisitors = 0L;

    // Engagement metrics
    @Column(name = "appointment_requests")
    private Long appointmentRequests = 0L;

    @Column(name = "appointment_confirmations")
    private Long appointmentConfirmations = 0L;

    @Column(name = "appointment_completions")
    private Long appointmentCompletions = 0L;

    @Column(name = "message_inquiries")
    private Long messageInquiries = 0L;

    @Column(name = "phone_clicks")
    private Long phoneClicks = 0L;

    @Column(name = "email_clicks")
    private Long emailClicks = 0L;

    @Column(name = "direction_clicks")
    private Long directionClicks = 0L;

    @Column(name = "brochure_downloads")
    private Long brochureDownloads = 0L;

    @Column(name = "gallery_views")
    private Long galleryViews = 0L;

    @Column(name = "video_views")
    private Long videoViews = 0L;

    @Column(name = "social_media_clicks")
    private Long socialMediaClicks = 0L;

    // Content engagement
    @Column(name = "post_views")
    private Long postViews = 0L;

    @Column(name = "post_likes")
    private Long postLikes = 0L;

    @Column(name = "post_comments")
    private Long postComments = 0L;

    @Column(name = "post_shares")
    private Long postShares = 0L;

    // Search analytics
    @Column(name = "internal_searches")
    private Long internalSearches = 0L;

    @Column(name = "zero_result_searches")
    private Long zeroResultSearches = 0L;

    @Column(name = "search_to_appointment_rate")
    private Double searchToAppointmentRate = 0.0;

    // Conversion metrics
    @Column(name = "conversion_rate")
    private Double conversionRate = 0.0;

    @Column(name = "appointment_conversion_rate")
    private Double appointmentConversionRate = 0.0;

    @Column(name = "inquiry_conversion_rate")
    private Double inquiryConversionRate = 0.0;

    @Column(name = "enrollment_conversion_rate")
    private Double enrollmentConversionRate = 0.0;

    // Campaign metrics
    @Column(name = "campaign_views")
    private Long campaignViews = 0L;

    @Column(name = "campaign_clicks")
    private Long campaignClicks = 0L;

    @Column(name = "campaign_applications")
    private Long campaignApplications = 0L;

    @Column(name = "campaign_conversions")
    private Long campaignConversions = 0L;

    @Column(name = "promo_code_usage")
    private Long promoCodeUsage = 0L;

    @Column(name = "discount_amount_used", precision = 19, scale = 2)
    private BigDecimal discountAmountUsed = BigDecimal.ZERO;

    // Rating and feedback
    @Column(name = "average_rating")
    private Double averageRating = 0.0;

    @Column(name = "total_ratings")
    private Long totalRatings = 0L;

    @Column(name = "survey_responses")
    private Long surveyResponses = 0L;

    @Column(name = "survey_completion_rate")
    private Double surveyCompletionRate = 0.0;

    // Performance metrics
    @Column(name = "page_load_time_ms")
    private Integer pageLoadTimeMs = 0;

    @Column(name = "server_response_time_ms")
    private Integer serverResponseTimeMs = 0;

    @Column(name = "error_rate")
    private Double errorRate = 0.0;

    @Column(name = "uptime_percentage")
    private Double uptimePercentage = 100.0;

    // Financial metrics (for subscription analytics)
    @Column(name = "revenue", precision = 19, scale = 2)
    private BigDecimal revenue = BigDecimal.ZERO;

    @Column(name = "new_subscriptions")
    private Long newSubscriptions = 0L;

    @Column(name = "canceled_subscriptions")
    private Long canceledSubscriptions = 0L;

    @Column(name = "subscription_renewals")
    private Long subscriptionRenewals = 0L;

    @Column(name = "churn_rate")
    private Double churnRate = 0.0;

    // Comparison metrics (vs previous period)
    @Column(name = "visitors_growth_rate")
    private Double visitorsGrowthRate = 0.0;

    @Column(name = "appointments_growth_rate")
    private Double appointmentsGrowthRate = 0.0;

    @Column(name = "inquiries_growth_rate")
    private Double inquiriesGrowthRate = 0.0;

    @Column(name = "rating_change")
    private Double ratingChange = 0.0;

    // Additional custom metrics (JSON format for flexibility)
    @Column(name = "custom_metrics", columnDefinition = "JSON")
    private String customMetrics;

    // Data source information
    @Column(name = "data_source")
    private String dataSource;

    @Column(name = "last_calculated_at")
    private LocalDateTime lastCalculatedAt;

    @Column(name = "calculation_duration_ms")
    private Long calculationDurationMs;
}