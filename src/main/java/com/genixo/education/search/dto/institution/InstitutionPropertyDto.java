package com.genixo.education.search.dto.institution;

import com.genixo.education.search.enumaration.PropertyDataType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstitutionPropertyDto {
    private Long id;
    private String name;
    private String displayName;
    private String description;
    private PropertyDataType dataType;
    private Boolean isRequired;
    private Boolean isSearchable;
    private Boolean isFilterable;
    private Boolean showInCard;
    private Boolean showInProfile;
    private Integer sortOrder;
    private String options;
    private String defaultValue;

    // Validation rules
    private Double minValue;
    private Double maxValue;
    private Integer minLength;
    private Integer maxLength;
    private String regexPattern;

    private InstitutionTypeSummaryDto institutionType;
    private Boolean isActive;
    private LocalDateTime createdAt;
}