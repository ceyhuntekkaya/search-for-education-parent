package com.genixo.education.search.repository.survey;

import com.genixo.education.search.dto.survey.SatisfactionTrendDto;
import com.genixo.education.search.entity.survey.SurveyRating;
import com.genixo.education.search.enumaration.RatingCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface SurveyRatingRepository extends JpaRepository<SurveyRating, Long> {
    @Query("SELECT sr FROM SurveyRating sr WHERE sr.isActive = true AND sr.id = :id")
    Optional<SurveyRating> findByIdAndIsActiveTrue(@Param("id") Long id);

    @Query("SELECT sr FROM SurveyRating sr " +
            "WHERE sr.school.id = :schoolId AND sr.isActive = true AND sr.isPublic = true " +
            "AND (:category IS NULL OR sr.ratingCategory = :category) " +
            "ORDER BY sr.createdAt DESC")
    List<SurveyRating> findPublicRatingsBySchoolAndCategory(
            @Param("schoolId") Long schoolId,
            @Param("category") RatingCategory category,
            Pageable pageable);

    @Query("SELECT sr.ratingCategory, AVG(sr.ratingValue) FROM SurveyRating sr " +
            "WHERE sr.school.id = :schoolId AND sr.isActive = true AND sr.isPublic = true " +
            "GROUP BY sr.ratingCategory")
    Map<RatingCategory, Double> getAverageRatingsBySchoolAndCategory(@Param("schoolId") Long schoolId);

    @Query("SELECT sr.ratingCategory, AVG(sr.ratingValue) FROM SurveyRating sr " +
            "WHERE sr.surveyResponse.id = :responseId AND sr.isActive = true " +
            "GROUP BY sr.ratingCategory")
    Map<RatingCategory, Double> getAverageRatingsByResponseAndCategory(@Param("responseId") Long responseId);

    @Query("SELECT AVG(sr.ratingValue) FROM SurveyRating sr " +
            "WHERE sr.school.id = :schoolId AND sr.ratingCategory = :category AND sr.isActive = true AND sr.isPublic = true")
    Double getAverageRatingBySchoolAndCategory(@Param("schoolId") Long schoolId, @Param("category") RatingCategory category);

    @Query("SELECT new com.genixo.education.search.dto.survey.SatisfactionTrendDto(" +
            "CAST(sr.createdAt AS date), " +
            "AVG(CASE WHEN sr.ratingCategory = 'OVERALL_SATISFACTION' THEN sr.ratingValue ELSE NULL END), " +
            "AVG(CASE WHEN sr.ratingCategory = 'CLEANLINESS' THEN sr.ratingValue ELSE NULL END), " +
            "AVG(CASE WHEN sr.ratingCategory = 'STAFF_FRIENDLINESS' THEN sr.ratingValue ELSE NULL END), " +
            "AVG(CASE WHEN sr.ratingCategory = 'FACILITIES' THEN sr.ratingValue ELSE NULL END), " +
            "AVG(CASE WHEN sr.ratingCategory = 'COMMUNICATION' THEN sr.ratingValue ELSE NULL END), " +
            "COUNT(sr), " +
            "'STABLE') " + // trend direction would be calculated separately
            "FROM SurveyRating sr " +
            "WHERE sr.school.id = :schoolId AND sr.isActive = true AND sr.isPublic = true " +
            "AND sr.createdAt BETWEEN :fromDate AND :toDate " +
            "GROUP BY CAST(sr.createdAt AS date) " +
            "ORDER BY CAST(sr.createdAt AS date) ASC")
    List<SatisfactionTrendDto> getSatisfactionTrends(
            @Param("schoolId") Long schoolId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate);

    @Query("SELECT COUNT(sr) FROM SurveyRating sr WHERE sr.school.id = :schoolId AND sr.isActive = true AND sr.isPublic = true")
    Long countPublicRatingsBySchool(@Param("schoolId") Long schoolId);

    @Query("SELECT sr.ratingValue, COUNT(sr) FROM SurveyRating sr " +
            "WHERE sr.school.id = :schoolId AND sr.ratingCategory = :category AND sr.isActive = true AND sr.isPublic = true " +
            "GROUP BY sr.ratingValue ORDER BY sr.ratingValue ASC")
    List<Object[]> getRatingDistributionBySchoolAndCategory(@Param("schoolId") Long schoolId, @Param("category") RatingCategory category);

    @Query("SELECT sr FROM SurveyRating sr " +
            "WHERE sr.isActive = true AND sr.isFlagged = true AND sr.isPublic = true " +
            "ORDER BY sr.flaggedAt DESC")
    List<SurveyRating> findFlaggedPublicRatings(Pageable pageable);

    @Query("SELECT sr FROM SurveyRating sr " +
            "WHERE sr.school.id = :schoolId AND sr.isActive = true " +
            "AND sr.ratingValue >= 4 AND sr.isPublic = true " +
            "ORDER BY sr.createdAt DESC")
    List<SurveyRating> findTopRatingsBySchool(@Param("schoolId") Long schoolId, Pageable pageable);

    @Query("SELECT sr FROM SurveyRating sr " +
            "WHERE sr.school.id = :schoolId AND sr.isActive = true " +
            "AND sr.ratingValue <= 2 AND sr.isPublic = true " +
            "ORDER BY sr.createdAt DESC")
    List<SurveyRating> findLowRatingsBySchool(@Param("schoolId") Long schoolId, Pageable pageable);

    @Query("SELECT AVG(sr.ratingValue) FROM SurveyRating sr " +
            "WHERE sr.ratingCategory = :category AND sr.isActive = true AND sr.isPublic = true")
    Double getOverallAverageRatingByCategory(@Param("category") RatingCategory category);

    @Query("SELECT COUNT(sr) FROM SurveyRating sr " +
            "WHERE sr.isActive = true AND sr.createdAt >= :fromDate")
    Long countRatingsSince(@Param("fromDate") LocalDateTime fromDate);
}
