package com.genixo.education.search.dto.subscription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionStatisticsDto {



    public SubscriptionStatisticsDto(Long totalSubscriptions,
                                     Long activeSubscriptions,
                                     Long trialSubscriptions,
                                     Long canceledSubscriptions,
                                     Long pastDueSubscriptions,
                                     Integer totalRevenue,
                                     Integer averageRevenuePerUser) {
        this.totalSubscriptions = totalSubscriptions;
        this.activeSubscriptions = activeSubscriptions;
        this.trialSubscriptions = trialSubscriptions;
        this.canceledSubscriptions = canceledSubscriptions;
        this.expiredSubscriptions = pastDueSubscriptions; // mapping
        this.totalRevenue = BigDecimal.valueOf(totalRevenue);
        this.averageRevenuePerUser = BigDecimal.valueOf(averageRevenuePerUser);
    }



    private Long totalSubscriptions;
    private Long activeSubscriptions;
    private Long trialSubscriptions;
    private Long expiredSubscriptions;
    private Long canceledSubscriptions;

    // Revenue metrics
    private BigDecimal totalRevenue;
    private BigDecimal monthlyRecurringRevenue;
    private BigDecimal annualRecurringRevenue;
    private BigDecimal averageRevenuePerUser;

    // Growth metrics
    private Long newSubscriptionsThisMonth;
    private Long canceledSubscriptionsThisMonth;
    private Double churnRate;
    private Double growthRate;

    // Plan distribution
    private Long basicPlanSubscriptions;
    private Long premiumPlanSubscriptions;
    private Long enterprisePlanSubscriptions;

    // Payment metrics
    private Long totalPayments;
    private Long successfulPayments;
    private Long failedPayments;
    private Double paymentSuccessRate;

    // Usage metrics
    private Long totalSchoolsManaged;
    private Long totalUsersManaged;
    private Long totalAppointmentsCreated;
    private Double averageStorageUsed;
}