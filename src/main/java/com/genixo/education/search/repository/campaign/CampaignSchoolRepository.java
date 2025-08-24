package com.genixo.education.search.repository.campaign;

import com.genixo.education.search.entity.campaign.CampaignSchool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignSchoolRepository extends JpaRepository<CampaignSchool, Long> {
}
