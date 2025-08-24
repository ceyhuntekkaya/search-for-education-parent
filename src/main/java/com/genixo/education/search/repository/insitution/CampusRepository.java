package com.genixo.education.search.repository.insitution;

import com.genixo.education.search.dto.institution.CampusSummaryDto;
import com.genixo.education.search.entity.institution.Campus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CampusRepository extends JpaRepository<Campus, Long> {
    @Query("SELECT c FROM Campus c WHERE c.isActive = true AND c.id = :id")
    Optional<Campus> findByIdAndIsActiveTrue(@Param("id") Long id);

    @Query("SELECT c FROM Campus c WHERE c.isActive = true AND LOWER(c.slug) = LOWER(:slug)")
    Optional<Campus> findBySlugAndIsActiveTrue(@Param("slug") String slug);

    @Query("SELECT c FROM Campus c WHERE c.isActive = true AND c.isSubscribed = true AND LOWER(c.slug) = LOWER(:slug)")
    Optional<Campus> findBySlugAndIsActiveTrueAndIsSubscribedTrue(@Param("slug") String slug);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Campus c WHERE LOWER(c.name) = LOWER(:name) AND c.brand.id = :brandId AND c.isActive = true")
    boolean existsByNameIgnoreCaseAndBrandIdAndIsActiveTrue(@Param("name") String name, @Param("brandId") Long brandId);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Campus c WHERE c.slug = :slug AND c.isActive = true")
    boolean existsBySlug(@Param("slug") String slug);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Campus c WHERE c.brand.id = :brandId AND c.isActive = true")
    boolean existsByBrandIdAndIsActiveTrue(@Param("brandId") Long brandId);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Campus c WHERE c.id = :campusId AND c.brand.id = :brandId AND c.isActive = true")
    boolean existsByIdAndBrandId(@Param("campusId") Long campusId, @Param("brandId") Long brandId);

    @Query("SELECT c FROM Campus c WHERE c.brand.id = :brandId AND c.isActive = true ORDER BY c.name ASC")
    List<Campus> findByBrandIdAndIsActiveTrueOrderByName(@Param("brandId") Long brandId);

    @Query("SELECT c FROM Campus c WHERE c.isActive = true ORDER BY c.name ASC")
    List<Campus> findAllActiveOrderByName();

    @Query("SELECT c FROM Campus c WHERE c.id IN :ids AND c.isActive = true ORDER BY c.name ASC")
    List<Campus> findByIdInAndIsActiveTrueOrderByName(@Param("ids") List<Long> ids);

    @Modifying
    @Query("UPDATE Campus c SET c.viewCount = c.viewCount + 1 WHERE c.id = :id")
    void incrementViewCount(@Param("id") Long id);

    @Query("SELECT new com.genixo.education.search.dto.institution.CampusSummaryDto(" +
            "c.id, c.name, c.slug, c.logoUrl, " +
            "new com.genixo.education.search.dto.location.ProvinceSummaryDto(c.province.id, c.province.name), " +
            "new com.genixo.education.search.dto.location.DistrictSummaryDto(c.district.id, c.district.name), " +
            "c.ratingAverage, " +
            "COALESCE((SELECT COUNT(s) FROM School s WHERE s.campus.id = c.id AND s.isActive = true), 0L), " +
            "c.isSubscribed) " +
            "FROM Campus c WHERE c.isActive = true ORDER BY c.name ASC")
    List<CampusSummaryDto> findCampusSummaries();

    @Query("SELECT c FROM Campus c WHERE c.isActive = true AND " +
            "(:searchTerm IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.province.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.district) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Campus> searchCampuses(@Param("searchTerm") String searchTerm);

    @Query("SELECT c FROM Campus c WHERE c.isActive = true AND c.isSubscribed = true AND " +
            "c.province.name IN :cities ORDER BY c.name ASC")
    List<Campus> findSubscribedCampusesByCities(@Param("cities") List<String> cities);

    @Query("SELECT DISTINCT c.province FROM Campus c WHERE c.isActive = true AND c.isSubscribed = true " +
            "AND c.province IS NOT NULL ORDER BY c.province.name ASC")
    List<String> findDistinctActiveCities();

    List<Long> findIdsByBrandId(Long entityId);

    @Query("SELECT c.id FROM Campus c WHERE c.isActive = true")
    List<Long> findAllActiveCampusIds();
}
