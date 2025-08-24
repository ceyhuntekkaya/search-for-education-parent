package com.genixo.education.search.dto.campaign;

import com.genixo.education.search.enumaration.CampaignSchoolStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignSchoolDto {
    private Long id;
    private Long campaignId;
    private String campaignTitle;
    private Long schoolId;
    private String schoolName;
    private String campusName;
    private String assignedByUserName;
    private LocalDateTime assignedAt;
    private CampaignSchoolStatus status;

    // School-specific customizations
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

    // Usage tracking
    private Integer usageCount;
    private Integer applicationCount;
    private Integer conversionCount;
    private BigDecimal revenueGenerated;

    // Performance metrics
    private Long viewCount;
    private Long clickCount;
    private Long inquiryCount;
    private Long appointmentCount;

    // Approval
    private Boolean approvedBySchool;
    private String approvedBySchoolUserName;
    private LocalDateTime approvedBySchoolAt;
    private String schoolNotes;

    // Calculated fields
    private String effectiveDiscount;
    private String effectivePeriod;
    private Double performanceScore;
}
