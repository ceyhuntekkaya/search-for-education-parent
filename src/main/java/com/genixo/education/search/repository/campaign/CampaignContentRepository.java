package com.genixo.education.search.repository.campaign;

import com.genixo.education.search.entity.campaign.CampaignContent;
import com.genixo.education.search.enumaration.CampaignContentType;
import com.genixo.education.search.enumaration.ContentApprovalStatus;
import com.genixo.education.search.enumaration.ContentUsageContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignContentRepository extends JpaRepository<CampaignContent, Long> {
    @Query("SELECT cc FROM CampaignContent cc WHERE cc.campaign.id = :campaignId AND cc.isActive = true " +
            "ORDER BY cc.isPrimary DESC, cc.sortOrder ASC, cc.createdAt DESC")
    List<CampaignContent> findByCampaignIdAndIsActiveTrue(@Param("campaignId") Long campaignId);

    @Query("SELECT cc FROM CampaignContent cc WHERE cc.campaign.id = :campaignId " +
            "AND cc.contentType = :contentType AND cc.isActive = true " +
            "ORDER BY cc.isPrimary DESC, cc.sortOrder ASC")
    List<CampaignContent> findByCampaignIdAndContentTypeAndIsActiveTrue(@Param("campaignId") Long campaignId,
                                                                        @Param("contentType") CampaignContentType contentType);

    @Query("SELECT cc FROM CampaignContent cc WHERE cc.campaign.id = :campaignId " +
            "AND cc.usageContext = :usageContext AND cc.isActive = true " +
            "ORDER BY cc.isPrimary DESC, cc.sortOrder ASC")
    List<CampaignContent> findByCampaignIdAndUsageContextAndIsActiveTrue(@Param("campaignId") Long campaignId,
                                                                         @Param("usageContext") ContentUsageContext usageContext);

    @Query("SELECT cc FROM CampaignContent cc WHERE cc.campaign.id = :campaignId " +
            "AND cc.isPrimary = true AND cc.isActive = true " +
            "ORDER BY cc.sortOrder ASC")
    List<CampaignContent> findPrimaryContentByCampaignId(@Param("campaignId") Long campaignId);

    @Query("SELECT cc FROM CampaignContent cc WHERE cc.approvalStatus = :status AND cc.isActive = true " +
            "ORDER BY cc.createdAt ASC")
    List<CampaignContent> findByApprovalStatusAndIsActiveTrue(@Param("status") ContentApprovalStatus status);

    @Query("SELECT cc FROM CampaignContent cc WHERE cc.campaign.id = :campaignId " +
            "AND cc.languageCode = :languageCode AND cc.isActive = true " +
            "ORDER BY cc.isPrimary DESC, cc.sortOrder ASC")
    List<CampaignContent> findByCampaignIdAndLanguageCodeAndIsActiveTrue(@Param("campaignId") Long campaignId,
                                                                         @Param("languageCode") String languageCode);

    @Query("SELECT cc FROM CampaignContent cc WHERE cc.campaign.id = :campaignId " +
            "AND cc.isTestVariant = :isTestVariant AND cc.isActive = true " +
            "ORDER BY cc.sortOrder ASC")
    List<CampaignContent> findByCampaignIdAndIsTestVariantAndIsActiveTrue(@Param("campaignId") Long campaignId,
                                                                          @Param("isTestVariant") Boolean isTestVariant);

    @Query("UPDATE CampaignContent cc SET cc.viewCount = cc.viewCount + 1 WHERE cc.id = :id")
    @org.springframework.data.jpa.repository.Modifying
    void incrementViewCount(@Param("id") Long id);

    @Query("UPDATE CampaignContent cc SET cc.clickCount = cc.clickCount + 1 WHERE cc.id = :id")
    @org.springframework.data.jpa.repository.Modifying
    void incrementClickCount(@Param("id") Long id);

    @Query("UPDATE CampaignContent cc SET cc.downloadCount = cc.downloadCount + 1 WHERE cc.id = :id")
    @org.springframework.data.jpa.repository.Modifying
    void incrementDownloadCount(@Param("id") Long id);

    @Query("UPDATE CampaignContent cc SET cc.shareCount = cc.shareCount + 1 WHERE cc.id = :id")
    @org.springframework.data.jpa.repository.Modifying
    void incrementShareCount(@Param("id") Long id);
}
