package com.genixo.education.search.dto.subscription;

import com.genixo.education.search.dto.institution.CampusSummaryDto;
import com.genixo.education.search.enumaration.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionDto {
    private Long id;
    private SubscriptionStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime trialEndDate;
    private LocalDateTime nextBillingDate;
    private BigDecimal price;
    private String currency;
    private BigDecimal discountAmount;
    private Double discountPercentage;
    private String couponCode;
    private Boolean autoRenew;
    private LocalDateTime canceledAt;
    private String cancellationReason;
    private LocalDateTime gracePeriodEnd;

    // Usage tracking
    private Integer currentSchoolsCount;
    private Integer currentUsersCount;
    private Integer currentMonthAppointments;
    private Integer currentGalleryItems;
    private Integer currentMonthPosts;
    private Long storageUsedMb;

    // Billing information
    private String billingName;
    private String billingEmail;
    private String billingPhone;
    private String billingAddress;
    private String taxNumber;
    private String taxOffice;

    // Relationships
    private CampusSummaryDto campus;
    private SubscriptionPlanSummaryDto subscriptionPlan;
    private List<PaymentSummaryDto> recentPayments;
    private List<InvoiceSummaryDto> recentInvoices;

    private Boolean isActive;
    private LocalDateTime createdAt;
}