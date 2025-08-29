package com.genixo.education.search.service;

import com.genixo.education.search.common.exception.BusinessException;
import com.genixo.education.search.dto.analytics.*;
import com.genixo.education.search.dto.institution.SchoolSummaryDto;
import com.genixo.education.search.entity.analytics.*;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.entity.user.UserInstitutionAccess;
import com.genixo.education.search.entity.user.UserRole;
import com.genixo.education.search.enumaration.*;
import com.genixo.education.search.repository.analytics.*;
import com.genixo.education.search.service.auth.JwtService;
import com.genixo.education.search.service.converter.AnalyticsConverterService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AnalyticsService Tests")
class AnalyticsServiceTest {

    @Mock
    private AnalyticsRepository analyticsRepository;
    @Mock
    private VisitorLogRepository visitorLogRepository;
    @Mock
    private SearchLogRepository searchLogRepository;
    @Mock
    private PerformanceMetricsRepository performanceMetricsRepository;
    @Mock
    private AnalyticsConverterService converterService;
    @Mock
    private JwtService jwtService;
    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private AnalyticsService analyticsService;

    private User systemUser;
    private User regularUser;
    private User brandUser;

    @BeforeEach
    void setUp() {
        systemUser = createUser(1L, RoleLevel.SYSTEM);
        regularUser = createUser(2L, RoleLevel.BRAND);
        brandUser = createUser(3L, RoleLevel.BRAND);

        // Brand user with brand access
        MockInstitutionAccess brandAccess = new MockInstitutionAccess();
        brandAccess.setAccessType(AccessType.BRAND);
        brandAccess.setEntityId(1L);
        brandAccess.setExpiresAt(null);
        UserInstitutionAccess brandAccessEntity = new UserInstitutionAccess();
        brandAccessEntity.setAccessType(brandAccess.getAccessType());
        brandAccessEntity.setEntityId(brandAccess.getEntityId());
        brandAccessEntity.setExpiresAt(brandAccess.getExpiresAt());
        // ceyhun


        brandUser.setInstitutionAccess(Set.of(brandAccessEntity));

      //   doNothing().when(schoolRepository).incrementViewCount(1L); ceyhun

        regularUser.setInstitutionAccess(Collections.emptySet());
    }

    @Nested
    @DisplayName("getDashboard() Tests")
    class GetDashboardTests {

        private LocalDate startDate;
        private LocalDate endDate;
        private AnalyticsSummaryDto currentPeriod;
        private AnalyticsSummaryDto previousPeriod;
        private List<SchoolSummaryDto> topSchools;
        private List<String> topSearchTerms;

        @BeforeEach
        void setUp() {
            startDate = LocalDate.of(2024, 1, 1);
            endDate = LocalDate.of(2024, 1, 31);

            currentPeriod = AnalyticsSummaryDto.builder()
                    .pageViews(1000L)
                    .uniqueVisitors(800L)
                    .appointmentRequests(50L)
                    .messageInquiries(30L)
                    .conversionRate(6.25)
                    .averageRating(4.2)
                    .build();

            previousPeriod = AnalyticsSummaryDto.builder()
                    .pageViews(800L)
                    .uniqueVisitors(600L)
                    .appointmentRequests(40L)
                    .messageInquiries(25L)
                    .conversionRate(5.42)
                    .averageRating(4.0)
                    .build();

            topSchools = List.of(
                    SchoolSummaryDto.builder().id(1L).name("Top School").build()
            );

            topSearchTerms = List.of("okul", "eğitim", "anaokulu");
        }

