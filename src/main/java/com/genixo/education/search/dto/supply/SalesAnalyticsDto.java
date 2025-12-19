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
public class SalesAnalyticsDto {

    private BigDecimal totalSales;
    private BigDecimal monthlySales;
    private BigDecimal averageOrderValue;
    private List<SalesDataPoint> salesByPeriod;
    private List<SalesByCategory> salesByCategory;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SalesDataPoint {
        private LocalDate period;
        private BigDecimal amount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SalesByCategory {
        private Long categoryId;
        private String categoryName;
        private BigDecimal amount;
        private Long orderCount;
    }
}

