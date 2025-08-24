package com.genixo.education.search.repository.survey;

import com.genixo.education.search.entity.survey.SurveyQuestion;
import com.genixo.education.search.enumaration.QuestionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SurveyQuestionRepository extends JpaRepository<SurveyQuestion, Long> {
    @Query("SELECT sq FROM SurveyQuestion sq WHERE sq.isActive = true AND sq.id = :id")
    Optional<SurveyQuestion> findByIdAndIsActiveTrue(@Param("id") Long id);

    @Query("SELECT sq FROM SurveyQuestion sq WHERE sq.survey.id = :surveyId AND sq.isActive = true ORDER BY sq.sortOrder ASC")
    List<SurveyQuestion> findBySurveyIdAndIsActiveTrueOrderBySortOrderAsc(@Param("surveyId") Long surveyId);

    @Query("SELECT sq FROM SurveyQuestion sq WHERE sq.survey.id = :surveyId AND sq.isRequired = true AND sq.isActive = true ORDER BY sq.sortOrder ASC")
    List<SurveyQuestion> findBySurveyIdAndIsRequiredTrueAndIsActiveTrue(@Param("surveyId") Long surveyId);

    @Query("SELECT sq FROM SurveyQuestion sq WHERE sq.survey.id = :surveyId AND sq.questionType = :questionType AND sq.isActive = true ORDER BY sq.sortOrder ASC")
    List<SurveyQuestion> findBySurveyIdAndQuestionTypeAndIsActiveTrueOrderBySortOrderAsc(@Param("surveyId") Long surveyId, @Param("questionType") QuestionType questionType);

    @Query("SELECT COALESCE(MAX(sq.sortOrder), 0) FROM SurveyQuestion sq WHERE sq.survey.id = :surveyId AND sq.isActive = true")
    Integer getMaxSortOrderBySurveyId(@Param("surveyId") Long surveyId);

    @Query("SELECT COUNT(sq) FROM SurveyQuestion sq WHERE sq.survey.id = :surveyId AND sq.isActive = true")
    Long countBySurveyIdAndIsActiveTrue(@Param("surveyId") Long surveyId);

    @Query("SELECT COUNT(sq) FROM SurveyQuestion sq WHERE sq.survey.id = :surveyId AND sq.isRequired = true AND sq.isActive = true")
    Long countRequiredQuestionsBySurveyId(@Param("surveyId") Long surveyId);

    @Query("SELECT sq FROM SurveyQuestion sq WHERE sq.survey.id = :surveyId AND sq.questionType IN ('RATING_STAR', 'RATING_SCALE') AND sq.isActive = true ORDER BY sq.sortOrder ASC")
    List<SurveyQuestion> findRatingQuestionsBySurveyId(@Param("surveyId") Long surveyId);

    @Query("SELECT sq FROM SurveyQuestion sq WHERE sq.showIfQuestionId = :questionId AND sq.isActive = true")
    List<SurveyQuestion> findConditionalQuestionsByParentId(@Param("questionId") Long questionId);

    @Query("SELECT DISTINCT sq.survey.id FROM SurveyQuestion sq WHERE sq.questionType = :questionType AND sq.isActive = true")
    List<Long> findSurveyIdsByQuestionType(@Param("questionType") QuestionType questionType);
}
