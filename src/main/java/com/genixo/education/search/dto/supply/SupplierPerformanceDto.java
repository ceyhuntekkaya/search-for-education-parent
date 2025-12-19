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
public class SupplierPerformanceDto {

    private BigDecimal averageRating;
    private Long totalRatings;
    private BigDecimal deliveryRating;
    private BigDecimal qualityRating;
    private BigDecimal communicationRating;
    private Integer onTimeDeliveryRate;
    private BigDecimal averageResponseTimeHours;
    private Long totalOrders;
    private Long completedOrders;
    private BigDecimal orderCompletionRate;
}

