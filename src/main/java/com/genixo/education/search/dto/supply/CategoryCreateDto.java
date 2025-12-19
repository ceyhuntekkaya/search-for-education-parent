package com.genixo.education.search.dto.supply;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryCreateDto {

    @NotBlank(message = "Category name is required")
    private String name;

    private String description;

    private Long parentId;

    @Builder.Default
    private Boolean isActive = true;

    private Integer displayOrder;
}

