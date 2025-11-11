package com.genixo.education.search.repository.location;

import com.genixo.education.search.entity.location.District;
import com.genixo.education.search.entity.location.Province;
import com.genixo.education.search.enumaration.SocioeconomicLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {
    @Query("SELECT d FROM District d WHERE d.province.id = :provinceId AND d.isActive = true ORDER BY d.sortOrder ASC, d.name ASC")
    List<District> findByProvinceIdAndIsActiveTrueOrderBySortOrderAscNameAsc(@Param("provinceId") Long provinceId);

    @Query("SELECT d FROM District d WHERE d.id = :id AND d.isActive = true")
    Optional<District> findByIdAndIsActiveTrue(@Param("id") Long id);

    @Query("SELECT d FROM District d WHERE LOWER(d.slug) = LOWER(:slug) AND d.isActive = true")
    Optional<District> findBySlugAndIsActiveTrue(@Param("slug") String slug);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END " +
            "FROM District d WHERE LOWER(d.name) = LOWER(:name) AND d.province.id = :provinceId AND d.isActive = true")
    boolean existsByNameIgnoreCaseAndProvinceIdAndIsActiveTrue(@Param("name") String name, @Param("provinceId") Long provinceId);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END " +
            "FROM District d WHERE LOWER(d.name) = LOWER(:name) AND d.province.id = :provinceId AND d.id != :id AND d.isActive = true")
    boolean existsByNameIgnoreCaseAndProvinceIdAndIdNotAndIsActiveTrue(@Param("name") String name, @Param("provinceId") Long provinceId, @Param("id") Long id);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM District d WHERE d.slug = :slug")
    boolean existsBySlug(@Param("slug") String slug);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END " +
            "FROM District d WHERE d.province.id = :provinceId AND d.isActive = true")
    boolean existsByProvinceIdAndIsActiveTrue(@Param("provinceId") Long provinceId);

    @Query("SELECT d FROM District d WHERE d.province.id = :provinceId AND d.isCentral = true AND d.isActive = true ORDER BY d.name ASC")
    List<District> findByProvinceIdAndIsCentralTrueAndIsActiveTrueOrderByNameAsc(@Param("provinceId") Long provinceId);

    @Query("SELECT COUNT(d) FROM District d WHERE d.isActive = true")
    Long countByIsActiveTrue();

    @Query("SELECT COUNT(d) FROM District d WHERE d.isActive = true AND d.isCentral = true")
    Long countByIsActiveTrueAndIsCentralTrue();

    // Complex search query for districts
    @Query("SELECT d FROM District d WHERE d.isActive = true " +
            "AND (:searchTerm IS NULL OR " +
            "    LOWER(d.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "    LOWER(d.nameEn) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "AND (:provinceId IS NULL OR d.province.id = :provinceId) " +
            "AND (:hasSchools IS NULL OR " +
            "    (:hasSchools = true AND d.schoolCount > 0) OR " +
            "    (:hasSchools = false AND (d.schoolCount IS NULL OR d.schoolCount = 0))) " +
            "AND (:minSchoolCount IS NULL OR d.schoolCount >= :minSchoolCount) " +
            "AND (:minSocioeconomicLevel IS NULL OR d.socioeconomicLevel >= :minSocioeconomicLevel) " +
            "AND (:hasMetroStation IS NULL OR d.hasMetroStation = :hasMetroStation) " +
            "AND (:hasUniversity IS NULL OR d.universityCount > 0)")
    Page<District> searchDistricts(
            @Param("searchTerm") String searchTerm,
            @Param("provinceId") Long provinceId,
            @Param("hasSchools") Boolean hasSchools,
            @Param("minSchoolCount") Integer minSchoolCount,
            @Param("minSocioeconomicLevel") SocioeconomicLevel minSocioeconomicLevel,
            @Param("hasMetroStation") Boolean hasMetroStation,
            @Param("hasUniversity") Boolean hasUniversity,
            Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END " +
            "FROM District d WHERE d.id = :districtId AND d.province.id = :provinceId " +
            "AND d.province.country.id = :countryId AND d.isActive = true")
    boolean existsByValidHierarchy(@Param("districtId") Long districtId, @Param("provinceId") Long provinceId, @Param("countryId") Long countryId);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
            "FROM School s WHERE s.campus.province.id = :provinceId AND s.isActive = true")
    boolean hasActiveSchools(@Param("provinceId") Long provinceId);


    @Query("SELECT d FROM District d WHERE d.isActive = true AND " +
            "d.socioeconomicLevel IN ('HIGH', 'VERY_HIGH') ORDER BY d.socioeconomicLevel DESC, d.name ASC")
    List<District> findByHighSocioeconomicLevels();

    @Query("SELECT d FROM District d WHERE d.isActive = true AND " +
            "d.averageIncome IS NOT NULL ORDER BY d.averageIncome DESC")
    List<District> findByOrderByAverageIncomeDesc(Pageable pageable);

    @Query("SELECT d FROM District d WHERE d.isActive = true AND " +
            "d.educationQualityIndex IS NOT NULL ORDER BY d.educationQualityIndex DESC")
    List<District> findByOrderByEducationQualityIndexDesc(Pageable pageable);

    @Query("SELECT d FROM District d WHERE d.isActive = true AND " +
            "d.publicTransportScore IS NOT NULL ORDER BY d.publicTransportScore DESC")
    List<District> findByOrderByPublicTransportScoreDesc(Pageable pageable);

    @Query("SELECT d FROM District d WHERE d.isActive = true AND " +
            "d.safetyIndex IS NOT NULL ORDER BY d.safetyIndex DESC")
    List<District> findByOrderBySafetyIndexDesc(Pageable pageable);

    @Query("SELECT DISTINCT d.trafficCongestionLevel FROM District d " +
            "WHERE d.isActive = true AND d.trafficCongestionLevel IS NOT NULL")
    List<String> findDistinctTrafficCongestionLevels();

    @Query("SELECT d FROM District d WHERE d.isActive = true AND " +
            "d.hasMetroStation = true ORDER BY d.name ASC")
    List<District> findDistrictsWithMetroStation();

    @Query("SELECT d FROM District d WHERE d.isActive = true AND " +
            "d.universityCount > 0 ORDER BY d.universityCount DESC, d.name ASC")
    List<District> findDistrictsWithUniversities();



}
