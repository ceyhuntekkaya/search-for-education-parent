package com.genixo.education.search.dto.subscription;

import com.genixo.education.search.enumaration.BillingPeriod;
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
public class SubscriptionSearchDto {
    private String searchTerm;
    private List<SubscriptionStatus> statuses;
    private Long subscriptionPlanId;
    private BillingPeriod billingPeriod;
    private Boolean autoRenew;
    private LocalDateTime startDateAfter;
    private LocalDateTime startDateBefore;
    private LocalDateTime endDateAfter;
    private LocalDateTime endDateBefore;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private String currency;

    // Pagination
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}