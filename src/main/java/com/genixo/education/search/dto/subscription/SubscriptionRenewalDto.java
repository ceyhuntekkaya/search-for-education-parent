package com.genixo.education.search.dto.subscription;

import com.genixo.education.search.enumaration.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionRenewalDto {
    private Long subscriptionId;
    private LocalDateTime renewalDate;
    private LocalDateTime newEndDate;
    private BigDecimal renewalAmount;
    private String currency;
    private Boolean autoRenewed;
    private String renewalNotes;

    // Plan changes
    private Long newPlanId;
    private String planChangeReason;
    private BigDecimal planChangeAmount;

    // Payment info
    private Long paymentId;
    private PaymentStatus paymentStatus;
    private String paymentMethod;
}