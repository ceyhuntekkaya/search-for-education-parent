package com.genixo.education.search.dto.content;

import com.genixo.education.search.dto.institution.SchoolSummaryDto;
import com.genixo.education.search.dto.user.UserSummaryDto;
import com.genixo.education.search.enumaration.MessagePriority;
import com.genixo.education.search.enumaration.MessageStatus;
import com.genixo.education.search.enumaration.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDto {
    private Long id;
    private String senderName;
    private String senderEmail;
    private String senderPhone;
    private String subject;
    private String content;
    private MessageType messageType;
    private MessagePriority priority;
    private MessageStatus status;
    private String referenceNumber;

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

    // Message handling
    private LocalDateTime readAt;
    private UserSummaryDto readBy;
    private LocalDateTime firstResponseAt;
    private LocalDateTime lastResponseAt;
    private LocalDateTime resolvedAt;
    private UserSummaryDto resolvedBy;
    private Double responseTimeHours;
    private Double resolutionTimeHours;

    // Follow-up
    private Boolean followUpRequired;
    private LocalDateTime followUpDate;
    private String followUpNotes;

    // Internal notes
    private String internalNotes;
    private String tags;

    // Technical information
    private String ipAddress;
    private String userAgent;
    private String sourcePage;
    private String utmSource;
    private String utmMedium;
    private String utmCampaign;

    // Attachments
    private Boolean hasAttachments;
    private String attachments; // JSON

    // Satisfaction
    private Integer satisfactionRating;
    private String satisfactionFeedback;
    private LocalDateTime satisfactionDate;

    // Relationships
    private SchoolSummaryDto school;
    private UserSummaryDto senderUser;
    private UserSummaryDto assignedToUser;
    private Boolean isActive;
    private LocalDateTime createdAt;
}