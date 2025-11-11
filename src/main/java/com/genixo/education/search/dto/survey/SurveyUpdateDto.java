package com.genixo.education.search.dto.survey;

import com.genixo.education.search.enumaration.SurveyTriggerEvent;
import com.genixo.education.search.enumaration.SurveyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyUpdateDto {
    private String title;
    private String description;
    private SurveyType surveyType;
    private SurveyTriggerEvent triggerEvent;
    private Boolean isActive;
    private Boolean isAnonymous;
    private Boolean isMandatory;
    private Boolean showResultsToPublic;
    private Integer sendDelayHours;
    private Integer reminderDelayHours;
    private Integer maxReminders;
    private Integer expiresAfterDays;
    private String primaryColor;
    private String logoUrl;
    private String headerImageUrl;
    private String customCss;
    private String welcomeMessage;
    private String thankYouMessage;
    private String completionRedirectUrl;
    private String emailSubject;
    private String emailBody;
    private String emailTemplateId;
    private List<SurveyQuestionCreateDto> questions;
}