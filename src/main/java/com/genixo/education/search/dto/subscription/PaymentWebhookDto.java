package com.genixo.education.search.dto.subscription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentWebhookDto {
    private String transactionId;
    private String eventType;
    private String errorMessage;
    private String rawData;
    private BigDecimal refundAmount;
    private String refundReason;
}
