package com.genixo.education.search.entity.content;

import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.enumaration.MessagePriority;
import com.genixo.education.search.enumaration.MessageStatus;
import com.genixo.education.search.enumaration.MessageType;
import com.genixo.education.search.entity.institution.School;
import com.genixo.education.search.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Table(name = "messages")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Message extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_user_id")
    private User senderUser; // Gönderen kullanıcı (veli)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_user_id")
    private User assignedToUser; // Atanan personel

    @Column(name = "sender_name", nullable = false)
    private String senderName;

    @Column(name = "sender_email", nullable = false)
    private String senderEmail;

    @Column(name = "sender_phone")
    private String senderPhone;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType messageType = MessageType.GENERAL_INQUIRY;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private MessagePriority priority = MessagePriority.NORMAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MessageStatus status = MessageStatus.NEW;

    @Column(name = "reference_number", unique = true)
    private String referenceNumber;

    // Student information
    @Column(name = "student_name")
    private String studentName;

    @Column(name = "student_age")
    private Integer studentAge;

    @Column(name = "grade_interested")
    private String gradeInterested;

    @Column(name = "enrollment_year")
    private String enrollmentYear;

    // Contact preferences
    @Column(name = "preferred_contact_method")
    private String preferredContactMethod;

    @Column(name = "preferred_contact_time")
    private String preferredContactTime;

    @Column(name = "request_callback")
    private Boolean requestCallback = false;

    @Column(name = "request_appointment")
    private Boolean requestAppointment = false;

    // Message handling
    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "read_by")
    private Long readBy;

    @Column(name = "first_response_at")
    private LocalDateTime firstResponseAt;

    @Column(name = "last_response_at")
    private LocalDateTime lastResponseAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "resolved_by")
    private Long resolvedBy;

    @Column(name = "response_time_hours")
    private Double responseTimeHours;

    @Column(name = "resolution_time_hours")
    private Double resolutionTimeHours;

    // Follow-up
    @Column(name = "follow_up_required")
    private Boolean followUpRequired = false;

    @Column(name = "follow_up_date")
    private LocalDateTime followUpDate;

    @Column(name = "follow_up_notes")
    private String followUpNotes;

    // Internal notes
    @Column(name = "internal_notes", columnDefinition = "TEXT")
    private String internalNotes;

    @Column(name = "tags")
    private String tags; // Comma separated

    // Technical information
    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "source_page")
    private String sourcePage;

    @Column(name = "utm_source")
    private String utmSource;

    @Column(name = "utm_medium")
    private String utmMedium;

    @Column(name = "utm_campaign")
    private String utmCampaign;

    // Attachments
    @Column(name = "has_attachments")
    private Boolean hasAttachments = false;

    @Column(name = "attachments", columnDefinition = "JSON")
    private String attachments; // JSON array of file information

    // Satisfaction
    @Column(name = "satisfaction_rating")
    private Integer satisfactionRating; // 1-5 stars

    @Column(name = "satisfaction_feedback")
    private String satisfactionFeedback;

    @Column(name = "satisfaction_date")
    private LocalDateTime satisfactionDate;
}