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
public class SpendingByCategoryReportDto {

    private String reportId;
    private LocalDateTime generatedAt;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private Long companyId;
    private String companyName;
    private BigDecimal totalSpending;

    private List<CategorySpending> categorySpendings;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategorySpending {
        private Long categoryId;
        private String categoryName;
        private BigDecimal amount;
        private Long orderCount;
        private Long productCount;
        private Double percentage;
        private BigDecimal averageOrderValue;
    }
}

