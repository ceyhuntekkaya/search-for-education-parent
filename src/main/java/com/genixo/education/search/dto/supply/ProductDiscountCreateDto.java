package com.genixo.education.search.dto.supply;

import com.genixo.education.search.enumaration.DiscountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDiscountCreateDto {

    @NotBlank(message = "Discount name is required")
    private String discountName;

    @NotNull(message = "Discount type is required")
    private DiscountType discountType;

    private BigDecimal discountValue;

    private Integer minQuantity;

    private Integer maxQuantity;

    private LocalDate startDate;

    private LocalDate endDate;

    @Builder.Default
    private Boolean isActive = true;
}

