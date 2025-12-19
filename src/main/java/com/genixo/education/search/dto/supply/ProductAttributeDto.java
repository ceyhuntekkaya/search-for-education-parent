package com.genixo.education.search.dto.supply;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductAttributeDto {

    private Long id;
    private Long productId;
    private String productName;
    private String attributeName;
    private String attributeValue;
}

