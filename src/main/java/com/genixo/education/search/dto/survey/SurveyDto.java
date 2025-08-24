package com.genixo.education.search.dto.survey;

import com.genixo.education.search.enumaration.SurveyTriggerEvent;
import com.genixo.education.search.enumaration.SurveyType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyDto {
    private Long id;
    private String title;
    private String description;
    private SurveyType surveyType;
    private SurveyTriggerEvent triggerEvent;
    private Boolean isActive;
    private Boolean isAnonymous;
    private Boolean isMandatory;
    private Boolean showResultsToPublic;

    // Timing settings
    private Integer sendDelayHours;
    private Integer reminderDelayHours;
    private Integer maxReminders;
    private Integer expiresAfterDays;

    // Styling and branding
    private String primaryColor;
    private String logoUrl;
    private String headerImageUrl;
    private String customCss;

    // Messages
    private String welcomeMessage;
    private String thankYouMessage;
    private String completionRedirectUrl;

    // Email template
    private String emailSubject;
    private String emailBody;
    private String emailTemplateId;

    // Analytics
    private Long totalSent;
    private Long totalStarted;
    private Long totalCompleted;
    private Integer averageCompletionTimeSeconds;
    private Double averageRating;

    // Calculated fields
    private Double startRate;
    private Double completionRate;
    private Integer questionCount;
    private String estimatedDuration;
    private Boolean hasRatingQuestions;

    // Relationships
    private List<SurveyQuestionDto> questions;

    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}