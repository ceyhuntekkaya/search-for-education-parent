package com.genixo.education.search.repository.search;


import com.genixo.education.search.entity.search.SchoolSearchViewWithProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * School Search Repository - DUAL MODE
 * Frontend (ID bazlı) ve AI (Name bazlı) aramaları
 */
@Repository
public interface SchoolSearchDualRepository extends JpaRepository<SchoolSearchViewWithProperties, Long> {

    // ============================================================================
    // FRONTEND SEARCH (ID BAZLI)
    // ============================================================================

    /**
     * Frontend için ID bazlı arama
     * ZORUNLU: institutionTypeId, provinceId
     * OPSIYONEL: Diğer tüm parametreler
     */
    @Query(value = """
        SELECT * FROM school_search_materialized_v2
        WHERE institution_type_id = :institutionTypeId
          AND province_id = :provinceId
          AND (:districtId IS NULL OR district_id = :districtId)
          AND (:neighborhoodId IS NULL OR neighborhood_id = :neighborhoodId)
          AND (:minAge IS NULL OR min_age <= :minAge)
          AND (:maxAge IS NULL OR max_age >= :maxAge)
          AND (:minFee IS NULL OR monthly_fee >= :minFee)
          AND (:maxFee IS NULL OR monthly_fee <= :maxFee)
          AND (:curriculumType IS NULL OR LOWER(curriculum_type) = LOWER(:curriculumType))
          AND (:languageOfInstruction IS NULL OR LOWER(language_of_instruction) = LOWER(:languageOfInstruction))
          AND (:minRating IS NULL OR rating_average >= :minRating)
          AND (:isSubscribed IS NULL OR campus_is_subscribed = :isSubscribed)
          AND (:searchTerm IS NULL OR 
               search_vector @@ plainto_tsquery('turkish', :searchTerm) OR
               LOWER(school_name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
          AND (
              :propertyIds IS NULL OR
              CARDINALITY(:propertyIds) = 0 OR
              EXISTS (
                  SELECT 1 
                  FROM jsonb_array_elements(properties_json) elem
                  WHERE (elem->>'property_id')::bigint = ANY(:propertyIds)
              )
          )
        ORDER BY
            CASE WHEN :sortBy = 'RATING' AND :sortDirection = 'DESC' THEN rating_average END DESC NULLS LAST,
            CASE WHEN :sortBy = 'RATING' AND :sortDirection = 'ASC' THEN rating_average END ASC NULLS LAST,
            CASE WHEN :sortBy = 'PRICE' AND :sortDirection = 'DESC' THEN monthly_fee END DESC NULLS LAST,
            CASE WHEN :sortBy = 'PRICE' AND :sortDirection = 'ASC' THEN monthly_fee END ASC NULLS LAST,
            CASE WHEN :sortBy = 'NAME' AND :sortDirection = 'DESC' THEN school_name END DESC,
            CASE WHEN :sortBy = 'NAME' AND :sortDirection = 'ASC' THEN school_name END ASC,
            CASE WHEN :sortBy = 'CREATED_DATE' AND :sortDirection = 'DESC' THEN created_at END DESC NULLS LAST,
            CASE WHEN :sortBy = 'CREATED_DATE' AND :sortDirection = 'ASC' THEN created_at END ASC NULLS LAST,
            quality_score DESC
        """,
            countQuery = """
        SELECT COUNT(*) FROM school_search_materialized_v2
        WHERE institution_type_id = :institutionTypeId
          AND province_id = :provinceId
          AND (:districtId IS NULL OR district_id = :districtId)
          AND (:neighborhoodId IS NULL OR neighborhood_id = :neighborhoodId)
          AND (:minAge IS NULL OR min_age <= :minAge)
          AND (:maxAge IS NULL OR max_age >= :maxAge)
          AND (:minFee IS NULL OR monthly_fee >= :minFee)
          AND (:maxFee IS NULL OR monthly_fee <= :maxFee)
          AND (:curriculumType IS NULL OR LOWER(curriculum_type) = LOWER(:curriculumType))
          AND (:languageOfInstruction IS NULL OR LOWER(language_of_instruction) = LOWER(:languageOfInstruction))
          AND (:minRating IS NULL OR rating_average >= :minRating)
          AND (:isSubscribed IS NULL OR campus_is_subscribed = :isSubscribed)
          AND (:searchTerm IS NULL OR 
               search_vector @@ plainto_tsquery('turkish', :searchTerm) OR
               LOWER(school_name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
          AND (
              :propertyIds IS NULL OR
              CARDINALITY(:propertyIds) = 0 OR
              EXISTS (
                  SELECT 1 
                  FROM jsonb_array_elements(properties_json) elem
                  WHERE (elem->>'property_id')::bigint = ANY(:propertyIds)
              )
          )
        """,
            nativeQuery = true)
    Page<SchoolSearchViewWithProperties> searchByIds(
            @Param("institutionTypeId") Long institutionTypeId,
            @Param("provinceId") Long provinceId,
            @Param("districtId") Long districtId,
            @Param("neighborhoodId") Long neighborhoodId,
            @Param("minAge") Integer minAge,
            @Param("maxAge") Integer maxAge,
            @Param("minFee") Double minFee,
            @Param("maxFee") Double maxFee,
            @Param("curriculumType") String curriculumType,
            @Param("languageOfInstruction") String languageOfInstruction,
            @Param("minRating") Double minRating,
            @Param("isSubscribed") Boolean isSubscribed,
            @Param("searchTerm") String searchTerm,
            @Param("propertyIds") Long[] propertyIds,
            @Param("sortBy") String sortBy,
            @Param("sortDirection") String sortDirection,
            Pageable pageable
    );

