package com.genixo.education.search.dto.subscription;

import com.genixo.education.search.enumaration.PaymentMethod;
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
public class PaymentCreateDto {
    private Long subscriptionId;
    private BigDecimal amount;
    private String currency;
    private PaymentMethod paymentMethod;
    private String description;
    private LocalDateTime dueDate;

    // Payment gateway info
    private String gatewayName;
    private String externalPaymentId;

    // Card information
    private String cardLastFour;
    private String cardBrand;
    private String cardHolderName;

    // Billing period
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
}