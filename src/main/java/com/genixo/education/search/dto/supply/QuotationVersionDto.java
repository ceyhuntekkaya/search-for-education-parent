package com.genixo.education.search.dto.supply;

import com.genixo.education.search.enumaration.Currency;
import com.genixo.education.search.enumaration.QuotationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuotationVersionDto {

    private Long id;
    private Integer versionNumber;
    private QuotationStatus status;
    private BigDecimal totalAmount;
    private Currency currency;
    private LocalDate validUntil;
    private Integer deliveryDays;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

