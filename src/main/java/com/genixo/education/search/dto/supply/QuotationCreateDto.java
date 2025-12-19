package com.genixo.education.search.dto.supply;

import com.genixo.education.search.enumaration.Currency;
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
public class QuotationCreateDto {

    @NotNull(message = "RFQ ID is required")
    private Long rfqId;

    @NotNull(message = "Supplier ID is required")
    private Long supplierId;

    private BigDecimal totalAmount;

    private Currency currency;

    private LocalDate validUntil;

    private Integer deliveryDays;

    private String paymentTerms;

    private String warrantyTerms;

    private String notes;
}

