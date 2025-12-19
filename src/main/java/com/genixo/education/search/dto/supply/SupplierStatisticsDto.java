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
public class SupplierStatisticsDto {

    private Long totalProducts;
    private Long activeProducts;
    private Long totalOrders;
    private Long completedOrders;
    private Long pendingOrders;
    private BigDecimal totalSales;
    private BigDecimal averageOrderValue;
    private Long totalQuotations;
    private Long acceptedQuotations;
    private BigDecimal quotationAcceptanceRate;
    private Long totalRatings;
    private BigDecimal averageRating;
}

