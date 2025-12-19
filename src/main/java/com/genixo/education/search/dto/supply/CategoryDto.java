package com.genixo.education.search.dto.supply;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {

    private Long id;
    private String name;
    private String description;
    private Long parentId;
    private String parentName;
    private Boolean isActive;
    private Integer displayOrder;
    private LocalDateTime createdAt;
    private Integer subCategoryCount;
    private List<CategoryDto> subCategories;
}

