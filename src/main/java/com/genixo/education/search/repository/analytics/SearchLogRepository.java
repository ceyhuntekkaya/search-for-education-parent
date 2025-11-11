package com.genixo.education.search.repository.analytics;

import com.genixo.education.search.dto.analytics.SearchLogSummaryDto;
import com.genixo.education.search.entity.analytics.SearchLog;
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
public interface SearchLogRepository extends JpaRepository<SearchLog, Long> {

    @Query("SELECT sl FROM SearchLog sl " +
            "WHERE (:startDate IS NULL OR DATE(sl.searchTime) >= :startDate) " +
            "AND (:endDate IS NULL OR DATE(sl.searchTime) <= :endDate) " +
            "AND (:schoolId IS NULL OR sl.clickedSchoolId = :schoolId) " +
            "AND (:campusId IS NULL OR EXISTS(SELECT 1 FROM School s WHERE s.id = sl.clickedSchoolId AND s.campus.id = :campusId)) " +
            "AND (:brandId IS NULL OR EXISTS(SELECT 1 FROM School s WHERE s.id = sl.clickedSchoolId AND s.campus.brand.id = :brandId))")
    Page<SearchLog> findSearchLogsByFilter(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("schoolId") Long schoolId,
            @Param("campusId") Long campusId,
            @Param("brandId") Long brandId,
            Pageable pageable);

    @Query("SELECT new com.genixo.education.search.dto.analytics.SearchLogSummaryDto(" +
            "sl.searchTime, sl.searchQuery, sl.searchType, sl.resultsCount, sl.zeroResults, " +
            "CASE WHEN sl.clickedSchoolId IS NOT NULL THEN " +
            "(SELECT s.name FROM School s WHERE s.id = sl.clickedSchoolId) ELSE null END, " +
            "sl.userLocation, sl.deviceType) " +
            "FROM SearchLog sl " +
            "WHERE DATE(sl.searchTime) BETWEEN :startDate AND :endDate " +
            "AND (:schoolId IS NULL OR sl.clickedSchoolId = :schoolId) " +
            "AND (:campusId IS NULL OR EXISTS(SELECT 1 FROM School s WHERE s.id = sl.clickedSchoolId AND s.campus.id = :campusId)) " +
            "AND (:brandId IS NULL OR EXISTS(SELECT 1 FROM School s WHERE s.id = sl.clickedSchoolId AND s.campus.brand.id = :brandId)) " +
            "ORDER BY sl.searchTime DESC")
    List<SearchLogSummaryDto> getSearchSummary(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("schoolId") Long schoolId,
            @Param("campusId") Long campusId,
            @Param("brandId") Long brandId);

    @Query("SELECT new com.genixo.education.search.dto.analytics.SearchLogSummaryDto(" +
            "sl.searchTime, sl.searchQuery, sl.searchType, sl.resultsCount, sl.zeroResults, " +
            "CASE WHEN sl.clickedSchoolId IS NOT NULL THEN " +
            "(SELECT s.name FROM School s WHERE s.id = sl.clickedSchoolId) ELSE null END, " +
            "sl.userLocation, sl.deviceType) " +
            "FROM SearchLog sl " +
            "ORDER BY sl.searchTime DESC")
    List<SearchLogSummaryDto> getRecentSearches(Pageable pageable);

    default List<SearchLogSummaryDto> getRecentSearches(int limit) {
        return getRecentSearches(PageRequest.of(0, limit));
    }

    @Query("SELECT sl.searchQuery FROM SearchLog sl " +
            "WHERE DATE(sl.searchTime) BETWEEN :startDate AND :endDate " +
            "AND sl.searchQuery IS NOT NULL AND sl.searchQuery != '' " +
            "GROUP BY sl.searchQuery " +
            "ORDER BY COUNT(sl) DESC")
    List<String> getTopSearchTerms(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    default List<String> getTopSearchTerms(LocalDate startDate, LocalDate endDate, int limit) {
        return getTopSearchTerms(startDate, endDate, PageRequest.of(0, limit));
    }

    @Query("SELECT COUNT(sl) FROM SearchLog sl " +
            "WHERE DATE(sl.searchTime) BETWEEN :startDate AND :endDate " +
            "AND sl.zeroResults = true")
    Long countZeroResultSearches(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(sl) FROM SearchLog sl " +
            "WHERE DATE(sl.searchTime) BETWEEN :startDate AND :endDate")
    Long countTotalSearches(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT sl.searchQuery FROM SearchLog sl " +
            "WHERE DATE(sl.searchTime) BETWEEN :startDate AND :endDate " +
            "AND sl.zeroResults = true " +
            "GROUP BY sl.searchQuery " +
            "ORDER BY COUNT(sl) DESC")
    List<String> getTopZeroResultQueries(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, Pageable pageable);

    @Query("SELECT COALESCE(AVG(sl.responseTimeMs), 0.0) FROM SearchLog sl " +
            "WHERE DATE(sl.searchTime) BETWEEN :startDate AND :endDate " +
            "AND sl.responseTimeMs IS NOT NULL")
    Double getAverageSearchResponseTime(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT sl FROM SearchLog sl " +
            "WHERE sl.clickedSchoolId = :schoolId " +
            "ORDER BY sl.searchTime DESC")
    List<SearchLog> findByClickedSchoolIdOrderBySearchTimeDesc(@Param("schoolId") Long schoolId, Pageable pageable);

    @Query("SELECT sl.searchQuery, COUNT(sl) as searchCount FROM SearchLog sl " +
            "WHERE DATE(sl.searchTime) BETWEEN :startDate AND :endDate " +
            "GROUP BY sl.searchQuery " +
            "HAVING COUNT(sl) >= :minCount " +
            "ORDER BY COUNT(sl) DESC")
    List<Object[]> getPopularSearchTerms(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("minCount") Long minCount,
            Pageable pageable);
}
