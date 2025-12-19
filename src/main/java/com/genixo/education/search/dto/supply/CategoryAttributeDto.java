package com.genixo.education.search.dto.supply;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryAttributeDto {

    private Long id;
    private Long categoryId;
    private String categoryName;
    private String attributeName;
    private String possibleValues;
    private Boolean isRequired;
    private Integer displayOrder;
}

