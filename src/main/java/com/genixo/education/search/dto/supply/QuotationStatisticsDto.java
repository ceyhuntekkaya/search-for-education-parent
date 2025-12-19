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
public class QuotationStatisticsDto {

    private Long totalQuotations;
    private Long draftQuotations;
    private Long submittedQuotations;
    private Long acceptedQuotations;
    private Long rejectedQuotations;
    private BigDecimal acceptanceRate;
    private BigDecimal averageQuotationValue;
    private Long averageResponseTimeDays;
}

