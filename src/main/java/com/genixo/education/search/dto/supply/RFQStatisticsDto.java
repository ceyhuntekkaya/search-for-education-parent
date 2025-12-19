package com.genixo.education.search.dto.supply;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RFQStatisticsDto {

    private Long totalRFQs;
    private Long draftRFQs;
    private Long publishedRFQs;
    private Long closedRFQs;
    private Long totalQuotations;
    private Long acceptedQuotations;
    private Double quotationAcceptanceRate;
    private Long averageQuotationsPerRFQ;
}

