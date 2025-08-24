package com.genixo.education.search.dto.campaign;

import com.genixo.education.search.enumaration.CampaignStatus;
import com.genixo.education.search.enumaration.CampaignType;
import com.genixo.education.search.enumaration.DiscountType;
import com.genixo.education.search.enumaration.TargetAudience;
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
public class CampaignSearchDto {
    private String searchTerm;
    private List<CampaignType> campaignTypes;
    private List<DiscountType> discountTypes;
    private List<CampaignStatus> statuses;
    private LocalDate startDateFrom;
    private LocalDate startDateTo;
    private LocalDate endDateFrom;
    private LocalDate endDateTo;
    private Boolean isActive;
    private Boolean isFeatured;
    private Boolean isPublic;
    private List<TargetAudience> targetAudiences;
    private Integer minTargetAge;
    private Integer maxTargetAge;
    private Long schoolId;
    private Long createdByUserId;
    private Boolean hasPromoCode;
    private Double minDiscountPercentage;
    private Double maxDiscountPercentage;
    private BigDecimal minDiscountAmount;
    private BigDecimal maxDiscountAmount;

    // Analytics filters
    private Long minViewCount;
    private Long minApplicationCount;
    private Double minConversionRate;

    // Sorting
    private String sortBy; // START_DATE, END_DATE, CREATED_DATE, VIEW_COUNT, CONVERSION_RATE
    private String sortDirection;

    // Pagination
    private Integer page;
    private Integer size;
}