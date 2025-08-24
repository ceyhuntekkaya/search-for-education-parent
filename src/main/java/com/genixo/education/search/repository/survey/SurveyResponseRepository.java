package com.genixo.education.search.repository.survey;

import com.genixo.education.search.dto.survey.SchoolSurveyPerformanceDto;
import com.genixo.education.search.entity.survey.SurveyResponse;
import com.genixo.education.search.enumaration.ResponseStatus;
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
public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {
    @Query("SELECT sr FROM SurveyResponse sr WHERE sr.isActive = true AND sr.id = :id")
    Optional<SurveyResponse> findByIdAndIsActiveTrue(@Param("id") Long id);

    @Query("SELECT sr FROM SurveyResponse sr WHERE sr.isActive = true AND sr.responseToken = :token")
    Optional<SurveyResponse> findByResponseTokenAndIsActiveTrue(@Param("token") String token);

    @Query("SELECT CASE WHEN COUNT(sr) > 0 THEN true ELSE false END FROM SurveyResponse sr WHERE sr.appointment.id = :appointmentId AND sr.isActive = true")
    boolean existsByAppointmentIdAndIsActiveTrue(@Param("appointmentId") Long appointmentId);

    @Query("SELECT sr FROM SurveyResponse sr WHERE sr.survey.id = :surveyId AND sr.isActive = true ORDER BY sr.createdAt DESC")
    Page<SurveyResponse> findBySurveyIdAndIsActiveTrueOrderByCreatedAtDesc(@Param("surveyId") Long surveyId, Pageable pageable);

    @Query("SELECT COUNT(sr) FROM SurveyResponse sr WHERE sr.survey.id = :surveyId AND sr.isActive = true")
    Long countBySurveyIdAndIsActiveTrue(@Param("surveyId") Long surveyId);

    @Query("SELECT COUNT(sr) FROM SurveyResponse sr WHERE sr.survey.id = :surveyId AND sr.status IN :statuses AND sr.isActive = true")
    Long countBySurveyIdAndStatusInAndIsActiveTrue(@Param("surveyId") Long surveyId, @Param("statuses") List<ResponseStatus> statuses);

    @Query("SELECT sr FROM SurveyResponse sr WHERE sr.school.id = :schoolId AND sr.isActive = true ORDER BY sr.createdAt DESC")
    Page<SurveyResponse> findBySchoolIdAndIsActiveTrueOrderByCreatedAtDesc(@Param("schoolId") Long schoolId, Pageable pageable);

    @Query("SELECT sr FROM SurveyResponse sr WHERE sr.appointment.id = :appointmentId AND sr.isActive = true")
    Optional<SurveyResponse> findByAppointmentIdAndIsActiveTrue(@Param("appointmentId") Long appointmentId);

    @Query("SELECT AVG(sr.overallRating) FROM SurveyResponse sr WHERE sr.survey.id = :surveyId AND sr.overallRating IS NOT NULL AND sr.isActive = true")
    Double getAverageOverallRatingBySurveyId(@Param("surveyId") Long surveyId);

    @Query("SELECT AVG(sr.completionTimeSeconds) FROM SurveyResponse sr WHERE sr.survey.id = :surveyId AND sr.completionTimeSeconds IS NOT NULL AND sr.isActive = true")
    Integer getAverageCompletionTimeBySurveyId(@Param("surveyId") Long surveyId);

    @Query("SELECT sr FROM SurveyResponse sr " +
            "WHERE sr.survey.id = :surveyId AND sr.isActive = true " +
            "AND sr.status IN ('STARTED', 'IN_PROGRESS') " +
            "AND sr.reminderCount < :maxReminders " +
            "AND (sr.lastReminderSentAt IS NULL )")
    List<SurveyResponse> findIncompleteResponsesNeedingReminders(
            @Param("surveyId") Long surveyId,
            @Param("maxReminders") Integer maxReminders);

    @Query("SELECT sr FROM SurveyResponse sr " +
            "WHERE sr.survey.id = :surveyId AND sr.isActive = true " +
            "AND sr.createdAt BETWEEN :periodStart AND :periodEnd " +
            "ORDER BY sr.createdAt DESC")
    List<SurveyResponse> findBySurveyIdAndPeriodAndIsActiveTrue(
            @Param("surveyId") Long surveyId,
            @Param("periodStart") LocalDateTime periodStart,
            @Param("periodEnd") LocalDateTime periodEnd);

    @Query("SELECT new com.genixo.education.search.dto.survey.SchoolSurveyPerformanceDto(" +
            "s.id, s.name, s.campus.name, " +
            "COUNT(sr), " +
            "CASE WHEN COUNT(DISTINCT sr.appointment.id) > 0 THEN CAST(COUNT(sr) AS DOUBLE) / CAST(COUNT(DISTINCT sr.appointment.id) AS DOUBLE) ELSE 0.0 END, " +
            "CASE WHEN COUNT(sr) > 0 THEN CAST(SUM(CASE WHEN sr.status IN ('COMPLETED', 'SUBMITTED') THEN 1 ELSE 0 END) AS DOUBLE) / CAST(COUNT(sr) AS DOUBLE) ELSE 0.0 END, " +
            "AVG(sr.overallRating), " +
            "AVG(sr.cleanlinessRating), " +
            "AVG(sr.staffRating), " +
            "AVG(sr.facilitiesRating), " +
            "AVG(sr.communicationRating), " +
            "CASE WHEN COUNT(sr) > 0 THEN CAST(SUM(CASE WHEN sr.wouldRecommend = true THEN 1 ELSE 0 END) AS DOUBLE) / CAST(COUNT(sr) AS DOUBLE) ELSE 0.0 END, " +
            "AVG(CAST(sr.likelihoodToEnroll AS DOUBLE)), " +
            "'GOOD') " + // performance level would be calculated based on ratings
            "FROM SurveyResponse sr " +
            "JOIN sr.school s " +
            "WHERE s.id IN :schoolIds AND sr.isActive = true " +
            "GROUP BY s.id, s.name, s.campus.name")
    List<SchoolSurveyPerformanceDto> getSchoolPerformanceComparison(@Param("schoolIds") List<Long> schoolIds);

    @Query("SELECT COUNT(sr) FROM SurveyResponse sr WHERE sr.isActive = true AND sr.createdAt >= :fromDate")
    Long countResponsesSince(@Param("fromDate") LocalDateTime fromDate);

    @Query("SELECT AVG(sr.overallRating) FROM SurveyResponse sr WHERE sr.isActive = true AND sr.overallRating IS NOT NULL")
    Double getOverallAverageRating();

    @Query("SELECT sr FROM SurveyResponse sr WHERE sr.isActive = true AND sr.status = 'COMPLETED' AND sr.submittedAt IS NULL ORDER BY sr.completedAt ASC")
    List<SurveyResponse> findCompletedButNotSubmittedResponses(Pageable pageable);
}
