package com.genixo.education.search.dto.institution;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyTypeDto {
    private Long id;
    private String name;
    private String displayName;
    private Long propertyGroupTypeId;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
