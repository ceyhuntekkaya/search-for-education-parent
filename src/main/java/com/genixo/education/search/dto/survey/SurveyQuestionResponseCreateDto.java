package com.genixo.education.search.dto.survey;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyQuestionResponseCreateDto {
    private Long questionId;
    private String textResponse;
    private Double numberResponse;
    private LocalDate dateResponse;
    private LocalTime timeResponse;
    private LocalDateTime datetimeResponse;
    private Boolean booleanResponse;
    private Integer ratingResponse;
    private String choiceResponses;
    private String matrixResponses;
    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private String fileType;
    private String otherText;
    private Integer responseTimeSeconds;
    private Boolean wasSkipped;
    private String skipReason;
    private Integer confidenceLevel;
    private Integer revisionCount;
}