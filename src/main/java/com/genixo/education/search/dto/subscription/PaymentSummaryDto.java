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
public class PaymentSummaryDto {
    private Long id;
    private String paymentReference;
    private BigDecimal amount;
    private String currency;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private LocalDateTime paymentDate;
    private LocalDateTime dueDate;
    private String cardLastFour;
    private String cardBrand;
    private Boolean isRefunded;
}