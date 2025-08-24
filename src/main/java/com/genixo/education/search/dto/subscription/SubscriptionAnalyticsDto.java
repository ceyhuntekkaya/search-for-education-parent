package com.genixo.education.search.dto.subscription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionAnalyticsDto {
    private Long subscriptionId;
    private Long totalPayments;
    private BigDecimal totalAmountPaid;
    private BigDecimal averagePaymentAmount;
    private Double paymentSuccessRate;
    private Long failedPaymentCount;
    private Double schoolsUsagePercentage;
    private Double usersUsagePercentage;
    private Double appointmentsUsagePercentage;
    private Double storageUsagePercentage;
    private Long daysSinceStart;
    private Long daysUntilRenewal;




}
