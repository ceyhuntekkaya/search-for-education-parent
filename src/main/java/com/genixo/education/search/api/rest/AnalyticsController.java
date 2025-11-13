package com.genixo.education.search.api.rest;

import com.genixo.education.search.dto.analytics.*;
import com.genixo.education.search.enumaration.PerformanceMetricCategory;
import com.genixo.education.search.service.AnalyticsService;
import com.genixo.education.search.service.auth.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Analytics Management", description = "APIs for analytics data, performance monitoring and reporting")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final JwtService jwtService;

    // ================================ DASHBOARD ANALYTICS ================================

    @GetMapping("/dashboard")
    @Operation(summary = "Get analytics dashboard", description = "Get comprehensive analytics dashboard with key metrics")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Dashboard data retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid date range")
    })
    public ResponseEntity<ApiResponse<AnalyticsDashboardDto>> getDashboard(
            @Parameter(description = "Start date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "School ID filter") @RequestParam(required = false) Long schoolId,
            @Parameter(description = "Campus ID filter") @RequestParam(required = false) Long campusId,
            @Parameter(description = "Brand ID filter") @RequestParam(required = false) Long brandId,
            HttpServletRequest request) {

        AnalyticsDashboardDto dashboard = analyticsService.getDashboard(startDate, endDate, schoolId, campusId, brandId, request);
        ApiResponse<AnalyticsDashboardDto> response = ApiResponse.success(dashboard, "Dashboard data retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(java.time.LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/summary")
    @Operation(summary = "Get analytics summary", description = "Get analytics summary for a specific period")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Summary retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<AnalyticsSummaryDto>> getAnalyticsSummary(
            @Parameter(description = "Start date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "School ID filter") @RequestParam(required = false) Long schoolId,
            @Parameter(description = "Campus ID filter") @RequestParam(required = false) Long campusId,
            @Parameter(description = "Brand ID filter") @RequestParam(required = false) Long brandId,
            HttpServletRequest request) {

        log.debug("Get analytics summary request for period: {} to {}", startDate, endDate);

        AnalyticsSummaryDto summary = analyticsService.getAnalyticsSummary(startDate, endDate, schoolId, campusId, brandId);

        ApiResponse<AnalyticsSummaryDto> response = ApiResponse.success(summary, "Summary retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(java.time.LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/data")
    @Operation(summary = "Get analytics data", description = "Get detailed analytics data with filters and pagination")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Analytics data retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<Page<AnalyticsDto>>> getAnalyticsData(
            @Valid @RequestBody AnalyticsFilterDto filter,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        log.debug("Get analytics data request with filter");

        Page<AnalyticsDto> analyticsPage = analyticsService.getAnalyticsData(filter, page, size, request);

        ApiResponse<Page<AnalyticsDto>> response = ApiResponse.success(analyticsPage, "Analytics data retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(java.time.LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/compare")
    @Operation(summary = "Compare analytics", description = "Compare analytics data between two time periods")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Comparison completed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid date ranges")
    })
    public ResponseEntity<ApiResponse<AnalyticsComparisonDto>> compareAnalytics(
            @Valid @RequestBody AnalyticsComparisonRequestDto comparisonRequest,
            HttpServletRequest request) {

        AnalyticsComparisonDto comparison = analyticsService.compareAnalytics(
                comparisonRequest.getStartDate(),
                comparisonRequest.getEndDate(),
                comparisonRequest.getCompareStartDate(),
                comparisonRequest.getCompareEndDate(),
                comparisonRequest.getSchoolId(),
                comparisonRequest.getCampusId(),
                comparisonRequest.getBrandId(),
                request
        );

        ApiResponse<AnalyticsComparisonDto> response = ApiResponse.success(comparison, "Comparison completed successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(java.time.LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ VISITOR ANALYTICS ================================

    @PostMapping("/visitors/log")
    @Operation(summary = "Log visitor activity", description = "Log visitor page visit and interactions")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Visitor log created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid visitor data")
    })
    public ResponseEntity<ApiResponse<VisitorLogDto>> logVisitor(
            @Valid @RequestBody VisitorLogDto visitorLogDto,
            HttpServletRequest request) {

        log.debug("Log visitor request for page: {}", visitorLogDto.getPageUrl());

        VisitorLogDto loggedVisitor = analyticsService.logVisitor(visitorLogDto);

        ApiResponse<VisitorLogDto> response = ApiResponse.success(loggedVisitor, "Visitor log created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(java.time.LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/visitors")
    @Operation(summary = "Get visitor logs", description = "Get visitor logs with filters and pagination")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Visitor logs retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<Page<VisitorLogDto>>> getVisitorLogs(
            @Valid @RequestBody AnalyticsFilterDto filter,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        log.debug("Get visitor logs request");

        Page<VisitorLogDto> visitorLogs = analyticsService.getVisitorLogs(filter, page, size, request);

        ApiResponse<Page<VisitorLogDto>> response = ApiResponse.success(visitorLogs, "Visitor logs retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(java.time.LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/visitors/summary")
    @Operation(summary = "Get visitor summary", description = "Get aggregated visitor statistics")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Visitor summary retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<List<VisitorLogSummaryDto>>> getVisitorSummary(
            @Parameter(description = "Start date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "School ID filter") @RequestParam(required = false) Long schoolId,
            @Parameter(description = "Campus ID filter") @RequestParam(required = false) Long campusId,
            @Parameter(description = "Brand ID filter") @RequestParam(required = false) Long brandId,
            HttpServletRequest request) {

        log.debug("Get visitor summary request for period: {} to {}", startDate, endDate);

        List<VisitorLogSummaryDto> summary = analyticsService.getVisitorSummary(startDate, endDate, schoolId, campusId, brandId, request);

        ApiResponse<List<VisitorLogSummaryDto>> response = ApiResponse.success(summary, "Visitor summary retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(java.time.LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ SEARCH ANALYTICS ================================

    @PostMapping("/searches/log")
    @Operation(summary = "Log search activity", description = "Log user search queries and results")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Search log created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid search data")
    })
    public ResponseEntity<ApiResponse<SearchLogDto>> logSearch(
            @Valid @RequestBody SearchLogDto searchLogDto,
            HttpServletRequest request) {

        log.debug("Log search request: {}", searchLogDto.getSearchQuery());

        SearchLogDto loggedSearch = analyticsService.logSearch(searchLogDto);

        ApiResponse<SearchLogDto> response = ApiResponse.success(loggedSearch, "Search log created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(java.time.LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/searches")
    @Operation(summary = "Get search logs", description = "Get search logs with filters and pagination")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Search logs retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<Page<SearchLogDto>>> getSearchLogs(
            @Valid @RequestBody AnalyticsFilterDto filter,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        log.debug("Get search logs request");

        Page<SearchLogDto> searchLogs = analyticsService.getSearchLogs(filter, page, size, request);

        ApiResponse<Page<SearchLogDto>> response = ApiResponse.success(searchLogs, "Search logs retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(java.time.LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/searches/summary")
    @Operation(summary = "Get search summary", description = "Get aggregated search statistics")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Search summary retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<List<SearchLogSummaryDto>>> getSearchSummary(
            @Parameter(description = "Start date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "School ID filter") @RequestParam(required = false) Long schoolId,
            @Parameter(description = "Campus ID filter") @RequestParam(required = false) Long campusId,
            @Parameter(description = "Brand ID filter") @RequestParam(required = false) Long brandId,
            HttpServletRequest request) {

        log.debug("Get search summary request for period: {} to {}", startDate, endDate);

        List<SearchLogSummaryDto> summary = analyticsService.getSearchSummary(startDate, endDate, schoolId, campusId, brandId, request);

        ApiResponse<List<SearchLogSummaryDto>> response = ApiResponse.success(summary, "Search summary retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(java.time.LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ PERFORMANCE METRICS ================================

    @PostMapping("/performance/log")
    @Operation(summary = "Log performance metric", description = "Log system performance metrics")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Performance metric logged successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid performance data")
    })
    public ResponseEntity<ApiResponse<PerformanceMetricsDto>> logPerformanceMetric(
            @Valid @RequestBody PerformanceMetricsDto metricsDto,
            HttpServletRequest request) {

        log.debug("Log performance metric request: {}", metricsDto.getEndpointUrl());

        PerformanceMetricsDto loggedMetrics = analyticsService.logPerformanceMetric(metricsDto);

        ApiResponse<PerformanceMetricsDto> response = ApiResponse.success(loggedMetrics, "Performance metric logged successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(java.time.LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/performance/summary")
    @Operation(summary = "Get performance summary", description = "Get aggregated performance metrics")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Performance summary retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "System access required")
    })
    public ResponseEntity<ApiResponse<List<PerformanceSummaryDto>>> getPerformanceSummary(
            @Parameter(description = "Start date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Performance metric category") @RequestParam(required = false) PerformanceMetricCategory category,
            HttpServletRequest request) {

        log.debug("Get performance summary request for period: {} to {}", startDate, endDate);

        List<PerformanceSummaryDto> summary = analyticsService.getPerformanceSummary(startDate, endDate, category, request);

        ApiResponse<List<PerformanceSummaryDto>> response = ApiResponse.success(summary, "Performance summary retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(java.time.LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ REAL-TIME ANALYTICS ================================

    @GetMapping("/realtime")
    @Operation(summary = "Get real-time analytics", description = "Get current real-time analytics data")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Real-time analytics retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "System access required")
    })
    public ResponseEntity<ApiResponse<RealTimeAnalyticsDto>> getRealTimeAnalytics(
            HttpServletRequest request) {

        log.debug("Get real-time analytics request");

        RealTimeAnalyticsDto realTimeData = analyticsService.getRealTimeAnalytics(request);

        ApiResponse<RealTimeAnalyticsDto> response = ApiResponse.success(realTimeData, "Real-time analytics retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(java.time.LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ EXPORT FUNCTIONALITY ================================

    @PostMapping("/export")
    @Operation(summary = "Request analytics export", description = "Request export of analytics data in various formats")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "202", description = "Export request accepted"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid export request"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<AnalyticsExportDto>> requestExport(
            @Valid @RequestBody AnalyticsExportDto exportRequest,
            HttpServletRequest request) {

        AnalyticsExportDto export = analyticsService.requestExport(exportRequest, request);

        ApiResponse<AnalyticsExportDto> response = ApiResponse.success(export, "Export request accepted");
        response.setPath(request.getRequestURI());
        response.setTimestamp(java.time.LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/export/{exportId}/status")
    @Operation(summary = "Get export status", description = "Get status of analytics export request")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Export status retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Export not found")
    })
    public ResponseEntity<ApiResponse<ExportStatusDto>> getExportStatus(
            @Parameter(description = "Export ID") @PathVariable String exportId,
            HttpServletRequest request) {

        log.debug("Get export status request: {}", exportId);

        // This would be implemented in the service
        ExportStatusDto status = ExportStatusDto.builder()
                .exportId(exportId)
                .status("PROCESSING")
                .progress(75)
                .message("Processing analytics data...")
                .estimatedCompletionTime(java.time.LocalDateTime.now().plusMinutes(5))
                .build();

        ApiResponse<ExportStatusDto> response = ApiResponse.success(status, "Export status retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(java.time.LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ ANALYTICS ALERTS ================================

    @PostMapping("/alerts")
    @Operation(summary = "Create analytics alert", description = "Create alert for analytics thresholds")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Alert created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid alert configuration"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<AnalyticsAlertDto>> createAlert(
            @Valid @RequestBody AnalyticsAlertDto alertDto,
            HttpServletRequest request) {

        AnalyticsAlertDto createdAlert = analyticsService.createAlert(alertDto, request);

        ApiResponse<AnalyticsAlertDto> response = ApiResponse.success(createdAlert, "Alert created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(java.time.LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/alerts")
    @Operation(summary = "Get active alerts", description = "Get all active analytics alerts")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Active alerts retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "System access required")
    })
    public ResponseEntity<ApiResponse<List<AnalyticsAlertDto>>> getActiveAlerts(
            HttpServletRequest request) {

        log.debug("Get active alerts request");

        List<AnalyticsAlertDto> alerts = analyticsService.getActiveAlerts(request);

        ApiResponse<List<AnalyticsAlertDto>> response = ApiResponse.success(alerts, "Active alerts retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(java.time.LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ CUSTOM QUERIES ================================

    @PostMapping("/query")
    @Operation(summary = "Execute custom analytics query", description = "Execute custom analytics query with specific metrics and filters")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Query executed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid query parameters"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<AnalyticsResultDto>> executeCustomQuery(
            @Valid @RequestBody AnalyticsQueryDto queryDto,
            HttpServletRequest request) {


        AnalyticsResultDto result = analyticsService.executeCustomQuery(queryDto, request);

        ApiResponse<AnalyticsResultDto> response = ApiResponse.success(result, "Query executed successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(java.time.LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ ANALYTICS REPORTS ================================

    @PostMapping("/reports/generate")
    @Operation(summary = "Generate analytics report", description = "Generate comprehensive analytics report")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Report generated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid report parameters"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<AnalyticsReportDto>> generateReport(
            @Valid @RequestBody ReportGenerationRequestDto reportRequest,
            HttpServletRequest request) {


        AnalyticsReportDto report = analyticsService.generateReport(
                reportRequest.getReportType(),
                reportRequest.getStartDate(),
                reportRequest.getEndDate(),
                reportRequest.getSchoolId(),
                reportRequest.getCampusId(),
                reportRequest.getBrandId(),
                request
        );

        ApiResponse<AnalyticsReportDto> response = ApiResponse.success(report, "Report generated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(java.time.LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ CACHE MANAGEMENT ================================

    @PostMapping("/cache/clear")
    @Operation(summary = "Clear analytics cache", description = "Clear all analytics-related cache (admin only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cache cleared successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Admin access required")
    })
    public ResponseEntity<ApiResponse<Void>> clearAnalyticsCache(
            HttpServletRequest request) {


        analyticsService.clearAnalyticsCache();

        ApiResponse<Void> response = ApiResponse.success(null, "Analytics cache cleared successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(java.time.LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/cache/refresh-realtime")
    @Operation(summary = "Refresh real-time cache", description = "Refresh real-time analytics cache")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Real-time cache refreshed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "System access required")
    })
    public ResponseEntity<ApiResponse<Void>> refreshRealTimeCache(
            HttpServletRequest request) {

        log.debug("Refresh real-time cache request");

        analyticsService.refreshRealTimeCache();

        ApiResponse<Void> response = ApiResponse.success(null, "Real-time cache refreshed successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(java.time.LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ HELPER REQUEST DTOs ================================

    @Setter
    @Getter
    public static class AnalyticsComparisonRequestDto {
        private LocalDate startDate;
        private LocalDate endDate;
        private LocalDate compareStartDate;
        private LocalDate compareEndDate;
        private Long schoolId;
        private Long campusId;
        private Long brandId;

    }

    @Setter
    @Getter
    public static class ReportGenerationRequestDto {
        private String reportType;
        private LocalDate startDate;
        private LocalDate endDate;
        private Long schoolId;
        private Long campusId;
        private Long brandId;

    }

    @Setter
    @Getter
    public static class ExportStatusDto {
        private String exportId;
        private String status;
        private Integer progress;
        private String message;
        private java.time.LocalDateTime estimatedCompletionTime;
        private String downloadUrl;
        private String errorMessage;

        public static ExportStatusDtoBuilder builder() {
            return new ExportStatusDtoBuilder();
        }

        public static class ExportStatusDtoBuilder {
            private String exportId;
            private String status;
            private Integer progress;
            private String message;
            private java.time.LocalDateTime estimatedCompletionTime;
            private String downloadUrl;
            private String errorMessage;

            public ExportStatusDtoBuilder exportId(String exportId) {
                this.exportId = exportId;
                return this;
            }

            public ExportStatusDtoBuilder status(String status) {
                this.status = status;
                return this;
            }

            public ExportStatusDtoBuilder progress(Integer progress) {
                this.progress = progress;
                return this;
            }

            public ExportStatusDtoBuilder message(String message) {
                this.message = message;
                return this;
            }

            public ExportStatusDtoBuilder estimatedCompletionTime(java.time.LocalDateTime estimatedCompletionTime) {
                this.estimatedCompletionTime = estimatedCompletionTime;
                return this;
            }

            public ExportStatusDtoBuilder downloadUrl(String downloadUrl) {
                this.downloadUrl = downloadUrl;
                return this;
            }

            public ExportStatusDtoBuilder errorMessage(String errorMessage) {
                this.errorMessage = errorMessage;
                return this;
            }

            public ExportStatusDto build() {
                ExportStatusDto dto = new ExportStatusDto();
                dto.setExportId(this.exportId);
                dto.setStatus(this.status);
                dto.setProgress(this.progress);
                dto.setMessage(this.message);
                dto.setEstimatedCompletionTime(this.estimatedCompletionTime);
                dto.setDownloadUrl(this.downloadUrl);
                dto.setErrorMessage(this.errorMessage);
                return dto;
            }
        }
    }
}