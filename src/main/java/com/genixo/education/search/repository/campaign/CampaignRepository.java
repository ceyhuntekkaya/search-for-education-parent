package com.genixo.education.search.repository.campaign;

import com.genixo.education.search.dto.campaign.CampaignAnalyticsDto;
import com.genixo.education.search.entity.campaign.Campaign;
import com.genixo.education.search.enumaration.CampaignStatus;
import com.genixo.education.search.enumaration.CampaignType;
import com.genixo.education.search.enumaration.DiscountType;
import com.genixo.education.search.enumaration.TargetAudience;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    @Query("SELECT c FROM Campaign c WHERE c.isActive = true AND c.id = :id")
    Optional<Campaign> findByIdAndIsActiveTrue(@Param("id") Long id);

    @Query("SELECT c FROM Campaign c WHERE c.isActive = true AND LOWER(c.slug) = LOWER(:slug)")
    Optional<Campaign> findBySlugAndIsActiveTrue(@Param("slug") String slug);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Campaign c WHERE LOWER(c.promoCode) = LOWER(:promoCode) AND c.isActive = true")
    boolean existsByPromoCodeIgnoreCase(@Param("promoCode") String promoCode);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Campaign c WHERE c.slug = :slug AND c.isActive = true")
    boolean existsBySlug(@Param("slug") String slug);

    @Query("SELECT c FROM Campaign c WHERE c.isActive = true " +
            "AND LOWER(c.promoCode) = LOWER(:promoCode)")
    Optional<Campaign> findByPromoCodeIgnoreCaseAndIsActiveTrue(@Param("promoCode") String promoCode);

    // Active campaigns query
    @Query("SELECT c FROM Campaign c WHERE c.isActive = true " +
            "AND c.status = 'ACTIVE' " +
            "AND c.startDate <= :currentDate " +
            "AND c.endDate >= :currentDate " +
            "ORDER BY c.priority DESC, c.sortOrder ASC")
    List<Campaign> findActiveCampaigns(@Param("currentDate") LocalDate currentDate);

    // Public active campaigns
    @Query("SELECT c FROM Campaign c WHERE c.isActive = true " +
            "AND c.status = 'ACTIVE' " +
            "AND c.isPublic = true " +
            "AND c.startDate <= :currentDate " +
            "AND c.endDate >= :currentDate " +
            "ORDER BY c.isFeatured DESC, c.priority DESC, c.sortOrder ASC")
    List<Campaign> findPublicActiveCampaigns(@Param("currentDate") LocalDate currentDate);

    // Campaigns by school
    @Query("SELECT c FROM Campaign c " +
            "JOIN c.campaignSchools cs " +
            "WHERE c.isActive = true " +
            "AND cs.school.id = :schoolId " +
            "AND cs.status = 'ACTIVE' " +
            "AND cs.isActive = true " +
            "GROUP BY c.id " +
            "ORDER BY c.priority DESC, c.sortOrder ASC")
    List<Campaign> findBySchoolId(@Param("schoolId") Long schoolId);

    // Public campaigns by school
    @Query("SELECT DISTINCT c FROM Campaign c " +
            "JOIN c.campaignSchools cs " +
            "WHERE c.isActive = true " +
            "AND c.status = 'ACTIVE' " +
            "AND c.isPublic = true " +
            "AND c.startDate <= :currentDate " +
            "AND c.endDate >= :currentDate " +
            "AND cs.school.id = :schoolId " +
            "AND cs.status = 'ACTIVE' " +
            "AND cs.isActive = true " +
            "ORDER BY c.isFeatured DESC, c.priority DESC, c.sortOrder ASC")
    List<Campaign> findPublicCampaignsBySchool(@Param("schoolId") Long schoolId, @Param("currentDate") LocalDate currentDate);

    @Query("SELECT c FROM Campaign c WHERE c.id IN :ids AND c.isActive = true")
    List<Campaign> findByIdInAndIsActiveTrue(@Param("ids") List<Long> ids);

    // Complex search query
    @Query("SELECT DISTINCT c FROM Campaign c " +
            "LEFT JOIN c.campaignSchools cs " +
            "LEFT JOIN cs.school s " +
            "WHERE c.isActive = true " +
            "AND (:searchTerm IS NULL OR " +
            "    LOWER(c.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "    LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "    LOWER(c.shortDescription) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "AND (:campaignTypes IS NULL OR c.campaignType IN :campaignTypes) " +
            "AND (:discountTypes IS NULL OR c.discountType IN :discountTypes) " +
            "AND (:statuses IS NULL OR c.status IN :statuses) " +
            "AND (:startDateFrom IS NULL OR c.startDate >= :startDateFrom) " +
            "AND (:startDateTo IS NULL OR c.startDate <= :startDateTo) " +
            "AND (:endDateFrom IS NULL OR c.endDate >= :endDateFrom) " +
            "AND (:endDateTo IS NULL OR c.endDate <= :endDateTo) " +
            "AND (:isActive IS NULL OR c.isActive = :isActive) " +
            "AND (:isFeatured IS NULL OR c.isFeatured = :isFeatured) " +
            "AND (:isPublic IS NULL OR c.isPublic = :isPublic) " +
            "AND (:targetAudiences IS NULL OR c.targetAudience IN :targetAudiences) " +
            "AND (:minTargetAge IS NULL OR c.targetAgeMin IS NULL OR c.targetAgeMin >= :minTargetAge) " +
            "AND (:maxTargetAge IS NULL OR c.targetAgeMax IS NULL OR c.targetAgeMax <= :maxTargetAge) " +
            "AND (:schoolId IS NULL OR cs.school.id = :schoolId) " +
            "AND (:createdByUserId IS NULL OR c.createdByUser.id = :createdByUserId) " +
            "AND (:hasPromoCode IS NULL OR " +
            "    (:hasPromoCode = true AND c.promoCode IS NOT NULL) OR " +
            "    (:hasPromoCode = false AND c.promoCode IS NULL)) " +
            "AND (:minDiscountPercentage IS NULL OR c.discountPercentage IS NULL OR c.discountPercentage >= :minDiscountPercentage) " +
            "AND (:maxDiscountPercentage IS NULL OR c.discountPercentage IS NULL OR c.discountPercentage <= :maxDiscountPercentage) " +
            "AND (:minDiscountAmount IS NULL OR c.discountAmount IS NULL OR c.discountAmount >= :minDiscountAmount) " +
            "AND (:maxDiscountAmount IS NULL OR c.discountAmount IS NULL OR c.discountAmount <= :maxDiscountAmount) " +
            "AND (:minViewCount IS NULL OR c.viewCount >= :minViewCount) " +
            "AND (:minApplicationCount IS NULL OR c.applicationCount >= :minApplicationCount) " +
            "AND (:minConversionRate IS NULL OR c.conversionRate >= :minConversionRate) " +
            "AND (COALESCE(:accessibleSchoolIds, null) IS NULL OR cs.school.id IN :accessibleSchoolIds)")
    Page<Campaign> searchCampaigns(
            @Param("searchTerm") String searchTerm,
            @Param("campaignTypes") List<CampaignType> campaignTypes,
            @Param("discountTypes") List<DiscountType> discountTypes,
            @Param("statuses") List<CampaignStatus> statuses,
            @Param("startDateFrom") LocalDate startDateFrom,
            @Param("startDateTo") LocalDate startDateTo,
            @Param("endDateFrom") LocalDate endDateFrom,
            @Param("endDateTo") LocalDate endDateTo,
            @Param("isActive") Boolean isActive,
            @Param("isFeatured") Boolean isFeatured,
            @Param("isPublic") Boolean isPublic,
            @Param("targetAudiences") List<TargetAudience> targetAudiences,
            @Param("minTargetAge") Integer minTargetAge,
            @Param("maxTargetAge") Integer maxTargetAge,
            @Param("schoolId") Long schoolId,
            @Param("createdByUserId") Long createdByUserId,
            @Param("hasPromoCode") Boolean hasPromoCode,
            @Param("minDiscountPercentage") Double minDiscountPercentage,
            @Param("maxDiscountPercentage") Double maxDiscountPercentage,
            @Param("minDiscountAmount") BigDecimal minDiscountAmount,
            @Param("maxDiscountAmount") BigDecimal maxDiscountAmount,
            @Param("minViewCount") Long minViewCount,
            @Param("minApplicationCount") Long minApplicationCount,
            @Param("minConversionRate") Double minConversionRate,
            @Param("accessibleSchoolIds") List<Long> accessibleSchoolIds,
            Pageable pageable);

    // Analytics queries
