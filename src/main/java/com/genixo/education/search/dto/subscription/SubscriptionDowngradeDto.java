package com.genixo.education.search.dto.subscription;

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
public class SubscriptionDowngradeDto {
    private Long subscriptionId;
    private Long newPlanId;
    private LocalDateTime downgradeDate;
    private LocalDateTime effectiveDate;
    private String downgradeReason;
    private Boolean prorateRefund;
    private BigDecimal refundAmount;
    private String currency;

    // Data migration warnings
    private List<String> dataLossWarnings;
    private List<String> featureLossWarnings;
    private Boolean acknowledgeDataLoss;
}