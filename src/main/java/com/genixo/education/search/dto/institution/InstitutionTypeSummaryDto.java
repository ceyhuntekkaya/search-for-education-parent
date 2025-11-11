package com.genixo.education.search.dto.institution;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstitutionTypeSummaryDto {
    private Long id;
    private String name;
    private String displayName;
    private String iconUrl;
    private String colorCode;
    private Long schoolCount;
}