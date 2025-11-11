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
public class CouponValidationDto {
    private String couponCode;
    private Long subscriptionPlanId;
    private BigDecimal orderAmount;
    private String currency;

    // Validation result
    private Boolean isValid;
    private String validationMessage;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
}