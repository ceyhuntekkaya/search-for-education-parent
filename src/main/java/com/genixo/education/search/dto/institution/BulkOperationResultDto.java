package com.genixo.education.search.dto.institution;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkOperationResultDto {
    private Boolean success;
    private Integer totalRecords;
    private Integer successfulOperations;
    private Integer failedOperations;
    private List<String> errors;
    private List<String> warnings;
    private String operationId;
    private LocalDateTime operationDate;
}