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
public class CategoryAttributeCreateDto {

    @NotBlank(message = "Attribute name is required")
    private String attributeName;

    private String possibleValues; // JSON string: ["Value1", "Value2"]

    @Builder.Default
    private Boolean isRequired = false;

    private Integer displayOrder;
}

