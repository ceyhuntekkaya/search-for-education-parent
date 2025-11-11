package com.genixo.education.search.dto.subscription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceCreateDto {
    private Long subscriptionId;
    private Long paymentId;
    private LocalDateTime dueDate;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private Double taxRate;
    private BigDecimal discountAmount;
    private String currency;
    private String description;
    private String notes;

    // Billing period
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;

    // Line items
    private String lineItems; // JSON

    // E-invoice settings
    private Boolean generateEInvoice;
}
