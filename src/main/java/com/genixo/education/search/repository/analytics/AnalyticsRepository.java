package com.genixo.education.search.repository.analytics;

import com.genixo.education.search.dto.analytics.AnalyticsSummaryDto;
import com.genixo.education.search.dto.institution.SchoolSummaryDto;
import com.genixo.education.search.entity.analytics.Analytics;
import com.genixo.education.search.enumaration.MetricType;
import com.genixo.education.search.enumaration.TimePeriod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AnalyticsRepository extends JpaRepository<Analytics, Long> {
    @Query("SELECT a FROM Analytics a " +
            "WHERE a.isActive = true " +
            "AND (:startDate IS NULL OR a.date >= :startDate) " +
            "AND (:endDate IS NULL OR a.date <= :endDate) " +
            "AND (:timePeriod IS NULL OR a.timePeriod = :timePeriod) " +
            "AND (:metricTypes IS NULL OR a.metricType IN :metricTypes) " +
            "AND (:brandId IS NULL OR a.brand.id = :brandId) " +
            "AND (:campusId IS NULL OR a.campus.id = :campusId) " +
            "AND (:schoolId IS NULL OR a.school.id = :schoolId) " +
            "AND (:dataSource IS NULL OR a.dataSource = :dataSource)")
    Page<Analytics> findAnalyticsByFilter(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("timePeriod") TimePeriod timePeriod,
            @Param("metricTypes") List<MetricType> metricTypes,
            @Param("brandId") Long brandId,
            @Param("campusId") Long campusId,
            @Param("schoolId") Long schoolId,
            @Param("dataSource") String dataSource,
            Pageable pageable);

    @Query("SELECT new com.genixo.education.search.dto.analytics.AnalyticsSummaryDto(" +
            "MAX(a.date), " +
            "COALESCE(SUM(a.pageViews), 0L), " +
            "COALESCE(SUM(a.uniqueVisitors), 0L), " +
            "COALESCE(SUM(a.appointmentRequests), 0L), " +
            "COALESCE(SUM(a.messageInquiries), 0L), " +
            "COALESCE(AVG(a.conversionRate), 0.0), " +
            "COALESCE(AVG(a.averageRating), 0.0), " +
            "CASE WHEN :schoolId IS NOT NULL THEN (SELECT s.name FROM School s WHERE s.id = :schoolId) " +
            "     WHEN :campusId IS NOT NULL THEN (SELECT c.name FROM Campus c WHERE c.id = :campusId) " +
            "     WHEN :brandId IS NOT NULL THEN (SELECT b.name FROM Brand b WHERE b.id = :brandId) " +
            "     ELSE 'System Wide' END, " +
            "'Mixed') " +
            "FROM Analytics a " +
            "WHERE a.isActive = true " +
            "AND a.date BETWEEN :startDate AND :endDate " +
            "AND (:brandId IS NULL OR a.brand.id = :brandId) " +
            "AND (:campusId IS NULL OR a.campus.id = :campusId) " +
            "AND (:schoolId IS NULL OR a.school.id = :schoolId)")
    AnalyticsSummaryDto getAnalyticsSummary(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("schoolId") Long schoolId,
            @Param("campusId") Long campusId,
            @Param("brandId") Long brandId);

    @Query("SELECT new com.genixo.education.search.dto.institution.SchoolSummaryDto(" +
            "s.id, s.name, s.slug, s.logoUrl, s.institutionType.displayName, " +
            "s.minAge, s.maxAge, s.monthlyFee, s.ratingAverage, s.ratingCount, " +
            "s.facebookUrl, s.twitterUrl, s.instagramUrl, s.linkedinUrl, s.instagramUrl,  " +
            "CASE WHEN EXISTS(SELECT 1 FROM CampaignSchool cs WHERE cs.school.id = s.id AND cs.status = 'ACTIVE') " +
            "THEN true ELSE false END)" +

            "FROM Analytics a " +
            "JOIN a.school s " +
            "WHERE a.isActive = true AND s.isActive = true " +
            "AND a.date BETWEEN :startDate AND :endDate " +
            "GROUP BY s.id, s.name, s.slug, s.logoUrl, s.institutionType.displayName, " +
            "s.minAge, s.maxAge, s.monthlyFee, s.ratingAverage, s.ratingCount " +
            "ORDER BY SUM(a.pageViews) DESC")
    List<SchoolSummaryDto> getTopSchoolsByViews(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    @Query("SELECT new com.genixo.education.search.dto.institution.SchoolSummaryDto(" +
            "s.id, s.name, s.slug, s.logoUrl, s.institutionType.displayName, " +
            "s.minAge, s.maxAge, s.monthlyFee, s.ratingAverage, s.ratingCount, " +
            "s.facebookUrl, s.twitterUrl, s.instagramUrl, s.linkedinUrl, s.instagramUrl,  " +
            "CASE WHEN EXISTS(SELECT 1 FROM CampaignSchool cs WHERE cs.school.id = s.id AND cs.status = 'ACTIVE') " +
            "THEN true ELSE false END)" +

            "FROM Analytics a " +
            "JOIN a.school s " +
            "WHERE a.isActive = true AND s.isActive = true " +
            "AND a.date BETWEEN :startDate AND :endDate " +
            "GROUP BY s.id, s.name, s.slug, s.logoUrl, s.institutionType.displayName, " +
            "s.minAge, s.maxAge, s.monthlyFee, s.ratingAverage, s.ratingCount " +
            "ORDER BY SUM(a.appointmentRequests) DESC")
    List<SchoolSummaryDto> getTopSchoolsByConversions(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    // Helper methods for repository
    default List<SchoolSummaryDto> getTopSchoolsByViews(LocalDate startDate, LocalDate endDate, int limit) {
        return getTopSchoolsByViews(startDate, endDate, PageRequest.of(0, limit));
    }

    default List<SchoolSummaryDto> getTopSchoolsByConversions(LocalDate startDate, LocalDate endDate, int limit) {
        return getTopSchoolsByConversions(startDate, endDate, PageRequest.of(0, limit));
    }

    @Query("SELECT a FROM Analytics a " +
            "WHERE a.isActive = true AND a.school.id = :schoolId " +
            "ORDER BY a.date DESC")
    List<Analytics> findBySchoolIdOrderByDateDesc(@Param("schoolId") Long schoolId, Pageable pageable);

    @Query("SELECT a FROM Analytics a " +
            "WHERE a.isActive = true AND a.campus.id = :campusId " +
            "ORDER BY a.date DESC")
    List<Analytics> findByCampusIdOrderByDateDesc(@Param("campusId") Long campusId, Pageable pageable);

    @Query("SELECT a FROM Analytics a " +
            "WHERE a.isActive = true AND a.brand.id = :brandId " +
            "ORDER BY a.date DESC")
    List<Analytics> findByBrandIdOrderByDateDesc(@Param("brandId") Long brandId, Pageable pageable);

    @Query("SELECT COALESCE(SUM(a.pageViews), 0) FROM Analytics a " +
            "WHERE a.isActive = true AND a.date = :date " +
            "AND (:schoolId IS NULL OR a.school.id = :schoolId)")
    Long getTotalPageViewsForDate(@Param("date") LocalDate date, @Param("schoolId") Long schoolId);

    @Query("SELECT COALESCE(SUM(a.uniqueVisitors), 0) FROM Analytics a " +
            "WHERE a.isActive = true AND a.date = :date " +
            "AND (:schoolId IS NULL OR a.school.id = :schoolId)")
    Long getTotalUniqueVisitorsForDate(@Param("date") LocalDate date, @Param("schoolId") Long schoolId);

    @Query("SELECT COALESCE(AVG(a.conversionRate), 0.0) FROM Analytics a " +
            "WHERE a.isActive = true " +
            "AND a.date BETWEEN :startDate AND :endDate " +
            "AND (:schoolId IS NULL OR a.school.id = :schoolId)")
    Double getAverageConversionRate(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("schoolId") Long schoolId);
}
