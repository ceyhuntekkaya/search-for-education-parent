package com.genixo.education.search.dto.pricing;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkPricingResultDto {
    private Boolean success;
    private Integer totalRecords;
    private Integer successfulOperations;
    private Integer failedOperations;
    private List<String> errors;
    private List<String> warnings;
    private String operationId;
    private LocalDateTime operationDate;
    private List<Long> createdPricingIds;
}