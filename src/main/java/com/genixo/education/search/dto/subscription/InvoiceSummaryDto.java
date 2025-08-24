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
public class InvoiceSummaryDto {
    private Long id;
    private String invoiceNumber;
    private LocalDateTime invoiceDate;
    private LocalDateTime dueDate;
    private InvoiceStatus invoiceStatus;
    private BigDecimal totalAmount;
    private String currency;
    private String pdfFileUrl;
    private Boolean isPaid;
    private Integer daysOverdue;
}
