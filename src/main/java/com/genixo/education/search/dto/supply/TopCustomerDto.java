package com.genixo.education.search.dto.supply;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopCustomerDto {

    private Long companyId;
    private String companyName;
    private BigDecimal totalSpending;
    private Long orderCount;
    private BigDecimal averageOrderValue;
}

