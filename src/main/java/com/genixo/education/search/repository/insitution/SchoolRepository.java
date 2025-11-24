package com.genixo.education.search.repository.insitution;

import com.genixo.education.search.dto.appointment.AppointmentAvailabilityDto;
import com.genixo.education.search.dto.institution.SchoolStatisticsDto;
import com.genixo.education.search.dto.institution.SchoolSummaryDto;
import com.genixo.education.search.entity.institution.School;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
            "s.facebookUrl, s.twitterUrl, s.instagramUrl, s.linkedinUrl, s.instagramUrl,  " +
            "CASE WHEN EXISTS(SELECT 1 FROM CampaignSchool cs WHERE cs.school.id = s.id AND cs.status = 'ACTIVE') " +
            "THEN true ELSE false END)" +




            "FROM School s WHERE s.isActive = true ORDER BY s.name ASC")
    List<SchoolSummaryDto> findSchoolSummaries();

    // Repository

        // 1. Sadece ID'leri getir (pagination burada)
        // Repository
        @Query(value = """
                WITH filtered_schools AS (
                    SELECT DISTINCT s.id, s.name, s.rating_average, s.created_at
                    FROM schools s
                    LEFT JOIN campuses c ON c.id = s.campus_id
                    LEFT JOIN brands b ON b.id = c.brand_id
                    LEFT JOIN provinces p ON p.id = c.province_id
                    LEFT JOIN neighborhoods n ON n.id = c.neighborhood_id
                    LEFT JOIN institution_types it ON it.id = s.institution_type_id
                    LEFT JOIN institution_property_values pv ON pv.school_id = s.id
                                    
                                    
                   LEFT JOIN school_pricing sp ON sp.school_id = s.id
                                    
                                    
                                    
                                    
                    WHERE s.is_active = true
                    AND (:searchTerm IS NULL OR :searchTerm = '' OR
                        LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
                        LOWER(s.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
                        LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
                        LOWER(b.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
                        LOWER(it.display_name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
                    AND (:institutionTypeIds IS NULL OR it.id = ANY(CAST(:institutionTypeIds AS bigint[])))
                    AND (:propertyFilters IS NULL OR pv.property_id = ANY(CAST(:propertyFilters AS bigint[])))
                    AND (:minAge IS NULL OR s.min_age IS NULL OR s.min_age >= :minAge)
                    AND (:maxAge IS NULL OR s.max_age IS NULL OR s.max_age <= :maxAge)
                                      
                                      
                    AND (:minFee IS NULL OR sp.annual_tuition IS NULL OR sp.annual_tuition >= :minFee)
                    AND (:maxFee IS NULL OR sp.annual_tuition IS NULL OR sp.annual_tuition <= :maxFee)
                                      
                                      
                                      
                    AND (:curriculumType IS NULL OR :curriculumType = '' OR
                        LOWER(s.curriculum_type) LIKE LOWER(CONCAT('%', :curriculumType, '%')))
                    AND (:languageOfInstruction IS NULL OR :languageOfInstruction = '' OR
                        LOWER(s.language_of_instruction) LIKE LOWER(CONCAT('%', :languageOfInstruction, '%')))
                    AND (:countryId IS NULL OR p.country_id = :countryId)
                    AND (:provinceId IS NULL OR c.province_id = :provinceId)
                    AND (:districtId IS NULL OR n.district_id = :districtId)
                    AND (:neighborhoodId IS NULL OR c.neighborhood_id = :neighborhoodId)
                    AND (:minRating IS NULL OR s.rating_average IS NULL OR s.rating_average >= :minRating)
                    AND (:hasActiveCampaigns IS NULL OR
                        (:hasActiveCampaigns = true AND EXISTS(
                            SELECT 1 FROM campaign_schools cs 
                            WHERE cs.school_id = s.id AND cs.status = 'ACTIVE')) OR
                        (:hasActiveCampaigns = false AND NOT EXISTS(
                            SELECT 1 FROM campaign_schools cs 
                            WHERE cs.school_id = s.id AND cs.status = 'ACTIVE')))
                    AND (:isSubscribed IS NULL OR c.is_subscribed = :isSubscribed)
                        AND (:propertyFilters IS NULL OR s.id IN (
                            SELECT pv2.school_id
                            FROM institution_property_values pv2
                            WHERE pv2.property_id = ANY(CAST(:propertyFilters AS bigint[]))
                            GROUP BY pv2.school_id
                            HAVING COUNT(DISTINCT pv2.property_id) = array_length(CAST(:propertyFilters AS bigint[]), 1)
                        ))
                )
                SELECT id FROM filtered_schools
                ORDER BY 
                    CASE WHEN :sortBy = 'name' AND :sortDirection = 'ASC' THEN name END ASC,
                    CASE WHEN :sortBy = 'name' AND :sortDirection = 'DESC' THEN name END DESC,
                    CASE WHEN :sortBy = 'rating' AND :sortDirection = 'ASC' THEN rating_average END ASC,
                    CASE WHEN :sortBy = 'rating' AND :sortDirection = 'DESC' THEN rating_average END DESC,
                    CASE WHEN :sortBy = 'created' AND :sortDirection = 'ASC' THEN created_at END ASC,
                    CASE WHEN :sortBy = 'created' AND :sortDirection = 'DESC' THEN created_at END DESC
                LIMIT :limit OFFSET :offset
                """, nativeQuery = true)
        List<Long> searchSchoolIds(
                @Param("searchTerm") String searchTerm,
                @Param("institutionTypeIds") String institutionTypeIds, // PostgreSQL array olarak
                @Param("propertyFilters") String propertyFilters, // PostgreSQL array olarak
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
                @Param("minRating") Double minRating,
                @Param("hasActiveCampaigns") Boolean hasActiveCampaigns,
                @Param("isSubscribed") Boolean isSubscribed,
                @Param("sortBy") String sortBy,
                @Param("sortDirection") String sortDirection,
                @Param("limit") int limit,
                @Param("offset") int offset);




        /*
         @Query(value = """
                WITH filtered_schools AS (
                    SELECT DISTINCT s.id, s.name, s.rating_average, s.created_at
                    FROM schools s
                    LEFT JOIN campuses c ON c.id = s.campus_id
                    LEFT JOIN brands b ON b.id = c.brand_id
                    LEFT JOIN provinces p ON p.id = c.province_id
                    LEFT JOIN neighborhoods n ON n.id = c.neighborhood_id
                    LEFT JOIN institution_types it ON it.id = s.institution_type_id
                    LEFT JOIN institution_property_values pv ON pv.school_id = s.id
                    WHERE s.is_active = true
                    AND (:searchTerm IS NULL OR :searchTerm = '' OR
                        LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
                        LOWER(s.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
                        LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
                        LOWER(b.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
                        LOWER(it.display_name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
                    AND (:institutionTypeIds IS NULL OR it.id = ANY(CAST(:institutionTypeIds AS bigint[])))
                    AND (:propertyFilters IS NULL OR pv.property_id = ANY(CAST(:propertyFilters AS bigint[])))
                    AND (:minAge IS NULL OR s.min_age IS NULL OR s.min_age >= :minAge)
                    AND (:maxAge IS NULL OR s.max_age IS NULL OR s.max_age <= :maxAge)
                    AND (:minFee IS NULL OR s.monthly_fee IS NULL OR s.monthly_fee >= :minFee)
                    AND (:maxFee IS NULL OR s.monthly_fee IS NULL OR s.monthly_fee <= :maxFee)
                    AND (:curriculumType IS NULL OR :curriculumType = '' OR
                        LOWER(s.curriculum_type) LIKE LOWER(CONCAT('%', :curriculumType, '%')))
                    AND (:languageOfInstruction IS NULL OR :languageOfInstruction = '' OR
                        LOWER(s.language_of_instruction) LIKE LOWER(CONCAT('%', :languageOfInstruction, '%')))
                    AND (:countryId IS NULL OR p.country_id = :countryId)
                    AND (:provinceId IS NULL OR c.province_id = :provinceId)
                    AND (:districtId IS NULL OR n.district_id = :districtId)
                    AND (:neighborhoodId IS NULL OR c.neighborhood_id = :neighborhoodId)
                    AND (:minRating IS NULL OR s.rating_average IS NULL OR s.rating_average >= :minRating)
                    AND (:hasActiveCampaigns IS NULL OR
                        (:hasActiveCampaigns = true AND EXISTS(
                            SELECT 1 FROM campaign_schools cs
                            WHERE cs.school_id = s.id AND cs.status = 'ACTIVE')) OR
                        (:hasActiveCampaigns = false AND NOT EXISTS(
                            SELECT 1 FROM campaign_schools cs
                            WHERE cs.school_id = s.id AND cs.status = 'ACTIVE')))
                    AND (:isSubscribed IS NULL OR c.is_subscribed = :isSubscribed)
                        AND (:propertyFilters IS NULL OR s.id IN (
                            SELECT pv2.school_id
                            FROM institution_property_values pv2
                            WHERE pv2.property_id = ANY(CAST(:propertyFilters AS bigint[]))
                            GROUP BY pv2.school_id
                            HAVING COUNT(DISTINCT pv2.property_id) = array_length(CAST(:propertyFilters AS bigint[]), 1)
                        ))
                )
                SELECT id FROM filtered_schools
                ORDER BY
                    CASE WHEN :sortBy = 'name' AND :sortDirection = 'ASC' THEN name END ASC,
                    CASE WHEN :sortBy = 'name' AND :sortDirection = 'DESC' THEN name END DESC,
                    CASE WHEN :sortBy = 'rating' AND :sortDirection = 'ASC' THEN rating_average END ASC,
                    CASE WHEN :sortBy = 'rating' AND :sortDirection = 'DESC' THEN rating_average END DESC,
                    CASE WHEN :sortBy = 'created' AND :sortDirection = 'ASC' THEN created_at END ASC,
                    CASE WHEN :sortBy = 'created' AND :sortDirection = 'DESC' THEN created_at END DESC
                LIMIT :limit OFFSET :offset
                """, nativeQuery = true)
        List<Long> searchSchoolIds(
                @Param("searchTerm") String searchTerm,
                @Param("institutionTypeIds") String institutionTypeIds, // PostgreSQL array olarak
                @Param("propertyFilters") String propertyFilters, // PostgreSQL array olarak
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
                @Param("minRating") Double minRating,
                @Param("hasActiveCampaigns") Boolean hasActiveCampaigns,
                @Param("isSubscribed") Boolean isSubscribed,
                @Param("sortBy") String sortBy,
                @Param("sortDirection") String sortDirection,
                @Param("limit") int limit,
                @Param("offset") int offset);
         */

    @Query(value = """
    SELECT COUNT(DISTINCT s.id)
    FROM schools s
    LEFT JOIN campuses c ON c.id = s.campus_id
    LEFT JOIN brands b ON b.id = c.brand_id
    LEFT JOIN provinces p ON p.id = c.province_id
    LEFT JOIN neighborhoods n ON n.id = c.neighborhood_id
    LEFT JOIN institution_types it ON it.id = s.institution_type_id
    LEFT JOIN institution_property_values pv ON pv.school_id = s.id
    WHERE s.is_active = true
    AND (:searchTerm IS NULL OR :searchTerm = '' OR
        LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
        LOWER(s.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
        LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
        LOWER(b.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
        LOWER(it.display_name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
    AND (:institutionTypeIds IS NULL OR it.id = ANY(CAST(:institutionTypeIds AS bigint[])))
    AND (:minAge IS NULL OR s.min_age IS NULL OR s.min_age <= :minAge)
    AND (:maxAge IS NULL OR s.max_age IS NULL OR s.max_age >= :maxAge)
    AND (:minFee IS NULL OR s.monthly_fee IS NULL OR s.monthly_fee >= :minFee)
    AND (:maxFee IS NULL OR s.monthly_fee IS NULL OR s.monthly_fee <= :maxFee)
    AND (:curriculumType IS NULL OR :curriculumType = '' OR
        LOWER(s.curriculum_type) LIKE LOWER(CONCAT('%', :curriculumType, '%')))
    AND (:languageOfInstruction IS NULL OR :languageOfInstruction = '' OR
        LOWER(s.language_of_instruction) LIKE LOWER(CONCAT('%', :languageOfInstruction, '%')))
    AND (:countryId IS NULL OR p.country_id = :countryId)
    AND (:provinceId IS NULL OR c.province_id = :provinceId)
    AND (:districtId IS NULL OR n.district_id = :districtId)
    AND (:neighborhoodId IS NULL OR c.neighborhood_id = :neighborhoodId)
    AND (:minRating IS NULL OR s.rating_average IS NULL OR s.rating_average >= :minRating)
    AND (:hasActiveCampaigns IS NULL OR
        (:hasActiveCampaigns = true AND EXISTS(
            SELECT 1 FROM campaign_schools cs 
            WHERE cs.school_id = s.id AND cs.status = 'ACTIVE')) OR
        (:hasActiveCampaigns = false AND NOT EXISTS(
            SELECT 1 FROM campaign_schools cs 
            WHERE cs.school_id = s.id AND cs.status = 'ACTIVE')))
    AND (:isSubscribed IS NULL OR c.is_subscribed = :isSubscribed)
    """, nativeQuery = true)
    long countSchools(
            @Param("searchTerm") String searchTerm,
            @Param("institutionTypeIds") String institutionTypeIds,
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
            @Param("minRating") Double minRating,
            @Param("hasActiveCampaigns") Boolean hasActiveCampaigns,
            @Param("isSubscribed") Boolean isSubscribed);

    @EntityGraph(attributePaths = {
            "campus.brand",
            "campus.province.country",
            "campus.neighborhood.district",
            "institutionType",
            "propertyValues.property.propertyType"
    })
    // Repository'de
    @Query("SELECT s FROM School s " +
            "LEFT JOIN FETCH s.campus c " +
            "LEFT JOIN FETCH s.institutionType it " +
            "LEFT JOIN FETCH s.propertyValues pv " +
            "LEFT JOIN FETCH pv.property p " +
            "LEFT JOIN FETCH p.propertyType pt " +
            "WHERE s.id IN :ids")
    List<School> findByIdsWithAllDetails(@Param("ids") List<Long> ids);



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

    Optional<School> findByIdAndIsActiveTrueAndCampusIsSubscribedTrue(Long schoolId);

    @Query("SELECT s.id FROM School s WHERE s.isActive = true")
    List<Long> findAllActiveSchoolIds();

    @Query("SELECT s.id FROM School s WHERE s.campus.id = :campusId")
    List<Long> findIdsByCampusId(@Param("campusId") Long campusId);

    @Query("SELECT s.id FROM School s WHERE s.campus.brand.id = :brandId AND s.isActive = true")
    List<Long> findIdsByBrandId(@Param("brandId") Long brandId);


    @Query("SELECT s FROM School s WHERE s.isActive = true AND s.campus.brand.id = :id " +
            "ORDER BY s.createdAt DESC")
    Set<School> findByBrandIdAndIsActiveTrue(@Param("id") Long id);

    @Query("SELECT s FROM School s WHERE s.isActive = true AND s.campus.id = :id " +
            "ORDER BY s.createdAt DESC")
    Set<School> findByCampusIdAndIsActiveTrue(@Param("id") Long id);


    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END " +
            "FROM School b WHERE LOWER(b.name) = LOWER(:name) AND b.id != :id AND b.isActive = true")
    boolean existsByNameIgnoreCaseAndIdNot(@Param("name") String name, @Param("id") Long id);


    @Query("SELECT s FROM School s WHERE s.campus.id = :campusId ")
    List<School> getSchoolsByCampus(@Param("campusId") Long campusId);
}
