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
public class ProductAttributeCreateDto {

    @NotBlank(message = "Attribute name is required")
    private String attributeName;

    @NotBlank(message = "Attribute value is required")
    private String attributeValue;
}

