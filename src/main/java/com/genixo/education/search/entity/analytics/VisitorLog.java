package com.genixo.education.search.entity.analytics;

import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.enumaration.DeviceType;
import com.genixo.education.search.enumaration.TrafficSource;
import com.genixo.education.search.entity.institution.Brand;
import com.genixo.education.search.entity.institution.Campus;
import com.genixo.education.search.entity.institution.School;
import com.genixo.education.search.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "visitor_logs")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class VisitorLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // Null if anonymous visitor

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    private School school; // Which school page was visited

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campus_id")
    private Campus campus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Column(name = "visitor_id")
    private String visitorId; // Anonymous visitor tracking

    @Column(name = "ip_address", nullable = false)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "page_url", nullable = false)
    private String pageUrl;

    @Column(name = "page_title")
    private String pageTitle;

    @Column(name = "referrer_url")
    private String referrerUrl;

    @Column(name = "visit_time", nullable = false)
    private LocalDateTime visitTime;

    @Column(name = "time_on_page_seconds")
    private Integer timeOnPageSeconds;

    @Enumerated(EnumType.STRING)
    @Column(name = "device_type")
    private DeviceType deviceType;

    @Column(name = "browser_name")
    private String browserName;

    @Column(name = "browser_version")
    private String browserVersion;

    @Column(name = "operating_system")
    private String operatingSystem;

    @Column(name = "screen_resolution")
    private String screenResolution;

    @Column(name = "language")
    private String language;

    @Column(name = "country")
    private String country;

    @Column(name = "city")
    private String city;

    @Column(name = "region")
    private String region;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "timezone")
    private String timezone;

    @Enumerated(EnumType.STRING)
    @Column(name = "traffic_source")
    private TrafficSource trafficSource;

    @Column(name = "utm_source")
    private String utmSource;

    @Column(name = "utm_medium")
    private String utmMedium;

    @Column(name = "utm_campaign")
    private String utmCampaign;

    @Column(name = "utm_term")
    private String utmTerm;

    @Column(name = "utm_content")
    private String utmContent;

    @Column(name = "search_keywords")
    private String searchKeywords;

    @Column(name = "is_new_visitor")
    private Boolean isNewVisitor = true;

    @Column(name = "is_bounce")
    private Boolean isBounce = false;

    @Column(name = "page_depth")
    private Integer pageDepth = 1; // How many pages visited in session

    @Column(name = "scroll_depth_percentage")
    private Integer scrollDepthPercentage = 0;

    @Column(name = "exit_page")
    private Boolean exitPage = false;

    // Engagement tracking
    @Column(name = "clicked_phone")
    private Boolean clickedPhone = false;

    @Column(name = "clicked_email")
    private Boolean clickedEmail = false;

    @Column(name = "clicked_directions")
    private Boolean clickedDirections = false;

    @Column(name = "viewed_gallery")
    private Boolean viewedGallery = false;

    @Column(name = "downloaded_brochure")
    private Boolean downloadedBrochure = false;

    @Column(name = "requested_appointment")
    private Boolean requestedAppointment = false;

    @Column(name = "sent_message")
    private Boolean sentMessage = false;

    @Column(name = "clicked_social_media")
    private Boolean clickedSocialMedia = false;

    // Performance metrics
    @Column(name = "page_load_time_ms")
    private Integer pageLoadTimeMs;

    @Column(name = "dom_content_loaded_ms")
    private Integer domContentLoadedMs;

    @Column(name = "first_paint_ms")
    private Integer firstPaintMs;

    @Column(name = "largest_contentful_paint_ms")
    private Integer largestContentfulPaintMs;

    // Bot detection
    @Column(name = "is_bot")
    private Boolean isBot = false;

    @Column(name = "bot_name")
    private String botName;

    // GDPR compliance
    @Column(name = "consent_given")
    private Boolean consentGiven = false;

    @Column(name = "anonymized")
    private Boolean anonymized = false;
}
