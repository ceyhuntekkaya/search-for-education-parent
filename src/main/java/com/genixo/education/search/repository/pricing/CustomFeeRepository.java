package com.genixo.education.search.repository.pricing;

import com.genixo.education.search.entity.pricing.CustomFee;
import com.genixo.education.search.enumaration.CustomFeeStatus;
import com.genixo.education.search.enumaration.CustomFeeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomFeeRepository extends JpaRepository<CustomFee, Long> {

    @Query("SELECT cf FROM CustomFee cf WHERE cf.isActive = true AND cf.id = :id")
    Optional<CustomFee> findByIdAndIsActiveTrue(@Param("id") Long id);

    @Query("SELECT CASE WHEN COUNT(cf) > 0 THEN true ELSE false END " +
            "FROM CustomFee cf WHERE LOWER(cf.feeName) = LOWER(:feeName) " +
            "AND cf.schoolPricing.id = :pricingId AND cf.isActive = true")
    boolean existsByFeeNameIgnoreCaseAndSchoolPricingIdAndIsActiveTrue(
            @Param("feeName") String feeName,
            @Param("pricingId") Long pricingId);

    @Query("SELECT CASE WHEN COUNT(cf) > 0 THEN true ELSE false END " +
            "FROM CustomFee cf WHERE LOWER(cf.feeName) = LOWER(:feeName) " +
            "AND cf.schoolPricing.id = :pricingId AND cf.id != :id AND cf.isActive = true")
    boolean existsByFeeNameIgnoreCaseAndSchoolPricingIdAndIdNotAndIsActiveTrue(
            @Param("feeName") String feeName,
            @Param("pricingId") Long pricingId,
            @Param("id") Long id);

    @Query("SELECT cf FROM CustomFee cf " +
            "WHERE cf.schoolPricing.id = :pricingId AND cf.isActive = true " +
            "ORDER BY cf.displayOrder ASC, cf.feeName ASC")
    List<CustomFee> findBySchoolPricingIdAndIsActiveTrueOrderByDisplayOrder(@Param("pricingId") Long pricingId);

    @Query("SELECT cf FROM CustomFee cf " +
            "WHERE cf.schoolPricing.school.id = :schoolId AND cf.isActive = true " +
            "ORDER BY cf.schoolPricing.academicYear DESC, cf.schoolPricing.gradeLevel ASC, cf.displayOrder ASC")
    List<CustomFee> findBySchoolIdAndIsActiveTrueOrderByYearAndGrade(@Param("schoolId") Long schoolId);

    @Query("SELECT cf FROM CustomFee cf " +
            "WHERE cf.feeType = :feeType AND cf.isActive = true " +
            "ORDER BY cf.feeName ASC")
    List<CustomFee> findByFeeTypeAndIsActiveTrue(@Param("feeType") CustomFeeType feeType);

    @Query("SELECT cf FROM CustomFee cf " +
            "WHERE cf.status = :status AND cf.isActive = true " +
            "ORDER BY cf.createdAt ASC")
    List<CustomFee> findByStatusAndIsActiveTrue(@Param("status") CustomFeeStatus status);

    @Query("SELECT cf FROM CustomFee cf " +
            "WHERE cf.isMandatory = true AND cf.status = 'ACTIVE' " +
            "AND cf.schoolPricing.id = :pricingId AND cf.isActive = true " +
            "ORDER BY cf.displayOrder ASC")
    List<CustomFee> findMandatoryFeesByPricingId(@Param("pricingId") Long pricingId);

    @Query("SELECT cf FROM CustomFee cf " +
            "WHERE cf.appliesToNewStudents = true AND cf.status = 'ACTIVE' " +
            "AND cf.schoolPricing.id = :pricingId AND cf.isActive = true " +
            "AND (:currentDate BETWEEN COALESCE(cf.validFrom, '1900-01-01') AND COALESCE(cf.validUntil, '2100-12-31'))")
    List<CustomFee> findApplicableFeesForNewStudents(
            @Param("pricingId") Long pricingId,
            @Param("currentDate") LocalDate currentDate);

    @Query("SELECT cf FROM CustomFee cf " +
            "WHERE cf.appliesToExistingStudents = true AND cf.status = 'ACTIVE' " +
            "AND cf.schoolPricing.id = :pricingId AND cf.isActive = true " +
            "AND (:currentDate BETWEEN COALESCE(cf.validFrom, '1900-01-01') AND COALESCE(cf.validUntil, '2100-12-31'))")
    List<CustomFee> findApplicableFeesForExistingStudents(
            @Param("pricingId") Long pricingId,
            @Param("currentDate") LocalDate currentDate);

    @Query("SELECT cf FROM CustomFee cf " +
            "WHERE cf.appliesToGrades IS NOT NULL " +
            "AND JSON_CONTAINS(cf.appliesToGrades, JSON_QUOTE(:gradeLevel)) = 1 " +
            "AND cf.schoolPricing.school.id = :schoolId " +
            "AND cf.status = 'ACTIVE' AND cf.isActive = true")
    List<CustomFee> findFeesApplicableToGrade(
            @Param("schoolId") Long schoolId,
            @Param("gradeLevel") String gradeLevel);

    @Query("SELECT cf FROM CustomFee cf " +
            "WHERE cf.requiresApproval = true AND cf.status = 'PENDING_APPROVAL' " +
            "AND cf.isActive = true " +
            "ORDER BY cf.createdAt ASC")
    List<CustomFee> findFeesRequiringApproval();

    @Query("SELECT COALESCE(SUM(cf.feeAmount), 0) FROM CustomFee cf " +
            "WHERE cf.schoolPricing.id = :pricingId AND cf.isMandatory = true " +
            "AND cf.status = 'ACTIVE' AND cf.isActive = true")
    java.math.BigDecimal calculateTotalMandatoryFees(@Param("pricingId") Long pricingId);

    @Query("SELECT COALESCE(SUM(cf.feeAmount), 0) FROM CustomFee cf " +
            "WHERE cf.schoolPricing.id = :pricingId " +
            "AND cf.status = 'ACTIVE' AND cf.isActive = true")
    java.math.BigDecimal calculateTotalCustomFees(@Param("pricingId") Long pricingId);

    @Query("SELECT cf FROM CustomFee cf " +
            "WHERE cf.validUntil < CURRENT_DATE AND cf.status = 'ACTIVE' AND cf.isActive = true")
    List<CustomFee> findExpiredActiveFees();

    @Query("SELECT cf FROM CustomFee cf " +
            "WHERE cf.documentationRequired = true AND cf.status = 'ACTIVE' " +
            "AND cf.schoolPricing.id = :pricingId AND cf.isActive = true")
    List<CustomFee> findFeesRequiringDocumentation(@Param("pricingId") Long pricingId);

    @Query("SELECT cf FROM CustomFee cf " +
            "WHERE cf.installmentAllowed = true AND cf.status = 'ACTIVE' " +
            "AND cf.schoolPricing.id = :pricingId AND cf.isActive = true")
    List<CustomFee> findInstallmentEligibleFees(@Param("pricingId") Long pricingId);

    @Query("SELECT cf.feeType, COUNT(cf), AVG(cf.feeAmount), SUM(cf.totalCollected) " +
            "FROM CustomFee cf " +
            "WHERE cf.schoolPricing.school.id = :schoolId AND cf.isActive = true " +
            "GROUP BY cf.feeType " +
            "ORDER BY COUNT(cf) DESC")
    List<Object[]> getFeeStatisticsBySchoolAndType(@Param("schoolId") Long schoolId);

    @Query("SELECT cf FROM CustomFee cf " +
            "WHERE cf.discountEligible = true AND cf.status = 'ACTIVE' " +
            "AND cf.schoolPricing.id = :pricingId AND cf.isActive = true")
    List<CustomFee> findDiscountEligibleFees(@Param("pricingId") Long pricingId);

    @Query("SELECT cf FROM CustomFee cf " +
            "WHERE cf.scholarshipApplicable = true AND cf.status = 'ACTIVE' " +
            "AND cf.schoolPricing.id = :pricingId AND cf.isActive = true")
    List<CustomFee> findScholarshipApplicableFees(@Param("pricingId") Long pricingId);

    @Modifying
    @Query("UPDATE CustomFee cf SET cf.status = :status, cf.updatedBy = :userId " +
            "WHERE cf.id = :id")
    void updateStatusById(
            @Param("id") Long id,
            @Param("status") CustomFeeStatus status,
            @Param("userId") Long userId);

    @Modifying
    @Query("UPDATE CustomFee cf SET cf.studentsCharged = cf.studentsCharged + :count " +
            "WHERE cf.id = :id")
    void incrementStudentsCharged(@Param("id") Long id, @Param("count") Integer count);

    @Modifying
    @Query("UPDATE CustomFee cf SET cf.studentsPaid = cf.studentsPaid + 1, " +
            "cf.totalCollected = cf.totalCollected + cf.feeAmount " +
            "WHERE cf.id = :id")
    void incrementStudentsPaidAndTotalCollected(@Param("id") Long id);

    @Query("SELECT cf FROM CustomFee cf " +
            "WHERE cf.minimumAge <= :age AND (cf.maximumAge IS NULL OR cf.maximumAge >= :age) " +
            "AND cf.schoolPricing.id = :pricingId AND cf.status = 'ACTIVE' AND cf.isActive = true")
    List<CustomFee> findFeesApplicableToAge(
            @Param("pricingId") Long pricingId,
            @Param("age") Integer age);

}
