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
public class SpendingBySupplierReportDto {

    private String reportId;
    private LocalDateTime generatedAt;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private Long companyId;
    private String companyName;
    private BigDecimal totalSpending;

    private List<SupplierSpending> supplierSpendings;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SupplierSpending {
        private Long supplierId;
        private String supplierCompanyName;
        private String supplierEmail;
        private String supplierPhone;
        private BigDecimal amount;
        private Long orderCount;
        private Double percentage;
        private BigDecimal averageOrderValue;
        private BigDecimal averageRating;
        private Long totalRatings;
    }
}

