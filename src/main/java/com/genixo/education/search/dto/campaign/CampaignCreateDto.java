package com.genixo.education.search.dto.campaign;

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
public class CampaignCreateDto {
    private String title;
    private String description;
    private String shortDescription;
    private CampaignType campaignType;
    private DiscountType discountType;

    // Discount values
    private BigDecimal discountAmount;
    private Double discountPercentage;
    private BigDecimal maxDiscountAmount;
    private BigDecimal minPurchaseAmount;

    // Campaign period
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate earlyBirdEndDate;
    private LocalDate registrationDeadline;

    // Enrollment specific dates
    private LocalDate enrollmentStartDate;
    private LocalDate enrollmentEndDate;
    private String academicYear;

    // Campaign settings
    private Boolean isFeatured;
    private Boolean isPublic;
    private Boolean requiresApproval;

    // Usage limits
    private Integer usageLimit;
    private Integer perUserLimit;
    private Integer perSchoolLimit;

    // Target audience
    private TargetAudience targetAudience;
    private String targetGradeLevels;
    private Integer targetAgeMin;
    private Integer targetAgeMax;
    private Boolean targetNewStudentsOnly;
    private Boolean targetSiblingDiscount;

    // Promotional content
    private String promoCode;
    private String bannerImageUrl;
    private String thumbnailImageUrl;
    private String videoUrl;
    private String ctaText;
    private String ctaUrl;
    private String badgeText;
    private String badgeColor;

    // Terms and conditions
    private String termsAndConditions;
    private String finePrint;
    private String exclusions;

    // SEO
    private String metaTitle;
    private String metaDescription;
    private String metaKeywords;

    // Additional features
    private Integer freeTrialDays;
    private String installmentOptions;
    private Integer paymentDeadlineDays;
    private String refundPolicy;
    private String freeServices;
    private String bonusFeatures;
    private String giftItems;

    // Display and priority
    private Integer priority;
    private Integer sortOrder;

    // Creator
    private Long createdByUserId;

    // School assignments
    private List<Long> schoolIds;
}