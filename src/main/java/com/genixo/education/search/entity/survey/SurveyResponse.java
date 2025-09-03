package com.genixo.education.search.entity.survey;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.entity.appointment.Appointment;
import com.genixo.education.search.enumaration.ResponseStatus;
import com.genixo.education.search.entity.institution.School;
import com.genixo.education.search.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "survey_responses")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class SurveyResponse extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey survey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "respondent_user_id")
    private User respondentUser; // Anketi dolduran kullanıcı (veli)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment; // Hangi randevu için

    @Column(name = "response_token", unique = true, nullable = false)
    private String responseToken; // Anonim yanıtlar için unique token

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ResponseStatus status = ResponseStatus.STARTED;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "completion_time_seconds")
    private Integer completionTimeSeconds;

    // Contact information (for anonymous responses)
    @Column(name = "respondent_name")
    private String respondentName;

    @Column(name = "respondent_email")
    private String respondentEmail;

    @Column(name = "respondent_phone")
    private String respondentPhone;

    // Technical information
    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "browser_info", columnDefinition = "JSON")
    private String browserInfo;

    @Column(name = "device_info", columnDefinition = "JSON")
    private String deviceInfo;

    // Survey invitation tracking
    @Column(name = "invitation_sent_at")
    private LocalDateTime invitationSentAt;

    @Column(name = "invitation_opened_at")
    private LocalDateTime invitationOpenedAt;

    @Column(name = "reminder_count")
    private Integer reminderCount = 0;

    @Column(name = "last_reminder_sent_at")
    private LocalDateTime lastReminderSentAt;

    // Overall ratings calculated from questions
    @Column(name = "overall_rating")
    private Double overallRating;

    @Column(name = "cleanliness_rating")
    private Double cleanlinessRating;

    @Column(name = "staff_rating")
    private Double staffRating;

    @Column(name = "facilities_rating")
    private Double facilitiesRating;

    @Column(name = "communication_rating")
    private Double communicationRating;

    // Additional feedback
    @Column(name = "general_feedback", columnDefinition = "TEXT")
    private String generalFeedback;

    @Column(name = "suggestions", columnDefinition = "TEXT")
    private String suggestions;

    @Column(name = "complaints", columnDefinition = "TEXT")
    private String complaints;

    @Column(name = "would_recommend")
    private Boolean wouldRecommend;

    @Column(name = "likelihood_to_enroll")
    private Integer likelihoodToEnroll; // 1-10 scale

    // Relationships
    @OneToMany(mappedBy = "surveyResponse", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<SurveyQuestionResponse> questionResponses = new HashSet<>();
}