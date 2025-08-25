package com.genixo.education.search.repository.campaign;

import com.genixo.education.search.entity.campaign.CampaignSchool;
import com.genixo.education.search.enumaration.CampaignSchoolStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CampaignSchoolRepository extends JpaRepository<CampaignSchool, Long> {
    @Query("SELECT cs FROM CampaignSchool cs WHERE cs.campaign.id = :campaignId AND cs.school.id = :schoolId AND cs.isActive = true")
    Optional<CampaignSchool> findByCampaignIdAndSchoolIdAndIsActiveTrue(@Param("campaignId") Long campaignId,
                                                                        @Param("schoolId") Long schoolId);

    @Query("SELECT CASE WHEN COUNT(cs) > 0 THEN true ELSE false END " +
            "FROM CampaignSchool cs WHERE cs.campaign.id = :campaignId AND cs.school.id = :schoolId AND cs.isActive = true")
    boolean existsByCampaignIdAndSchoolIdAndIsActiveTrue(@Param("campaignId") Long campaignId, @Param("schoolId") Long schoolId);

    @Query("SELECT CASE WHEN COUNT(cs) > 0 THEN true ELSE false END " +
            "FROM CampaignSchool cs WHERE cs.campaign.id = :campaignId AND cs.school.id = :schoolId " +
            "AND cs.status = :status AND cs.isActive = true")
    boolean existsByCampaignIdAndSchoolIdAndStatusAndIsActiveTrue(@Param("campaignId") Long campaignId,
                                                                  @Param("schoolId") Long schoolId,
                                                                  @Param("status") CampaignSchoolStatus status);

    @Query("SELECT cs FROM CampaignSchool cs WHERE cs.campaign.id = :campaignId AND cs.isActive = true " +
            "ORDER BY cs.priority DESC, cs.school.name ASC")
    List<CampaignSchool> findByCampaignIdAndIsActiveTrue(@Param("campaignId") Long campaignId);

    @Query("SELECT cs FROM CampaignSchool cs WHERE cs.school.id = :schoolId AND cs.isActive = true " +
            "AND cs.status = 'ACTIVE' " +
            "ORDER BY cs.priority DESC, cs.campaign.startDate DESC")
    List<CampaignSchool> findBySchoolIdAndIsActiveTrueAndStatusActive(@Param("schoolId") Long schoolId);

    @Query("SELECT cs FROM CampaignSchool cs WHERE cs.campaign.id = :campaignId AND cs.school.id IN :schoolIds AND cs.isActive = true")
    List<CampaignSchool> findByCampaignIdAndSchoolIdIn(@Param("campaignId") Long campaignId, @Param("schoolIds") List<Long> schoolIds);

    @Query("SELECT COUNT(cs) FROM CampaignSchool cs WHERE cs.campaign.id = :campaignId AND cs.status = 'ACTIVE' AND cs.isActive = true")
    Long countActiveByCampaignId(@Param("campaignId") Long campaignId);

    @Query("SELECT cs FROM CampaignSchool cs WHERE cs.approvedBySchool = false AND cs.status = 'PENDING' AND cs.isActive = true " +
            "ORDER BY cs.assignedAt ASC")
    List<CampaignSchool> findPendingApprovals();
}
