package com.genixo.education.search.dto.subscription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionNotificationDto {
    private String notificationType; // PAYMENT_DUE, PAYMENT_FAILED, TRIAL_ENDING, PLAN_CHANGE
    private Long subscriptionId;
    private String recipientEmail;
    private String recipientName;
    private String subject;
    private String message;
    private Map<String, Object> templateData;

    // Delivery
    private String deliveryMethod; // EMAIL, SMS, PUSH, IN_APP
    private LocalDateTime scheduledAt;
    private LocalDateTime sentAt;
    private Boolean wasDelivered;
    private String deliveryStatus;
    private String failureReason;

    // Tracking
    private Boolean wasOpened;
    private Boolean wasClicked;
    private LocalDateTime openedAt;
    private LocalDateTime clickedAt;
}
