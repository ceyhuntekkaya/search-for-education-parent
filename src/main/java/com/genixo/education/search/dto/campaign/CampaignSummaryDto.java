package com.genixo.education.search.dto.campaign;

import com.genixo.education.search.enumaration.CampaignStatus;
import com.genixo.education.search.enumaration.CampaignType;
import com.genixo.education.search.enumaration.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignSummaryDto {
    private Long id;
    private String title;
    private String shortDescription;
    private CampaignType campaignType;
    private DiscountType discountType;
    private String displayDiscount;
    private LocalDate startDate;
    private LocalDate endDate;
    private CampaignStatus status;
    private String badgeText;
    private String badgeColor;
    private String thumbnailImageUrl;
    private Boolean isActive;
    private Boolean isExpired;
    private Integer daysRemaining;
    private Long schoolCount;
    private Long applicationCount;
    private Double conversionRate;
}
