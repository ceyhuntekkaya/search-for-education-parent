package com.genixo.education.search.dto.institution;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstitutionComparisonDto {
    private List<SchoolDto> schools;
    private List<String> comparisonCategories;
    private Map<String, Map<Long, Object>> comparisonData;
    private List<String> recommendations;
}