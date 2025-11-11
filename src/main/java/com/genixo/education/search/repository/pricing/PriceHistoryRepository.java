package com.genixo.education.search.repository.pricing;

import com.genixo.education.search.entity.pricing.PriceHistory;
import com.genixo.education.search.enumaration.PriceChangeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {

    @Query("SELECT ph FROM PriceHistory ph " +
            "WHERE ph.schoolPricing.id = :pricingId AND ph.isActive = true " +
            "ORDER BY ph.changeDate DESC")
    List<PriceHistory> findBySchoolPricingIdOrderByChangeDateDesc(@Param("pricingId") Long pricingId);

    @Query("SELECT ph FROM PriceHistory ph " +
            "WHERE ph.schoolPricing.school.id = :schoolId " +
            "AND (:gradeLevel IS NULL OR ph.schoolPricing.gradeLevel = :gradeLevel) " +
            "AND DATE(ph.changeDate) >= :startDate AND DATE(ph.changeDate) <= :endDate " +
            "AND ph.isActive = true " +
            "ORDER BY ph.changeDate ASC")
    List<PriceHistory> findPriceTrendsForSchool(
            @Param("schoolId") Long schoolId,
            @Param("gradeLevel") String gradeLevel,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT ph FROM PriceHistory ph " +
            "WHERE ph.changeType = :changeType AND ph.isActive = true " +
            "AND ph.changeDate >= :startDate AND ph.changeDate <= :endDate " +
            "ORDER BY ph.changeDate DESC")
    List<PriceHistory> findByChangeTypeAndDateRange(
            @Param("changeType") PriceChangeType changeType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT ph FROM PriceHistory ph " +
            "WHERE ph.schoolPricing.school.id = :schoolId " +
            "AND ph.fieldName = :fieldName AND ph.isActive = true " +
            "ORDER BY ph.changeDate DESC")
    List<PriceHistory> findBySchoolIdAndFieldNameOrderByChangeDateDesc(
            @Param("schoolId") Long schoolId,
            @Param("fieldName") String fieldName);

    @Query("SELECT ph FROM PriceHistory ph " +
            "WHERE ph.changedByUser.id = :userId AND ph.isActive = true " +
            "ORDER BY ph.changeDate DESC")
    List<PriceHistory> findByChangedByUserIdOrderByChangeDateDesc(@Param("userId") Long userId);


}
