package com.genixo.education.search.dto.survey;

import com.genixo.education.search.enumaration.QuestionType;
import com.genixo.education.search.enumaration.RatingCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyQuestionResponseDto {
    private Long id;
    private Long surveyResponseId;
    private Long questionId;
    private String questionText;
    private QuestionType questionType;
    private RatingCategory ratingCategory;

    // Different response types
    private String textResponse;
    private Double numberResponse;
    private LocalDate dateResponse;
    private LocalTime timeResponse;
    private LocalDateTime datetimeResponse;
    private Boolean booleanResponse;
    private Integer ratingResponse;
    private String choiceResponses;
    private String matrixResponses;

    // File uploads
    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private String fileType;

    // Other option text
    private String otherText;

    // Response metadata
    private Integer responseTimeSeconds;
    private Boolean wasSkipped;
    private String skipReason;
    private Integer responseOrder;
    private Integer revisionCount;
    private Integer confidenceLevel;

    // Calculated fields
    private String displayValue;
    private String formattedResponse;
    private List<String> choiceResponsesList;
    private Map<String, String> matrixResponsesMap;
}