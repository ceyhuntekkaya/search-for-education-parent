package com.genixo.education.search.dto.campaign;

import com.genixo.education.search.enumaration.CampaignType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignAnalyticsDto {
    private Long campaignId;
    private String campaignTitle;
    private CampaignType campaignType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalViews;
    private Integer totalClicks;
    private Integer totalApplications;
    private Integer totalConversions;
    private Integer totalUsages;
    private Integer totalRevenueGenerated;
    private Integer totalDiscountGiven;
    private Long uniqueUsers;
    private Long totalSchools;
    private Integer schoolUsageCount;


}