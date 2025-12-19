package com.genixo.education.search.dto.supply;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpendingAnalyticsDto {

    private BigDecimal totalSpending;
    private BigDecimal monthlySpending;
    private BigDecimal averageOrderValue;
    private List<SpendingDataPoint> spendingByPeriod;
    private List<SpendingByCategory> spendingByCategory;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SpendingDataPoint {
        private LocalDate period;
        private BigDecimal amount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SpendingByCategory {
        private Long categoryId;
        private String categoryName;
        private BigDecimal amount;
        private Long orderCount;
    }
}

