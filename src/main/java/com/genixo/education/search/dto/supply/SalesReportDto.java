package com.genixo.education.search.dto.supply;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesReportDto {

    private String reportId;
    private LocalDateTime generatedAt;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private Long supplierId;
    private String supplierCompanyName;

    // Summary
    private BigDecimal totalSales;
    private Long totalOrders;
    private Long totalQuotations;
    private Long totalCustomers;

    // Orders by status
    private Long pendingOrders;
    private Long confirmedOrders;
    private Long deliveredOrders;
    private Long cancelledOrders;

    // Sales breakdown
    private List<SalesByCategory> salesByCategory;
    private List<SalesByCustomer> salesByCustomer;
    private List<SalesByMonth> salesByMonth;

    // Top items
    private List<TopProductDto> topProducts;
    private List<TopCustomerDto> topCustomers;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SalesByCategory {
        private Long categoryId;
        private String categoryName;
        private BigDecimal amount;
        private Long orderCount;
        private Double percentage;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SalesByCustomer {
        private Long companyId;
        private String companyName;
        private BigDecimal amount;
        private Long orderCount;
        private Double percentage;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SalesByMonth {
        private LocalDate month;
        private BigDecimal amount;
        private Long orderCount;
    }
}

