package com.genixo.education.search.dto.subscription;

import com.genixo.education.search.enumaration.InvoiceStatus;
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
public class InvoiceDto {
    private Long id;
    private String invoiceNumber;
    private LocalDateTime invoiceDate;
    private LocalDateTime dueDate;
    private InvoiceStatus invoiceStatus;

    // Amounts
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private Double taxRate;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private String currency;

    // Description and notes
    private String description;
    private String notes;

    // Billing information (snapshot)
    private String billingName;
    private String billingEmail;
    private String billingPhone;
    private String billingAddress;
    private String taxNumber;
    private String taxOffice;

    // Files
    private String pdfFileUrl;
    private LocalDateTime pdfGeneratedAt;

    // Billing period
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;

    // Line items
    private String lineItems; // JSON

    // E-invoice
    private String eInvoiceUuid;
    private String eInvoiceStatus;
    private LocalDateTime eInvoiceSentAt;
    private String eInvoiceResponse;

    // Relationships
    private SubscriptionSummaryDto subscription;
    private PaymentSummaryDto payment;

    private Boolean isActive;
    private LocalDateTime createdAt;
}