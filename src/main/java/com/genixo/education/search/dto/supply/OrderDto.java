package com.genixo.education.search.dto.supply;

import com.genixo.education.search.enumaration.Currency;
import com.genixo.education.search.enumaration.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {

    private Long id;
    private String orderNumber;
    private Long quotationId;
    private Long companyId;
    private String companyName;
    private Long supplierId;
    private String supplierCompanyName;
    private OrderStatus status;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private Currency currency;
    private String deliveryAddress;
    private LocalDate expectedDeliveryDate;
    private LocalDate actualDeliveryDate;
    private String notes;
    private String invoiceNumber;
    private String trackingNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer itemCount;
}

