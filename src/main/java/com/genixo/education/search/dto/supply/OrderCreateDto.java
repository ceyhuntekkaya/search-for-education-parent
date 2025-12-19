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
public class OrderCreateDto {

    @NotNull(message = "Quotation ID is required")
    private Long quotationId;

    @NotNull(message = "Company ID is required")
    private Long companyId;

    private String deliveryAddress;

    private LocalDate expectedDeliveryDate;

    private String notes;
}

