package com.genixo.education.search.dto.analytics;

import com.genixo.education.search.dto.institution.BrandSummaryDto;
import com.genixo.education.search.dto.institution.CampusSummaryDto;
import com.genixo.education.search.dto.institution.SchoolSummaryDto;
import com.genixo.education.search.enumaration.DeviceType;
import com.genixo.education.search.enumaration.TrafficSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitorLogDto {
    private Long id;
    private String sessionId;
    private String visitorId;
    private String ipAddress;
    private String userAgent;
    private String pageUrl;
    private String pageTitle;
    private String referrerUrl;
    private LocalDateTime visitTime;
    private Integer timeOnPageSeconds;

    // Device info
    private DeviceType deviceType;
    private String browserName;
    private String browserVersion;
    private String operatingSystem;
    private String screenResolution;
    private String language;

    // Location info
    private String country;
    private String city;
    private String region;
    private Double latitude;
    private Double longitude;
    private String timezone;

    // Traffic source
    private TrafficSource trafficSource;
    private String utmSource;
    private String utmMedium;
    private String utmCampaign;
    private String utmTerm;
    private String utmContent;
    private String searchKeywords;

    // Visitor behavior
    private Boolean isNewVisitor;
    private Boolean isBounce;
    private Integer pageDepth;
    private Integer scrollDepthPercentage;
    private Boolean exitPage;

    // Engagement tracking
    private Boolean clickedPhone;
    private Boolean clickedEmail;
    private Boolean clickedDirections;
    private Boolean viewedGallery;
    private Boolean downloadedBrochure;
    private Boolean requestedAppointment;
    private Boolean sentMessage;
    private Boolean clickedSocialMedia;

    // Performance metrics
    private Integer pageLoadTimeMs;
    private Integer domContentLoadedMs;
    private Integer firstPaintMs;
    private Integer largestContentfulPaintMs;

    // Bot detection
    private Boolean isBot;
    private String botName;

    // GDPR compliance
    private Boolean consentGiven;
    private Boolean anonymized;

    // Relationships
    private SchoolSummaryDto school;
    private CampusSummaryDto campus;
    private BrandSummaryDto brand;
    private LocalDateTime createdAt;
}