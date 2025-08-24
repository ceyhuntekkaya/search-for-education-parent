package com.genixo.education.search.dto.subscription;


import com.genixo.education.search.enumaration.SubscriptionStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Filter criteria for subscription searches")
public class SubscriptionFilterDto {

    @Schema(description = "Filter by subscription status", example = "ACTIVE")
    private SubscriptionStatus status;

    @Schema(description = "Filter by subscription plan ID", example = "1")
    private Long planId;

    @Schema(description = "Filter by campus name (partial match)", example = "Bahçeşehir")
    private String campusName;

    @Schema(description = "Filter by brand ID", example = "1")
    private Long brandId;

    @Schema(description = "Filter subscriptions created after this date")
    private LocalDateTime startDate;

    @Schema(description = "Filter subscriptions created before this date")
    private LocalDateTime endDate;

    @Schema(description = "Filter by auto renewal status", example = "true")
    private Boolean autoRenew;

    @Schema(description = "Filter by minimum price", example = "100.00")
    private BigDecimal minPrice;

    @Schema(description = "Filter by maximum price", example = "1000.00")
    private BigDecimal maxPrice;

    @Schema(description = "Filter by currency code", example = "TRY")
    private String currency;

    @Schema(description = "Filter subscriptions with trial period")
    private Boolean hasTrial;

    @Schema(description = "Filter subscriptions ending soon (days)", example = "30")
    private Integer endingInDays;

    @Schema(description = "Filter subscriptions that are past due")
    private Boolean isPastDue;

    @Schema(description = "Filter subscriptions that are suspended")
    private Boolean isSuspended;

    @Schema(description = "Filter by billing city", example = "İstanbul")
    private String billingCity;

    @Schema(description = "Filter by tax number (partial match)")
    private String taxNumber;

    @Schema(description = "Filter subscriptions with failed payments")
    private Boolean hasFailedPayments;

    @Schema(description = "Filter by next billing date range start")
    private LocalDateTime nextBillingDateStart;

    @Schema(description = "Filter by next billing date range end")
    private LocalDateTime nextBillingDateEnd;

    @Schema(description = "Filter by trial end date range start")
    private LocalDateTime trialEndDateStart;

    @Schema(description = "Filter by trial end date range end")
    private LocalDateTime trialEndDateEnd;

    @Schema(description = "Filter subscriptions created by specific user ID")
    private Long createdByUserId;

    @Schema(description = "Filter subscriptions updated by specific user ID")
    private Long updatedByUserId;

    @Schema(description = "Search in billing name and email", example = "john@example.com")
    private String searchTerm;

    @Schema(description = "Filter by subscription plan features (JSON path)", example = "hasAnalytics")
    private String planFeature;

    @Schema(description = "Filter by usage percentage threshold (0-100)", example = "80")
    private Integer usageThreshold;

    @Schema(description = "Filter subscriptions over usage limit")
    private Boolean isOverUsageLimit;

    @Schema(description = "Filter by campus subscription status")
    private Boolean campusIsSubscribed;

    @Schema(description = "Include only subscriptions with recent activity")
    private Boolean hasRecentActivity;

    @Schema(description = "Filter by payment method", example = "CREDIT_CARD")
    private String lastPaymentMethod;

    @Schema(description = "Filter subscriptions with pending invoices")
    private Boolean hasPendingInvoices;

    @Schema(description = "Filter subscriptions eligible for upgrade")
    private Boolean eligibleForUpgrade;

    @Schema(description = "Filter subscriptions at risk of churn")
    private Boolean atRiskOfChurn;
}