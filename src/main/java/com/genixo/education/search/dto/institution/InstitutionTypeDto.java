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
public class InstitutionTypeDto {
    private Long id;
    private String name;
    private String displayName;
    private String description;
    private String iconUrl;
    private String colorCode;
    private Integer sortOrder;
    private String defaultProperties;
    private List<InstitutionPropertyDto> properties;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private String groupName;
    private Long groupId;

}