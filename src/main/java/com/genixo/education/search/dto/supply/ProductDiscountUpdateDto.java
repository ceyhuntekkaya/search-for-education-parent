package com.genixo.education.search.dto.supply;

import com.genixo.education.search.enumaration.DiscountType;
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
public class ProductDiscountUpdateDto {

    private String discountName;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private Integer minQuantity;
    private Integer maxQuantity;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
}

