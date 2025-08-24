package com.genixo.education.search.dto.campaign;

import com.genixo.education.search.enumaration.CampaignStatus;
import com.genixo.education.search.enumaration.DiscountType;
import com.genixo.education.search.enumaration.TargetAudience;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignUpdateDto {
    private String title;
    private String description;
    private String shortDescription;
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

    // Campaign settings
    private CampaignStatus status;
    private Boolean isFeatured;
    private Boolean isPublic;

    // Usage limits
    private Integer usageLimit;
    private Integer perUserLimit;
    private Integer perSchoolLimit;

    // Target audience
    private TargetAudience targetAudience;
    private String targetGradeLevels;
    private Integer targetAgeMin;
    private Integer targetAgeMax;

    // Promotional content
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

    // Additional features
    private Integer freeTrialDays;
    private String installmentOptions;
    private Integer paymentDeadlineDays;
    private String refundPolicy;

    // Display
    private Integer priority;
    private Integer sortOrder;
}
