package com.genixo.education.search.dto.survey;

import com.genixo.education.search.enumaration.ConditionType;
import com.genixo.education.search.enumaration.QuestionType;
import com.genixo.education.search.enumaration.RatingCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyQuestionDto {
    private Long id;
    private Long surveyId;
    private String questionText;
    private String description;
    private QuestionType questionType;
    private RatingCategory ratingCategory;
    private Boolean isRequired;
    private Integer sortOrder;
    private Boolean isActive;

    // For choice questions
    private String options;
    private Boolean allowOtherOption;
    private String otherOptionLabel;

    // For rating questions
    private Integer ratingScaleMin;
    private Integer ratingScaleMax;
    private Integer ratingScaleStep;
    private String ratingLabels;

    // For text questions
    private Integer textMinLength;
    private Integer textMaxLength;
    private String placeholderText;

    // Conditional logic
    private Long showIfQuestionId;
    private String showIfAnswer;
    private ConditionType showIfCondition;

    // Styling
    private String customCssClass;
    private String helpText;
    private String imageUrl;

    // Analytics
    private Long totalResponses;
    private Double averageRating;
    private Long skipCount;

    // Calculated fields
    private String questionTypeDisplayName;
    private String ratingCategoryDisplayName;
    private List<String> optionsList;
    private Map<String, String> ratingLabelsMap;
    private Double responseRate;
    private Double skipRate;
}