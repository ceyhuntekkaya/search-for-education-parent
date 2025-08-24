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
public class SchoolSurveyPerformanceDto {
    private Long schoolId;
    private String schoolName;
    private String campusName;
    private Long totalResponses;
    private Double responseRate;
    private Double completionRate;
    private Double overallRating;
    private Double cleanlinessRating;
    private Double staffRating;
    private Double facilitiesRating;
    private Double communicationRating;
    private Double recommendationRate;
    private Double averageLikelihoodToEnroll;
    private String performanceLevel; // EXCELLENT, GOOD, AVERAGE, BELOW_AVERAGE, POOR
    private List<String> topStrengths;
    private List<String> improvementAreas;
    private String overallTrend; // IMPROVING, DECLINING, STABLE
}