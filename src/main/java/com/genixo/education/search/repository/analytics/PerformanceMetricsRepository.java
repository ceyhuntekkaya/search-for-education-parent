package com.genixo.education.search.repository.analytics;

import com.genixo.education.search.dto.analytics.PerformanceSummaryDto;
import com.genixo.education.search.entity.analytics.PerformanceMetrics;
import com.genixo.education.search.enumaration.PerformanceMetricCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PerformanceMetricsRepository extends JpaRepository<PerformanceMetrics, Long> {

    @Query("SELECT new com.genixo.education.search.dto.analytics.PerformanceSummaryDto(" +
            "pm.timestamp, pm.metricCategory, pm.endpointUrl, pm.responseTimeMs, pm.success, " +
            "pm.memoryUsagePercentage, pm.cpuUsagePercentage, pm.cacheHit, pm.thresholdExceeded) " +
            "FROM PerformanceMetrics pm " +
            "WHERE DATE(pm.timestamp) BETWEEN :startDate AND :endDate " +
            "AND (:category IS NULL OR pm.metricCategory = :category) " +
            "ORDER BY pm.timestamp DESC")
    List<PerformanceSummaryDto> getPerformanceSummary(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("category") PerformanceMetricCategory category);

    @Query("SELECT COALESCE(AVG(pm.responseTimeMs), 0.0) FROM PerformanceMetrics pm " +
            "WHERE DATE(pm.timestamp) BETWEEN :startDate AND :endDate " +
            "AND pm.metricCategory = :category " +
            "AND pm.responseTimeMs IS NOT NULL")
    Double getAverageResponseTime(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("category") PerformanceMetricCategory category);

    @Query("SELECT CASE WHEN COUNT(pm) > 0 THEN " +
            "CAST(COUNT(CASE WHEN pm.success = true THEN 1 END) AS DOUBLE) / COUNT(pm) * 100 " +
            "ELSE 100.0 END " +
            "FROM PerformanceMetrics pm " +
            "WHERE DATE(pm.timestamp) BETWEEN :startDate AND :endDate")
    Double getSystemUptime(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT CASE WHEN COUNT(pm) > 0 THEN " +
            "CAST(COUNT(CASE WHEN pm.success = false THEN 1 END) AS DOUBLE) / COUNT(pm) * 100 " +
            "ELSE 0.0 END " +
            "FROM PerformanceMetrics pm " +
            "WHERE DATE(pm.timestamp) BETWEEN :startDate AND :endDate")
    Double getErrorRate(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(AVG(pm.cpuUsagePercentage), 0.0) FROM PerformanceMetrics pm " +
            "WHERE pm.timestamp >= :since " +
            "AND pm.cpuUsagePercentage IS NOT NULL")
    Double getCurrentSystemLoad(@Param("since") LocalDateTime since);

    // Helper method for current system load
    default Double getCurrentSystemLoad() {
        return getCurrentSystemLoad(LocalDateTime.now().minusMinutes(5));
    }

    @Query("SELECT COALESCE(AVG(pm.memoryUsagePercentage), 0.0) FROM PerformanceMetrics pm " +
            "WHERE pm.timestamp >= :since " +
            "AND pm.memoryUsagePercentage IS NOT NULL")
    Double getCurrentMemoryUsage(@Param("since") LocalDateTime since);

    // Helper method for current memory usage
    default Double getCurrentMemoryUsage() {
        return getCurrentMemoryUsage(LocalDateTime.now().minusMinutes(5));
    }

    @Query("SELECT pm.errorMessage FROM PerformanceMetrics pm " +
            "WHERE pm.timestamp >= :since " +
            "AND pm.success = false " +
            "AND pm.errorMessage IS NOT NULL " +
            "GROUP BY pm.errorMessage " +
            "ORDER BY COUNT(pm) DESC")
    List<String> getActiveAlerts(@Param("since") LocalDateTime since);

    // Helper method for active alerts
    default List<String> getActiveAlerts() {
        return getActiveAlerts(LocalDateTime.now().minusHours(1));
    }

    @Query("SELECT COUNT(DISTINCT pm.errorMessage) FROM PerformanceMetrics pm " +
            "WHERE pm.timestamp >= :since " +
            "AND pm.success = false " +
            "AND pm.thresholdExceeded = true")
    Integer getCriticalAlertsCount(@Param("since") LocalDateTime since);

    // Helper method for critical alerts count
    default Integer getCriticalAlertsCount() {
        return getCriticalAlertsCount(LocalDateTime.now().minusHours(1));
    }

    @Query("SELECT COUNT(DISTINCT pm.errorMessage) FROM PerformanceMetrics pm " +
            "WHERE pm.timestamp >= :since " +
            "AND pm.success = false " +
            "AND (pm.thresholdExceeded = false OR pm.thresholdExceeded IS NULL)")
    Integer getWarningAlertsCount(@Param("since") LocalDateTime since);

    // Helper method for warning alerts count
    default Integer getWarningAlertsCount() {
        return getWarningAlertsCount(LocalDateTime.now().minusHours(1));
    }

    @Query("SELECT pm FROM PerformanceMetrics pm " +
            "WHERE pm.thresholdExceeded = true " +
            "AND pm.timestamp >= :since " +
            "ORDER BY pm.timestamp DESC")
    List<PerformanceMetrics> getThresholdExceededMetrics(@Param("since") LocalDateTime since, Pageable pageable);

    @Query("SELECT pm.endpointUrl, COALESCE(AVG(pm.responseTimeMs), 0.0) FROM PerformanceMetrics pm " +
            "WHERE DATE(pm.timestamp) BETWEEN :startDate AND :endDate " +
            "AND pm.metricCategory = 'WEB_REQUEST' " +
            "GROUP BY pm.endpointUrl " +
            "ORDER BY AVG(pm.responseTimeMs) DESC")
    List<Object[]> getSlowestEndpoints(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, Pageable pageable);

    @Query("SELECT pm.endpointUrl, COUNT(pm) FROM PerformanceMetrics pm " +
            "WHERE DATE(pm.timestamp) BETWEEN :startDate AND :endDate " +
            "AND pm.success = false " +
            "GROUP BY pm.endpointUrl " +
            "ORDER BY COUNT(pm) DESC")
    List<Object[]> getMostErrorProneEndpoints(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(pm) > 0 THEN " +
            "CAST(COUNT(CASE WHEN pm.cacheHit = true THEN 1 END) AS DOUBLE) / COUNT(pm) * 100 " +
            "ELSE 0.0 END " +
            "FROM PerformanceMetrics pm " +
            "WHERE DATE(pm.timestamp) BETWEEN :startDate AND :endDate " +
            "AND pm.cacheHit IS NOT NULL")
    Double getCacheHitRate(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
