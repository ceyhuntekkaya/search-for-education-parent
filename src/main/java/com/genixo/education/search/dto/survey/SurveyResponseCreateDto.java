package com.genixo.education.search.dto.survey;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyResponseCreateDto {
    private Long surveyId;
    private Long respondentUserId;
    private Long schoolId;
    private Long appointmentId;
    private String respondentName;
    private String respondentEmail;
    private String respondentPhone;
    private String ipAddress;
    private String userAgent;
    private String browserInfo;
    private String deviceInfo;
    private String generalFeedback;
    private String suggestions;
    private String complaints;
    private Boolean wouldRecommend;
    private Integer likelihoodToEnroll;
    private List<SurveyQuestionResponseCreateDto> questionResponses;
}