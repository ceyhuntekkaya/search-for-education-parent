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
public class QuotationPerformanceReportDto {

    private String reportId;
    private LocalDateTime generatedAt;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private Long supplierId;
    private String supplierCompanyName;

    // Summary
    private Long totalQuotations;
    private Long draftQuotations;
    private Long submittedQuotations;
    private Long acceptedQuotations;
    private Long rejectedQuotations;
    private Long expiredQuotations;

    // Performance metrics
    private BigDecimal acceptanceRate;
    private BigDecimal averageQuotationValue;
    private BigDecimal averageResponseTimeHours;
    private Long averageResponseTimeDays;

    // Quotations by status
    private List<QuotationByStatus> quotationsByStatus;

    // Performance by month
    private List<PerformanceByMonth> performanceByMonth;

    // Top RFQs
    private List<TopRFQ> topRFQs;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QuotationByStatus {
        private String status;
        private Long count;
        private BigDecimal totalValue;
        private Double percentage;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PerformanceByMonth {
        private LocalDate month;
        private Long totalQuotations;
        private Long acceptedQuotations;
        private BigDecimal acceptanceRate;
        private BigDecimal averageValue;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TopRFQ {
        private Long rfqId;
        private String rfqTitle;
        private Long quotationCount;
        private Boolean isAccepted;
        private BigDecimal quotationValue;
    }
}

