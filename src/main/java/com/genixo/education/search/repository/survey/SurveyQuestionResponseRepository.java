package com.genixo.education.search.repository.survey;

import com.genixo.education.search.entity.survey.SurveyQuestionResponse;
import com.genixo.education.search.enumaration.QuestionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SurveyQuestionResponseRepository extends JpaRepository<SurveyQuestionResponse, Long> {
    @Query("SELECT sqr FROM SurveyQuestionResponse sqr WHERE sqr.isActive = true AND sqr.id = :id")
    Optional<SurveyQuestionResponse> findByIdAndIsActiveTrue(@Param("id") Long id);

    @Query("SELECT sqr FROM SurveyQuestionResponse sqr WHERE sqr.surveyResponse.id = :responseId AND sqr.isActive = true ORDER BY sqr.question.sortOrder ASC")
    List<SurveyQuestionResponse> findByResponseIdAndIsActiveTrueOrderByQuestionSortOrder(@Param("responseId") Long responseId);

    @Query("SELECT sqr FROM SurveyQuestionResponse sqr WHERE sqr.question.id = :questionId AND sqr.isActive = true")
    List<SurveyQuestionResponse> findByQuestionIdAndIsActiveTrue(@Param("questionId") Long questionId);

    @Query("SELECT COUNT(sqr) FROM SurveyQuestionResponse sqr WHERE sqr.question.id = :questionId AND sqr.isActive = true")
    Long countByQuestionIdAndIsActiveTrue(@Param("questionId") Long questionId);

    @Query("SELECT sqr FROM SurveyQuestionResponse sqr " +
            "WHERE sqr.surveyResponse.id = :responseId AND sqr.question.questionType = :questionType AND sqr.isActive = true")
    List<SurveyQuestionResponse> findByResponseIdAndQuestionTypeAndIsActiveTrue(
            @Param("responseId") Long responseId,
            @Param("questionType") QuestionType questionType);

    @Query("SELECT sqr.question.id FROM SurveyQuestionResponse sqr " +
            "WHERE sqr.surveyResponse.id = :responseId AND sqr.wasSkipped = false AND sqr.isActive = true")
    List<Long> findAnsweredQuestionIdsByResponseId(@Param("responseId") Long responseId);

    @Query("SELECT AVG(sqr.ratingResponse) FROM SurveyQuestionResponse sqr " +
            "WHERE sqr.question.id = :questionId AND sqr.ratingResponse IS NOT NULL AND sqr.isActive = true")
    Double getAverageRatingByQuestionId(@Param("questionId") Long questionId);

    @Query("SELECT AVG(sqr.responseTimeSeconds) FROM SurveyQuestionResponse sqr " +
            "WHERE sqr.question.id = :questionId AND sqr.responseTimeSeconds IS NOT NULL AND sqr.isActive = true")
    Double getAverageResponseTimeByQuestionId(@Param("questionId") Long questionId);

    @Query("SELECT COUNT(sqr) FROM SurveyQuestionResponse sqr " +
            "WHERE sqr.question.id = :questionId AND sqr.wasSkipped = true AND sqr.isActive = true")
    Long getSkipCountByQuestionId(@Param("questionId") Long questionId);

    @Query("SELECT sqr.textResponse, COUNT(sqr) as count FROM SurveyQuestionResponse sqr " +
            "WHERE sqr.question.id = :questionId AND sqr.textResponse IS NOT NULL AND sqr.isActive = true " +
            "GROUP BY sqr.textResponse ORDER BY count DESC")
    List<Object[]> getTextResponseFrequencyByQuestionId(@Param("questionId") Long questionId);

    @Query("SELECT sqr.ratingResponse, COUNT(sqr) as count FROM SurveyQuestionResponse sqr " +
            "WHERE sqr.question.id = :questionId AND sqr.ratingResponse IS NOT NULL AND sqr.isActive = true " +
            "GROUP BY sqr.ratingResponse ORDER BY sqr.ratingResponse ASC")
    List<Object[]> getRatingDistributionByQuestionId(@Param("questionId") Long questionId);

    @Query("SELECT COUNT(sqr) FROM SurveyQuestionResponse sqr " +
            "WHERE sqr.question.id = :questionId AND sqr.booleanResponse = :value AND sqr.isActive = true")
    Long getBooleanResponseCountByQuestionId(@Param("questionId") Long questionId, @Param("value") Boolean value);

    @Query("SELECT sqr FROM SurveyQuestionResponse sqr " +
            "WHERE sqr.question.questionType IN ('TEXT_SHORT', 'TEXT_LONG') " +
            "AND sqr.textResponse IS NOT NULL AND sqr.isActive = true " +
            "ORDER BY sqr.createdAt DESC")
    List<SurveyQuestionResponse> findRecentTextResponses(org.springframework.data.domain.Pageable pageable);
}
