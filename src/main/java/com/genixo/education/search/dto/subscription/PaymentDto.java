package com.genixo.education.search.dto.subscription;

import com.genixo.education.search.enumaration.PaymentMethod;
import com.genixo.education.search.enumaration.PaymentStatus;
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
public class PaymentDto {
    private Long id;
    private String paymentReference;
    private String externalPaymentId;
    private BigDecimal amount;
    private String currency;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private LocalDateTime paymentDate;
    private LocalDateTime dueDate;
    private String description;
    private String failureReason;

    // Refund information
    private BigDecimal refundAmount;
    private LocalDateTime refundDate;
    private String refundReason;

    // Payment gateway
    private String gatewayName;
    private String gatewayTransactionId;
    private String gatewayResponse;

    // Card information (masked)
    private String cardLastFour;
    private String cardBrand;
    private String cardHolderName;

    // Billing period
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;

    // Relationships
    private SubscriptionSummaryDto subscription;
    private InvoiceSummaryDto invoice;

    private Boolean isActive;
    private LocalDateTime createdAt;
}