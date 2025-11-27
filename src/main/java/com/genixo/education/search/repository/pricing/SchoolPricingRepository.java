package com.genixo.education.search.repository.pricing;

import com.genixo.education.search.dto.ai.PriceRangeInfo;
import com.genixo.education.search.dto.ai.RAGContextDTO;
import com.genixo.education.search.dto.pricing.MarketAveragesDto;
import com.genixo.education.search.dto.pricing.PricingAnalyticsDto;
import com.genixo.education.search.entity.pricing.SchoolPricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolPricingRepository extends JpaRepository<SchoolPricing, Long> {

    @Query("SELECT sp FROM SchoolPricing sp WHERE sp.isActive = true AND sp.id = :id")
    Optional<SchoolPricing> findByIdAndIsActiveTrue(@Param("id") Long id);

    @Query("SELECT CASE WHEN COUNT(sp) > 0 THEN true ELSE false END " +
            "FROM SchoolPricing sp WHERE sp.school.id = :schoolId " +
            "AND sp.academicYear = :academicYear AND sp.gradeLevel = :gradeLevel AND sp.isActive = true")
    boolean existsBySchoolIdAndAcademicYearAndGradeLevelAndIsActiveTrue(
            @Param("schoolId") Long schoolId,
            @Param("academicYear") String academicYear,
            @Param("gradeLevel") String gradeLevel);

    @Query("SELECT sp FROM SchoolPricing sp " +
            "WHERE sp.school.id = :schoolId AND sp.gradeLevel = :gradeLevel " +
            "AND sp.academicYear = :academicYear AND sp.isCurrent = true " +
            "AND sp.status = 'ACTIVE' AND sp.isActive = true")
    Optional<SchoolPricing> findCurrentPricingBySchoolAndGradeAndYear(
            @Param("schoolId") Long schoolId,
            @Param("gradeLevel") String gradeLevel,
            @Param("academicYear") String academicYear);

    @Query("SELECT sp FROM SchoolPricing sp " +
            "WHERE sp.school.id = :schoolId AND sp.isActive = true " +
            "ORDER BY sp.academicYear DESC, sp.version DESC, sp.createdAt DESC")
    List<SchoolPricing> findBySchoolIdAndIsActiveTrueOrderByCreatedAtDesc(@Param("schoolId") Long schoolId);

    @Query("SELECT sp FROM SchoolPricing sp " +
            "WHERE sp.school.id = :schoolId AND sp.isCurrent = true " +
            "AND sp.status = 'ACTIVE' AND sp.isActive = true " +
            "ORDER BY sp.gradeLevel ASC")
    List<SchoolPricing> findCurrentPricingsBySchoolIdOrderByGradeLevel(@Param("schoolId") Long schoolId);

    @Query("SELECT sp FROM SchoolPricing sp " +
            "WHERE sp.school.id IN :schoolIds AND sp.gradeLevel = :gradeLevel " +
            "AND sp.academicYear = :academicYear AND sp.isCurrent = true " +
            "AND sp.status = 'ACTIVE' AND sp.isActive = true")
    List<SchoolPricing> findCurrentPricingBySchoolIdsAndGradeAndYear(
            @Param("schoolIds") List<Long> schoolIds,
            @Param("gradeLevel") String gradeLevel,
            @Param("academicYear") String academicYear);

    @Modifying
    @Query("UPDATE SchoolPricing sp SET sp.isCurrent = false, sp.updatedBy = :userId " +
            "WHERE sp.school.id = :schoolId AND sp.gradeLevel = :gradeLevel " +
            "AND sp.academicYear = :academicYear AND sp.id != :excludeId")
    void deactivateCurrentPricingForSchoolAndGrade(
            @Param("schoolId") Long schoolId,
            @Param("gradeLevel") String gradeLevel,
            @Param("academicYear") String academicYear,
            @Param("excludeId") Long excludeId,
            @Param("userId") Long userId);

    @Modifying
    @Query("UPDATE SchoolPricing sp SET sp.isCurrent = false, sp.updatedBy = :userId " +
            "WHERE sp.school.id = :schoolId AND sp.gradeLevel = :gradeLevel " +
            "AND sp.academicYear = :academicYear")
    void deactivateCurrentPricingForSchoolAndGrade(
            @Param("schoolId") Long schoolId,
            @Param("gradeLevel") String gradeLevel,
            @Param("academicYear") String academicYear,
            @Param("userId") Long userId);

    @Query("SELECT sp FROM SchoolPricing sp " +
            "WHERE (:schoolIds IS NULL OR sp.school.id IN :schoolIds) " +
            "AND (:gradeLevels IS NULL OR sp.gradeLevel IN :gradeLevels) " +
            "AND (:academicYears IS NULL OR sp.academicYear IN :academicYears) " +
            "AND (:startDate IS NULL OR DATE(sp.createdAt) >= :startDate) " +
            "AND (:endDate IS NULL OR DATE(sp.createdAt) <= :endDate) " +
            "AND sp.isActive = true " +
            "ORDER BY sp.school.name ASC, sp.academicYear DESC, sp.gradeLevel ASC")
    List<SchoolPricing> findPricingsForReport(
            @Param("schoolIds") List<Long> schoolIds,
            @Param("gradeLevels") List<String> gradeLevels,
            @Param("academicYears") List<String> academicYears,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT sp FROM SchoolPricing sp " +
            "WHERE (:schoolIds IS NULL OR sp.school.id IN :schoolIds) " +
            "AND (:includeInactive = true OR sp.isActive = true) " +
            "ORDER BY sp.school.name ASC, sp.academicYear DESC, sp.gradeLevel ASC")
    List<SchoolPricing> findPricingsForExport(
            @Param("schoolIds") List<Long> schoolIds,
            @Param("includeInactive") Boolean includeInactive);

    // Analytics queries
    @Query("SELECT new com.genixo.education.search.dto.pricing.PricingAnalyticsDto(" +
            "COUNT(sp), " +
            "COALESCE(CAST(AVG(sp.monthlyTuition) AS double), 0.0), " +
            "COALESCE(CAST(MIN(sp.monthlyTuition) AS double), 0.0), " +
            "COALESCE(CAST(MAX(sp.monthlyTuition) AS double), 0.0), " +
            "COALESCE(CAST(AVG(sp.annualTuition) AS double), 0.0), " +
            "COALESCE(CAST(MIN(sp.annualTuition) AS double), 0.0), " +
            "COALESCE(CAST(MAX(sp.annualTuition) AS double), 0.0), " +
            "COALESCE(CAST(AVG(sp.registrationFee) AS double), 0.0), " +
            "COALESCE(CAST(AVG(sp.totalAnnualCost) AS double), 0.0), " +
            "COUNT(DISTINCT sp.gradeLevel), " +
            "COUNT(DISTINCT sp.academicYear)) " +
            "FROM SchoolPricing sp " +
            "WHERE sp.school.id = :schoolId AND sp.isActive = true AND sp.status = 'ACTIVE'")
    PricingAnalyticsDto getPricingAnalytics(@Param("schoolId") Long schoolId);


    @Query("SELECT new com.genixo.education.search.dto.pricing.MarketAveragesDto(" +
            "COALESCE(CAST(AVG(sp.monthlyTuition) AS double), 0.0), " +
            "COALESCE(CAST(AVG(sp.annualTuition) AS double), 0.0), " +
            "COALESCE(CAST(AVG(sp.registrationFee) AS double), 0.0), " +
            "COALESCE(CAST(AVG(sp.totalAnnualCost) AS double), 0.0), " +
            "COALESCE(CAST(MIN(sp.monthlyTuition) AS double), 0.0), " +
            "COALESCE(CAST(MAX(sp.monthlyTuition) AS double), 0.0), " +
            "COUNT(sp)) " +
            "FROM SchoolPricing sp " +
            "WHERE sp.gradeLevel = :gradeLevel AND sp.academicYear = :academicYear " +
            "AND sp.isActive = true AND sp.status = 'ACTIVE' AND sp.isCurrent = true")
    MarketAveragesDto getMarketAverages(
            @Param("gradeLevel") String gradeLevel,
            @Param("academicYear") String academicYear);

    @Query("SELECT sp FROM SchoolPricing sp " +
            "WHERE sp.gradeLevel = :gradeLevel AND sp.academicYear = :academicYear " +
            "AND sp.isActive = true AND sp.status = 'ACTIVE' AND sp.isCurrent = true " +
            "ORDER BY sp.monthlyTuition ASC")
    List<SchoolPricing> findMarketPricingByGradeAndYear(
            @Param("gradeLevel") String gradeLevel,
            @Param("academicYear") String academicYear);

    @Query("SELECT sp FROM SchoolPricing sp " +
            "WHERE sp.validUntil < CURRENT_DATE AND sp.status = 'ACTIVE' AND sp.isActive = true")
    List<SchoolPricing> findExpiredActivePricings();

    @Query("SELECT DISTINCT sp.academicYear FROM SchoolPricing sp " +
            "WHERE sp.school.id = :schoolId AND sp.isActive = true " +
            "ORDER BY sp.academicYear DESC")
    List<String> findDistinctAcademicYearsBySchoolId(@Param("schoolId") Long schoolId);

    @Query("SELECT DISTINCT sp.gradeLevel FROM SchoolPricing sp " +
            "WHERE sp.school.id = :schoolId AND sp.isActive = true " +
            "ORDER BY sp.gradeLevel ASC")
    List<String> findDistinctGradeLevelsBySchoolId(@Param("schoolId") Long schoolId);

    @Query("SELECT sp FROM SchoolPricing sp " +
            "WHERE sp.status = 'PENDING_APPROVAL' AND sp.isActive = true " +
            "ORDER BY sp.createdAt ASC")
    List<SchoolPricing> findPendingApprovalPricings();

    @Query("SELECT sp FROM SchoolPricing sp " +
            "WHERE sp.school.id = :schoolId AND sp.academicYear = :academicYear " +
            "AND sp.isActive = true " +
            "ORDER BY sp.version DESC, sp.createdAt DESC")
    List<SchoolPricing> findPricingVersionsBySchoolAndYear(
            @Param("schoolId") Long schoolId,
            @Param("academicYear") String academicYear);

    @Query("SELECT sp FROM SchoolPricing sp " +
            "WHERE sp.school.campus.id = :campusId AND sp.isActive = true " +
            "AND sp.status = 'ACTIVE' AND sp.isCurrent = true " +
            "ORDER BY sp.school.name ASC, sp.gradeLevel ASC")
    List<SchoolPricing> findCurrentPricingsByCampusId(@Param("campusId") Long campusId);

    @Query("SELECT sp FROM SchoolPricing sp " +
            "WHERE sp.school.campus.brand.id = :brandId AND sp.isActive = true " +
            "AND sp.status = 'ACTIVE' AND sp.isCurrent = true " +
            "ORDER BY sp.school.campus.name ASC, sp.school.name ASC, sp.gradeLevel ASC")
    List<SchoolPricing> findCurrentPricingsByBrandId(@Param("brandId") Long brandId);

    @Query("SELECT MIN(sp.monthlyTuition), MAX(sp.monthlyTuition), AVG(sp.monthlyTuition) " +
            "FROM SchoolPricing sp " +
            "WHERE sp.gradeLevel = :gradeLevel AND sp.academicYear = :academicYear " +
            "AND sp.isActive = true AND sp.status = 'ACTIVE' AND sp.isCurrent = true " +
            "AND sp.monthlyTuition IS NOT NULL")
    Object[] getMonthlyTuitionStatistics(
            @Param("gradeLevel") String gradeLevel,
            @Param("academicYear") String academicYear);

    @Query("SELECT sp.school.campus.province, COUNT(sp), AVG(sp.monthlyTuition) " +
            "FROM SchoolPricing sp " +
            "WHERE sp.gradeLevel = :gradeLevel AND sp.academicYear = :academicYear " +
            "AND sp.isActive = true AND sp.status = 'ACTIVE' AND sp.isCurrent = true " +
            "GROUP BY sp.school.campus.province " +
            "ORDER BY sp.school.campus.province.name ASC")
    List<Object[]> getPricingStatisticsByCity(
            @Param("gradeLevel") String gradeLevel,
            @Param("academicYear") String academicYear);

    @Query("SELECT sp.school.institutionType.displayName, COUNT(sp), AVG(sp.monthlyTuition), AVG(sp.annualTuition) " +
            "FROM SchoolPricing sp " +
            "WHERE sp.academicYear = :academicYear AND sp.isActive = true " +
            "AND sp.status = 'ACTIVE' AND sp.isCurrent = true " +
            "GROUP BY sp.school.institutionType.displayName " +
            "ORDER BY AVG(sp.monthlyTuition) DESC")
    List<Object[]> getPricingStatisticsByInstitutionType(@Param("academicYear") String academicYear);

    @Query("SELECT sp FROM SchoolPricing sp " +
            "WHERE sp.monthlyTuition BETWEEN :minPrice AND :maxPrice " +
            "AND sp.gradeLevel = :gradeLevel AND sp.academicYear = :academicYear " +
            "AND sp.isActive = true AND sp.status = 'ACTIVE' AND sp.isCurrent = true " +
            "ORDER BY sp.monthlyTuition ASC")
    List<SchoolPricing> findByPriceRangeAndGradeAndYear(
            @Param("minPrice") java.math.BigDecimal minPrice,
            @Param("maxPrice") java.math.BigDecimal maxPrice,
            @Param("gradeLevel") String gradeLevel,
            @Param("academicYear") String academicYear);

    @Query("SELECT sp FROM SchoolPricing sp " +
            "WHERE sp.needBasedAidAvailable = true AND sp.isActive = true " +
            "AND sp.status = 'ACTIVE' AND sp.isCurrent = true " +
            "ORDER BY sp.monthlyTuition ASC")
    List<SchoolPricing> findSchoolsWithNeedBasedAid();

    @Query("SELECT sp FROM SchoolPricing sp " +
            "WHERE sp.meritBasedAidAvailable = true AND sp.isActive = true " +
            "AND sp.status = 'ACTIVE' AND sp.isCurrent = true " +
            "ORDER BY sp.monthlyTuition ASC")
    List<SchoolPricing> findSchoolsWithMeritBasedAid();

    @Query("SELECT sp FROM SchoolPricing sp " +
            "WHERE sp.siblingDiscountPercentage > 0 AND sp.isActive = true " +
            "AND sp.status = 'ACTIVE' AND sp.isCurrent = true " +
            "ORDER BY sp.siblingDiscountPercentage DESC")
    List<SchoolPricing> findSchoolsWithSiblingDiscount();

    @Query("SELECT COUNT(sp) FROM SchoolPricing sp " +
            "WHERE sp.school.campus.id = :campusId AND sp.isActive = true")
    Long countPricingsByCampusId(@Param("campusId") Long campusId);

    @Query("SELECT sp FROM SchoolPricing sp " +
            "WHERE sp.approvedBy IS NULL AND sp.status = 'PENDING_APPROVAL' " +
            "AND sp.isActive = true " +
            "ORDER BY sp.createdAt ASC")
    List<SchoolPricing> findUnapprovedPricings();

    @Modifying
    @Query("UPDATE SchoolPricing sp SET sp.status = :status, sp.updatedBy = :userId " +
            "WHERE sp.id = :id")
    void updateStatusById(
            @Param("id") Long id,
            @Param("status") com.genixo.education.search.enumaration.PricingStatus status,
            @Param("userId") Long userId);

    @Query("SELECT sp FROM SchoolPricing sp " +
            "WHERE sp.createdAt >= :startDate AND sp.createdAt <= :endDate " +
            "AND sp.isActive = true " +
            "ORDER BY sp.createdAt DESC")
    List<SchoolPricing> findCreatedBetweenDates(
            @Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate);

    @Query("SELECT sp FROM SchoolPricing sp " +
            "WHERE sp.school.id = :id")
    SchoolPricing findBySchoolId(@Param("id") Long id);


    @Query("SELECT new com.genixo.education.search.dto.ai.PriceRangeInfo(min(sp.annualTuition), max(sp.annualTuition), avg(sp.annualTuition)) FROM SchoolPricing sp where sp.school.campus.province.name = :city and sp.school.institutionType.name = :institutionType")
    PriceRangeInfo getPriceStats(@Param("city") String city, @Param("institutionType") String institutionType);
}
