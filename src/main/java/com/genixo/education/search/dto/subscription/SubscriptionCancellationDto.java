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
public class SubscriptionCancellationDto {
    private Long subscriptionId;
    private LocalDateTime cancellationDate;
    private LocalDateTime effectiveDate;
    private String cancellationReason;
    private String cancellationCategory; // PRICE, FEATURES, SUPPORT, COMPETITOR, OTHER
    private String feedback;
    private Boolean immediateCancel;
    private Boolean processRefund;
    private BigDecimal refundAmount;
    private String refundReason;

}
