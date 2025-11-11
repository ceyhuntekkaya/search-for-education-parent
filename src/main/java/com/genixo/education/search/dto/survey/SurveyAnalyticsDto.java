package com.genixo.education.search.dto.survey;

import com.genixo.education.search.enumaration.RatingCategory;
import com.genixo.education.search.enumaration.SurveyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyAnalyticsDto {
    private Long surveyId;
    private String surveyTitle;
    private SurveyType surveyType;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;

    // Response metrics
    private Long totalInvitationsSent;
    private Long totalStarted;
    private Long totalCompleted;
    private Long totalSubmitted;
    private Long totalAbandoned;
    private Double startRate;
    private Double completionRate;
    private Double submissionRate;
    private Double abandonmentRate;

    // Time metrics
    private Integer averageCompletionTimeSeconds;
    private Integer medianCompletionTimeSeconds;
    private Integer fastestCompletionTimeSeconds;
    private Integer slowestCompletionTimeSeconds;

    // Rating metrics
    private Double overallAverageRating;
    private Map<RatingCategory, Double> categoryAverageRatings;
    private Map<RatingCategory, Long> categoryResponseCounts;
    private Map<Integer, Long> ratingDistribution;

    // Feedback metrics
    private Long totalFeedbackResponses;
    private Long positiveRecommendations;
    private Long negativeRecommendations;
    private Double recommendationRate;
    private Double averageLikelihoodToEnroll;

    // Question analytics
    private List<QuestionAnalyticsDto> questionAnalytics;

    // Time-based analytics
    private List<DailySurveyStatsDto> dailyStats;

    // Device and channel analytics
    private Map<String, Long> deviceTypeDistribution;
    private Map<String, Long> browserDistribution;
    private Map<String, Double> channelCompletionRates;

    // Satisfaction trends
    private List<SatisfactionTrendDto> satisfactionTrends;

    public SurveyAnalyticsDto(Long surveyId, String surveyTitle, SurveyType surveyType,
                              LocalDateTime periodStart, LocalDateTime periodEnd,
                              Long totalInvitationsSent, Long totalStarted, Long totalCompleted,
                              Long totalSubmitted, Long totalAbandoned,
                              Double startRate, Double completionRate, Double submissionRate, Double abandonmentRate,
                              Integer averageCompletionTimeSeconds, Integer medianCompletionTimeSeconds,
                              Integer fastestCompletionTimeSeconds, Integer slowestCompletionTimeSeconds,
                              Double overallAverageRating) {

        this.surveyId = surveyId;
        this.surveyTitle = surveyTitle;
        this.surveyType = surveyType;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.totalInvitationsSent = totalInvitationsSent;
        this.totalStarted = totalStarted;
        this.totalCompleted = totalCompleted;
        this.totalSubmitted = totalSubmitted;
        this.totalAbandoned = totalAbandoned;
        this.startRate = startRate;
        this.completionRate = completionRate;
        this.submissionRate = submissionRate;
        this.abandonmentRate = abandonmentRate;
        this.averageCompletionTimeSeconds = averageCompletionTimeSeconds;
        this.medianCompletionTimeSeconds = medianCompletionTimeSeconds;
        this.fastestCompletionTimeSeconds = fastestCompletionTimeSeconds;
        this.slowestCompletionTimeSeconds = slowestCompletionTimeSeconds;
        this.overallAverageRating = overallAverageRating;

        // Diğer complex alanlar serviste doldurulacak
        this.categoryAverageRatings = new HashMap<>();
        this.categoryResponseCounts = new HashMap<>();
        this.ratingDistribution = new HashMap<>();
        this.deviceTypeDistribution = new HashMap<>();
        this.browserDistribution = new HashMap<>();
        this.channelCompletionRates = new HashMap<>();
    }


}