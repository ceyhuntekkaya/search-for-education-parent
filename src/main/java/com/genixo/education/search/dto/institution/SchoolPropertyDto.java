package com.genixo.education.search.dto.institution;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolPropertyDto {
    private Long schoolId;
    private Long propertyTypeId;
    private Long institutionPropertyValueId;
    private Long institutionPropertyId;
    private String name;
    private String displayName;
    private Long propertyGroupTypeId;
    private Integer sortOrder;
    private String groupName;
    private String groupDisplayName;
    private Long institutionTypeId;
    private String institutionTypeName;
    private Integer groupSortOrder;
}
