package com.genixo.education.search.dto.supply;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RFQItemCreateDto {

    private Long categoryId;

    @NotBlank(message = "Item name is required")
    private String itemName;

    private String specifications;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    private String unit; // "adet", "kg", "metre"
}

