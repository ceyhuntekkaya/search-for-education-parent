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
public class ProcurementReportDto {

    private String reportId;
    private LocalDateTime generatedAt;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private Long companyId;
    private String companyName;

    // Summary
    private BigDecimal totalSpending;
    private Long totalOrders;
    private Long totalRFQs;
    private Long totalQuotations;
    private Long totalSuppliers;

    // Orders by status
    private Long pendingOrders;
    private Long confirmedOrders;
    private Long deliveredOrders;
    private Long cancelledOrders;

    // Spending breakdown
    private List<SpendingByCategory> spendingByCategory;
    private List<SpendingBySupplier> spendingBySupplier;
    private List<SpendingByMonth> spendingByMonth;

    // Top items
    private List<TopProductDto> topProducts;
    private List<TopSupplierDto> topSuppliers;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SpendingByCategory {
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
    public static class SpendingBySupplier {
        private Long supplierId;
        private String supplierCompanyName;
        private BigDecimal amount;
        private Long orderCount;
        private Double percentage;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SpendingByMonth {
        private LocalDate month;
        private BigDecimal amount;
        private Long orderCount;
    }
}

