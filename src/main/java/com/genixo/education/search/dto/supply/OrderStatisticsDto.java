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
public class OrderStatisticsDto {

    private Long totalOrders;
    private Long pendingOrders;
    private Long confirmedOrders;
    private Long preparingOrders;
    private Long shippedOrders;
    private Long deliveredOrders;
    private Long cancelledOrders;
    private BigDecimal totalSpending;
    private BigDecimal averageOrderValue;
    private Long averageDeliveryDays;
}

