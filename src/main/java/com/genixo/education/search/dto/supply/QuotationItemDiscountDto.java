package com.genixo.education.search.dto.supply;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuotationItemDiscountDto {

    @NotNull(message = "Discount amount is required")
    private BigDecimal discountAmount;
}

