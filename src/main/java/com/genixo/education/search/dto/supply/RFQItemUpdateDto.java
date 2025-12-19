package com.genixo.education.search.dto.supply;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RFQItemUpdateDto {

    private Long categoryId;
    private String itemName;
    private String specifications;
    private Integer quantity;
    private String unit;
}

