package com.genixo.education.search.dto.appointment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentFeedbackDto {
    private Long appointmentId;
    private Long parentUserId;
    private String parentName;
    private Integer overallRating; // 1-5 stars
    private Integer staffRating;
    private Integer facilityRating;
    private Integer communicationRating;
    private Integer timelinessRating;
    private Integer informationQualityRating;
    private String positiveAspects;
    private String improvementSuggestions;
    private String generalComments;
    private Boolean wouldRecommend;
    private Integer likelihoodToEnroll; // 1-10 scale
    private LocalDateTime submittedAt;
    private String feedbackSource; // EMAIL, SMS, WEBSITE, MOBILE_APP
}
