package com.genixo.education.search.dto.supply;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryUpdateDto {

    private String name;

    private String description;

    private Long parentId;

    private Boolean isActive;

    private Integer displayOrder;
}

