package com.genixo.education.search.dto.subscription;

import com.genixo.education.search.enumaration.PaymentMethod;
import com.genixo.education.search.enumaration.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentSearchDto {
    private String searchTerm;
    private Long subscriptionId;
    private List<PaymentStatus> statuses;
    private List<PaymentMethod> paymentMethods;
    private LocalDateTime paymentDateAfter;
    private LocalDateTime paymentDateBefore;
    private LocalDateTime dueDateAfter;
    private LocalDateTime dueDateBefore;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private String currency;
    private String gatewayName;
    private Boolean isRefunded;

    // Pagination
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}