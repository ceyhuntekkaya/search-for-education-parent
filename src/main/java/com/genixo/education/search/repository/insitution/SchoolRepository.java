package com.genixo.education.search.repository.insitution;

import com.genixo.education.search.dto.institution.SchoolStatisticsDto;
import com.genixo.education.search.dto.institution.SchoolSummaryDto;
import com.genixo.education.search.entity.institution.School;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolRepository extends JpaRepository<School, Long> {
    @Query("SELECT s FROM School s WHERE s.isActive = true AND s.id = :id")
    Optional<School> findByIdAndIsActiveTrue(@Param("id") Long id);

    @Query("SELECT s FROM School s WHERE s.isActive = true AND LOWER(s.slug) = LOWER(:slug)")
    Optional<School> findBySlugAndIsActiveTrue(@Param("slug") String slug);

    @Query("SELECT s FROM School s " +
            "WHERE s.isActive = true AND s.campus.isSubscribed = true AND LOWER(s.slug) = LOWER(:slug)")
    Optional<School> findBySlugAndIsActiveTrueAndCampusIsSubscribedTrue(@Param("slug") String slug);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
            "FROM School s WHERE LOWER(s.name) = LOWER(:name) AND s.campus.id = :campusId AND s.isActive = true")
    boolean existsByNameIgnoreCaseAndCampusIdAndIsActiveTrue(@Param("name") String name, @Param("campusId") Long campusId);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
            "FROM School s WHERE s.slug = :slug AND s.isActive = true")
    boolean existsBySlug(@Param("slug") String slug);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
            "FROM School s WHERE s.id = :schoolId AND s.campus.id = :campusId AND s.isActive = true")
    boolean existsByIdAndCampusId(@Param("schoolId") Long schoolId, @Param("campusId") Long campusId);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
            "FROM School s WHERE s.id = :schoolId AND s.campus.brand.id = :brandId AND s.isActive = true")
    boolean existsByIdAndCampusBrandId(@Param("schoolId") Long schoolId, @Param("brandId") Long brandId);

    @Query("SELECT s FROM School s WHERE s.campus.id = :campusId AND s.isActive = true ORDER BY s.name ASC")
    List<School> findByCampusIdAndIsActiveTrueOrderByName(@Param("campusId") Long campusId);

    @Query("SELECT s FROM School s WHERE s.institutionType.id = :typeId AND s.isActive = true ORDER BY s.name ASC")
    List<School> findByInstitutionTypeIdAndIsActiveTrueOrderByName(@Param("typeId") Long typeId);

    @Query("SELECT s FROM School s WHERE s.id IN :ids AND s.isActive = true ORDER BY s.name ASC")
    List<School> findByIdInAndIsActiveTrue(@Param("ids") List<Long> ids);

    @Modifying
    @Query("UPDATE School s SET s.isActive = :active, s.updatedBy = :userId WHERE s.id = :id")
    void updateIsActiveByIdAndUserId(@Param("id") Long id, @Param("active") Boolean active, @Param("userId") Long userId);

    @Modifying
    @Query("UPDATE School s SET s.viewCount = s.viewCount + 1 WHERE s.id = :id")
    void incrementViewCount(@Param("id") Long id);

    @Query("SELECT new com.genixo.education.search.dto.institution.SchoolSummaryDto(" +
            "s.id, s.name, s.slug, s.logoUrl, s.institutionType.displayName, " +
            "s.minAge, s.maxAge, s.monthlyFee, s.ratingAverage, s.ratingCount, " +
            "CASE WHEN EXISTS(SELECT 1 FROM CampaignSchool cs WHERE cs.school.id = s.id AND cs.status = 'ACTIVE') " +
            "THEN true ELSE false END) " +
            "FROM School s WHERE s.isActive = true ORDER BY s.name ASC")
    List<SchoolSummaryDto> findSchoolSummaries();

    // Complex search query
    @Query("SELECT DISTINCT s FROM School s " +
            "LEFT JOIN s.campus c " +
            "LEFT JOIN c.brand b " +
            "LEFT JOIN s.institutionType it " +
            "LEFT JOIN s.propertyValues pv " +
            "LEFT JOIN pv.property p " +
            "WHERE s.isActive = true " +
            "AND (:searchTerm IS NULL OR " +
            "    LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "    LOWER(s.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "    LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "    LOWER(b.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "    LOWER(it.displayName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "AND (:institutionTypeIds IS NULL OR s.institutionType.id IN :institutionTypeIds) " +
            "AND (:minAge IS NULL OR s.minAge IS NULL OR s.minAge <= :minAge) " +
            "AND (:maxAge IS NULL OR s.maxAge IS NULL OR s.maxAge >= :maxAge) " +
            "AND (:minFee IS NULL OR s.monthlyFee IS NULL OR s.monthlyFee >= :minFee) " +
            "AND (:maxFee IS NULL OR s.monthlyFee IS NULL OR s.monthlyFee <= :maxFee) " +
            "AND (:curriculumType IS NULL OR LOWER(s.curriculumType) LIKE LOWER(CONCAT('%', :curriculumType, '%'))) " +
            "AND (:languageOfInstruction IS NULL OR LOWER(s.languageOfInstruction) LIKE LOWER(CONCAT('%', :languageOfInstruction, '%'))) " +
            "AND (:countryId IS NULL OR c.province.country.id = :countryId) " +
            "AND (:provinceId IS NULL OR c.province.id = :provinceId) " +
            "AND (:districtId IS NULL OR c.neighborhood.district.id = :districtId) " +
            "AND (:neighborhoodId IS NULL OR c.neighborhood.id = :neighborhoodId) " +
            "AND (:minRating IS NULL OR s.ratingAverage IS NULL OR s.ratingAverage >= :minRating) " +
            "AND (:hasActiveCampaigns IS NULL OR " +
            "    (:hasActiveCampaigns = true AND EXISTS(SELECT 1 FROM CampaignSchool cs WHERE cs.school.id = s.id AND cs.status = 'ACTIVE')) OR " +
            "    (:hasActiveCampaigns = false AND NOT EXISTS(SELECT 1 FROM CampaignSchool cs WHERE cs.school.id = s.id AND cs.status = 'ACTIVE'))) " +
            "AND (:isSubscribed IS NULL OR c.isSubscribed = :isSubscribed) " +
            "AND (:latitude IS NULL OR :longitude IS NULL OR :radiusKm IS NULL OR " +
            "    (6371 * acos(cos(radians(:latitude)) * cos(radians(COALESCE(c.latitude, 0))) * " +
            "    cos(radians(COALESCE(c.longitude, 0)) - radians(:longitude)) + " +
            "    sin(radians(:latitude)) * sin(radians(COALESCE(c.latitude, 0))))) <= :radiusKm)")
    Page<School> searchSchools(
            @Param("searchTerm") String searchTerm,
            @Param("institutionTypeIds") List<Long> institutionTypeIds,
            @Param("minAge") Integer minAge,
            @Param("maxAge") Integer maxAge,
            @Param("minFee") Double minFee,
            @Param("maxFee") Double maxFee,
            @Param("curriculumType") String curriculumType,
            @Param("languageOfInstruction") String languageOfInstruction,
            @Param("countryId") Long countryId,
            @Param("provinceId") Long provinceId,
            @Param("districtId") Long districtId,
            @Param("neighborhoodId") Long neighborhoodId,
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radiusKm") Double radiusKm,
            @Param("minRating") Double minRating,
            @Param("hasActiveCampaigns") Boolean hasActiveCampaigns,
            @Param("isSubscribed") Boolean isSubscribed,
            Pageable pageable);


    /* ceyhun
    @Query("SELECT new com.genixo.education.search.dto.institution.SchoolStatisticsDto(" +
            "s.viewCount, " +
            "COALESCE((SELECT COUNT(v) FROM VisitorLog v WHERE v.school.id = s.id AND v.visitTime >= :thirtyDaysAgo), 0L), " +
            "COALESCE((SELECT COUNT(v) FROM VisitorLog v WHERE v.school.id = s.id AND v.visitTime >= :sevenDaysAgo), 0L), " +
            "COALESCE((SELECT COUNT(v) FROM VisitorLog v WHERE v.school.id = s.id AND v.visitTime >= CURRENT_DATE), 0L), " +
            "COALESCE((SELECT COUNT(a) FROM Appointment a WHERE a.school.id = s.id), 0L), " +
            "COALESCE((SELECT COUNT(a) FROM Appointment a WHERE a.school.id = s.id AND a.status = 'COMPLETED'), 0L), " +
            "COALESCE((SELECT COUNT(m) FROM Message m WHERE m.school.id = s.id), 0L), " +
            "0L, " + // enrollments - would need separate entity
            "CASE WHEN (SELECT COUNT(a) FROM Appointment a WHERE a.school.id = s.id) > 0 " +
            "THEN CAST((SELECT COUNT(a) FROM Appointment a WHERE a.school.id = s.id AND a.status = 'COMPLETED') AS DOUBLE) / " +
            "     CAST((SELECT COUNT(a) FROM Appointment a WHERE a.school.id = s.id) AS DOUBLE) * 100 " +
            "ELSE 0.0 END, " +
            "CASE WHEN s.viewCount > 0 " +
            "THEN CAST((SELECT COUNT(a) FROM Appointment a WHERE a.school.id = s.id) AS DOUBLE) / " +
            "     CAST(s.viewCount AS DOUBLE) * 100 " +
            "ELSE 0.0 END, " +
            "COALESCE(s.ratingAverage, 0.0), " +
            "COALESCE(s.ratingCount, 0L), " +
            "'{}', " + // rating distribution - would be calculated separately
            "COALESCE(s.likeCount, 0L), " +
            "COALESCE(s.postCount, 0L), " +
            "COALESCE(s.likeCount, 0L)) " +
            "FROM School s WHERE s.id = :schoolId AND s.isActive = true")
    SchoolStatisticsDto getSchoolStatistics(@Param("schoolId") Long schoolId, @Param("thirtyDaysAgo") LocalDateTime thirtyDaysAgo, @Param("sevenDaysAgo") LocalDateTime sevenDaysAgo);


     */
    @Query("SELECT s FROM School s WHERE s.isActive = true AND " +
            "s.monthlyFee BETWEEN :minPrice AND :maxPrice " +
            "ORDER BY s.monthlyFee ASC")
    List<School> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);

    @Query("SELECT s FROM School s WHERE s.isActive = true AND " +
            "s.ratingAverage >= :minRating " +
            "ORDER BY s.ratingAverage DESC, s.ratingCount DESC")
    List<School> findTopRatedSchools(@Param("minRating") Double minRating, Pageable pageable);

    @Query("SELECT DISTINCT s.curriculumType FROM School s WHERE s.isActive = true AND s.curriculumType IS NOT NULL")
    List<String> findDistinctCurriculumTypes();

    @Query("SELECT DISTINCT s.languageOfInstruction FROM School s WHERE s.isActive = true AND s.languageOfInstruction IS NOT NULL")
    List<String> findDistinctLanguagesOfInstruction();

    @Query("SELECT COUNT(s) FROM School s WHERE s.isActive = true AND s.campus.isSubscribed = true")
    Long countActiveSubscribedSchools();

    @Query("SELECT s FROM School s WHERE s.isActive = true AND s.campus.isSubscribed = true " +
            "ORDER BY s.createdAt DESC")
    List<School> findLatestSubscribedSchools(Pageable pageable);

    List<Long> findIdsByCampus_Brand_Id(Long entityId);

    Optional<Object> findByIdAndIsActiveTrueAndCampusIsSubscribedTrue(Long schoolId);

    @Query("SELECT s.id FROM School s WHERE s.isActive = true")
    List<Long> findAllActiveSchoolIds();

    List<Long> findIdsByCampusId(Long entityId);
}
