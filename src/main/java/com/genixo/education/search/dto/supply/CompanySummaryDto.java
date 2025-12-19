package com.genixo.education.search.dto.supply;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanySummaryDto {

    private BigDecimal totalSpending;
    private Long activeOrders;
    private Long pendingOrders;
    private Long completedOrders;
    private Long totalRFQs;
    private Long publishedRFQs;
    private Long totalQuotations;
    private Long acceptedQuotations;
    private Long totalSuppliers;
    private Long activeSuppliers;
}

