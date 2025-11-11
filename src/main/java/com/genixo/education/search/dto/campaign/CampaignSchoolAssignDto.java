package com.genixo.education.search.dto.campaign;

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
public class CampaignSchoolAssignDto {
    private Long campaignId;
    private List<Long> schoolIds;
    private Long assignedByUserId;

    // Optional customizations
    private BigDecimal customDiscountAmount;
    private Double customDiscountPercentage;
    private Integer customUsageLimit;
    private LocalDate customStartDate;
    private LocalDate customEndDate;
    private String customTerms;

    // Display settings
    private Integer priority;
    private Boolean isFeaturedOnSchool;
    private Boolean showOnHomepage;
    private Boolean showOnPricingPage;
}