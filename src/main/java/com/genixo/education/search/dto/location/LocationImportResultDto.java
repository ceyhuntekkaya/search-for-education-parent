package com.genixo.education.search.dto.location;

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
public class LocationImportResultDto {
    private Boolean success;
    private Integer totalRecords;
    private Integer successfulImports;
    private Integer failedImports;
    private Integer skippedRecords;
    private List<String> errors;
    private List<String> warnings;
    private String importId;
    private LocalDateTime importDate;
}