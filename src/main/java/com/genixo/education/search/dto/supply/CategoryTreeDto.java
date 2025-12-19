package com.genixo.education.search.dto.supply;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryTreeDto {

    private Long id;
    private String name;
    private String description;
    private Long parentId;
    private Boolean isActive;
    private Integer displayOrder;
    private Integer subCategoryCount;
    private List<CategoryTreeDto> subCategories;
}

