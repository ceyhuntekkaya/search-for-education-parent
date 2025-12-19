package com.genixo.education.search.dto.supply;

import com.genixo.education.search.enumaration.Currency;
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
public class QuotationUpdateDto {

    private BigDecimal totalAmount;
    private Currency currency;
    private LocalDate validUntil;
    private Integer deliveryDays;
    private String paymentTerms;
    private String warrantyTerms;
    private String notes;
}

