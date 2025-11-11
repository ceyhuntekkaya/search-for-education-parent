package com.genixo.education.search.dto.institution;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstitutionTypeListDto {
    private InstitutionTypeDto institutionTypeDto;
    private List<PropertyGroupTypeDto> propertyGroupTypeDtos;
}
