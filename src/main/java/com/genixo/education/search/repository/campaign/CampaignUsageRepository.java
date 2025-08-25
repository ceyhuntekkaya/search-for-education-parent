package com.genixo.education.search.repository.campaign;

import com.genixo.education.search.entity.campaign.CampaignUsage;
import com.genixo.education.search.enumaration.CampaignUsageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CampaignUsageRepository extends JpaRepository<CampaignUsage, Long> {
    @Query("SELECT cu FROM CampaignUsage cu WHERE cu.campaign.id = :campaignId AND cu.isActive = true " +
            "ORDER BY cu.usageDate DESC")
    Page<CampaignUsage> findByCampaignIdAndIsActiveTrue(@Param("campaignId") Long campaignId, Pageable pageable);

    @Query("SELECT cu FROM CampaignUsage cu WHERE cu.school.id = :schoolId AND cu.isActive = true " +
            "ORDER BY cu.usageDate DESC")
    Page<CampaignUsage> findBySchoolIdAndIsActiveTrue(@Param("schoolId") Long schoolId, Pageable pageable);

    @Query("SELECT cu FROM CampaignUsage cu WHERE cu.user.id = :userId AND cu.isActive = true " +
            "ORDER BY cu.usageDate DESC")
    Page<CampaignUsage> findByUserIdAndIsActiveTrue(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(cu) > 0 THEN true ELSE false END " +
            "FROM CampaignUsage cu WHERE cu.campaign.id = :campaignId AND cu.status IN :statuses")
    boolean existsByCampaignIdAndStatusIn(@Param("campaignId") Long campaignId, @Param("statuses") List<CampaignUsageStatus> statuses);

    @Query("SELECT CASE WHEN COUNT(cu) > 0 THEN true ELSE false END " +
            "FROM CampaignUsage cu WHERE cu.campaign.id = :campaignId AND cu.school.id = :schoolId AND cu.status IN :statuses")
    boolean existsByCampaignIdAndSchoolIdAndStatusIn(@Param("campaignId") Long campaignId,
                                                     @Param("schoolId") Long schoolId,
                                                     @Param("statuses") List<CampaignUsageStatus> statuses);

    @Query("SELECT COUNT(cu) FROM CampaignUsage cu WHERE cu.campaign.id = :campaignId AND cu.user.id = :userId " +
            "AND cu.status != :excludeStatus")
    long countByCampaignIdAndUserIdAndStatusNot(@Param("campaignId") Long campaignId,
                                                @Param("userId") Long userId,
                                                @Param("excludeStatus") CampaignUsageStatus excludeStatus);

    @Query("SELECT COUNT(cu) FROM CampaignUsage cu WHERE cu.campaign.id = :campaignId AND cu.school.id = :schoolId " +
            "AND cu.status != :excludeStatus")
    long countByCampaignIdAndSchoolIdAndStatusNot(@Param("campaignId") Long campaignId,
                                                  @Param("schoolId") Long schoolId,
                                                  @Param("excludeStatus") CampaignUsageStatus excludeStatus);

    @Query("SELECT cu FROM CampaignUsage cu WHERE cu.validationCode = :code AND cu.isValidated = false " +
            "AND cu.validationExpiresAt > :currentTime")
    Optional<CampaignUsage> findByValidationCodeAndNotExpired(@Param("code") String code,
                                                              @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT cu FROM CampaignUsage cu WHERE cu.followUpRequired = true AND cu.followUpCompleted = false " +
            "AND cu.followUpDate <= :date AND cu.isActive = true " +
            "ORDER BY cu.followUpDate ASC")
    List<CampaignUsage> findPendingFollowUps(@Param("date") java.time.LocalDate date);

    // Analytics queries
    @Query("SELECT COUNT(cu) FROM CampaignUsage cu WHERE cu.campaign.id = :campaignId " +
            "AND cu.usageDate BETWEEN :startDate AND :endDate")
    Long countByCampaignIdAndDateRange(@Param("campaignId") Long campaignId,
                                       @Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(cu.discountAmount) FROM CampaignUsage cu WHERE cu.campaign.id = :campaignId " +
            "AND cu.status IN ('VALIDATED', 'APPROVED', 'PROCESSED', 'COMPLETED')")
    java.math.BigDecimal getTotalDiscountByCampaignId(@Param("campaignId") Long campaignId);

    @Query("SELECT SUM(cu.finalAmount) FROM CampaignUsage cu WHERE cu.campaign.id = :campaignId " +
            "AND cu.status IN ('VALIDATED', 'APPROVED', 'PROCESSED', 'COMPLETED')")
    java.math.BigDecimal getTotalRevenueByCampaignId(@Param("campaignId") Long campaignId);

    @Query("SELECT COUNT(DISTINCT cu.user.id) FROM CampaignUsage cu WHERE cu.campaign.id = :campaignId")
    Long getUniqueUserCountByCampaignId(@Param("campaignId") Long campaignId);
}