    // ============================================================================
    // AI SEARCH (NAME BAZLI)
    // ============================================================================

    /**
     * AI için Name bazlı arama
     * ZORUNLU: institutionTypeName, provinceName
     * OPSIYONEL: Diğer tüm parametreler
     */
    @Query(value = """
        SELECT * FROM school_search_materialized_v2
        WHERE LOWER(institution_type_name) = LOWER(:institutionTypeName)
          AND LOWER(province_name) = LOWER(:provinceName)
          AND (:districtName IS NULL OR LOWER(district_name) = LOWER(:districtName))
          AND (:neighborhoodName IS NULL OR LOWER(neighborhood_name) = LOWER(:neighborhoodName))
          AND (:minAge IS NULL OR min_age <= :minAge)
          AND (:maxAge IS NULL OR max_age >= :maxAge)
          AND (:minFee IS NULL OR monthly_fee >= :minFee)
          AND (:maxFee IS NULL OR monthly_fee <= :maxFee)
          AND (:curriculumType IS NULL OR LOWER(curriculum_type) = LOWER(:curriculumType))
          AND (:languageOfInstruction IS NULL OR LOWER(language_of_instruction) = LOWER(:languageOfInstruction))
          AND (:minRating IS NULL OR rating_average >= :minRating)
          AND (:isSubscribed IS NULL OR campus_is_subscribed = :isSubscribed)
          AND (:searchTerm IS NULL OR 
               search_vector @@ plainto_tsquery('turkish', :searchTerm) OR
               LOWER(school_name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
          AND (
              :propertyNames IS NULL OR
              CARDINALITY(:propertyNames) = 0 OR
              EXISTS (
                  SELECT 1 
                  FROM jsonb_array_elements(properties_json) elem
                  WHERE LOWER(elem->>'property_display_name') = ANY(
                      SELECT LOWER(unnest(:propertyNames))
                  )
              )
          )
        ORDER BY
            CASE WHEN :sortBy = 'rating' AND :sortDirection = 'desc' THEN rating_average END DESC NULLS LAST,
            CASE WHEN :sortBy = 'rating' AND :sortDirection = 'asc' THEN rating_average END ASC NULLS LAST,
            CASE WHEN :sortBy = 'price' AND :sortDirection = 'desc' THEN monthly_fee END DESC NULLS LAST,
            CASE WHEN :sortBy = 'price' AND :sortDirection = 'asc' THEN monthly_fee END ASC NULLS LAST,
            CASE WHEN :sortBy = 'name' AND :sortDirection = 'desc' THEN school_name END DESC,
            CASE WHEN :sortBy = 'name' AND :sortDirection = 'asc' THEN school_name END ASC,
            CASE WHEN :sortBy = 'created' AND :sortDirection = 'desc' THEN created_at END DESC NULLS LAST,
            CASE WHEN :sortBy = 'created' AND :sortDirection = 'asc' THEN created_at END ASC NULLS LAST,
            quality_score DESC
        """,
            countQuery = """
        SELECT COUNT(*) FROM school_search_materialized_v2
        WHERE LOWER(institution_type_name) = LOWER(:institutionTypeName)
          AND LOWER(province_name) = LOWER(:provinceName)
          AND (:districtName IS NULL OR LOWER(district_name) = LOWER(:districtName))
          AND (:neighborhoodName IS NULL OR LOWER(neighborhood_name) = LOWER(:neighborhoodName))
          AND (:minAge IS NULL OR min_age <= :minAge)
          AND (:maxAge IS NULL OR max_age >= :maxAge)
          AND (:minFee IS NULL OR monthly_fee >= :minFee)
          AND (:maxFee IS NULL OR monthly_fee <= :maxFee)
          AND (:curriculumType IS NULL OR LOWER(curriculum_type) = LOWER(:curriculumType))
          AND (:languageOfInstruction IS NULL OR LOWER(language_of_instruction) = LOWER(:languageOfInstruction))
          AND (:minRating IS NULL OR rating_average >= :minRating)
          AND (:isSubscribed IS NULL OR campus_is_subscribed = :isSubscribed)
          AND (:searchTerm IS NULL OR 
               search_vector @@ plainto_tsquery('turkish', :searchTerm) OR
               LOWER(school_name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
          AND (
              :propertyNames IS NULL OR
              CARDINALITY(:propertyNames) = 0 OR
              EXISTS (
                  SELECT 1 
                  FROM jsonb_array_elements(properties_json) elem
                  WHERE LOWER(elem->>'property_display_name') = ANY(
                      SELECT LOWER(unnest(:propertyNames))
                  )
              )
          )
        """,
            nativeQuery = true)
    Page<SchoolSearchViewWithProperties> searchByNames(
            @Param("institutionTypeName") String institutionTypeName,
            @Param("provinceName") String provinceName,
            @Param("districtName") String districtName,
            @Param("neighborhoodName") String neighborhoodName,
            @Param("minAge") Integer minAge,
            @Param("maxAge") Integer maxAge,
            @Param("minFee") Double minFee,
            @Param("maxFee") Double maxFee,
            @Param("curriculumType") String curriculumType,
            @Param("languageOfInstruction") String languageOfInstruction,
            @Param("minRating") Double minRating,
            @Param("isSubscribed") Boolean isSubscribed,
            @Param("searchTerm") String searchTerm,
            @Param("propertyNames") String[] propertyNames,
            @Param("sortBy") String sortBy,
            @Param("sortDirection") String sortDirection,
            Pageable pageable
    );

