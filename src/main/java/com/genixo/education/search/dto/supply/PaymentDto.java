package com.genixo.education.search.dto.supply;

import com.genixo.education.search.enumaration.Currency;
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
    private Long orderId;
    private String orderNumber;
    private Long companyId;
    private String companyName;
    private Long supplierId;
    private String supplierCompanyName;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private BigDecimal amount;
    private Currency currency;
    private String transactionId;
    private LocalDateTime paidAt;
    private String notes;
    private LocalDateTime createdAt;
}

