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
public class SupplierSummaryDto {

    private BigDecimal totalSales;
    private Long activeQuotations;
    private Long pendingQuotations;
    private Long submittedQuotations;
    private Long acceptedQuotations;
    private Long totalOrders;
    private Long pendingOrders;
    private Long completedOrders;
    private Long totalProducts;
    private Long activeProducts;
    private BigDecimal averageRating;
    private Long totalRatings;
}

