package com.genixo.education.search.dto.content;

import com.genixo.education.search.enumaration.MessagePriority;
import com.genixo.education.search.enumaration.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageCreateDto {
    private Long schoolId;
    private String senderName;
    private String senderEmail;
    private String senderPhone;
    private String subject;
    private String content;
    private MessageType messageType;
    private MessagePriority priority;

    // Student information
    private String studentName;
    private Integer studentAge;
    private String gradeInterested;
    private String enrollmentYear;

    // Contact preferences
    private String preferredContactMethod;
    private String preferredContactTime;
    private Boolean requestCallback;
    private Boolean requestAppointment;

    // Technical information
    private String ipAddress;
    private String userAgent;
    private String sourcePage;
    private String utmSource;
    private String utmMedium;
    private String utmCampaign;

    // Attachments
    private String attachments; // JSON
}
