package com.genixo.education.search.service.converter;

import com.genixo.education.search.dto.analytics.*;
import com.genixo.education.search.dto.institution.BrandSummaryDto;
import com.genixo.education.search.dto.institution.CampusSummaryDto;
import com.genixo.education.search.dto.institution.SchoolSummaryDto;
import com.genixo.education.search.entity.analytics.*;
import com.genixo.education.search.entity.institution.Brand;
import com.genixo.education.search.entity.institution.Campus;
import com.genixo.education.search.entity.institution.School;
import com.genixo.education.search.enumaration.*;
import com.genixo.education.search.util.ConversionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsConverterService {

    private final InstitutionConverterService institutionConverterService;


    public AnalyticsDto mapToDto(Analytics entity) {
        if (entity == null) {
            return null;
        }

        return AnalyticsDto.builder()
                .id(entity.getId())
                .date(entity.getDate())
                .metricType(entity.getMetricType())
                .timePeriod(entity.getTimePeriod())

                // Visitor metrics
                .pageViews(entity.getPageViews())
                .uniqueVisitors(entity.getUniqueVisitors())
                .newVisitors(entity.getNewVisitors())
                .returningVisitors(entity.getReturningVisitors())
                .bounceRate(entity.getBounceRate())
                .averageSessionDurationSeconds(entity.getAverageSessionDurationSeconds())
                .pagesPerSession(entity.getPagesPerSession())

                // Traffic sources
                .directTraffic(entity.getDirectTraffic())
                .organicSearchTraffic(entity.getOrganicSearchTraffic())
                .paidSearchTraffic(entity.getPaidSearchTraffic())
                .socialMediaTraffic(entity.getSocialMediaTraffic())
                .referralTraffic(entity.getReferralTraffic())
                .emailTraffic(entity.getEmailTraffic())

                // Device analytics
                .mobileVisitors(entity.getMobileVisitors())
                .desktopVisitors(entity.getDesktopVisitors())
                .tabletVisitors(entity.getTabletVisitors())

                // Geographic data
                .localVisitors(entity.getLocalVisitors())
                .nationalVisitors(entity.getNationalVisitors())
                .internationalVisitors(entity.getInternationalVisitors())

                // Engagement metrics
                .appointmentRequests(entity.getAppointmentRequests())
                .appointmentConfirmations(entity.getAppointmentConfirmations())
                .appointmentCompletions(entity.getAppointmentCompletions())
                .messageInquiries(entity.getMessageInquiries())
                .phoneClicks(entity.getPhoneClicks())
                .emailClicks(entity.getEmailClicks())
                .directionClicks(entity.getDirectionClicks())
                .brochureDownloads(entity.getBrochureDownloads())
                .galleryViews(entity.getGalleryViews())
                .videoViews(entity.getVideoViews())
                .socialMediaClicks(entity.getSocialMediaClicks())

                // Content engagement
                .postViews(entity.getPostViews())
                .postLikes(entity.getPostLikes())
                .postComments(entity.getPostComments())
                .postShares(entity.getPostShares())

                // Search analytics
                .internalSearches(entity.getInternalSearches())
                .zeroResultSearches(entity.getZeroResultSearches())
                .searchToAppointmentRate(entity.getSearchToAppointmentRate())

                // Conversion metrics
                .conversionRate(entity.getConversionRate())
                .appointmentConversionRate(entity.getAppointmentConversionRate())
                .inquiryConversionRate(entity.getInquiryConversionRate())
                .enrollmentConversionRate(entity.getEnrollmentConversionRate())

                // Campaign metrics
                .campaignViews(entity.getCampaignViews())
                .campaignClicks(entity.getCampaignClicks())
                .campaignApplications(entity.getCampaignApplications())
                .campaignConversions(entity.getCampaignConversions())
                .promoCodeUsage(entity.getPromoCodeUsage())
                .discountAmountUsed(entity.getDiscountAmountUsed())

                // Rating and feedback
                .averageRating(entity.getAverageRating())
                .totalRatings(entity.getTotalRatings())
                .surveyResponses(entity.getSurveyResponses())
                .surveyCompletionRate(entity.getSurveyCompletionRate())

                // Performance metrics
                .pageLoadTimeMs(entity.getPageLoadTimeMs())
                .serverResponseTimeMs(entity.getServerResponseTimeMs())
                .errorRate(entity.getErrorRate())
                .uptimePercentage(entity.getUptimePercentage())

                // Financial metrics
                .revenue(entity.getRevenue())
                .newSubscriptions(entity.getNewSubscriptions())
                .canceledSubscriptions(entity.getCanceledSubscriptions())
                .subscriptionRenewals(entity.getSubscriptionRenewals())
                .churnRate(entity.getChurnRate())

                // Growth metrics
                .visitorsGrowthRate(entity.getVisitorsGrowthRate())
                .appointmentsGrowthRate(entity.getAppointmentsGrowthRate())
                .inquiriesGrowthRate(entity.getInquiriesGrowthRate())
                .ratingChange(entity.getRatingChange())

                // Custom metrics
                .customMetrics(entity.getCustomMetrics())

                // Metadata
                .dataSource(entity.getDataSource())
                .lastCalculatedAt(entity.getLastCalculatedAt())
                .calculationDurationMs(entity.getCalculationDurationMs())

                // Relationships
                .brand(mapBrandToSummaryDto(entity.getBrand()))
                .campus(mapCampusToSummaryDto(entity.getCampus()))
                .school(mapSchoolToSummaryDto(entity.getSchool()))

                // Base entity fields
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public AnalyticsSummaryDto mapToSummaryDto(Analytics entity) {
        if (entity == null) {
            return null;
        }

        String institutionName = getInstitutionName(entity);
        String institutionType = getInstitutionType(entity);

        return AnalyticsSummaryDto.builder()
                .date(entity.getDate())
                .pageViews(entity.getPageViews())
                .uniqueVisitors(entity.getUniqueVisitors())
                .appointmentRequests(entity.getAppointmentRequests())
                .messageInquiries(entity.getMessageInquiries())
                .conversionRate(entity.getConversionRate())
                .averageRating(entity.getAverageRating())
                .institutionName(institutionName)
                .institutionType(institutionType)
                .build();
    }

    // ================== VISITOR LOG CONVERSIONS ==================

    public VisitorLogDto mapToDto(VisitorLog entity) {
        if (entity == null) {
            return null;
        }

        return VisitorLogDto.builder()
                .id(entity.getId())
                .sessionId(entity.getSessionId())
                .visitorId(entity.getVisitorId())
                .ipAddress(entity.getIpAddress())
                .userAgent(entity.getUserAgent())
                .pageUrl(entity.getPageUrl())
                .pageTitle(entity.getPageTitle())
                .referrerUrl(entity.getReferrerUrl())
                .visitTime(entity.getVisitTime())
                .timeOnPageSeconds(entity.getTimeOnPageSeconds())

                // Device info
                .deviceType(entity.getDeviceType())
                .browserName(entity.getBrowserName())
                .browserVersion(entity.getBrowserVersion())
                .operatingSystem(entity.getOperatingSystem())
                .screenResolution(entity.getScreenResolution())
                .language(entity.getLanguage())

                // Location info
                .country(entity.getCountry())
                .city(entity.getCity())
                .region(entity.getRegion())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .timezone(entity.getTimezone())

                // Traffic source
                .trafficSource(entity.getTrafficSource())
                .utmSource(entity.getUtmSource())
                .utmMedium(entity.getUtmMedium())
                .utmCampaign(entity.getUtmCampaign())
                .utmTerm(entity.getUtmTerm())
                .utmContent(entity.getUtmContent())
                .searchKeywords(entity.getSearchKeywords())

                // Visitor behavior
                .isNewVisitor(entity.getIsNewVisitor())
                .isBounce(entity.getIsBounce())
                .pageDepth(entity.getPageDepth())
                .scrollDepthPercentage(entity.getScrollDepthPercentage())
                .exitPage(entity.getExitPage())

                // Engagement tracking
                .clickedPhone(entity.getClickedPhone())
                .clickedEmail(entity.getClickedEmail())
                .clickedDirections(entity.getClickedDirections())
                .viewedGallery(entity.getViewedGallery())
                .downloadedBrochure(entity.getDownloadedBrochure())
                .requestedAppointment(entity.getRequestedAppointment())
                .sentMessage(entity.getSentMessage())
                .clickedSocialMedia(entity.getClickedSocialMedia())

                // Performance metrics
                .pageLoadTimeMs(entity.getPageLoadTimeMs())
                .domContentLoadedMs(entity.getDomContentLoadedMs())
                .firstPaintMs(entity.getFirstPaintMs())
                .largestContentfulPaintMs(entity.getLargestContentfulPaintMs())

                // Bot detection
                .isBot(entity.getIsBot())
                .botName(entity.getBotName())

                // GDPR compliance
                .consentGiven(entity.getConsentGiven())
                .anonymized(entity.getAnonymized())

                // Relationships
                .school(mapSchoolToSummaryDto(entity.getSchool()))
                .campus(mapCampusToSummaryDto(entity.getCampus()))
                .brand(mapBrandToSummaryDto(entity.getBrand()))

                .createdAt(entity.getCreatedAt())
                .build();
    }

    public VisitorLogSummaryDto mapToSummaryDto(VisitorLog entity) {
        if (entity == null) {
            return null;
        }

        String institutionName = getInstitutionName(entity.getSchool(), entity.getCampus(), entity.getBrand());

        return VisitorLogSummaryDto.builder()
                .visitTime(entity.getVisitTime())
                .pageUrl(entity.getPageUrl())
                .pageTitle(entity.getPageTitle())
                .deviceType(entity.getDeviceType())
                .trafficSource(entity.getTrafficSource())
                .country(entity.getCountry())
                .city(entity.getCity())
                .timeOnPageSeconds(entity.getTimeOnPageSeconds())
                .isNewVisitor(entity.getIsNewVisitor())
                .institutionName(institutionName)
                .build();
    }

    // ================== SEARCH LOG CONVERSIONS ==================

    public SearchLogDto mapToDto(SearchLog entity) {
        if (entity == null) {
            return null;
        }

        return SearchLogDto.builder()
                .id(entity.getId())
                .sessionId(entity.getSessionId())
                .searchQuery(entity.getSearchQuery())
                .cleanedQuery(entity.getCleanedQuery())
                .searchTime(entity.getSearchTime())
                .searchType(entity.getSearchType())
                .resultsCount(entity.getResultsCount())
                .zeroResults(entity.getZeroResults())
                .responseTimeMs(entity.getResponseTimeMs())

                // Search filters
                .filtersApplied(entity.getFiltersApplied())
                .sortOrder(entity.getSortOrder())
                .pageNumber(entity.getPageNumber())
                .resultsPerPage(entity.getResultsPerPage())

                // User behavior
                .clickedResultPosition(entity.getClickedResultPosition())
                .clickedSchoolId(entity.getClickedSchoolId())
                .clickedSchoolName(getClickedSchoolName(entity.getClickedSchoolId()))
                .timeToClickSeconds(entity.getTimeToClickSeconds())
                .refinedSearch(entity.getRefinedSearch())
                .abandonedSearch(entity.getAbandonedSearch())

                // Geographic context
                .userLocation(entity.getUserLocation())
                .searchRadiusKm(entity.getSearchRadiusKm())
                .ipAddress(entity.getIpAddress())

                // Device context
                .deviceType(entity.getDeviceType())
                .userAgent(entity.getUserAgent())

                // Search intent
                .searchIntent(entity.getSearchIntent())
                .confidenceScore(entity.getConfidenceScore())

                // Search suggestions
                .autocompleteUsed(entity.getAutocompleteUsed())
                .suggestionSelected(entity.getSuggestionSelected())

                // A/B testing
                .experimentId(entity.getExperimentId())
                .variant(entity.getVariant())

                .createdAt(entity.getCreatedAt())
                .build();
    }

    public SearchLogSummaryDto mapToSummaryDto(SearchLog entity) {
        if (entity == null) {
            return null;
        }

        String clickedSchoolName = getClickedSchoolName(entity.getClickedSchoolId());

        return SearchLogSummaryDto.builder()
                .searchTime(entity.getSearchTime())
                .searchQuery(entity.getSearchQuery())
                .searchType(entity.getSearchType())
                .resultsCount(entity.getResultsCount())
                .zeroResults(entity.getZeroResults())
                .clickedSchoolName(clickedSchoolName)
                .userLocation(entity.getUserLocation())
                .deviceType(entity.getDeviceType())
                .build();
    }

    // ================== PERFORMANCE METRICS CONVERSIONS ==================

    public PerformanceMetricsDto mapToDto(PerformanceMetrics entity) {
        if (entity == null) {
            return null;
        }

        return PerformanceMetricsDto.builder()
                .id(entity.getId())
                .timestamp(entity.getTimestamp())
                .metricCategory(entity.getMetricCategory())
                .endpointUrl(entity.getEndpointUrl())
                .httpMethod(entity.getHttpMethod())
                .responseTimeMs(entity.getResponseTimeMs())
                .httpStatusCode(entity.getHttpStatusCode())
                .success(entity.getSuccess())
                .errorMessage(entity.getErrorMessage())
                .errorStackTrace(entity.getErrorStackTrace())

                // Database performance
                .dbQueryCount(entity.getDbQueryCount())
                .dbQueryTimeMs(entity.getDbQueryTimeMs())
                .dbConnectionTimeMs(entity.getDbConnectionTimeMs())

                // Memory usage
                .memoryUsedMb(entity.getMemoryUsedMb())
                .memoryTotalMb(entity.getMemoryTotalMb())
                .memoryUsagePercentage(entity.getMemoryUsagePercentage())

                // CPU usage
                .cpuUsagePercentage(entity.getCpuUsagePercentage())
                .cpuTimeMs(entity.getCpuTimeMs())

                // Cache performance
                .cacheHit(entity.getCacheHit())
                .cacheKey(entity.getCacheKey())
                .cacheTtlSeconds(entity.getCacheTtlSeconds())

                // File I/O
                .fileReadCount(entity.getFileReadCount())
                .fileWriteCount(entity.getFileWriteCount())
                .fileIoTimeMs(entity.getFileIoTimeMs())

                // Network metrics
                .bytesSent(entity.getBytesSent())
                .bytesReceived(entity.getBytesReceived())
                .networkLatencyMs(entity.getNetworkLatencyMs())

                // Third-party API calls
                .externalApiCalls(entity.getExternalApiCalls())
                .externalApiTimeMs(entity.getExternalApiTimeMs())
                .externalApiErrors(entity.getExternalApiErrors())

                // User context
                .userId(entity.getUserId())
                .sessionId(entity.getSessionId())
                .ipAddress(entity.getIpAddress())
                .userAgent(entity.getUserAgent())

                // Server context
                .serverName(entity.getServerName())
                .serverInstance(entity.getServerInstance())
                .applicationVersion(entity.getApplicationVersion())
                .jvmVersion(entity.getJvmVersion())

                // Request details
                .requestSizeBytes(entity.getRequestSizeBytes())
                .responseSizeBytes(entity.getResponseSizeBytes())
                .gzipEnabled(entity.getGzipEnabled())
                .keepAlive(entity.getKeepAlive())

                // Business metrics
                .featureName(entity.getFeatureName())
                .businessOperation(entity.getBusinessOperation())
                .conversionEvent(entity.getConversionEvent())

                // Alert thresholds
                .thresholdExceeded(entity.getThresholdExceeded())
                .thresholdType(entity.getThresholdType())
                .thresholdValue(entity.getThresholdValue())

                // Additional metrics
                .additionalMetrics(entity.getAdditionalMetrics())

                .createdAt(entity.getCreatedAt())
                .build();
    }

    public PerformanceSummaryDto mapToSummaryDto(PerformanceMetrics entity) {
        if (entity == null) {
            return null;
        }

        return PerformanceSummaryDto.builder()
                .timestamp(entity.getTimestamp())
                .metricCategory(entity.getMetricCategory())
                .endpointUrl(entity.getEndpointUrl())
                .responseTimeMs(entity.getResponseTimeMs())
                .success(entity.getSuccess())
                .memoryUsagePercentage(entity.getMemoryUsagePercentage())
                .cpuUsagePercentage(entity.getCpuUsagePercentage())
                .cacheHit(entity.getCacheHit())
                .thresholdExceeded(entity.getThresholdExceeded())
                .build();
    }

    // ================== COLLECTION CONVERSIONS ==================

    public List<AnalyticsDto> mapToDto(List<Analytics> entities) {
        return entities != null ? entities.stream()
                .map(this::mapToDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()) : new ArrayList<>();
    }

    public List<AnalyticsSummaryDto> mapToSummaryDto(List<Analytics> entities) {
        return entities != null ? entities.stream()
                .map(this::mapToSummaryDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()) : new ArrayList<>();
    }

    public List<VisitorLogDto> mapVisitorLogsToDto(List<VisitorLog> entities) {
        return entities != null ? entities.stream()
                .map(this::mapToDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()) : new ArrayList<>();
    }

    public List<VisitorLogSummaryDto> mapVisitorLogsToSummaryDto(List<VisitorLog> entities) {
        return entities != null ? entities.stream()
                .map(this::mapToSummaryDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()) : new ArrayList<>();
    }

    public List<SearchLogDto> mapSearchLogsToDto(List<SearchLog> entities) {
        return entities != null ? entities.stream()
                .map(this::mapToDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()) : new ArrayList<>();
    }

    public List<SearchLogSummaryDto> mapSearchLogsToSummaryDto(List<SearchLog> entities) {
        return entities != null ? entities.stream()
                .map(this::mapToSummaryDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()) : new ArrayList<>();
    }

    public List<PerformanceMetricsDto> mapPerformanceMetricsToDto(List<PerformanceMetrics> entities) {
        return entities != null ? entities.stream()
                .map(this::mapToDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()) : new ArrayList<>();
    }

    public List<PerformanceSummaryDto> mapPerformanceMetricsToSummaryDto(List<PerformanceMetrics> entities) {
        return entities != null ? entities.stream()
                .map(this::mapToSummaryDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()) : new ArrayList<>();
    }

    // ================== HELPER METHODS ==================

    private BrandSummaryDto mapBrandToSummaryDto(Brand brand) {
        return institutionConverterService != null && brand != null ?
                institutionConverterService.mapBrandToSummaryDto(brand) : null;
    }

    private CampusSummaryDto mapCampusToSummaryDto(Campus campus) {
        return institutionConverterService != null && campus != null ?
                institutionConverterService.mapCampusToSummaryDto(campus) : null;
    }

    private SchoolSummaryDto mapSchoolToSummaryDto(School school) {
        return institutionConverterService != null && school != null ?
                institutionConverterService.mapSchoolToSummaryDto(school) : null;
    }

    private String getInstitutionName(Analytics analytics) {
        if (analytics.getSchool() != null) {
            return analytics.getSchool().getName();
        } else if (analytics.getCampus() != null) {
            return analytics.getCampus().getName();
        } else if (analytics.getBrand() != null) {
            return analytics.getBrand().getName();
        }
        return "Sistem Geneli";
    }

    private String getInstitutionType(Analytics analytics) {
        if (analytics.getSchool() != null) {
            return "Okul";
        } else if (analytics.getCampus() != null) {
            return "Kampüs";
        } else if (analytics.getBrand() != null) {
            return "Marka";
        }
        return "Sistem";
    }

    private String getInstitutionName(School school, Campus campus, Brand brand) {
        if (school != null) {
            return school.getName();
        } else if (campus != null) {
            return campus.getName();
        } else if (brand != null) {
            return brand.getName();
        }
        return null;
    }

    private String getClickedSchoolName(Long schoolId) {
        // Bu method normalde school service'den school adını getirecek
        // Şimdilik null dönüyoruz, service katmanında implement edilecek
        return null;
    }

    // ================== DASHBOARD & COMPLEX DTO HELPERS ==================

    public AnalyticsDashboardDto buildDashboardDto(
            LocalDate dashboardDate,
            TimePeriod timePeriod,
            List<Analytics> analyticsData,
            List<SchoolSummaryDto> topSchoolsByViews,
            List<SchoolSummaryDto> topSchoolsByConversions,
            List<String> topSearchTerms,
            List<String> topTrafficSources,
            Map<String, Long> visitorsByCity,
            Map<String, Long> visitorsByCountry,
            Map<DeviceType, Long> visitorsByDevice,
            Map<TrafficSource, Long> visitorsBySource) {

        if (ConversionUtils.isEmpty(analyticsData)) {
            return createEmptyDashboard(dashboardDate, timePeriod);
        }

        // Aggregate main metrics
        long totalPageViews = analyticsData.stream()
                .mapToLong(a -> ConversionUtils.defaultIfNull(a.getPageViews(), 0L))
                .sum();

        long totalUniqueVisitors = analyticsData.stream()
                .mapToLong(a -> ConversionUtils.defaultIfNull(a.getUniqueVisitors(), 0L))
                .sum();

        long totalAppointments = analyticsData.stream()
                .mapToLong(a -> ConversionUtils.defaultIfNull(a.getAppointmentRequests(), 0L))
                .sum();

        long totalInquiries = analyticsData.stream()
                .mapToLong(a -> ConversionUtils.defaultIfNull(a.getMessageInquiries(), 0L))
                .sum();

        double avgConversionRate = analyticsData.stream()
                .filter(a -> a.getConversionRate() != null)
                .mapToDouble(Analytics::getConversionRate)
                .average()
                .orElse(0.0);

        return AnalyticsDashboardDto.builder()
                .dashboardDate(dashboardDate)
                .timePeriod(timePeriod)
                .totalPageViews(totalPageViews)
                .totalUniqueVisitors(totalUniqueVisitors)
                .totalAppointments(totalAppointments)
                .totalInquiries(totalInquiries)
                .overallConversionRate(avgConversionRate)
                .topSchoolsByViews(ConversionUtils.defaultIfNull(topSchoolsByViews, new ArrayList<>()))
                .topSchoolsByConversions(ConversionUtils.defaultIfNull(topSchoolsByConversions, new ArrayList<>()))
                .topSearchTerms(ConversionUtils.defaultIfNull(topSearchTerms, new ArrayList<>()))
                .topTrafficSources(ConversionUtils.defaultIfNull(topTrafficSources, new ArrayList<>()))
                .visitorsByCity(ConversionUtils.defaultIfNull(visitorsByCity, new HashMap<>()))
                .visitorsByCountry(ConversionUtils.defaultIfNull(visitorsByCountry, new HashMap<>()))
                .visitorsByDevice(ConversionUtils.defaultIfNull(visitorsByDevice, new HashMap<>()))
                .visitorsBySource(ConversionUtils.defaultIfNull(visitorsBySource, new HashMap<>()))
                .build();
    }

    private AnalyticsDashboardDto createEmptyDashboard(LocalDate dashboardDate, TimePeriod timePeriod) {
        return AnalyticsDashboardDto.builder()
                .dashboardDate(dashboardDate)
                .timePeriod(timePeriod)
                .totalPageViews(0L)
                .totalUniqueVisitors(0L)
                .totalAppointments(0L)
                .totalInquiries(0L)
                .overallConversionRate(0.0)
                .topSchoolsByViews(new ArrayList<>())
                .topSchoolsByConversions(new ArrayList<>())
                .topSearchTerms(new ArrayList<>())
                .topTrafficSources(new ArrayList<>())
                .visitorsByCity(new HashMap<>())
                .visitorsByCountry(new HashMap<>())
                .visitorsByDevice(new HashMap<>())
                .visitorsBySource(new HashMap<>())
                .build();
    }
}
