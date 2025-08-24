package com.genixo.education.search.dto.institution;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignSummaryDto {
    private Long id;
    private String title;
    private String description;
    private String campaignType;
    private String discountType;
    private Double discountAmount;
    private Double discountPercentage;
    private String badgeText;
    private String badgeColor;
    private String thumbnailImageUrl;
    private String startDate;
    private String endDate;
    private Boolean isActive;
}