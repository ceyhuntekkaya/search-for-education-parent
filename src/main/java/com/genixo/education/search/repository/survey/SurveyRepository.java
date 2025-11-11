package com.genixo.education.search.repository.survey;

import com.genixo.education.search.dto.survey.SurveyAnalyticsDto;
import com.genixo.education.search.entity.survey.Survey;
import com.genixo.education.search.enumaration.SurveyTriggerEvent;
import com.genixo.education.search.enumaration.SurveyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {

    @Query("SELECT s FROM Survey s WHERE s.isActive = true AND s.id = :id")
    Optional<Survey> findByIdAndIsActiveTrue(@Param("id") Long id);

    @Query("SELECT s FROM Survey s WHERE s.isActive = true AND s.id = :id AND s.showResultsToPublic = true")
    Optional<Survey> findByIdAndIsActiveTrueAndShowResultsToPublicTrue(@Param("id") Long id);

    @Query("SELECT s FROM Survey s WHERE s.isActive = true ORDER BY s.createdAt DESC")
    List<Survey> findAllActiveOrderByCreatedAtDesc();

    @Query("SELECT s FROM Survey s WHERE s.isActive = true AND s.surveyType = :surveyType ORDER BY s.createdAt DESC")
    List<Survey> findBySurveyTypeAndIsActiveTrueOrderByCreatedAtDesc(@Param("surveyType") SurveyType surveyType);

    @Query("SELECT s FROM Survey s WHERE s.isActive = true AND s.triggerEvent = :triggerEvent ORDER BY s.createdAt DESC")
    List<Survey> findByTriggerEventAndIsActiveTrueOrderByCreatedAtDesc(@Param("triggerEvent") SurveyTriggerEvent triggerEvent);

    // Complex search query
    @Query("SELECT DISTINCT s FROM Survey s " +
            "WHERE s.isActive = true " +
            "AND (:searchTerm IS NULL OR " +
            "    LOWER(s.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "    LOWER(s.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "AND (:surveyTypes IS NULL OR s.surveyType IN :surveyTypes) " +
            "AND (:triggerEvents IS NULL OR s.triggerEvent IN :triggerEvents) " +
            "AND (:isActive IS NULL OR s.isActive = :isActive) " +
            "AND (:isAnonymous IS NULL OR s.isAnonymous = :isAnonymous) " +
            "AND (:isMandatory IS NULL OR s.isMandatory = :isMandatory) " +
            "AND (:showResultsToPublic IS NULL OR s.showResultsToPublic = :showResultsToPublic) " +
            "AND (:createdFrom IS NULL OR s.createdAt >= :createdFrom) " +
            "AND (:createdTo IS NULL OR s.createdAt <= :createdTo) " +
            "AND (:minResponses IS NULL OR s.totalCompleted >= :minResponses) " +
            "AND (:maxResponses IS NULL OR s.totalCompleted <= :maxResponses) " +
            "AND (:minCompletionRate IS NULL OR " +
            "    (s.totalSent > 0 AND (CAST(s.totalCompleted AS DOUBLE) / CAST(s.totalSent AS DOUBLE)) >= :minCompletionRate)) " +
            "AND (:maxCompletionRate IS NULL OR " +
            "    (s.totalSent > 0 AND (CAST(s.totalCompleted AS DOUBLE) / CAST(s.totalSent AS DOUBLE)) <= :maxCompletionRate)) " +
            "AND (:minAverageRating IS NULL OR s.averageRating >= :minAverageRating) " +
            "AND (:maxAverageRating IS NULL OR s.averageRating <= :maxAverageRating) " +
            "AND (:accessibleSchoolIds IS NULL OR " +
            "    EXISTS(SELECT 1 FROM SurveyResponse sr WHERE sr.survey.id = s.id AND sr.school.id IN :accessibleSchoolIds))")
    Page<Survey> searchSurveys(
            @Param("searchTerm") String searchTerm,
            @Param("surveyTypes") List<SurveyType> surveyTypes,
            @Param("triggerEvents") List<SurveyTriggerEvent> triggerEvents,
            @Param("isActive") Boolean isActive,
            @Param("isAnonymous") Boolean isAnonymous,
            @Param("isMandatory") Boolean isMandatory,
            @Param("showResultsToPublic") Boolean showResultsToPublic,
            @Param("createdFrom") LocalDateTime createdFrom,
            @Param("createdTo") LocalDateTime createdTo,
            @Param("minResponses") Long minResponses,
            @Param("maxResponses") Long maxResponses,
            @Param("minCompletionRate") Double minCompletionRate,
            @Param("maxCompletionRate") Double maxCompletionRate,
            @Param("minAverageRating") Double minAverageRating,
            @Param("maxAverageRating") Double maxAverageRating,
            @Param("accessibleSchoolIds") List<Long> accessibleSchoolIds,
            Pageable pageable);

    @Query("SELECT new com.genixo.education.search.dto.survey.SurveyAnalyticsDto(" +
            "s.id, s.title, s.surveyType, " +
            "COALESCE((SELECT MIN(sr.createdAt) FROM SurveyResponse sr WHERE sr.survey.id = s.id), s.createdAt), " +
            "COALESCE((SELECT MAX(sr.createdAt) FROM SurveyResponse sr WHERE sr.survey.id = s.id), s.createdAt), " +
            "COALESCE(s.totalSent, 0L), " +
            "COALESCE(s.totalStarted, 0L), " +
            "COALESCE(s.totalCompleted, 0L), " +
            "COALESCE((SELECT COUNT(sr) FROM SurveyResponse sr WHERE sr.survey.id = s.id AND sr.status = 'SUBMITTED'), 0L), " +
            "COALESCE((SELECT COUNT(sr) FROM SurveyResponse sr WHERE sr.survey.id = s.id AND sr.status = 'ABANDONED'), 0L), " +
            "CASE WHEN s.totalSent > 0 THEN CAST(s.totalStarted AS DOUBLE) / CAST(s.totalSent AS DOUBLE) ELSE 0.0 END, " +
            "CASE WHEN s.totalStarted > 0 THEN CAST(s.totalCompleted AS DOUBLE) / CAST(s.totalStarted AS DOUBLE) ELSE 0.0 END, " +
            "CASE WHEN s.totalSent > 0 THEN CAST((SELECT COUNT(sr) FROM SurveyResponse sr WHERE sr.survey.id = s.id AND sr.status = 'SUBMITTED') AS DOUBLE) / CAST(s.totalSent AS DOUBLE) ELSE 0.0 END, " +
            "CASE WHEN s.totalSent > 0 THEN CAST((SELECT COUNT(sr) FROM SurveyResponse sr WHERE sr.survey.id = s.id AND sr.status = 'ABANDONED') AS DOUBLE) / CAST(s.totalSent AS DOUBLE) ELSE 0.0 END, " +
            "COALESCE(s.averageCompletionTimeSeconds, 0), " +
            "0, 0, 0, " + // median, fastest, slowest completion times - would need separate calculations
            "COALESCE(s.averageRating, 0.0)) " +
            "FROM Survey s WHERE s.id = :surveyId AND s.isActive = true")
    SurveyAnalyticsDto getSurveyAnalytics(@Param("surveyId") Long surveyId);

    @Query("SELECT COUNT(s) FROM Survey s WHERE s.isActive = true")
    Long countActiveSurveys();

    @Query("SELECT COUNT(s) FROM Survey s WHERE s.isActive = true AND s.surveyType = :surveyType")
    Long countActiveSurveysByType(@Param("surveyType") SurveyType surveyType);

    @Query("SELECT s FROM Survey s WHERE s.isActive = true AND s.triggerEvent = 'APPOINTMENT_COMPLETED' ORDER BY s.createdAt DESC")
    List<Survey> findAppointmentFeedbackSurveys();

    @Query("SELECT AVG(s.averageRating) FROM Survey s WHERE s.isActive = true AND s.averageRating IS NOT NULL")
    Double getOverallAverageRating();

    @Query("SELECT SUM(s.totalCompleted) FROM Survey s WHERE s.isActive = true")
    Long getTotalCompletedResponses();
}