package com.genixo.education.search.dto.survey;

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
public class QuestionAnalyticsDto {
    private Long questionId;
    private String questionText;
    private QuestionType questionType;
    private RatingCategory ratingCategory;
    private Long totalResponses;
    private Long skipCount;
    private Double responseRate;
    private Double skipRate;
    private Integer averageResponseTimeSeconds;

    // Type-specific analytics
    private Double averageRating;
    private Map<String, Long> choiceDistribution;
    private Map<Integer, Long> ratingDistribution;
    private Double averageNumericValue;
    private List<String> topTextResponses;
    private Long yesCount;
    private Long noCount;

    // Insights
    private String topChoice;
    private String leastPopularChoice;
    private Double satisfactionScore;
    private List<String> insights;
}