/* ceyhun
    @Query("SELECT new com.genixo.education.search.dto.campaign.CampaignAnalyticsDto(" +
            "c.startDate, " +
            "c.endDate, " +
            "c.viewCount, " +
            "c.clickCount, " +
            "c.applicationCount, " +
            "c.conversionCount, " +
            "c.usageCount, " +
            "COALESCE(CAST((SELECT SUM(cu.finalAmount) FROM CampaignUsage cu WHERE cu.campaign.id = c.id) AS DOUBLE), 0.0), " +
            "COALESCE(CAST((SELECT SUM(cu.discountAmount) FROM CampaignUsage cu WHERE cu.campaign.id = c.id) AS DOUBLE), 0.0)) " +
            "FROM Campaign c " +
            "WHERE c.id = :campaignId AND c.isActive = true")
    CampaignAnalyticsDto getCampaignAnalytics(@Param("campaignId") Long campaignId);





    // Overall analytics for multiple campaigns
    @Query("SELECT new com.genixo.education.search.dto.campaign.CampaignAnalyticsDto(" +

            "CAST(COALESCE(SUM(c.viewCount), 0L) AS DOUBLE), " +
            "CAST(COALESCE(SUM(c.clickCount), 0L) AS DOUBLE), " +
            "CAST(COALESCE(SUM(c.applicationCount), 0L) AS DOUBLE), " +
            "CAST(COALESCE(SUM(c.conversionCount), 0L) AS DOUBLE), " +
            "CAST(COALESCE(SUM(c.usageCount), 0) AS DOUBLE), " +
            "CAST(COALESCE((SELECT SUM(cu.finalAmount) FROM CampaignUsage cu WHERE cu.campaign.id IN :campaignIds AND cu.usageDate BETWEEN :periodStart AND :periodEnd), 0) AS DOUBLE), " +
            "CAST(COALESCE((SELECT SUM(cu.discountAmount) FROM CampaignUsage cu WHERE cu.campaign.id IN :campaignIds AND cu.usageDate BETWEEN :periodStart AND :periodEnd), 0) AS DOUBLE)) " +
            "FROM Campaign c " +
            "WHERE c.id IN :campaignIds AND c.isActive = true " +
            "AND c.startDate <= :periodEnd AND c.endDate >= :periodStart")
    CampaignAnalyticsDto getOverallAnalytics(@Param("campaignIds") List<Long> campaignIds,
                                             @Param("periodStart") LocalDate periodStart,
                                             @Param("periodEnd") LocalDate periodEnd);
 */
    // Expiring campaigns
    @Query("SELECT c FROM Campaign c WHERE c.isActive = true " +
            "AND c.status = 'ACTIVE' " +
            "AND c.endDate BETWEEN :startDate AND :endDate " +
            "ORDER BY c.endDate ASC")
    List<Campaign> findExpiringCampaigns(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Performance queries
    @Query("SELECT c FROM Campaign c WHERE c.isActive = true " +
            "AND c.conversionRate >= :minConversionRate " +
            "ORDER BY c.conversionRate DESC, c.conversionCount DESC")
    List<Campaign> findTopPerformingCampaigns(@Param("minConversionRate") Double minConversionRate, Pageable pageable);

    @Modifying
    @Query("UPDATE Campaign c SET c.viewCount = c.viewCount + 1 WHERE c.id = :id")
    void incrementViewCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Campaign c SET c.clickCount = c.clickCount + 1 WHERE c.id = :id")
    void incrementClickCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Campaign c SET c.applicationCount = c.applicationCount + 1 WHERE c.id = :id")
    void incrementApplicationCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Campaign c SET c.conversionCount = c.conversionCount + 1 WHERE c.id = :id")
    void incrementConversionCount(@Param("id") Long id);
}
