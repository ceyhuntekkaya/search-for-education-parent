package com.genixo.education.search.repository.analytics;

import com.genixo.education.search.dto.analytics.VisitorLogSummaryDto;
import com.genixo.education.search.entity.analytics.VisitorLog;
import com.genixo.education.search.enumaration.DeviceType;
import com.genixo.education.search.enumaration.TrafficSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface VisitorLogRepository extends JpaRepository<VisitorLog, Long> {
    @Query("SELECT vl FROM VisitorLog vl " +
            "WHERE (:startDate IS NULL OR DATE(vl.visitTime) >= :startDate) " +
            "AND (:endDate IS NULL OR DATE(vl.visitTime) <= :endDate) " +
            "AND (:schoolId IS NULL OR vl.school.id = :schoolId) " +
            "AND (:campusId IS NULL OR vl.campus.id = :campusId) " +
            "AND (:brandId IS NULL OR vl.brand.id = :brandId)")
    Page<VisitorLog> findVisitorLogsByFilter(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("schoolId") Long schoolId,
            @Param("campusId") Long campusId,
            @Param("brandId") Long brandId,
            Pageable pageable);

    @Query("SELECT new com.genixo.education.search.dto.analytics.VisitorLogSummaryDto(" +
            "vl.visitTime, vl.pageUrl, vl.pageTitle, vl.deviceType, vl.trafficSource, " +
            "vl.country, vl.city, vl.timeOnPageSeconds, vl.isNewVisitor, " +
            "CASE WHEN vl.school IS NOT NULL THEN vl.school.name " +
            "     WHEN vl.campus IS NOT NULL THEN vl.campus.name " +
            "     WHEN vl.brand IS NOT NULL THEN vl.brand.name " +
            "     ELSE 'Unknown' END) " +
            "FROM VisitorLog vl " +
            "WHERE DATE(vl.visitTime) BETWEEN :startDate AND :endDate " +
            "AND (:schoolId IS NULL OR vl.school.id = :schoolId) " +
            "AND (:campusId IS NULL OR vl.campus.id = :campusId) " +
            "AND (:brandId IS NULL OR vl.brand.id = :brandId) " +
            "ORDER BY vl.visitTime DESC")
    List<VisitorLogSummaryDto> getVisitorSummary(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("schoolId") Long schoolId,
            @Param("campusId") Long campusId,
            @Param("brandId") Long brandId);

    @Query("SELECT COUNT(DISTINCT vl.sessionId) FROM VisitorLog vl " +
            "WHERE vl.visitTime >= :since")
    Long countActiveUsers(@Param("since") LocalDateTime since);

    @Query("SELECT COUNT(DISTINCT vl.visitorId) FROM VisitorLog vl " +
            "WHERE vl.visitTime >= :since")
    Long countVisitorsSince(@Param("since") LocalDateTime since);

    @Query("SELECT new com.genixo.education.search.dto.analytics.VisitorLogSummaryDto(" +
            "vl.visitTime, vl.pageUrl, vl.pageTitle, vl.deviceType, vl.trafficSource, " +
            "vl.country, vl.city, vl.timeOnPageSeconds, vl.isNewVisitor, " +
            "CASE WHEN vl.school IS NOT NULL THEN vl.school.name " +
            "     WHEN vl.campus IS NOT NULL THEN vl.campus.name " +
            "     WHEN vl.brand IS NOT NULL THEN vl.brand.name " +
            "     ELSE 'Unknown' END) " +
            "FROM VisitorLog vl " +
            "ORDER BY vl.visitTime DESC")
    List<VisitorLogSummaryDto> getRecentVisitors(Pageable pageable);

    // Helper method for repository
    default List<VisitorLogSummaryDto> getRecentVisitors(int limit) {
        return getRecentVisitors(PageRequest.of(0, limit));
    }

    @Query("SELECT vl.trafficSource FROM VisitorLog vl " +
            "WHERE DATE(vl.visitTime) BETWEEN :startDate AND :endDate " +
            "GROUP BY vl.trafficSource " +
            "ORDER BY COUNT(vl) DESC")
    List<String> getTopTrafficSources(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    default List<String> getTopTrafficSources(LocalDate startDate, LocalDate endDate, int limit) {
        return getTopTrafficSources(startDate, endDate, PageRequest.of(0, limit));
    }

    @Query("SELECT vl.city, COUNT(vl) FROM VisitorLog vl " +
            "WHERE DATE(vl.visitTime) BETWEEN :startDate AND :endDate " +
            "AND (:schoolId IS NULL OR vl.school.id = :schoolId) " +
            "AND (:campusId IS NULL OR vl.campus.id = :campusId) " +
            "AND (:brandId IS NULL OR vl.brand.id = :brandId) " +
            "AND vl.city IS NOT NULL " +
            "GROUP BY vl.city " +
            "ORDER BY COUNT(vl) DESC")
    List<Object[]> getVisitorsByCityRaw(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("schoolId") Long schoolId,
            @Param("campusId") Long campusId,
            @Param("brandId") Long brandId);

    default Map<String, Long> getVisitorsByCity(LocalDate startDate, LocalDate endDate,
                                                Long schoolId, Long campusId, Long brandId) {
        return getVisitorsByCityRaw(startDate, endDate, schoolId, campusId, brandId)
                .stream()
                .collect(java.util.stream.Collectors.toMap(
                        arr -> (String) arr[0],
                        arr -> ((Number) arr[1]).longValue()
                ));
    }

    @Query("SELECT vl.deviceType, COUNT(vl) FROM VisitorLog vl " +
            "WHERE DATE(vl.visitTime) BETWEEN :startDate AND :endDate " +
            "AND (:schoolId IS NULL OR vl.school.id = :schoolId) " +
            "AND (:campusId IS NULL OR vl.campus.id = :campusId) " +
            "AND (:brandId IS NULL OR vl.brand.id = :brandId) " +
            "GROUP BY vl.deviceType " +
            "ORDER BY COUNT(vl) DESC")
    List<Object[]> getVisitorsByDeviceRaw(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("schoolId") Long schoolId,
            @Param("campusId") Long campusId,
            @Param("brandId") Long brandId);

    default Map<DeviceType, Long> getVisitorsByDevice(LocalDate startDate, LocalDate endDate,
                                                      Long schoolId, Long campusId, Long brandId) {
        return getVisitorsByDeviceRaw(startDate, endDate, schoolId, campusId, brandId)
                .stream()
                .collect(java.util.stream.Collectors.toMap(
                        arr -> (DeviceType) arr[0],
                        arr -> ((Number) arr[1]).longValue()
                ));
    }

    @Query("SELECT vl.trafficSource, COUNT(vl) FROM VisitorLog vl " +
            "WHERE DATE(vl.visitTime) BETWEEN :startDate AND :endDate " +
            "AND (:schoolId IS NULL OR vl.school.id = :schoolId) " +
            "AND (:campusId IS NULL OR vl.campus.id = :campusId) " +
            "AND (:brandId IS NULL OR vl.brand.id = :brandId) " +
            "GROUP BY vl.trafficSource " +
            "ORDER BY COUNT(vl) DESC")
    List<Object[]> getVisitorsBySourceRaw(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("schoolId") Long schoolId,
            @Param("campusId") Long campusId,
            @Param("brandId") Long brandId);

    default Map<TrafficSource, Long> getVisitorsBySource(LocalDate startDate, LocalDate endDate,
                                                         Long schoolId, Long campusId, Long brandId) {
        return getVisitorsBySourceRaw(startDate, endDate, schoolId, campusId, brandId)
                .stream()
                .collect(java.util.stream.Collectors.toMap(
                        arr -> (TrafficSource) arr[0],
                        arr -> ((Number) arr[1]).longValue()
                ));
    }

    @Query("SELECT COUNT(vl) FROM VisitorLog vl " +
            "WHERE DATE(vl.visitTime) = :date " +
            "AND (:schoolId IS NULL OR vl.school.id = :schoolId)")
    Long countVisitorsForDate(@Param("date") LocalDate date, @Param("schoolId") Long schoolId);

    @Query("SELECT COUNT(DISTINCT vl.visitorId) FROM VisitorLog vl " +
            "WHERE DATE(vl.visitTime) = :date " +
            "AND (:schoolId IS NULL OR vl.school.id = :schoolId)")
    Long countUniqueVisitorsForDate(@Param("date") LocalDate date, @Param("schoolId") Long schoolId);

    @Query("SELECT vl FROM VisitorLog vl " +
            "WHERE vl.school.id = :schoolId " +
            "ORDER BY vl.visitTime DESC")
    List<VisitorLog> findBySchoolIdOrderByVisitTimeDesc(@Param("schoolId") Long schoolId, Pageable pageable);

    @Query("SELECT vl FROM VisitorLog vl " +
            "WHERE vl.isBot = false AND vl.timeOnPageSeconds > 10 " +
            "ORDER BY vl.visitTime DESC")
    List<VisitorLog> findRealVisitors(Pageable pageable);

    @Query("SELECT COALESCE(AVG(vl.timeOnPageSeconds), 0) FROM VisitorLog vl " +
            "WHERE DATE(vl.visitTime) BETWEEN :startDate AND :endDate " +
            "AND vl.isBot = false " +
            "AND (:schoolId IS NULL OR vl.school.id = :schoolId)")
    Double getAverageTimeOnPage(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("schoolId") Long schoolId);
}
