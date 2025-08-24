package com.genixo.education.search.dto.appointment;

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
public class AppointmentTemplateDto {
    private Long id;
    private String templateName;
    private String templateType; // CONFIRMATION, REMINDER, CANCELLATION, RESCHEDULE, FOLLOW_UP
    private String subject;
    private String emailContent;
    private String smsContent;
    private String pushNotificationContent;
    private Boolean isActive;
    private Boolean isDefault;
    private String language;
    private List<String> availableVariables;
    private String previewUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}