    // ============================================================================
    // COĞRAFI ARAMA (Her iki mode için)
    // ============================================================================

    /**
     * Yakındaki okullar - ID bazlı
     */
    @Query(value = """
        SELECT *,
            (6371 * acos(
                cos(radians(:targetLat)) * cos(radians(latitude)) * 
                cos(radians(longitude) - radians(:targetLon)) + 
                sin(radians(:targetLat)) * sin(radians(latitude))
            )) AS distance_km
        FROM school_search_materialized_v2
        WHERE latitude IS NOT NULL 
          AND longitude IS NOT NULL
          AND institution_type_id = :institutionTypeId
          AND (6371 * acos(
                cos(radians(:targetLat)) * cos(radians(latitude)) * 
                cos(radians(longitude) - radians(:targetLon)) + 
                sin(radians(:targetLat)) * sin(radians(latitude))
              )) <= :radiusKm
        ORDER BY distance_km
        """,
            nativeQuery = true)
    List<SchoolSearchViewWithProperties> findNearbySchoolsById(
            @Param("institutionTypeId") Long institutionTypeId,
            @Param("targetLat") Double targetLat,
            @Param("targetLon") Double targetLon,
            @Param("radiusKm") Double radiusKm
    );

    /**
     * Yakındaki okullar - Name bazlı
     */
    @Query(value = """
        SELECT *,
            (6371 * acos(
                cos(radians(:targetLat)) * cos(radians(latitude)) * 
                cos(radians(longitude) - radians(:targetLon)) + 
                sin(radians(:targetLat)) * sin(radians(latitude))
            )) AS distance_km
        FROM school_search_materialized_v2
        WHERE latitude IS NOT NULL 
          AND longitude IS NOT NULL
          AND LOWER(institution_type_name) = LOWER(:institutionTypeName)
          AND (6371 * acos(
                cos(radians(:targetLat)) * cos(radians(latitude)) * 
                cos(radians(longitude) - radians(:targetLon)) + 
                sin(radians(:targetLat)) * sin(radians(latitude))
              )) <= :radiusKm
        ORDER BY distance_km
        """,
            nativeQuery = true)
    List<SchoolSearchViewWithProperties> findNearbySchoolsByName(
            @Param("institutionTypeName") String institutionTypeName,
            @Param("targetLat") Double targetLat,
            @Param("targetLon") Double targetLon,
            @Param("radiusKm") Double radiusKm
    );
}