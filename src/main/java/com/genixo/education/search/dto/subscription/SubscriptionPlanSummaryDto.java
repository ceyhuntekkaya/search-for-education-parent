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
public class SubscriptionPlanSummaryDto {
    private Long id;
    private String name;
    private String displayName;
    private BigDecimal price;
    private BillingPeriod billingPeriod;
    private Boolean isPopular;
    private Boolean hasAnalytics;
    private Boolean hasPrioritySupport;
    private Integer storageGb;
    private Integer subscriberCount;
}