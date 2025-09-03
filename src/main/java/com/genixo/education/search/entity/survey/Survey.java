package com.genixo.education.search.entity.survey;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.enumaration.SurveyTriggerEvent;
import com.genixo.education.search.enumaration.SurveyType;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

// ========================= SURVEY =========================
@Entity
@Table(name = "surveys")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Survey extends BaseEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "survey_type", nullable = false)
    private SurveyType surveyType;

    @Enumerated(EnumType.STRING)
    @Column(name = "trigger_event")
    private SurveyTriggerEvent triggerEvent;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_anonymous")
    private Boolean isAnonymous = false;

    @Column(name = "is_mandatory")
    private Boolean isMandatory = false;

    @Column(name = "show_results_to_public")
    private Boolean showResultsToPublic = false;

    // Timing settings
    @Column(name = "send_delay_hours")
    private Integer sendDelayHours = 24; // Randevu sonrası kaç saat bekle

    @Column(name = "reminder_delay_hours")
    private Integer reminderDelayHours = 72; // Hatırlatma için

    @Column(name = "max_reminders")
    private Integer maxReminders = 2;

    @Column(name = "expires_after_days")
    private Integer expiresAfterDays = 7; // Kaç gün sonra süresi dolar

    // Styling and branding
    @Column(name = "primary_color")
    private String primaryColor = "#007bff";

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "header_image_url")
    private String headerImageUrl;

    @Column(name = "custom_css", columnDefinition = "TEXT")
    private String customCss;

    // Messages
    @Column(name = "welcome_message", columnDefinition = "TEXT")
    private String welcomeMessage;

    @Column(name = "thank_you_message", columnDefinition = "TEXT")
    private String thankYouMessage;

    @Column(name = "completion_redirect_url")
    private String completionRedirectUrl;

    // Email template
    @Column(name = "email_subject")
    private String emailSubject;

    @Column(name = "email_body", columnDefinition = "TEXT")
    private String emailBody;

    @Column(name = "email_template_id")
    private String emailTemplateId;

    // Analytics
    @Column(name = "total_sent")
    private Long totalSent = 0L;

    @Column(name = "total_started")
    private Long totalStarted = 0L;

    @Column(name = "total_completed")
    private Long totalCompleted = 0L;

    @Column(name = "average_completion_time_seconds")
    private Integer averageCompletionTimeSeconds;

    @Column(name = "average_rating")
    private Double averageRating = 0.0;

    // Relationships
    @OneToMany(mappedBy = "survey", fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<SurveyQuestion> questions = new HashSet<>();

    @OneToMany(mappedBy = "survey", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<SurveyResponse> responses = new HashSet<>();
}
