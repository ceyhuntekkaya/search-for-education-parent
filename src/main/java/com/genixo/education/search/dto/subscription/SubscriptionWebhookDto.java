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
public class SubscriptionWebhookDto {
    private String eventType; // SUBSCRIPTION_CREATED, PAYMENT_SUCCESS, PAYMENT_FAILED
    private Long subscriptionId;
    private String webhookUrl;
    private Map<String, Object> payload;

    // Delivery
    private LocalDateTime triggeredAt;
    private LocalDateTime deliveredAt;
    private Integer attemptCount;
    private Boolean wasSuccessful;
    private Integer responseCode;
    private String responseBody;
    private String failureReason;

    // Retry info
    private LocalDateTime nextRetryAt;
    private Integer maxRetryAttempts;
    private Boolean retryEnabled;
}