package com.genixo.education.search.dto.location;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkLocationImportDto {
    private String fileUrl;
    private String fileType; // CSV, EXCEL
    private Boolean validateOnly;
    private Boolean overwriteExisting;
    private String mappingConfiguration; // JSON mapping for columns
}