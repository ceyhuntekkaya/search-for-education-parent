package com.genixo.education.search.dto.survey;

import com.genixo.education.search.enumaration.ResponseStatus;
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
public class SurveyResponseDto {
    private Long id;
    private Long surveyId;
    private String surveyTitle;
    private Long respondentUserId;
    private String respondentUserName;
    private Long schoolId;
    private String schoolName;
    private Long appointmentId;
    private String responseToken;
    private ResponseStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime submittedAt;
    private Integer completionTimeSeconds;

    // Contact information (for anonymous responses)
    private String respondentName;
    private String respondentEmail;
    private String respondentPhone;

    // Technical information
    private String ipAddress;
    private String userAgent;
    private String browserInfo;
    private String deviceInfo;

    // Survey invitation tracking
    private LocalDateTime invitationSentAt;
    private LocalDateTime invitationOpenedAt;
    private Integer reminderCount;
    private LocalDateTime lastReminderSentAt;

    // Overall ratings
    private Double overallRating;
    private Double cleanlinessRating;
    private Double staffRating;
    private Double facilitiesRating;
    private Double communicationRating;

    // Additional feedback
    private String generalFeedback;
    private String suggestions;
    private String complaints;
    private Boolean wouldRecommend;
    private Integer likelihoodToEnroll;

    // Calculated fields
    private String formattedCompletionTime;
    private String statusDisplayName;
    private Double progressPercentage;
    private Boolean isComplete;
    private Boolean isExpired;

    // Relationships
    private List<SurveyQuestionResponseDto> questionResponses;

    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
