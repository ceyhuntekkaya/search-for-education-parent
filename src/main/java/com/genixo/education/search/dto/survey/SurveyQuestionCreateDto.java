package com.genixo.education.search.dto.survey;

import com.genixo.education.search.enumaration.ConditionType;
import com.genixo.education.search.enumaration.QuestionType;
import com.genixo.education.search.enumaration.RatingCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyQuestionCreateDto {
    private String questionText;
    private String description;
    private QuestionType questionType;
    private RatingCategory ratingCategory;
    private Boolean isRequired;
    private Integer sortOrder;
    private String options;
    private Boolean allowOtherOption;
    private String otherOptionLabel;
    private Integer ratingScaleMin;
    private Integer ratingScaleMax;
    private Integer ratingScaleStep;
    private String ratingLabels;
    private Integer textMinLength;
    private Integer textMaxLength;
    private String placeholderText;
    private Long showIfQuestionId;
    private String showIfAnswer;
    private ConditionType showIfCondition;
    private String customCssClass;
    private String helpText;
    private String imageUrl;
}
