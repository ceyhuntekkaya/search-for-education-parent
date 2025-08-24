package com.genixo.education.search.dto.institution;

import com.genixo.education.search.dto.campaign.CampaignDto;
import com.genixo.education.search.dto.pricing.SchoolPricingDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolDetailDto {
    private SchoolDto school;
    private CampusDto campus;
    private BrandDto brand;
    private List<InstitutionPropertyValueDto> allProperties;
    private List<SchoolPricingDto> pricings;
    private List<CampaignDto> activeCampaigns;
    private SchoolStatisticsDto statistics;
}