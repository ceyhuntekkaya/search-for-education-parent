package com.genixo.education.search.dto.institution;


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
public class PropertyGroupTypeDto {
    private Long id;
    private String name;
    private String displayName;
    private Long institutionTypeId;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private List<PropertyTypeDto> propertyTypes;
    private Boolean isMultiple;
}
