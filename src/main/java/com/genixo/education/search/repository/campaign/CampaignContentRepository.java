package com.genixo.education.search.repository.campaign;

import com.genixo.education.search.entity.campaign.CampaignContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignContentRepository extends JpaRepository<CampaignContent, Long> {
}
