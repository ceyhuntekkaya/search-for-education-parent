package com.genixo.education.search.dto.institution;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstitutionPropertyValueSetDto {
    private Long propertyId;
    private String textValue;
    private Double numberValue;
    private Boolean booleanValue;
    private String dateValue;
    private String datetimeValue;
    private String jsonValue;
    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private String mimeType;
}