        @Test
        @DisplayName("Should generate dashboard successfully for system user")
        void shouldGenerateDashboardSuccessfullyForSystemUser() {
            // Given
            Long schoolId = 1L, campusId = 1L, brandId = 1L;

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(analyticsRepository.getAnalyticsSummary(startDate, endDate, schoolId, campusId, brandId))
                    .thenReturn(currentPeriod);
            when(analyticsRepository.getAnalyticsSummary(eq(startDate.minusDays(31)), eq(startDate.minusDays(1)),
                    eq(schoolId), eq(campusId), eq(brandId))).thenReturn(previousPeriod);

            // Mock dashboard data calls
            when(analyticsRepository.getTopSchoolsByViews(startDate, endDate, 5)).thenReturn(topSchools);
            when(analyticsRepository.getTopSchoolsByConversions(startDate, endDate, 5)).thenReturn(topSchools);
            when(searchLogRepository.getTopSearchTerms(startDate, endDate, 10)).thenReturn(topSearchTerms);
            when(visitorLogRepository.getTopTrafficSources(startDate, endDate, 5)).thenReturn(List.of("DIRECT", "ORGANIC_SEARCH"));
            when(visitorLogRepository.getVisitorsByCity(startDate, endDate, schoolId, campusId, brandId)).thenReturn(Map.of("Istanbul", 100L));
            when(visitorLogRepository.getVisitorsByDevice(startDate, endDate, schoolId, campusId, brandId)).thenReturn(Map.of(DeviceType.MOBILE, 60L));
            when(visitorLogRepository.getVisitorsBySource(startDate, endDate, schoolId, campusId, brandId)).thenReturn(Map.of(TrafficSource.DIRECT, 80L));
            when(performanceMetricsRepository.getAverageResponseTime(startDate, endDate, PerformanceMetricCategory.WEB_REQUEST)).thenReturn(2500.0);
            when(performanceMetricsRepository.getSystemUptime(startDate, endDate)).thenReturn(99.5);
            when(performanceMetricsRepository.getErrorRate(startDate, endDate)).thenReturn(0.5);

            // When
            AnalyticsDashboardDto result = analyticsService.getDashboard(startDate, endDate, schoolId, campusId, brandId, request);

            // Then
            assertNotNull(result);
            assertEquals(LocalDate.now(), result.getDashboardDate());
            assertEquals(TimePeriod.DAILY, result.getTimePeriod());
            assertEquals(1000L, result.getTotalPageViews());
            assertEquals(800L, result.getTotalUniqueVisitors());
            assertEquals(50L, result.getTotalAppointments());
            assertEquals(30L, result.getTotalInquiries());
            assertEquals(6.25, result.getOverallConversionRate());

            // Verify growth calculations
            assertEquals(33.33, result.getVisitorsGrowth(), 0.01); // (800-600)/600 * 100
            assertEquals(25.0, result.getAppointmentsGrowth(), 0.01); // (50-40)/40 * 100
            assertEquals(20.0, result.getInquiriesGrowth(), 0.01); // (30-25)/25 * 100

            // Verify dashboard data
            assertEquals(topSchools, result.getTopSchoolsByViews());
            assertEquals(topSearchTerms, result.getTopSearchTerms());
            assertEquals(2500.0, result.getAveragePageLoadTime());
            assertEquals(99.5, result.getSystemUptime());
            assertEquals(0.5, result.getErrorRate());

            verify(jwtService).getUser(request);
            verify(analyticsRepository, times(2)).getAnalyticsSummary(any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Should throw BusinessException when user has no access to analytics")
        void shouldThrowBusinessExceptionWhenUserHasNoAccessToAnalytics() {
            // Given
            Long schoolId = 1L, campusId = 1L, brandId = 1L;
            when(jwtService.getUser(request)).thenReturn(regularUser);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> analyticsService.getDashboard(startDate, endDate, schoolId, campusId, brandId, request));

            assertEquals("User does not have access to analytics data", exception.getMessage());

            verify(jwtService).getUser(request);
            verifyNoInteractions(analyticsRepository);
        }

        @Test
        @DisplayName("Should allow access for user with brand access")
        void shouldAllowAccessForUserWithBrandAccess() {
            // Given
            Long schoolId = null, campusId = null, brandId = 1L;

            when(jwtService.getUser(request)).thenReturn(brandUser);
            when(analyticsRepository.getAnalyticsSummary(startDate, endDate, schoolId, campusId, brandId))
                    .thenReturn(currentPeriod);
            when(analyticsRepository.getAnalyticsSummary(any(), any(), any(), any(), any()))
                    .thenReturn(previousPeriod);

            // Mock other required calls
            setupMockDashboardCalls();

            // When
            AnalyticsDashboardDto result = analyticsService.getDashboard(startDate, endDate, schoolId, campusId, brandId, request);

            // Then
            assertNotNull(result);
            verify(jwtService).getUser(request);
            verify(analyticsRepository, atLeastOnce()).getAnalyticsSummary(any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Should handle null values in growth calculations gracefully")
        void shouldHandleNullValuesInGrowthCalculationsGracefully() {
            // Given
            Long schoolId = 1L, campusId = 1L, brandId = 1L;

            AnalyticsSummaryDto nullPreviousPeriod = AnalyticsSummaryDto.builder()
                    .pageViews(null)
                    .uniqueVisitors(0L) // Zero previous value
                    .appointmentRequests(null)
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(analyticsRepository.getAnalyticsSummary(startDate, endDate, schoolId, campusId, brandId))
                    .thenReturn(currentPeriod);
            when(analyticsRepository.getAnalyticsSummary(any(), any(), eq(schoolId), eq(campusId), eq(brandId)))
                    .thenReturn(nullPreviousPeriod);

            setupMockDashboardCalls();

            // When
            AnalyticsDashboardDto result = analyticsService.getDashboard(startDate, endDate, schoolId, campusId, brandId, request);

            // Then
            assertNotNull(result);
            assertEquals(100.0, result.getVisitorsGrowth()); // 800 from 0 = 100% growth
            // Other growth values should handle null gracefully
            assertNotNull(result.getAppointmentsGrowth());

            verify(jwtService).getUser(request);
        }

        private void setupMockDashboardCalls() {
            when(analyticsRepository.getTopSchoolsByViews(any(), any(), anyInt())).thenReturn(Collections.emptyList());
            when(analyticsRepository.getTopSchoolsByConversions(any(), any(), anyInt())).thenReturn(Collections.emptyList());
            when(searchLogRepository.getTopSearchTerms(any(), any(), anyInt())).thenReturn(Collections.emptyList());
            when(visitorLogRepository.getTopTrafficSources(any(), any(), anyInt())).thenReturn(Collections.emptyList());
            when(visitorLogRepository.getVisitorsByCity(any(), any(), any(), any(), any())).thenReturn(Collections.emptyMap());
            when(visitorLogRepository.getVisitorsByDevice(any(), any(), any(), any(), any())).thenReturn(Collections.emptyMap());
            when(visitorLogRepository.getVisitorsBySource(any(), any(), any(), any(), any())).thenReturn(Collections.emptyMap());
            when(performanceMetricsRepository.getAverageResponseTime(any(), any(), any())).thenReturn(2000.0);
            when(performanceMetricsRepository.getSystemUptime(any(), any())).thenReturn(99.0);
            when(performanceMetricsRepository.getErrorRate(any(), any())).thenReturn(1.0);
        }
    }

    @Nested
    @DisplayName("logVisitor() Tests")
    class LogVisitorTests {

        private VisitorLogDto validVisitorLogDto;
        private VisitorLog savedVisitorLog;

        @BeforeEach
        void setUp() {
            validVisitorLogDto = VisitorLogDto.builder()
                    .sessionId("session123")
                    .visitorId("visitor456")
                    .ipAddress("192.168.1.1")
                    .userAgent("Mozilla/5.0")
                    .pageUrl("/school/test-school")
                    .pageTitle("Test School - Education")
                    .referrerUrl("https://google.com")
                    .visitTime(LocalDateTime.now())
                    .timeOnPageSeconds(120)
                    .deviceType(DeviceType.MOBILE)
                    .browserName("Chrome")
                    .browserVersion("91.0")
                    .operatingSystem("Android")
                    .screenResolution("1080x1920")
                    .language("tr-TR")
                    .country("Turkey")
                    .city("Istanbul")
                    .region("Marmara")
                    .latitude(41.0082)
                    .longitude(28.9784)
                    .timezone("Europe/Istanbul")
                    .trafficSource(TrafficSource.ORGANIC_SEARCH)
                    .utmSource("google")
                    .utmMedium("cpc")
                    .utmCampaign("school-ads")
                    .searchKeywords("anaokulu istanbul")
                    .isNewVisitor(true)
                    .isBounce(false)
                    .pageDepth(3)
                    .scrollDepthPercentage(75)
                    .exitPage(false)
                    .clickedPhone(true)
                    .clickedEmail(false)
                    .clickedDirections(true)
                    .viewedGallery(true)
                    .downloadedBrochure(false)
                    .requestedAppointment(true)
                    .sentMessage(false)
                    .clickedSocialMedia(true)
                    .pageLoadTimeMs(2500)
                    .domContentLoadedMs(1800)
                    .firstPaintMs(1200)
                    .largestContentfulPaintMs(3000)
                    .isBot(false)
                    .botName(null)
                    .consentGiven(true)
                    .anonymized(false)
                    .build();

            savedVisitorLog = new VisitorLog();
            savedVisitorLog.setId(1L);
            savedVisitorLog.setSessionId("session123");
            savedVisitorLog.setVisitorId("visitor456");
            savedVisitorLog.setPageUrl("/school/test-school");
        }

        @Test
        @DisplayName("Should log visitor successfully with all fields")
        void shouldLogVisitorSuccessfullyWithAllFields() {
            // Given
            VisitorLogDto expectedDto = VisitorLogDto.builder()
                    .id(1L)
                    .sessionId("session123")
                    .visitorId("visitor456")
                    .build();

            when(visitorLogRepository.save(any(VisitorLog.class))).thenReturn(savedVisitorLog);
            when(converterService.mapToDto(savedVisitorLog)).thenReturn(expectedDto);

            // When
            VisitorLogDto result = analyticsService.logVisitor(validVisitorLogDto);

            // Then
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("session123", result.getSessionId());
            assertEquals("visitor456", result.getVisitorId());

            verify(visitorLogRepository).save(argThat(visitorLog ->
                    visitorLog.getSessionId().equals("session123") &&
                            visitorLog.getVisitorId().equals("visitor456") &&
                            visitorLog.getIpAddress().equals("192.168.1.1") &&
                            visitorLog.getPageUrl().equals("/school/test-school") &&
                            visitorLog.getPageTitle().equals("Test School - Education") &&
                            visitorLog.getReferrerUrl().equals("https://google.com") &&
                            visitorLog.getTimeOnPageSeconds().equals(120) &&
                            visitorLog.getDeviceType() == DeviceType.MOBILE &&
                            visitorLog.getBrowserName().equals("Chrome") &&
                            visitorLog.getBrowserVersion().equals("91.0") &&
                            visitorLog.getOperatingSystem().equals("Android") &&
                            visitorLog.getScreenResolution().equals("1080x1920") &&
                            visitorLog.getLanguage().equals("tr-TR") &&
                            visitorLog.getCountry().equals("Turkey") &&
                            visitorLog.getCity().equals("Istanbul") &&
                            visitorLog.getRegion().equals("Marmara") &&
                            visitorLog.getLatitude().equals(41.0082) &&
                            visitorLog.getLongitude().equals(28.9784) &&
                            visitorLog.getTimezone().equals("Europe/Istanbul") &&
                            visitorLog.getTrafficSource() == TrafficSource.ORGANIC_SEARCH &&
                            visitorLog.getUtmSource().equals("google") &&
                            visitorLog.getUtmMedium().equals("cpc") &&
                            visitorLog.getUtmCampaign().equals("school-ads") &&
                            visitorLog.getSearchKeywords().equals("anaokulu istanbul") &&
                            visitorLog.getIsNewVisitor().equals(true) &&
                            visitorLog.getIsBounce().equals(false) &&
                            visitorLog.getPageDepth().equals(3) &&
                            visitorLog.getScrollDepthPercentage().equals(75) &&
                            visitorLog.getExitPage().equals(false) &&
                            visitorLog.getClickedPhone().equals(true) &&
                            visitorLog.getClickedEmail().equals(false) &&
                            visitorLog.getClickedDirections().equals(true) &&
                            visitorLog.getViewedGallery().equals(true) &&
                            visitorLog.getDownloadedBrochure().equals(false) &&
                            visitorLog.getRequestedAppointment().equals(true) &&
                            visitorLog.getSentMessage().equals(false) &&
                            visitorLog.getClickedSocialMedia().equals(true) &&
                            visitorLog.getPageLoadTimeMs().equals(2500) &&
                            visitorLog.getDomContentLoadedMs().equals(1800) &&
                            visitorLog.getFirstPaintMs().equals(1200) &&
                            visitorLog.getLargestContentfulPaintMs().equals(3000) &&
                            visitorLog.getIsBot().equals(false) &&
                            visitorLog.getBotName() == null &&
                            visitorLog.getConsentGiven().equals(true) &&
                            visitorLog.getAnonymized().equals(false)
            ));
            verify(converterService).mapToDto(savedVisitorLog);
        }

        @Test
        @DisplayName("Should handle minimal visitor data")
        void shouldHandleMinimalVisitorData() {
            // Given
            VisitorLogDto minimalDto = VisitorLogDto.builder()
                    .sessionId("session456")
                    .pageUrl("/")
                    .build();

            when(visitorLogRepository.save(any(VisitorLog.class))).thenReturn(savedVisitorLog);
            when(converterService.mapToDto(any(VisitorLog.class))).thenReturn(minimalDto);

            // When
            VisitorLogDto result = analyticsService.logVisitor(minimalDto);

            // Then
            assertNotNull(result);
            verify(visitorLogRepository).save(argThat(visitorLog ->
                    visitorLog.getSessionId().equals("session456") &&
                            visitorLog.getPageUrl().equals("/") &&
                            visitorLog.getVisitorId() == null &&
                            visitorLog.getDeviceType() == null &&
                            visitorLog.getIsBot() == null
            ));
        }

        @Test
        @DisplayName("Should set current time when visit time is null")
        void shouldSetCurrentTimeWhenVisitTimeIsNull() {
            // Given
            VisitorLogDto noTimeDto = VisitorLogDto.builder()
                    .sessionId("session789")
                    .pageUrl("/test")
                    .visitTime(null) // No visit time provided
                    .build();

            when(visitorLogRepository.save(any(VisitorLog.class))).thenReturn(savedVisitorLog);
            when(converterService.mapToDto(any(VisitorLog.class))).thenReturn(noTimeDto);

            // When
            analyticsService.logVisitor(noTimeDto);

            // Then
            verify(visitorLogRepository).save(argThat(visitorLog ->
                    visitorLog.getSessionId().equals("session789") &&
                            visitorLog.getVisitTime() != null &&
                            visitorLog.getVisitTime().isBefore(LocalDateTime.now().plusSeconds(1)) &&
                            visitorLog.getVisitTime().isAfter(LocalDateTime.now().minusSeconds(5))
            ));
        }

        @Test
        @DisplayName("Should handle bot detection correctly")
        void shouldHandleBotDetectionCorrectly() {
            // Given
            VisitorLogDto botDto = VisitorLogDto.builder()
                    .sessionId("bot_session")
                    .pageUrl("/robots.txt")
                    .userAgent("Googlebot/2.1")
                    .isBot(true)
                    .botName("Googlebot")
                    .build();

            when(visitorLogRepository.save(any(VisitorLog.class))).thenReturn(savedVisitorLog);
            when(converterService.mapToDto(any(VisitorLog.class))).thenReturn(botDto);

            // When
            analyticsService.logVisitor(botDto);

            // Then
            verify(visitorLogRepository).save(argThat(visitorLog ->
                    visitorLog.getIsBot().equals(true) &&
                            visitorLog.getBotName().equals("Googlebot") &&
                            visitorLog.getUserAgent().equals("Googlebot/2.1")
            ));
        }
    }

    @Nested
    @DisplayName("getAnalyticsSummary() Tests")
    class GetAnalyticsSummaryTests {

        @Test
        @DisplayName("Should get analytics summary successfully")
        void shouldGetAnalyticsSummarySuccessfully() {
            // Given
            LocalDate startDate = LocalDate.of(2024, 1, 1);
            LocalDate endDate = LocalDate.of(2024, 1, 31);
            Long schoolId = 1L, campusId = 1L, brandId = 1L;

            AnalyticsSummaryDto expectedSummary = AnalyticsSummaryDto.builder()
                    .date(endDate)
                    .pageViews(1500L)
                    .uniqueVisitors(1200L)
                    .appointmentRequests(75L)
                    .messageInquiries(45L)
                    .conversionRate(6.25)
                    .averageRating(4.3)
                    .institutionName("Test School")
                    .institutionType("PRIMARY_SCHOOL")
                    .build();

            when(analyticsRepository.getAnalyticsSummary(startDate, endDate, schoolId, campusId, brandId))
                    .thenReturn(expectedSummary);

            // When
            AnalyticsSummaryDto result = analyticsService.getAnalyticsSummary(startDate, endDate, schoolId, campusId, brandId);

            // Then
            assertNotNull(result);
            assertEquals(endDate, result.getDate());
            assertEquals(1500L, result.getPageViews());
            assertEquals(1200L, result.getUniqueVisitors());
            assertEquals(75L, result.getAppointmentRequests());
            assertEquals(45L, result.getMessageInquiries());
            assertEquals(6.25, result.getConversionRate());
            assertEquals(4.3, result.getAverageRating());
            assertEquals("Test School", result.getInstitutionName());
            assertEquals("PRIMARY_SCHOOL", result.getInstitutionType());

            verify(analyticsRepository).getAnalyticsSummary(startDate, endDate, schoolId, campusId, brandId);
        }

        @Test
        @DisplayName("Should handle null parameters correctly")
        void shouldHandleNullParametersCorrectly() {
            // Given
            LocalDate startDate = LocalDate.of(2024, 1, 1);
            LocalDate endDate = LocalDate.of(2024, 1, 31);
            Long schoolId = null, campusId = null, brandId = null;

            AnalyticsSummaryDto globalSummary = AnalyticsSummaryDto.builder()
                    .pageViews(5000L)
                    .uniqueVisitors(4000L)
                    .appointmentRequests(200L)
                    .messageInquiries(150L)
                    .build();

            when(analyticsRepository.getAnalyticsSummary(startDate, endDate, null, null, null))
                    .thenReturn(globalSummary);

            // When
            AnalyticsSummaryDto result = analyticsService.getAnalyticsSummary(startDate, endDate, schoolId, campusId, brandId);

            // Then
            assertNotNull(result);
            assertEquals(5000L, result.getPageViews());
            assertEquals(4000L, result.getUniqueVisitors());
            assertEquals(200L, result.getAppointmentRequests());
            assertEquals(150L, result.getMessageInquiries());

            verify(analyticsRepository).getAnalyticsSummary(startDate, endDate, null, null, null);
        }
    }

    // Helper methods
    private User createUser(Long id, RoleLevel roleLevel) {
        User user = new User();
        user.setId(id);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("test@example.com");

        MockUserRole mockRole = new MockUserRole();
        mockRole.setRoleLevel(roleLevel);
        user.setUserRoles(Set.of(createUserRole(roleLevel)));
        //user.setUserRoles(List.of(mockRole));
        user.setInstitutionAccess(Collections.emptySet());

        return user;
    }

    private UserRole createUserRole(RoleLevel roleLevel) {
        UserRole mockRole = new UserRole();
        mockRole.setRoleLevel(roleLevel);
        return mockRole;
    }




// ================================ BUSINESS LOGIC METHODS TESTS ================================

    @Nested
    @DisplayName("calculateGrowthRate() Tests")
    class CalculateGrowthRateTests {

        @Test
        @DisplayName("Should calculate positive growth rate correctly")
        void shouldCalculatePositiveGrowthRateCorrectly() {
            // Given - using reflection to access private method
            Long current = 150L;
            Long previous = 100L;

            // When
            Double result = invokeCalculateGrowthRate(current, previous);

            // Then
            assertEquals(50.0, result, 0.01); // (150-100)/100 * 100 = 50%
        }

        @Test
        @DisplayName("Should calculate negative growth rate correctly")
        void shouldCalculateNegativeGrowthRateCorrectly() {
            // Given
            Long current = 80L;
            Long previous = 100L;

            // When
            Double result = invokeCalculateGrowthRate(current, previous);

            // Then
            assertEquals(-20.0, result, 0.01); // (80-100)/100 * 100 = -20%
        }

        @Test
        @DisplayName("Should return 100% growth when previous is zero")
        void shouldReturn100PercentGrowthWhenPreviousIsZero() {
            // Given
            Long current = 50L;
            Long previous = 0L;

            // When
            Double result = invokeCalculateGrowthRate(current, previous);

            // Then
            assertEquals(100.0, result, 0.01);
        }

        @Test
        @DisplayName("Should return 0% when both current and previous are zero")
        void shouldReturn0PercentWhenBothCurrentAndPreviousAreZero() {
            // Given
            Long current = 0L;
            Long previous = 0L;

            // When
            Double result = invokeCalculateGrowthRate(current, previous);

            // Then
            assertEquals(0.0, result, 0.01);
        }

        @Test
        @DisplayName("Should return 100% when current has value and previous is null")
        void shouldReturn100PercentWhenCurrentHasValueAndPreviousIsNull() {
            // Given
            Long current = 75L;
            Long previous = null;

            // When
            Double result = invokeCalculateGrowthRate(current, previous);

            // Then
            assertEquals(100.0, result, 0.01);
        }

        @Test
        @DisplayName("Should return 0% when current is zero and previous has value")
        void shouldReturn0PercentWhenCurrentIsZeroAndPreviousHasValue() {
            // Given
            Long current = 0L;
            Long previous = 50L;

            // When
            Double result = invokeCalculateGrowthRate(current, previous);

            // Then
            assertEquals(-100.0, result, 0.01); // Complete decline
        }

        @Test
        @DisplayName("Should return -100% when current is null")
        void shouldReturnMinus100PercentWhenCurrentIsNull() {
            // Given
            Long current = null;
            Long previous = 50L;

            // When
            Double result = invokeCalculateGrowthRate(current, previous);

            // Then
            assertEquals(-100.0, result, 0.01);
        }

        @Test
        @DisplayName("Should handle large numbers correctly")
        void shouldHandleLargeNumbersCorrectly() {
            // Given
            Long current = 1000000L;
            Long previous = 800000L;

            // When
            Double result = invokeCalculateGrowthRate(current, previous);

            // Then
            assertEquals(25.0, result, 0.01); // (1000000-800000)/800000 * 100 = 25%
        }

        private Double invokeCalculateGrowthRate(Long current, Long previous) {
            try {
                java.lang.reflect.Method method = AnalyticsService.class.getDeclaredMethod("calculateGrowthRate", Long.class, Long.class);
                method.setAccessible(true);
                return (Double) method.invoke(analyticsService, current, previous);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Nested
    @DisplayName("classifySearchIntent() Tests")
    class ClassifySearchIntentTests {

        @Test
        @DisplayName("Should classify price-related queries as PRICE_COMPARISON")
        void shouldClassifyPriceRelatedQueriesAsPriceComparison() {
            // Given & When & Then
            assertEquals(SearchIntent.PRICE_COMPARISON, invokeClassifySearchIntent("okul fiyatı nedir"));
            assertEquals(SearchIntent.PRICE_COMPARISON, invokeClassifySearchIntent("anaokulu ücreti"));
            assertEquals(SearchIntent.PRICE_COMPARISON, invokeClassifySearchIntent("maliyet hesaplama"));
            assertEquals(SearchIntent.PRICE_COMPARISON, invokeClassifySearchIntent("FIYAT karşılaştırma")); // Case insensitive
        }

        @Test
        @DisplayName("Should classify appointment-related queries as TRANSACTIONAL")
        void shouldClassifyAppointmentRelatedQueriesAsTransactional() {
            // Given & When & Then
            assertEquals(SearchIntent.TRANSACTIONAL, invokeClassifySearchIntent("randevu almak istiyorum"));
            assertEquals(SearchIntent.TRANSACTIONAL, invokeClassifySearchIntent("okul görüşmesi"));
            assertEquals(SearchIntent.TRANSACTIONAL, invokeClassifySearchIntent("RANDEVU takvimi")); // Case insensitive
        }

        @Test
        @DisplayName("Should classify location-related queries as LOCAL")
        void shouldClassifyLocationRelatedQueriesAsLocal() {
            // Given & When & Then
            assertEquals(SearchIntent.LOCAL, invokeClassifySearchIntent("okul nerede"));
            assertEquals(SearchIntent.LOCAL, invokeClassifySearchIntent("adres bilgisi"));
            assertEquals(SearchIntent.LOCAL, invokeClassifySearchIntent("konum harita"));
            assertEquals(SearchIntent.LOCAL, invokeClassifySearchIntent("NEREDE bulunuyor")); // Case insensitive
        }

        @Test
        @DisplayName("Should classify informational queries as INFORMATIONAL")
        void shouldClassifyInformationalQueriesAsInformational() {
            // Given & When & Then
            assertEquals(SearchIntent.INFORMATIONAL, invokeClassifySearchIntent("okul nasıl"));
            assertEquals(SearchIntent.INFORMATIONAL, invokeClassifySearchIntent("eğitim sistemi ne"));
            assertEquals(SearchIntent.INFORMATIONAL, invokeClassifySearchIntent("anaokulu nedir"));
            assertEquals(SearchIntent.INFORMATIONAL, invokeClassifySearchIntent("müfredat NASIL")); // Case insensitive
        }

        @Test
        @DisplayName("Should classify review-related queries as REVIEW_SEEKING")
        void shouldClassifyReviewRelatedQueriesAsReviewSeeking() {
            // Given & When & Then
            assertEquals(SearchIntent.REVIEW_SEEKING, invokeClassifySearchIntent("okul yorumları"));
            assertEquals(SearchIntent.REVIEW_SEEKING, invokeClassifySearchIntent("veli değerlendirmeleri"));
            assertEquals(SearchIntent.REVIEW_SEEKING, invokeClassifySearchIntent("YORUM görüş")); // Case insensitive
        }

        @Test
        @DisplayName("Should classify general queries as INFORMATIONAL by default")
        void shouldClassifyGeneralQueriesAsInformationalByDefault() {
            // Given & When & Then
            assertEquals(SearchIntent.INFORMATIONAL, invokeClassifySearchIntent("anaokulu seçimi"));
            assertEquals(SearchIntent.INFORMATIONAL, invokeClassifySearchIntent("eğitim kalitesi"));
            assertEquals(SearchIntent.INFORMATIONAL, invokeClassifySearchIntent("öğretmen kadrosu"));
        }

        @Test
        @DisplayName("Should return UNCLEAR for null or empty queries")
        void shouldReturnUnclearForNullOrEmptyQueries() {
            // Given & When & Then
            assertEquals(SearchIntent.UNCLEAR, invokeClassifySearchIntent(null));
            assertEquals(SearchIntent.UNCLEAR, invokeClassifySearchIntent(""));
            assertEquals(SearchIntent.UNCLEAR, invokeClassifySearchIntent("   ")); // Whitespace only
        }

        @Test
        @DisplayName("Should handle complex queries with multiple keywords")
        void shouldHandleComplexQueriesWithMultipleKeywords() {
            // Given & When & Then - Should prioritize first matching keyword
            assertEquals(SearchIntent.PRICE_COMPARISON, invokeClassifySearchIntent("okul fiyatı ve randevu")); // Price comes first
            assertEquals(SearchIntent.TRANSACTIONAL, invokeClassifySearchIntent("randevu al fiyat nedir")); // Appointment comes first
            assertEquals(SearchIntent.LOCAL, invokeClassifySearchIntent("nerede okul fiyat randevu")); // Location comes first
        }

        private SearchIntent invokeClassifySearchIntent(String query) {
            try {
                java.lang.reflect.Method method = AnalyticsService.class.getDeclaredMethod("classifySearchIntent", String.class);
                method.setAccessible(true);
                return (SearchIntent) method.invoke(analyticsService, query);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Nested
    @DisplayName("cleanSearchQuery() Tests")
    class CleanSearchQueryTests {

        @Test
        @DisplayName("Should clean query by removing special characters")
        void shouldCleanQueryByRemovingSpecialCharacters() {
            // Given & When & Then
            assertEquals("anaokulu istanbul", invokeCleanSearchQuery("anaokulu İstanbul!@#$%"));
            assertEquals("okul secimi", invokeCleanSearchQuery("okul seçimi??***"));
            assertEquals("egitim kalite", invokeCleanSearchQuery("eğitim kalite&&&+++"));
        }

        @Test
        @DisplayName("Should convert to lowercase")
        void shouldConvertToLowercase() {
            // Given & When & Then
            assertEquals("anaokulu istanbul", invokeCleanSearchQuery("ANAOKULU İSTANBUL"));
            assertEquals("test okulu", invokeCleanSearchQuery("Test OKULU"));
            assertEquals("egitim sistemi", invokeCleanSearchQuery("EĞİTİM SİSTEMİ"));
        }

        @Test
        @DisplayName("Should preserve Turkish characters")
        void shouldPreserveTurkishCharacters() {
            // Given & When & Then
            assertEquals("anaokulu için öğrenci", invokeCleanSearchQuery("anaokulu için öğrenci"));
            assertEquals("güzel çocuk şarkısı", invokeCleanSearchQuery("güzel çocuk şarkısı"));
            assertEquals("üniversite ücreti", invokeCleanSearchQuery("üniversite ücreti"));
        }

        @Test
        @DisplayName("Should normalize multiple spaces to single space")
        void shouldNormalizeMultipleSpacesToSingleSpace() {
            // Given & When & Then
            assertEquals("anaokulu istanbul kadikoy", invokeCleanSearchQuery("anaokulu    istanbul     kadikoy"));
            assertEquals("okul secimi rehberi", invokeCleanSearchQuery("okul  secimi   rehberi"));
        }

        @Test
        @DisplayName("Should trim leading and trailing spaces")
        void shouldTrimLeadingAndTrailingSpaces() {
            // Given & When & Then
            assertEquals("anaokulu istanbul", invokeCleanSearchQuery("   anaokulu istanbul   "));
            assertEquals("test query", invokeCleanSearchQuery("\t test query \n"));
        }

        @Test
        @DisplayName("Should return null for null input")
        void shouldReturnNullForNullInput() {
            // Given & When & Then
            assertNull(invokeCleanSearchQuery(null));
        }

        @Test
        @DisplayName("Should handle empty strings")
        void shouldHandleEmptyStrings() {
            // Given & When & Then
            assertEquals("", invokeCleanSearchQuery(""));
            assertEquals("", invokeCleanSearchQuery("   "));
            assertEquals("", invokeCleanSearchQuery("!@#$%^&*()"));
        }

        @Test
        @DisplayName("Should preserve alphanumeric characters")
        void shouldPreserveAlphanumericCharacters() {
            // Given & When & Then
            assertEquals("okul123 test abc", invokeCleanSearchQuery("okul123 test ABC!@#"));
            assertEquals("2024 anaokulu kayit", invokeCleanSearchQuery("2024 anaokulu kayıt***"));
        }

        private String invokeCleanSearchQuery(String query) {
            try {
                java.lang.reflect.Method method = AnalyticsService.class.getDeclaredMethod("cleanSearchQuery", String.class);
                method.setAccessible(true);
                return (String) method.invoke(analyticsService, query);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Nested
    @DisplayName("validateUserAnalyticsAccess() Tests")
    class ValidateUserAnalyticsAccessTests {

        @Test
        @DisplayName("Should allow access for system user")
        void shouldAllowAccessForSystemUser() {
            // Given & When & Then - Should not throw exception
            assertDoesNotThrow(() ->
                    invokeValidateUserAnalyticsAccess(systemUser, 1L, 1L, 1L)
            );
        }

        @Test
        @DisplayName("Should allow access for user with brand access")
        void shouldAllowAccessForUserWithBrandAccess() {
            // Given & When & Then - Should not throw exception
            assertDoesNotThrow(() ->
                    invokeValidateUserAnalyticsAccess(brandUser, null, null, 1L)
            );
        }

        @Test
        @DisplayName("Should throw BusinessException for user without access")
        void shouldThrowBusinessExceptionForUserWithoutAccess() {
            // Given & When & Then
            BusinessException exception = assertThrows(BusinessException.class, () ->
                    invokeValidateUserAnalyticsAccess(regularUser, 1L, 1L, 1L)
            );

            assertEquals("User does not have access to analytics data", exception.getMessage());
        }

        @Test
        @DisplayName("Should allow access when user has school-level access")
        void shouldAllowAccessWhenUserHasSchoolLevelAccess() {
            // Given
            MockInstitutionAccess schoolAccess = new MockInstitutionAccess();
            schoolAccess.setAccessType(AccessType.SCHOOL);
            schoolAccess.setEntityId(5L);
            schoolAccess.setExpiresAt(null);

            User schoolUser = createUser(4L, RoleLevel.SCHOOL);

            UserInstitutionAccess userInstitutionAccess = new UserInstitutionAccess();
            userInstitutionAccess.setAccessType(AccessType.SCHOOL);
            userInstitutionAccess.setEntityId(5L);
            userInstitutionAccess.setExpiresAt(null);

            schoolUser.setInstitutionAccess(Set.of(userInstitutionAccess));

            // When & Then - Should not throw exception
            assertDoesNotThrow(() ->
                    invokeValidateUserAnalyticsAccess(schoolUser, 5L, null, null)
            );
        }

        @Test
        @DisplayName("Should allow access when user has campus-level access")
        void shouldAllowAccessWhenUserHasCampusLevelAccess() {
            // Given
            MockInstitutionAccess campusAccess = new MockInstitutionAccess();
            campusAccess.setAccessType(AccessType.CAMPUS);
            campusAccess.setEntityId(3L);
            campusAccess.setExpiresAt(null);

            UserInstitutionAccess userInstitutionAccess = new UserInstitutionAccess();
            userInstitutionAccess.setAccessType(AccessType.CAMPUS);
            userInstitutionAccess.setEntityId(3L);
            userInstitutionAccess.setExpiresAt(null);

            User campusUser = createUser(5L, RoleLevel.CAMPUS);
            campusUser.setInstitutionAccess(Set.of(userInstitutionAccess));

            // When & Then - Should not throw exception
            assertDoesNotThrow(() ->
                    invokeValidateUserAnalyticsAccess(campusUser, null, 3L, null)
            );
        }

        @Test
        @DisplayName("Should deny access when access has expired")
        void shouldDenyAccessWhenAccessHasExpired() {
            // Given
            MockInstitutionAccess expiredAccess = new MockInstitutionAccess();
            expiredAccess.setAccessType(AccessType.BRAND);
            expiredAccess.setEntityId(1L);
            expiredAccess.setExpiresAt(LocalDateTime.now().minusDays(1)); // Expired yesterday


            UserInstitutionAccess userInstitutionAccess = new UserInstitutionAccess();
            userInstitutionAccess.setAccessType(AccessType.BRAND);
            userInstitutionAccess.setEntityId(1L);
            userInstitutionAccess.setExpiresAt(LocalDateTime.now().minusDays(1));


            User expiredUser = createUser(6L, RoleLevel.BRAND);
            expiredUser.setInstitutionAccess(Set.of(userInstitutionAccess));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class, () ->
                    invokeValidateUserAnalyticsAccess(expiredUser, null, null, 1L)
            );

            assertEquals("User does not have access to analytics data", exception.getMessage());
        }

        @Test
        @DisplayName("Should allow access when entity IDs are null (global analytics)")
        void shouldAllowAccessWhenEntityIdsAreNull() {
            // Given - Brand user should have access to global analytics when all IDs are null

            // When & Then - Should not throw exception
            assertDoesNotThrow(() ->
                    invokeValidateUserAnalyticsAccess(brandUser, null, null, null)
            );
        }

        private void invokeValidateUserAnalyticsAccess(User user, Long schoolId, Long campusId, Long brandId) {
            try {
                java.lang.reflect.Method method = AnalyticsService.class.getDeclaredMethod(
                        "validateUserAnalyticsAccess", User.class, Long.class, Long.class, Long.class);
                method.setAccessible(true);
                method.invoke(analyticsService, user, schoolId, campusId, brandId);
            } catch (java.lang.reflect.InvocationTargetException e) {
                if (e.getCause() instanceof BusinessException) {
                    throw (BusinessException) e.getCause();
                }
                throw new RuntimeException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }



    // Mock inner classes
    @Setter
    @Getter
    private static class MockUserRole {
        private RoleLevel roleLevel;

    }

    @Setter
    @Getter
    private static class MockInstitutionAccess {
        private AccessType accessType;
        private Long entityId;
        private LocalDateTime expiresAt;

    }


}
