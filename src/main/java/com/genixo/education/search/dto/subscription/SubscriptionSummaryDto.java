package com.genixo.education.search.dto.subscription;

import com.genixo.education.search.enumaration.SubscriptionStatus;
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
public class SubscriptionSummaryDto {
    private Long id;
    private String campusName;
    private String planName;
    private SubscriptionStatus status;
    private BigDecimal price;
    private String currency;
    private LocalDateTime nextBillingDate;
    private LocalDateTime endDate;
    private Boolean autoRenew;
    private Integer daysRemaining;
    private Double usagePercentage;
}