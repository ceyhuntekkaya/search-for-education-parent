package com.genixo.education.search.dto.institution;

import com.genixo.education.search.enumaration.PropertyDataType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstitutionPropertyValueDto {
    private Long id;
    private Long propertyId;
    private String propertyName;
    private String propertyDisplayName;
    private PropertyDataType dataType;
    private Boolean showInCard;
    private Boolean showInProfile;

    // Values
    private String textValue;
    private Double numberValue;
    private Boolean booleanValue;
    private String dateValue;
    private String datetimeValue;
    private String jsonValue;
    private String fileUrl;
    private String fileName;

    // Display value (formatted for UI)
    private String displayValue;
    private String formattedValue;
}