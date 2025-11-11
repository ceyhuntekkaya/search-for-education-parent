package com.genixo.education.search.dto.analytics;

import com.genixo.education.search.enumaration.PerformanceMetricCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceMetricsDto {
    private Long id;
    private LocalDateTime timestamp;
    private PerformanceMetricCategory metricCategory;
    private String endpointUrl;
    private String httpMethod;
    private Integer responseTimeMs;
    private Integer httpStatusCode;
    private Boolean success;
    private String errorMessage;
    private String errorStackTrace;

    // Database performance
    private Integer dbQueryCount;
    private Integer dbQueryTimeMs;
    private Integer dbConnectionTimeMs;

    // Memory usage
    private Double memoryUsedMb;
    private Double memoryTotalMb;
    private Double memoryUsagePercentage;

    // CPU usage
    private Double cpuUsagePercentage;
    private Integer cpuTimeMs;

    // Cache performance
    private Boolean cacheHit;
    private String cacheKey;
    private Integer cacheTtlSeconds;

    // File I/O
    private Integer fileReadCount;
    private Integer fileWriteCount;
    private Integer fileIoTimeMs;

    // Network metrics
    private Long bytesSent;
    private Long bytesReceived;
    private Integer networkLatencyMs;

    // Third-party API calls
    private Integer externalApiCalls;
    private Integer externalApiTimeMs;
    private Integer externalApiErrors;

    // User context
    private Long userId;
    private String sessionId;
    private String ipAddress;
    private String userAgent;

    // Server context
    private String serverName;
    private String serverInstance;
    private String applicationVersion;
    private String jvmVersion;

    // Request details
    private Long requestSizeBytes;
    private Long responseSizeBytes;
    private Boolean gzipEnabled;
    private Boolean keepAlive;

    // Business metrics
    private String featureName;
    private String businessOperation;
    private Boolean conversionEvent;

    // Alert thresholds
    private Boolean thresholdExceeded;
    private String thresholdType;
    private Double thresholdValue;

    // Additional metrics
    private String additionalMetrics; // JSON

    private LocalDateTime createdAt;
}