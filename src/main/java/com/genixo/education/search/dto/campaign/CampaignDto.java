package com.genixo.education.search.dto.campaign;

import com.genixo.education.search.enumaration.CampaignStatus;
import com.genixo.education.search.enumaration.CampaignType;
import com.genixo.education.search.enumaration.DiscountType;
import com.genixo.education.search.enumaration.TargetAudience;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// ========================= CAMPAIGN DTO =========================
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignDto {
    private Long id;
    private String title;
    private String slug;
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

    // Campaign status
    private CampaignStatus status;
    private Boolean isFeatured;
    private Boolean isPublic;
    private Boolean requiresApproval;

    // Usage limits
    private Integer usageLimit;
    private Integer usageCount;
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

    // Analytics
    private Long viewCount;
    private Long clickCount;
    private Long applicationCount;
    private Long conversionCount;
    private Double conversionRate;

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

    // Creator info
    private String createdByUserName;
    private String approvedByUserName;
    private LocalDateTime approvedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Calculated fields
    private Boolean isActive;
    private Boolean isExpired;
    private Integer daysRemaining;
    private String formattedDiscountAmount;
    private String displayDiscount;
    private String campaignPeriod;

    // Relationships
    private List<CampaignSchoolDto> campaignSchools;
    private List<CampaignContentDto> campaignContents;
}
