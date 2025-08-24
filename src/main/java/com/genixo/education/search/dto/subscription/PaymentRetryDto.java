package com.genixo.education.search.dto.subscription;

import com.genixo.education.search.enumaration.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRetryDto {
    private Long paymentId;
    private LocalDateTime retryDate;
    private Integer retryAttempt;
    private String retryReason;
    private PaymentMethod newPaymentMethod;
    private String newPaymentToken;
    private Boolean updateDefaultPaymentMethod;
}
