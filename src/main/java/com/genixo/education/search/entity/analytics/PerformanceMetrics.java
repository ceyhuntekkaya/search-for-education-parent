package com.genixo.education.search.entity.analytics;

import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.enumaration.PerformanceMetricCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "performance_metrics")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceMetrics extends BaseEntity {

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    @Column(name = "metric_category", nullable = false)
    private PerformanceMetricCategory metricCategory;

    @Column(name = "endpoint_url")
    private String endpointUrl;

    @Column(name = "http_method")
    private String httpMethod;

    @Column(name = "response_time_ms", nullable = false)
    private Integer responseTimeMs;

    @Column(name = "http_status_code")
    private Integer httpStatusCode;

    @Column(name = "success")
    private Boolean success = true;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "error_stack_trace", columnDefinition = "TEXT")
    private String errorStackTrace;

    // Database performance
    @Column(name = "db_query_count")
    private Integer dbQueryCount;

    @Column(name = "db_query_time_ms")
    private Integer dbQueryTimeMs;

    @Column(name = "db_connection_time_ms")
    private Integer dbConnectionTimeMs;

    // Memory usage
    @Column(name = "memory_used_mb")
    private Double memoryUsedMb;

    @Column(name = "memory_total_mb")
    private Double memoryTotalMb;

    @Column(name = "memory_usage_percentage")
    private Double memoryUsagePercentage;

    // CPU usage
    @Column(name = "cpu_usage_percentage")
    private Double cpuUsagePercentage;

    @Column(name = "cpu_time_ms")
    private Integer cpuTimeMs;

    // Cache performance
    @Column(name = "cache_hit")
    private Boolean cacheHit = false;

    @Column(name = "cache_key")
    private String cacheKey;

    @Column(name = "cache_ttl_seconds")
    private Integer cacheTtlSeconds;

    // File I/O
    @Column(name = "file_read_count")
    private Integer fileReadCount = 0;

    @Column(name = "file_write_count")
    private Integer fileWriteCount = 0;

    @Column(name = "file_io_time_ms")
    private Integer fileIoTimeMs = 0;

    // Network metrics
    @Column(name = "bytes_sent")
    private Long bytesSent = 0L;

    @Column(name = "bytes_received")
    private Long bytesReceived = 0L;

    @Column(name = "network_latency_ms")
    private Integer networkLatencyMs;

    // Third-party API calls
    @Column(name = "external_api_calls")
    private Integer externalApiCalls = 0;

    @Column(name = "external_api_time_ms")
    private Integer externalApiTimeMs = 0;

    @Column(name = "external_api_errors")
    private Integer externalApiErrors = 0;

    // User context
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    // Server context
    @Column(name = "server_name")
    private String serverName;

    @Column(name = "server_instance")
    private String serverInstance;

    @Column(name = "application_version")
    private String applicationVersion;

    @Column(name = "jvm_version")
    private String jvmVersion;

    // Request details
    @Column(name = "request_size_bytes")
    private Long requestSizeBytes;

    @Column(name = "response_size_bytes")
    private Long responseSizeBytes;

    @Column(name = "gzip_enabled")
    private Boolean gzipEnabled = false;

    @Column(name = "keep_alive")
    private Boolean keepAlive = true;

    // Business metrics
    @Column(name = "feature_name")
    private String featureName;

    @Column(name = "business_operation")
    private String businessOperation;

    @Column(name = "conversion_event")
    private Boolean conversionEvent = false;

    // Alert thresholds
    @Column(name = "threshold_exceeded")
    private Boolean thresholdExceeded = false;

    @Column(name = "threshold_type")
    private String thresholdType;

    @Column(name = "threshold_value")
    private Double thresholdValue;

    // Additional context (JSON for flexibility)
    @Column(name = "additional_metrics")
    private String additionalMetrics;
}
