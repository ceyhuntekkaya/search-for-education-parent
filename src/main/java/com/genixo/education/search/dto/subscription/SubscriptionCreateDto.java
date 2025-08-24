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
public class SubscriptionCreateDto {
    private Long campusId;
    private Long subscriptionPlanId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String couponCode;
    private Boolean autoRenew;

    // Billing information
    private String billingName;
    private String billingEmail;
    private String billingPhone;
    private String billingAddress;
    private String taxNumber;
    private String taxOffice;

    private String currency;

    private PaymentMethod paymentMethod;
}