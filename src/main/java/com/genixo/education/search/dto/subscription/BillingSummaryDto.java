package com.genixo.education.search.dto.subscription;

import com.genixo.education.search.enumaration.PaymentMethod;
import com.genixo.education.search.enumaration.PaymentStatus;
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
public class BillingSummaryDto {
    private Long subscriptionId;
    private String campusName;

    // Current period
    private LocalDateTime currentPeriodStart;
    private LocalDateTime currentPeriodEnd;
    private BigDecimal currentPeriodAmount;
    private PaymentStatus currentPeriodStatus;

    // Next billing
    private LocalDateTime nextBillingDate;
    private BigDecimal nextBillingAmount;
    private String nextBillingCurrency;

    // Payment history
    private List<PaymentSummaryDto> recentPayments;
    private BigDecimal totalPaid;
    private BigDecimal totalOutstanding;

    // Payment method
    private PaymentMethod preferredPaymentMethod;
    private String savedCardLastFour;
    private String savedCardBrand;
    private LocalDateTime savedCardExpiry;

    // Billing alerts
    private List<String> billingAlerts;
    private Boolean hasOverduePayments;
    private Integer overdueCount;
    private BigDecimal overdueAmount;
}