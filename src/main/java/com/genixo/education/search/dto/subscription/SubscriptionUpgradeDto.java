package com.genixo.education.search.dto.subscription;

import com.genixo.education.search.enumaration.PaymentMethod;
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
public class SubscriptionUpgradeDto {
    private Long subscriptionId;
    private Long newPlanId;
    private LocalDateTime upgradeDate;
    private LocalDateTime effectiveDate;
    private String upgradeReason;
    private Boolean prorateCharges;
    private BigDecimal upgradeAmount;
    private BigDecimal proratedAmount;
    private String currency;

    // Payment
    private PaymentMethod paymentMethod;
    private String paymentToken;
    private Boolean processImmediately;
}
