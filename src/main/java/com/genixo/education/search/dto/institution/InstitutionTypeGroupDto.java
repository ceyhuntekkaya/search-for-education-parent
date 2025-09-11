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
public class InstitutionTypeGroupDto {

    private Long id;
    private String name;
    private String displayName;
    private String description;
    private String iconUrl;
    private String colorCode;
    private Integer sortOrder;
    private String defaultProperties;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
