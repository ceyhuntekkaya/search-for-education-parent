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
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuotationComparisonDto {

    private Long quotationId;
    private Long supplierId;
    private String supplierCompanyName;
    private BigDecimal averageRating;
    private QuotationStatus status;
    private BigDecimal totalAmount;
    private Currency currency;
    private LocalDate validUntil;
    private Integer deliveryDays;
    private String paymentTerms;
    private String warrantyTerms;
    private String notes;
    private Integer versionNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<QuotationItemComparisonDto> items;
}

