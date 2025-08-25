package com.genixo.education.search.service;

import com.genixo.education.search.common.exception.BusinessException;
import com.genixo.education.search.dto.analytics.*;
import com.genixo.education.search.entity.analytics.*;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.enumaration.*;
import com.genixo.education.search.repository.analytics.*;
import com.genixo.education.search.service.auth.JwtService;
import com.genixo.education.search.service.converter.AnalyticsConverterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AnalyticsService {

    private final AnalyticsRepository analyticsRepository;
    private final VisitorLogRepository visitorLogRepository;
    private final SearchLogRepository searchLogRepository;
    private final PerformanceMetricsRepository performanceMetricsRepository;
    private final AnalyticsConverterService converterService;
    private final JwtService jwtService;

    // ================================ ANALYTICS DASHBOARD ================================

    @Cacheable(value = "analytics_dashboard", key = "#startDate + '_' + #endDate + '_' + #schoolId + '_' + #campusId + '_' + #brandId")
    public AnalyticsDashboardDto getDashboard(LocalDate startDate, LocalDate endDate,
                                              Long schoolId, Long campusId, Long brandId,
                                              HttpServletRequest request) {
        log.info("Generating analytics dashboard for period: {} to {}", startDate, endDate);

        User user = jwtService.getUser(request);
        validateUserAnalyticsAccess(user, schoolId, campusId, brandId);

        // Get overview metrics
        AnalyticsSummaryDto currentPeriod = getAnalyticsSummary(startDate, endDate, schoolId, campusId, brandId);

        // Calculate previous period for growth comparison
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        LocalDate previousStart = startDate.minusDays(daysBetween + 1);
        LocalDate previousEnd = startDate.minusDays(1);
        AnalyticsSummaryDto previousPeriod = getAnalyticsSummary(previousStart, previousEnd, schoolId, campusId, brandId);

        // Calculate growth rates
        Double visitorsGrowth = calculateGrowthRate(currentPeriod.getUniqueVisitors(), previousPeriod.getUniqueVisitors());
        Double appointmentsGrowth = calculateGrowthRate(currentPeriod.getAppointmentRequests(), previousPeriod.getAppointmentRequests());
        Double inquiriesGrowth = calculateGrowthRate(currentPeriod.getMessageInquiries(), previousPeriod.getMessageInquiries());

        return AnalyticsDashboardDto.builder()
                .dashboardDate(LocalDate.now())
                .timePeriod(TimePeriod.DAILY)
                .totalPageViews(currentPeriod.getPageViews())
                .totalUniqueVisitors(currentPeriod.getUniqueVisitors())
                .totalAppointments(currentPeriod.getAppointmentRequests())
                .totalInquiries(currentPeriod.getMessageInquiries())
                .overallConversionRate(currentPeriod.getConversionRate())
                .visitorsGrowth(visitorsGrowth)
                .appointmentsGrowth(appointmentsGrowth)
                .inquiriesGrowth(inquiriesGrowth)
                .topSchoolsByViews(getTopSchoolsByViews(startDate, endDate, 5))
                .topSchoolsByConversions(getTopSchoolsByConversions(startDate, endDate, 5))
                .topSearchTerms(getTopSearchTerms(startDate, endDate, 10))
                .topTrafficSources(getTopTrafficSources(startDate, endDate, 5))
                .visitorsByCity(getVisitorsByCity(startDate, endDate, schoolId, campusId, brandId))
                .visitorsByDevice(getVisitorsByDevice(startDate, endDate, schoolId, campusId, brandId))
                .visitorsBySource(getVisitorsBySource(startDate, endDate, schoolId, campusId, brandId))
                .averagePageLoadTime(getAveragePageLoadTime(startDate, endDate))
                .systemUptime(getSystemUptime(startDate, endDate))
                .errorRate(getErrorRate(startDate, endDate))
                .build();
    }

    // ================================ VISITOR ANALYTICS ================================

    @Transactional
    public VisitorLogDto logVisitor(VisitorLogDto visitorLogDto) {
        log.debug("Logging visitor: {} on page: {}", visitorLogDto.getVisitorId(), visitorLogDto.getPageUrl());

        VisitorLog visitorLog = new VisitorLog();
        visitorLog.setSessionId(visitorLogDto.getSessionId());
        visitorLog.setVisitorId(visitorLogDto.getVisitorId());
        visitorLog.setIpAddress(visitorLogDto.getIpAddress());
        visitorLog.setUserAgent(visitorLogDto.getUserAgent());
        visitorLog.setPageUrl(visitorLogDto.getPageUrl());
        visitorLog.setPageTitle(visitorLogDto.getPageTitle());
        visitorLog.setReferrerUrl(visitorLogDto.getReferrerUrl());
        visitorLog.setVisitTime(visitorLogDto.getVisitTime() != null ? visitorLogDto.getVisitTime() : LocalDateTime.now());
        visitorLog.setTimeOnPageSeconds(visitorLogDto.getTimeOnPageSeconds());
        visitorLog.setDeviceType(visitorLogDto.getDeviceType());
        visitorLog.setBrowserName(visitorLogDto.getBrowserName());
        visitorLog.setBrowserVersion(visitorLogDto.getBrowserVersion());
        visitorLog.setOperatingSystem(visitorLogDto.getOperatingSystem());
        visitorLog.setScreenResolution(visitorLogDto.getScreenResolution());
        visitorLog.setLanguage(visitorLogDto.getLanguage());
        visitorLog.setCountry(visitorLogDto.getCountry());
        visitorLog.setCity(visitorLogDto.getCity());
        visitorLog.setRegion(visitorLogDto.getRegion());
        visitorLog.setLatitude(visitorLogDto.getLatitude());
        visitorLog.setLongitude(visitorLogDto.getLongitude());
        visitorLog.setTimezone(visitorLogDto.getTimezone());
        visitorLog.setTrafficSource(visitorLogDto.getTrafficSource());
        visitorLog.setUtmSource(visitorLogDto.getUtmSource());
        visitorLog.setUtmMedium(visitorLogDto.getUtmMedium());
        visitorLog.setUtmCampaign(visitorLogDto.getUtmCampaign());
        visitorLog.setUtmTerm(visitorLogDto.getUtmTerm());
        visitorLog.setUtmContent(visitorLogDto.getUtmContent());
        visitorLog.setSearchKeywords(visitorLogDto.getSearchKeywords());
        visitorLog.setIsNewVisitor(visitorLogDto.getIsNewVisitor());
        visitorLog.setIsBounce(visitorLogDto.getIsBounce());
        visitorLog.setPageDepth(visitorLogDto.getPageDepth());
        visitorLog.setScrollDepthPercentage(visitorLogDto.getScrollDepthPercentage());
        visitorLog.setExitPage(visitorLogDto.getExitPage());
        visitorLog.setClickedPhone(visitorLogDto.getClickedPhone());
        visitorLog.setClickedEmail(visitorLogDto.getClickedEmail());
        visitorLog.setClickedDirections(visitorLogDto.getClickedDirections());
        visitorLog.setViewedGallery(visitorLogDto.getViewedGallery());
        visitorLog.setDownloadedBrochure(visitorLogDto.getDownloadedBrochure());
        visitorLog.setRequestedAppointment(visitorLogDto.getRequestedAppointment());
        visitorLog.setSentMessage(visitorLogDto.getSentMessage());
        visitorLog.setClickedSocialMedia(visitorLogDto.getClickedSocialMedia());
        visitorLog.setPageLoadTimeMs(visitorLogDto.getPageLoadTimeMs());
        visitorLog.setDomContentLoadedMs(visitorLogDto.getDomContentLoadedMs());
        visitorLog.setFirstPaintMs(visitorLogDto.getFirstPaintMs());
        visitorLog.setLargestContentfulPaintMs(visitorLogDto.getLargestContentfulPaintMs());
        visitorLog.setIsBot(visitorLogDto.getIsBot());
        visitorLog.setBotName(visitorLogDto.getBotName());
        visitorLog.setConsentGiven(visitorLogDto.getConsentGiven());
        visitorLog.setAnonymized(visitorLogDto.getAnonymized());

        visitorLog = visitorLogRepository.save(visitorLog);
        return converterService.mapToDto(visitorLog);
    }

    public Page<VisitorLogDto> getVisitorLogs(AnalyticsFilterDto filter, int page, int size,
                                              HttpServletRequest request) {
        log.info("Getting visitor logs with filter: {}", filter);

        User user = jwtService.getUser(request);
        validateUserAnalyticsAccess(user, filter.getSchoolId(), filter.getCampusId(), filter.getBrandId());

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "visitTime"));

        Page<VisitorLog> visitorLogs = visitorLogRepository.findVisitorLogsByFilter(
                filter.getStartDate(),
                filter.getEndDate(),
                filter.getSchoolId(),
                filter.getCampusId(),
                filter.getBrandId(),
                pageable
        );

        return visitorLogs.map(converterService::mapToDto);
    }

    @Cacheable(value = "visitor_summary", key = "#startDate + '_' + #endDate + '_' + #schoolId + '_' + #campusId + '_' + #brandId")
    public List<VisitorLogSummaryDto> getVisitorSummary(LocalDate startDate, LocalDate endDate,
                                                        Long schoolId, Long campusId, Long brandId,
                                                        HttpServletRequest request) {
        log.info("Getting visitor summary for period: {} to {}", startDate, endDate);

        User user = jwtService.getUser(request);
        validateUserAnalyticsAccess(user, schoolId, campusId, brandId);

        return visitorLogRepository.getVisitorSummary(startDate, endDate, schoolId, campusId, brandId);
    }

    // ================================ SEARCH ANALYTICS ================================

    @Transactional
    public SearchLogDto logSearch(SearchLogDto searchLogDto) {
        log.debug("Logging search: {} with {} results", searchLogDto.getSearchQuery(), searchLogDto.getResultsCount());

        SearchLog searchLog = new SearchLog();
        searchLog.setSessionId(searchLogDto.getSessionId());
        searchLog.setSearchQuery(searchLogDto.getSearchQuery());
        searchLog.setCleanedQuery(cleanSearchQuery(searchLogDto.getSearchQuery()));
        searchLog.setSearchTime(searchLogDto.getSearchTime() != null ? searchLogDto.getSearchTime() : LocalDateTime.now());
        searchLog.setSearchType(searchLogDto.getSearchType());
        searchLog.setResultsCount(searchLogDto.getResultsCount());
        searchLog.setZeroResults(searchLogDto.getResultsCount() == null || searchLogDto.getResultsCount() == 0);
        searchLog.setResponseTimeMs(searchLogDto.getResponseTimeMs());
        searchLog.setFiltersApplied(searchLogDto.getFiltersApplied());
        searchLog.setSortOrder(searchLogDto.getSortOrder());
        searchLog.setPageNumber(searchLogDto.getPageNumber());
        searchLog.setResultsPerPage(searchLogDto.getResultsPerPage());
        searchLog.setClickedResultPosition(searchLogDto.getClickedResultPosition());
        searchLog.setClickedSchoolId(searchLogDto.getClickedSchoolId());
        searchLog.setTimeToClickSeconds(searchLogDto.getTimeToClickSeconds());
        searchLog.setRefinedSearch(searchLogDto.getRefinedSearch());
        searchLog.setAbandonedSearch(searchLogDto.getAbandonedSearch());
        searchLog.setUserLocation(searchLogDto.getUserLocation());
        searchLog.setSearchRadiusKm(searchLogDto.getSearchRadiusKm());
        searchLog.setIpAddress(searchLogDto.getIpAddress());
        searchLog.setDeviceType(searchLogDto.getDeviceType());
        searchLog.setUserAgent(searchLogDto.getUserAgent());
        searchLog.setSearchIntent(classifySearchIntent(searchLogDto.getSearchQuery()));
        searchLog.setConfidenceScore(searchLogDto.getConfidenceScore());
        searchLog.setAutocompleteUsed(searchLogDto.getAutocompleteUsed());
        searchLog.setSuggestionSelected(searchLogDto.getSuggestionSelected());
        searchLog.setExperimentId(searchLogDto.getExperimentId());
        searchLog.setVariant(searchLogDto.getVariant());

        searchLog = searchLogRepository.save(searchLog);
        return converterService.mapToDto(searchLog);
    }

    public Page<SearchLogDto> getSearchLogs(AnalyticsFilterDto filter, int page, int size,
                                            HttpServletRequest request) {
        log.info("Getting search logs with filter: {}", filter);

        User user = jwtService.getUser(request);
        validateUserAnalyticsAccess(user, filter.getSchoolId(), filter.getCampusId(), filter.getBrandId());

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "searchTime"));

        Page<SearchLog> searchLogs = searchLogRepository.findSearchLogsByFilter(
                filter.getStartDate(),
                filter.getEndDate(),
                filter.getSchoolId(),
                filter.getCampusId(),
                filter.getBrandId(),
                pageable
        );

        return searchLogs.map(converterService::mapToSearchLogDto);
    }

    @Cacheable(value = "search_summary", key = "#startDate + '_' + #endDate + '_' + #schoolId + '_' + #campusId + '_' + #brandId")
    public List<SearchLogSummaryDto> getSearchSummary(LocalDate startDate, LocalDate endDate,
                                                      Long schoolId, Long campusId, Long brandId,
                                                      HttpServletRequest request) {
        log.info("Getting search summary for period: {} to {}", startDate, endDate);

        User user = jwtService.getUser(request);
        validateUserAnalyticsAccess(user, schoolId, campusId, brandId);

        return searchLogRepository.getSearchSummary(startDate, endDate, schoolId, campusId, brandId);
    }

    // ================================ PERFORMANCE METRICS ================================

    @Transactional
    public PerformanceMetricsDto logPerformanceMetric(PerformanceMetricsDto metricsDto) {
        log.debug("Logging performance metric: {} - {}ms", metricsDto.getEndpointUrl(), metricsDto.getResponseTimeMs());

        PerformanceMetrics metrics = new PerformanceMetrics();
        metrics.setTimestamp(metricsDto.getTimestamp() != null ? metricsDto.getTimestamp() : LocalDateTime.now());
        metrics.setMetricCategory(metricsDto.getMetricCategory());
        metrics.setEndpointUrl(metricsDto.getEndpointUrl());
        metrics.setHttpMethod(metricsDto.getHttpMethod());
        metrics.setResponseTimeMs(metricsDto.getResponseTimeMs());
        metrics.setHttpStatusCode(metricsDto.getHttpStatusCode());
        metrics.setSuccess(metricsDto.getSuccess());
        metrics.setErrorMessage(metricsDto.getErrorMessage());
        metrics.setErrorStackTrace(metricsDto.getErrorStackTrace());
        metrics.setDbQueryCount(metricsDto.getDbQueryCount());
        metrics.setDbQueryTimeMs(metricsDto.getDbQueryTimeMs());
        metrics.setDbConnectionTimeMs(metricsDto.getDbConnectionTimeMs());
        metrics.setMemoryUsedMb(metricsDto.getMemoryUsedMb());
        metrics.setMemoryTotalMb(metricsDto.getMemoryTotalMb());
        metrics.setMemoryUsagePercentage(metricsDto.getMemoryUsagePercentage());
        metrics.setCpuUsagePercentage(metricsDto.getCpuUsagePercentage());
        metrics.setCpuTimeMs(metricsDto.getCpuTimeMs());
        metrics.setCacheHit(metricsDto.getCacheHit());
        metrics.setCacheKey(metricsDto.getCacheKey());
        metrics.setCacheTtlSeconds(metricsDto.getCacheTtlSeconds());
        metrics.setFileReadCount(metricsDto.getFileReadCount());
        metrics.setFileWriteCount(metricsDto.getFileWriteCount());
        metrics.setFileIoTimeMs(metricsDto.getFileIoTimeMs());
        metrics.setBytesSent(metricsDto.getBytesSent());
        metrics.setBytesReceived(metricsDto.getBytesReceived());
        metrics.setNetworkLatencyMs(metricsDto.getNetworkLatencyMs());
        metrics.setExternalApiCalls(metricsDto.getExternalApiCalls());
        metrics.setExternalApiTimeMs(metricsDto.getExternalApiTimeMs());
        metrics.setExternalApiErrors(metricsDto.getExternalApiErrors());
        metrics.setUserId(metricsDto.getUserId());
        metrics.setSessionId(metricsDto.getSessionId());
        metrics.setIpAddress(metricsDto.getIpAddress());
        metrics.setUserAgent(metricsDto.getUserAgent());
        metrics.setServerName(metricsDto.getServerName());
        metrics.setServerInstance(metricsDto.getServerInstance());
        metrics.setApplicationVersion(metricsDto.getApplicationVersion());
        metrics.setJvmVersion(metricsDto.getJvmVersion());
        metrics.setRequestSizeBytes(metricsDto.getRequestSizeBytes());
        metrics.setResponseSizeBytes(metricsDto.getResponseSizeBytes());
        metrics.setGzipEnabled(metricsDto.getGzipEnabled());
        metrics.setKeepAlive(metricsDto.getKeepAlive());
        metrics.setFeatureName(metricsDto.getFeatureName());
        metrics.setBusinessOperation(metricsDto.getBusinessOperation());
        metrics.setConversionEvent(metricsDto.getConversionEvent());

        // Check thresholds
        metrics.setThresholdExceeded(checkPerformanceThreshold(metricsDto));
        metrics.setThresholdType(metricsDto.getThresholdType());
        metrics.setThresholdValue(metricsDto.getThresholdValue());

        metrics.setAdditionalMetrics(metricsDto.getAdditionalMetrics());

        metrics = performanceMetricsRepository.save(metrics);
        return converterService.mapToDto(metrics);
    }

    @Cacheable(value = "performance_summary", key = "#startDate + '_' + #endDate + '_' + #category")
    public List<PerformanceSummaryDto> getPerformanceSummary(LocalDate startDate, LocalDate endDate,
                                                             PerformanceMetricCategory category,
                                                             HttpServletRequest request) {
        log.info("Getting performance summary for period: {} to {}, category: {}", startDate, endDate, category);

        User user = jwtService.getUser(request);
        validateUserSystemAccess(user);

        return performanceMetricsRepository.getPerformanceSummary(startDate, endDate, category);
    }

    // ================================ ANALYTICS DATA ================================

    public Page<AnalyticsDto> getAnalyticsData(AnalyticsFilterDto filter, int page, int size,
                                               HttpServletRequest request) {
        log.info("Getting analytics data with filter: {}", filter);

        User user = jwtService.getUser(request);
        validateUserAnalyticsAccess(user, filter.getSchoolId(), filter.getCampusId(), filter.getBrandId());

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date"));

        Page<Analytics> analytics = analyticsRepository.findAnalyticsByFilter(
                filter.getStartDate(),
                filter.getEndDate(),
                filter.getTimePeriod(),
                filter.getMetricTypes(),
                filter.getBrandId(),
                filter.getCampusId(),
                filter.getSchoolId(),
                filter.getDataSource(),
                pageable
        );

        return analytics.map(converterService::mapToDto);
    }

    @Cacheable(value = "analytics_summary", key = "#startDate + '_' + #endDate + '_' + #schoolId + '_' + #campusId + '_' + #brandId")
    public AnalyticsSummaryDto getAnalyticsSummary(LocalDate startDate, LocalDate endDate,
                                                   Long schoolId, Long campusId, Long brandId) {
        log.info("Getting analytics summary for period: {} to {}", startDate, endDate);

        return analyticsRepository.getAnalyticsSummary(startDate, endDate, schoolId, campusId, brandId);
    }

    public AnalyticsComparisonDto compareAnalytics(LocalDate startDate, LocalDate endDate,
                                                   LocalDate compareStartDate, LocalDate compareEndDate,
                                                   Long schoolId, Long campusId, Long brandId,
                                                   HttpServletRequest request) {
        log.info("Comparing analytics between periods");

        User user = jwtService.getUser(request);
        validateUserAnalyticsAccess(user, schoolId, campusId, brandId);

        AnalyticsSummaryDto currentPeriod = getAnalyticsSummary(startDate, endDate, schoolId, campusId, brandId);
        AnalyticsSummaryDto previousPeriod = getAnalyticsSummary(compareStartDate, compareEndDate, schoolId, campusId, brandId);

        Map<String, Double> changePercentages = calculateChangePercentages(currentPeriod, previousPeriod);

        return AnalyticsComparisonDto.builder()
                .comparisonType("PERIOD")
                .startDate(startDate)
                .endDate(endDate)
                .currentPeriod(currentPeriod)
                .previousPeriod(previousPeriod)
                .changePercentages(changePercentages)
                .significantChanges(identifySignificantChanges(changePercentages))
                .trendAnalysis(generateTrendAnalysis(changePercentages))
                .overallAssessment(generateOverallAssessment(changePercentages))
                .build();
    }

    // ================================ REAL-TIME ANALYTICS ================================

    @Cacheable(value = "realtime_analytics", key = "'current'", unless = "#result == null")
    public RealTimeAnalyticsDto getRealTimeAnalytics(HttpServletRequest request) {
        log.info("Getting real-time analytics");

        User user = jwtService.getUser(request);
        validateUserSystemAccess(user);

        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);

        return RealTimeAnalyticsDto.builder()
                .timestamp(LocalDateTime.now())
                .currentActiveUsers(visitorLogRepository.countActiveUsers(LocalDateTime.now().minusMinutes(5)))
                .lastHourVisitors(visitorLogRepository.countVisitorsSince(oneHourAgo))
                .lastHourAppointments(0L) // Would need appointment repository
                .lastHourInquiries(0L) // Would need message repository
                .recentVisitors(visitorLogRepository.getRecentVisitors(10))
                .recentSearches(searchLogRepository.getRecentSearches(10))
                .systemLoad(getCurrentSystemLoad())
                .memoryUsage(getCurrentMemoryUsage())
                .diskUsage(getCurrentDiskUsage())
                .allSystemsOperational(checkSystemHealth())
                .activeAlerts(getActiveAlerts())
                .criticalAlertsCount(getCriticalAlertsCount())
                .warningAlertsCount(getWarningAlertsCount())
                .build();
    }

    // ================================ EXPORT FUNCTIONALITY ================================

    @Transactional
    public AnalyticsExportDto requestExport(AnalyticsExportDto exportRequest, HttpServletRequest request) {
        log.info("Requesting analytics export: {}", exportRequest.getExportName());

        User user = jwtService.getUser(request);
        validateUserAnalyticsAccess(user, null, null, null);

        // Generate unique export ID
        String exportId = generateExportId();

        exportRequest.setExportId(exportId);
        exportRequest.setStatus("REQUESTED");
        exportRequest.setProgress(0);
        exportRequest.setRequestedAt(LocalDateTime.now());
        exportRequest.setRequestedBy(user.getFirstName() + " " + user.getLastName());
        exportRequest.setRequestedByEmail(user.getEmail());

        // Start async export process
        processExportAsync(exportRequest);

        return exportRequest;
    }

    // ================================ HELPER METHODS ================================

    private void validateUserAnalyticsAccess(User user, Long schoolId, Long campusId, Long brandId) {
        if (!hasSystemRole(user) &&
                !hasAccessToAnalytics(user, schoolId, campusId, brandId)) {
            throw new BusinessException("User does not have access to analytics data");
        }
    }

    private void validateUserSystemAccess(User user) {
        if (!hasSystemRole(user)) {
            throw new BusinessException("User does not have system access");
        }
    }

    private boolean hasSystemRole(User user) {
        return user.getUserRoles().stream()
                .anyMatch(userRole -> userRole.getRoleLevel() == RoleLevel.SYSTEM);
    }

    private boolean hasAccessToAnalytics(User user, Long schoolId, Long campusId, Long brandId) {
        return user.getInstitutionAccess().stream()
                .anyMatch(access -> {
                    if (access.getExpiresAt() != null && access.getExpiresAt().isBefore(LocalDateTime.now())) {
                        return false;
                    }

                    return switch (access.getAccessType()) {
                        case SCHOOL -> access.getEntityId().equals(schoolId);
                        case CAMPUS -> access.getEntityId().equals(campusId);
                        case BRAND -> access.getEntityId().equals(brandId);
                        default -> false;
                    };
                });
    }

    private String cleanSearchQuery(String query) {
        if (query == null) return null;
        return query.toLowerCase()
                .replaceAll("[^a-zA-Z0-9\\sğüşıöçĞÜŞİÖÇ]", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private SearchIntent classifySearchIntent(String query) {
        if (query == null || query.trim().isEmpty()) {
            return SearchIntent.UNCLEAR;
        }

        String lowerQuery = query.toLowerCase();

        if (lowerQuery.contains("fiyat") || lowerQuery.contains("ücret") || lowerQuery.contains("maliyet")) {
            return SearchIntent.PRICE_COMPARISON;
        } else if (lowerQuery.contains("randevu") || lowerQuery.contains("görüşme")) {
            return SearchIntent.TRANSACTIONAL;
        } else if (lowerQuery.contains("nerede") || lowerQuery.contains("adres") || lowerQuery.contains("konum")) {
            return SearchIntent.LOCAL;
        } else if (lowerQuery.contains("nasıl") || lowerQuery.contains("ne") || lowerQuery.contains("nedir")) {
            return SearchIntent.INFORMATIONAL;
        } else if (lowerQuery.contains("yorum") || lowerQuery.contains("değerlendirme")) {
            return SearchIntent.REVIEW_SEEKING;
        }

        return SearchIntent.INFORMATIONAL;
    }

    private Boolean checkPerformanceThreshold(PerformanceMetricsDto metricsDto) {
        // Default thresholds - could be configurable
        if (metricsDto.getResponseTimeMs() != null && metricsDto.getResponseTimeMs() > 5000) {
            return true;
        }
        if (metricsDto.getMemoryUsagePercentage() != null && metricsDto.getMemoryUsagePercentage() > 85.0) {
            return true;
        }
        if (metricsDto.getCpuUsagePercentage() != null && metricsDto.getCpuUsagePercentage() > 80.0) {
            return true;
        }
        return false;
    }

    private Double calculateGrowthRate(Long current, Long previous) {
        if (previous == null || previous == 0) {
            return current != null && current > 0 ? 100.0 : 0.0;
        }
        if (current == null) {
            return -100.0;
        }
        return ((double) (current - previous) / previous) * 100.0;
    }

    private List<com.genixo.education.search.dto.institution.SchoolSummaryDto> getTopSchoolsByViews(LocalDate startDate, LocalDate endDate, int limit) {
        return analyticsRepository.getTopSchoolsByViews(startDate, endDate, limit);
    }

    private List<com.genixo.education.search.dto.institution.SchoolSummaryDto> getTopSchoolsByConversions(LocalDate startDate, LocalDate endDate, int limit) {
        return analyticsRepository.getTopSchoolsByConversions(startDate, endDate, limit);
    }

    private List<String> getTopSearchTerms(LocalDate startDate, LocalDate endDate, int limit) {
        return searchLogRepository.getTopSearchTerms(startDate, endDate, limit);
    }

    private List<String> getTopTrafficSources(LocalDate startDate, LocalDate endDate, int limit) {
        return visitorLogRepository.getTopTrafficSources(startDate, endDate, limit);
    }

    private Map<String, Long> getVisitorsByCity(LocalDate startDate, LocalDate endDate, Long schoolId, Long campusId, Long brandId) {
        return visitorLogRepository.getVisitorsByCity(startDate, endDate, schoolId, campusId, brandId);
    }

    private Map<DeviceType, Long> getVisitorsByDevice(LocalDate startDate, LocalDate endDate, Long schoolId, Long campusId, Long brandId) {
        return visitorLogRepository.getVisitorsByDevice(startDate, endDate, schoolId, campusId, brandId);
    }

    private Map<TrafficSource, Long> getVisitorsBySource(LocalDate startDate, LocalDate endDate, Long schoolId, Long campusId, Long brandId) {
        return visitorLogRepository.getVisitorsBySource(startDate, endDate, schoolId, campusId, brandId);
    }

    private Double getAveragePageLoadTime(LocalDate startDate, LocalDate endDate) {
        return performanceMetricsRepository.getAverageResponseTime(startDate, endDate, PerformanceMetricCategory.WEB_REQUEST);
    }

    private Double getSystemUptime(LocalDate startDate, LocalDate endDate) {
        return performanceMetricsRepository.getSystemUptime(startDate, endDate);
    }

    private Double getErrorRate(LocalDate startDate, LocalDate endDate) {
        return performanceMetricsRepository.getErrorRate(startDate, endDate);
    }

    private Map<String, Double> calculateChangePercentages(AnalyticsSummaryDto current, AnalyticsSummaryDto previous) {
        Map<String, Double> changes = new java.util.HashMap<>();

        changes.put("pageViews", calculateGrowthRate(current.getPageViews(), previous.getPageViews()));
        changes.put("uniqueVisitors", calculateGrowthRate(current.getUniqueVisitors(), previous.getUniqueVisitors()));
        changes.put("appointmentRequests", calculateGrowthRate(current.getAppointmentRequests(), previous.getAppointmentRequests()));
        changes.put("messageInquiries", calculateGrowthRate(current.getMessageInquiries(), previous.getMessageInquiries()));

        if (current.getConversionRate() != null && previous.getConversionRate() != null) {
            changes.put("conversionRate", ((current.getConversionRate() - previous.getConversionRate()) / previous.getConversionRate()) * 100.0);
        }

        if (current.getAverageRating() != null && previous.getAverageRating() != null) {
            changes.put("averageRating", ((current.getAverageRating() - previous.getAverageRating()) / previous.getAverageRating()) * 100.0);
        }

        return changes;
    }

    private List<String> identifySignificantChanges(Map<String, Double> changePercentages) {
        List<String> significantChanges = new java.util.ArrayList<>();

        for (Map.Entry<String, Double> entry : changePercentages.entrySet()) {
            Double change = entry.getValue();
            if (Math.abs(change) >= 20.0) { // 20% threshold for significant change
                String direction = change > 0 ? "increased" : "decreased";
                significantChanges.add(String.format("%s %s by %.1f%%",
                        entry.getKey(), direction, Math.abs(change)));
            }
        }

        return significantChanges;
    }

    private List<String> generateTrendAnalysis(Map<String, Double> changePercentages) {
        List<String> trends = new java.util.ArrayList<>();

        // Analyze overall trends
        long positiveChanges = changePercentages.values().stream()
                .mapToLong(change -> change > 0 ? 1 : 0)
                .sum();

        long totalChanges = changePercentages.size();

        if (positiveChanges >= totalChanges * 0.7) {
            trends.add("Overall positive growth trend detected");
        } else if (positiveChanges <= totalChanges * 0.3) {
            trends.add("Declining trend across multiple metrics");
        } else {
            trends.add("Mixed performance with both growth and decline areas");
        }

        // Specific metric insights
        Double visitorGrowth = changePercentages.get("uniqueVisitors");
        Double conversionGrowth = changePercentages.get("appointmentRequests");

        if (visitorGrowth != null && conversionGrowth != null) {
            if (visitorGrowth > 10 && conversionGrowth < 0) {
                trends.add("Traffic is growing but conversion is declining - review user experience");
            } else if (visitorGrowth < 0 && conversionGrowth > 10) {
                trends.add("Lower traffic but higher conversion rates - quality over quantity trend");
            }
        }

        return trends;
    }

    private String generateOverallAssessment(Map<String, Double> changePercentages) {
        double avgChange = changePercentages.values().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        if (avgChange > 15) {
            return "EXCELLENT - Strong growth across key metrics";
        } else if (avgChange > 5) {
            return "GOOD - Positive growth with room for improvement";
        } else if (avgChange > -5) {
            return "STABLE - Performance is maintaining steady levels";
        } else if (avgChange > -15) {
            return "CONCERNING - Some metrics showing decline";
        } else {
            return "CRITICAL - Significant decline needs immediate attention";
        }
    }

    // System monitoring methods
    private Double getCurrentSystemLoad() {
        return performanceMetricsRepository.getCurrentSystemLoad();
    }

    private Double getCurrentMemoryUsage() {
        return performanceMetricsRepository.getCurrentMemoryUsage();
    }

    private Double getCurrentDiskUsage() {
        // This would typically come from system monitoring
        return 65.0; // Placeholder
    }

    private Boolean checkSystemHealth() {
        Double memoryUsage = getCurrentMemoryUsage();
        Double systemLoad = getCurrentSystemLoad();

        return memoryUsage != null && memoryUsage < 85.0 &&
                systemLoad != null && systemLoad < 80.0;
    }

    private List<String> getActiveAlerts() {
        return performanceMetricsRepository.getActiveAlerts();
    }

    private Integer getCriticalAlertsCount() {
        return performanceMetricsRepository.getCriticalAlertsCount();
    }

    private Integer getWarningAlertsCount() {
        return performanceMetricsRepository.getWarningAlertsCount();
    }

    private String generateExportId() {
        return "EXPORT_" + System.currentTimeMillis() + "_" +
                java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void processExportAsync(AnalyticsExportDto exportRequest) {
        // This would typically be handled by an async processor
        // For now, just log the request
        log.info("Processing export request: {}", exportRequest.getExportId());

        // In a real implementation, this would:
        // 1. Queue the export job
        // 2. Process data based on export type and filters
        // 3. Generate file in requested format
        // 4. Upload to cloud storage
        // 5. Update export status and provide download URL
        // 6. Send notification email
    }

    // ================================ ANALYTICS ALERTS ================================

    @Transactional
    public AnalyticsAlertDto createAlert(AnalyticsAlertDto alertDto, HttpServletRequest request) {
        log.info("Creating analytics alert: {}", alertDto.getAlertName());

        User user = jwtService.getUser(request);
        validateUserAnalyticsAccess(user, alertDto.getSchoolId(), alertDto.getCampusId(), alertDto.getBrandId());

        // Create alert entity and save
        // This would involve creating an AnalyticsAlert entity

        return alertDto; // Placeholder
    }

    public List<AnalyticsAlertDto> getActiveAlerts(HttpServletRequest request) {
        log.info("Getting active analytics alerts");

        User user = jwtService.getUser(request);
        validateUserSystemAccess(user);

        // Return active alerts for user's accessible institutions
        return List.of(); // Placeholder
    }

    // ================================ CUSTOM ANALYTICS QUERIES ================================

    public AnalyticsResultDto executeCustomQuery(AnalyticsQueryDto queryDto, HttpServletRequest request) {
        log.info("Executing custom analytics query: {}", queryDto.getQueryName());

        User user = jwtService.getUser(request);
        validateUserAnalyticsAccess(user, null, null, null);

        LocalDateTime startTime = LocalDateTime.now();

        // Execute custom query based on parameters
        List<Map<String, Object>> data = executeQuery(queryDto);

        Long executionTime = ChronoUnit.MILLIS.between(startTime, LocalDateTime.now());

        return AnalyticsResultDto.builder()
                .queryId(java.util.UUID.randomUUID().toString())
                .executedAt(LocalDateTime.now())
                .executionTimeMs(executionTime)
                .totalRows(data.size())
                .data(data)
                .summary(generateQuerySummary(data, queryDto))
                .totals(generateQueryTotals(data, queryDto))
                .columns(queryDto.getMetrics())
                .insights(generateQueryInsights(data, queryDto))
                .build();
    }

    private List<Map<String, Object>> executeQuery(AnalyticsQueryDto queryDto) {
        // This would execute the actual query based on metrics, dimensions, filters
        // For now, return empty list
        return List.of();
    }

    private Map<String, Object> generateQuerySummary(List<Map<String, Object>> data, AnalyticsQueryDto queryDto) {
        Map<String, Object> summary = new java.util.HashMap<>();
        summary.put("totalRows", data.size());
        summary.put("queryType", "CUSTOM");
        summary.put("timePeriod", queryDto.getGroupBy());
        return summary;
    }

    private Map<String, Object> generateQueryTotals(List<Map<String, Object>> data, AnalyticsQueryDto queryDto) {
        Map<String, Object> totals = new java.util.HashMap<>();

        for (String metric : queryDto.getMetrics()) {
            if (isNumericMetric(metric)) {
                double total = data.stream()
                        .mapToDouble(row -> {
                            Object value = row.get(metric);
                            return value instanceof Number ? ((Number) value).doubleValue() : 0.0;
                        })
                        .sum();
                totals.put(metric, total);
            }
        }

        return totals;
    }

    private List<String> generateQueryInsights(List<Map<String, Object>> data, AnalyticsQueryDto queryDto) {
        List<String> insights = new java.util.ArrayList<>();

        if (data.isEmpty()) {
            insights.add("No data available for the selected criteria");
        } else {
            insights.add(String.format("Query returned %d records", data.size()));

            // Add more specific insights based on the data
            if (queryDto.getMetrics().contains("pageViews")) {
                insights.add("Page views data is included in results");
            }
            if (queryDto.getMetrics().contains("appointmentRequests")) {
                insights.add("Appointment conversion data is available");
            }
        }

        return insights;
    }

    private boolean isNumericMetric(String metric) {
        List<String> numericMetrics = List.of(
                "pageViews", "uniqueVisitors", "appointmentRequests", "messageInquiries",
                "conversionRate", "bounceRate", "averageRating"
        );
        return numericMetrics.contains(metric);
    }

    // ================================ ANALYTICS REPORTS ================================

    public AnalyticsReportDto generateReport(String reportType, LocalDate startDate, LocalDate endDate,
                                             Long schoolId, Long campusId, Long brandId,
                                             HttpServletRequest request) {
        log.info("Generating analytics report: {} for period {} to {}", reportType, startDate, endDate);

        User user = jwtService.getUser(request);
        validateUserAnalyticsAccess(user, schoolId, campusId, brandId);

        String reportId = "RPT_" + System.currentTimeMillis();

        // Get dashboard data
        AnalyticsDashboardDto dashboardData = getDashboard(startDate, endDate, schoolId, campusId, brandId, request);

        // Get detailed metrics
        AnalyticsFilterDto filter = AnalyticsFilterDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .schoolId(schoolId)
                .campusId(campusId)
                .brandId(brandId)
                .build();

        Page<AnalyticsDto> analyticsPage = getAnalyticsData(filter, 0, 100, request);
        List<AnalyticsDto> detailedMetrics = analyticsPage.getContent();

        // Get visitor and search summaries
        List<VisitorLogSummaryDto> visitorSummary = getVisitorSummary(startDate, endDate, schoolId, campusId, brandId, request);
        List<SearchLogSummaryDto> searchSummary = getSearchSummary(startDate, endDate, schoolId, campusId, brandId, request);

        return AnalyticsReportDto.builder()
                .reportId(reportId)
                .reportTitle(generateReportTitle(reportType, startDate, endDate))
                .reportType(reportType)
                .startDate(startDate)
                .endDate(endDate)
                .generatedAt(LocalDateTime.now())
                .generatedBy(user.getFirstName() + " " + user.getLastName())
                .dashboardData(dashboardData)
                .detailedMetrics(detailedMetrics)
                .visitorSummary(visitorSummary)
                .searchSummary(searchSummary)
                .keyInsights(generateKeyInsights(dashboardData, detailedMetrics))
                .recommendations(generateRecommendations(dashboardData))
                .actionItems(generateActionItems(dashboardData))
                .isPublic(false)
                .build();
    }

    private String generateReportTitle(String reportType, LocalDate startDate, LocalDate endDate) {
        return String.format("%s Analytics Report - %s to %s",
                reportType.toUpperCase(),
                startDate.toString(),
                endDate.toString());
    }

    private List<String> generateKeyInsights(AnalyticsDashboardDto dashboard, List<AnalyticsDto> metrics) {
        List<String> insights = new java.util.ArrayList<>();

        if (dashboard.getTotalUniqueVisitors() != null && dashboard.getTotalUniqueVisitors() > 0) {
            insights.add(String.format("Total unique visitors: %,d", dashboard.getTotalUniqueVisitors()));
        }

        if (dashboard.getOverallConversionRate() != null) {
            insights.add(String.format("Overall conversion rate: %.2f%%", dashboard.getOverallConversionRate()));
        }

        if (dashboard.getVisitorsGrowth() != null) {
            String trend = dashboard.getVisitorsGrowth() > 0 ? "increased" : "decreased";
            insights.add(String.format("Visitor traffic %s by %.1f%%", trend, Math.abs(dashboard.getVisitorsGrowth())));
        }

        return insights;
    }

    private List<String> generateRecommendations(AnalyticsDashboardDto dashboard) {
        List<String> recommendations = new java.util.ArrayList<>();

        if (dashboard.getOverallConversionRate() != null && dashboard.getOverallConversionRate() < 2.0) {
            recommendations.add("Consider improving call-to-action buttons and contact forms to increase conversion rate");
        }

        if (dashboard.getAveragePageLoadTime() != null && dashboard.getAveragePageLoadTime() > 3000) {
            recommendations.add("Optimize page loading speed as it currently exceeds 3 seconds");
        }

        if (dashboard.getVisitorsGrowth() != null && dashboard.getVisitorsGrowth() < 0) {
            recommendations.add("Implement SEO improvements and content marketing to increase visitor traffic");
        }

        return recommendations;
    }

    private List<String> generateActionItems(AnalyticsDashboardDto dashboard) {
        List<String> actionItems = new java.util.ArrayList<>();

        actionItems.add("Review top performing schools and replicate successful strategies");
        actionItems.add("Analyze search terms with zero results and improve content coverage");
        actionItems.add("Monitor performance metrics and set up alerts for critical thresholds");

        if (dashboard.getErrorRate() != null && dashboard.getErrorRate() > 1.0) {
            actionItems.add("Investigate and fix system errors to improve user experience");
        }

        return actionItems;
    }

    // ================================ CACHE MANAGEMENT ================================

    @CacheEvict(value = {"analytics_dashboard", "analytics_summary", "visitor_summary", "search_summary", "realtime_analytics"}, allEntries = true)
    public void clearAnalyticsCache() {
        log.info("Clearing analytics cache");
    }

    @CacheEvict(value = "realtime_analytics", key = "'current'")
    public void refreshRealTimeCache() {
        log.debug("Refreshing real-time analytics cache");
    }
}