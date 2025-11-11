package com.genixo.education.search.dto.subscription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionUpdateDto {
    private Long subscriptionPlanId;
    private Boolean autoRenew;
    private String cancellationReason;

    // Billing information
    private String billingName;
    private String billingEmail;
    private String billingPhone;
    private String billingAddress;
    private String taxNumber;
    private String taxOffice;
}
