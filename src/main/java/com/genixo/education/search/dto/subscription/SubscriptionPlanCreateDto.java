package com.genixo.education.search.dto.subscription;

import com.genixo.education.search.enumaration.BillingPeriod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlanCreateDto {
    private String name;
    private String displayName;
    private String description;
    private BigDecimal price;
    private BillingPeriod billingPeriod;
    private Integer trialDays;

    // Limits
    private Integer maxSchools;
    private Integer maxUsers;
    private Integer maxAppointmentsPerMonth;
    private Integer maxGalleryItems;
    private Integer maxPostsPerMonth;

    // Features
    private Boolean hasAnalytics;
    private Boolean hasCustomDomain;
    private Boolean hasApiAccess;
    private Boolean hasPrioritySupport;
    private Boolean hasWhiteLabel;
    private Integer storageGb;

    // Display
    private Boolean isPopular;
    private Integer sortOrder;
    private Boolean isVisible;

    // Pricing and features (JSON)
    private String pricingTiers;
    private String features;
}