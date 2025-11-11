package com.genixo.education.search.dto.appointment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentNotificationDto {
    private Long appointmentId;
    private String notificationType; // CONFIRMATION, REMINDER, CANCELLATION, RESCHEDULE
    private String recipientType; // PARENT, STAFF, ALL
    private List<String> recipientEmails;
    private List<String> recipientPhones;
    private String subject;
    private String message;
    private Boolean sendEmail;
    private Boolean sendSms;
    private Boolean sendPushNotification;
    private LocalDateTime scheduledSendTime;
    private String templateId;
    private Map<String, String> templateVariables;
}
