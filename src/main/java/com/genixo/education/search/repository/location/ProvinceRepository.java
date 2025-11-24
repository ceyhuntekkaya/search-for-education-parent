package com.genixo.education.search.repository.location;

import com.genixo.education.search.entity.location.District;
import com.genixo.education.search.entity.location.Province;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProvinceRepository extends JpaRepository<Province, Long> {
    @Query("SELECT p FROM Province p WHERE p.country.id = :countryId AND p.isActive = true ORDER BY p.sortOrder ASC, p.name ASC")
    List<Province> findByCountryIdAndIsActiveTrueOrderBySortOrderAscNameAsc(@Param("countryId") Long countryId);

    @Query("SELECT p FROM Province p WHERE p.id = :id AND p.isActive = true")
    Optional<Province> findByIdAndIsActiveTrue(@Param("id") Long id);

    @Query("SELECT p FROM Province p WHERE LOWER(p.slug) = LOWER(:slug) AND p.isActive = true")
    Optional<Province> findBySlugAndIsActiveTrue(@Param("slug") String slug);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
            "FROM Province p WHERE LOWER(p.name) = LOWER(:name) AND p.country.id = :countryId AND p.isActive = true")
    boolean existsByNameIgnoreCaseAndCountryIdAndIsActiveTrue(@Param("name") String name, @Param("countryId") Long countryId);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
            "FROM Province p WHERE LOWER(p.name) = LOWER(:name) AND p.country.id = :countryId AND p.id != :id AND p.isActive = true")
    boolean existsByNameIgnoreCaseAndCountryIdAndIdNotAndIsActiveTrue(@Param("name") String name, @Param("countryId") Long countryId, @Param("id") Long id);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Province p WHERE p.code = :code AND p.isActive = true")
    boolean existsByCodeAndIsActiveTrue(@Param("code") String code);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Province p WHERE p.slug = :slug")
    boolean existsBySlug(@Param("slug") String slug);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
            "FROM Province p WHERE p.country.id = :countryId AND p.isActive = true")
    boolean existsByCountryIdAndIsActiveTrue(@Param("countryId") Long countryId);

    @Query("SELECT p FROM Province p WHERE p.isMetropolitan = true AND p.isActive = true ORDER BY p.name ASC")
    List<Province> findByIsMetropolitanTrueAndIsActiveTrueOrderByNameAsc();

    @Query("SELECT COUNT(p) FROM Province p WHERE p.isActive = true")
    Long countByIsActiveTrue();

    @Query("SELECT COUNT(p) FROM Province p WHERE p.isActive = true AND p.isMetropolitan = true")
    Long countByIsActiveTrueAndIsMetropolitanTrue();

    // Complex search query for provinces
    @Query("SELECT p FROM Province p WHERE p.isActive = true " +
            "AND (:searchTerm IS NULL OR " +
            "    LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "    LOWER(p.nameEn) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "    LOWER(p.region) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "AND (:countryId IS NULL OR p.country.id = :countryId) " +
            "AND (:hasSchools IS NULL OR " +
            "    (:hasSchools = true AND p.schoolCount > 0) OR " +
            "    (:hasSchools = false AND (p.schoolCount IS NULL OR p.schoolCount = 0))) " +
            "AND (:minSchoolCount IS NULL OR p.schoolCount >= :minSchoolCount) " +
            "AND (:isMetropolitan IS NULL OR p.isMetropolitan = :isMetropolitan) " +
            "AND (:hasUniversity IS NULL OR p.hasUniversity = :hasUniversity)")
    Page<Province> searchProvinces(
            @Param("searchTerm") String searchTerm,
            @Param("countryId") Long countryId,
            @Param("hasSchools") Boolean hasSchools,
            @Param("minSchoolCount") Integer minSchoolCount,
            @Param("isMetropolitan") Boolean isMetropolitan,
            @Param("hasUniversity") Boolean hasUniversity,
            Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
            "FROM Province p WHERE p.id = :provinceId AND p.country.id = :countryId AND p.isActive = true")
    boolean existsByValidHierarchy(@Param("provinceId") Long provinceId, @Param("countryId") Long countryId);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
            "FROM School s WHERE s.campus.neighborhood.district.id = :districtId AND s.isActive = true")
    boolean hasActiveSchools(@Param("districtId") Long districtId);

    @Query("SELECT DISTINCT p.region FROM Province p WHERE p.isActive = true AND p.region IS NOT NULL ORDER BY p.region ASC")
    List<String> findDistinctRegions();

    @Query("SELECT p FROM Province p WHERE p.isActive = true AND " +
            "p.gdpPerCapita IS NOT NULL ORDER BY p.gdpPerCapita DESC")
    List<Province> findByOrderByGdpPerCapitaDesc(Pageable pageable);

    @Query("SELECT p FROM Province p WHERE p.isActive = true AND " +
            "p.educationIndex IS NOT NULL ORDER BY p.educationIndex DESC")
    List<Province> findByOrderByEducationIndexDesc(Pageable pageable);

    @Query("SELECT n FROM Province n WHERE n.name = :name and n.country.id = :id ")
    List<Province> checkIfExist(@Param("name") String name, @Param("id") Long id);


}
