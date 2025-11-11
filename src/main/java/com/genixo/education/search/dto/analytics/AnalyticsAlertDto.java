package com.genixo.education.search.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsAlertDto {
    private Long id;
    private String alertName;
    private String alertDescription;
    private String metricName;
    private String condition; // GREATER_THAN, LESS_THAN, EQUALS, CHANGE_PERCENTAGE
    private Double thresholdValue;
    private String severity; // LOW, MEDIUM, HIGH, CRITICAL
    private Boolean isActive;
    private Boolean isTriggered;
    private LocalDateTime lastTriggeredAt;
    private Integer triggerCount;

    // Notification settings
    private List<String> notificationEmails;
    private Boolean emailNotificationEnabled;
    private Boolean smsNotificationEnabled;
    private Boolean slackNotificationEnabled;

    // Institution scope
    private Long brandId;
    private Long campusId;
    private Long schoolId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}