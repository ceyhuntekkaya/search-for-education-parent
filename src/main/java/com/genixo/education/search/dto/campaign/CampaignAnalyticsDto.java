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
    private LocalDate startDate;
    private LocalDate endDate;
    private Long totalViews;
    private Long totalClicks;
    private Long totalApplications;
    private Long totalConversions;
    private Long totalUsages;
    private Integer totalRevenueGenerated;
    private Integer totalDiscountGiven;

}