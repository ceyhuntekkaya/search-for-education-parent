package com.genixo.education.search.dto.survey;

import com.genixo.education.search.enumaration.SurveyType;
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
public class SurveyTemplateDto {
    private Long id;
    private String templateName;
    private String templateDescription;
    private SurveyType surveyType;
    private String industry;
    private String category;
    private Boolean isPublic;
    private Boolean isRecommended;
    private Integer usageCount;
    private Double averageRating;
    private String previewUrl;
    private List<SurveyQuestionDto> questions;
    private String createdByUserName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}