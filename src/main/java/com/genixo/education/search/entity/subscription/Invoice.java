package com.genixo.education.search.entity.subscription;

import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.enumaration.InvoiceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Invoice extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private Subscription subscription;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Column(name = "invoice_number", unique = true, nullable = false)
    private String invoiceNumber;

    @Column(name = "invoice_date", nullable = false)
    private LocalDateTime invoiceDate;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "invoice_status", nullable = false)
    private InvoiceStatus invoiceStatus;

    @Column(name = "subtotal", nullable = false, precision = 19, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "tax_amount", precision = 19, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "tax_rate")
    private Double taxRate = 0.0;

    @Column(name = "discount_amount", precision = 19, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "currency", nullable = false)
    private String currency = "TRY";

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Billing information (snapshot at invoice time)
    @Column(name = "billing_name", nullable = false)
    private String billingName;

    @Column(name = "billing_email")
    private String billingEmail;

    @Column(name = "billing_phone")
    private String billingPhone;

    @Column(name = "billing_address", columnDefinition = "TEXT")
    private String billingAddress;

    @Column(name = "tax_number")
    private String taxNumber;

    @Column(name = "tax_office")
    private String taxOffice;

    // PDF and file information
    @Column(name = "pdf_file_url")
    private String pdfFileUrl;

    @Column(name = "pdf_generated_at")
    private LocalDateTime pdfGeneratedAt;

    // Billing period
    @Column(name = "period_start")
    private LocalDateTime periodStart;

    @Column(name = "period_end")
    private LocalDateTime periodEnd;

    // Line items (JSON format for flexibility)
    @Column(name = "line_items")
    private String lineItems;

    // E-invoice integration
    @Column(name = "e_invoice_uuid")
    private String eInvoiceUuid;

    @Column(name = "e_invoice_status")
    private String eInvoiceStatus;

    @Column(name = "e_invoice_sent_at")
    private LocalDateTime eInvoiceSentAt;

    @Column(name = "e_invoice_response")
    private String eInvoiceResponse;

    @Column(name = "generate_e_invoice")
    private Boolean generateEInvoice;

}
