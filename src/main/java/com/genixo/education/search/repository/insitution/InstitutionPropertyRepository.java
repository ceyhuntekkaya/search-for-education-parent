package com.genixo.education.search.repository.insitution;

import com.genixo.education.search.entity.institution.InstitutionProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstitutionPropertyRepository extends JpaRepository<InstitutionProperty, Long> {
    @Query("SELECT ip FROM InstitutionProperty ip WHERE ip.isActive = true AND ip.id = :id")
    Optional<InstitutionProperty> findByIdAndIsActiveTrue(@Param("id") Long id);

    @Query("SELECT CASE WHEN COUNT(ip) > 0 THEN true ELSE false END " +
            "FROM InstitutionProperty ip WHERE LOWER(ip.name) = LOWER(:name) " +
            "AND ip.institutionType.id = :institutionTypeId AND ip.isActive = true")
    boolean existsByNameIgnoreCaseAndInstitutionTypeIdAndIsActiveTrue(
            @Param("name") String name,
            @Param("institutionTypeId") Long institutionTypeId);

    @Query("SELECT ip FROM InstitutionProperty ip " +
            "WHERE ip.institutionType.id = :institutionTypeId AND ip.isActive = true " +
            "ORDER BY ip.sortOrder ASC, ip.displayName ASC")
    List<InstitutionProperty> findByInstitutionTypeIdAndIsActiveTrueOrderBySortOrderAscDisplayNameAsc(
            @Param("institutionTypeId") Long institutionTypeId);

    @Query("SELECT ip FROM InstitutionProperty ip " +
            "WHERE ip.institutionType.id = :institutionTypeId AND ip.isActive = true " +
            "AND ip.isSearchable = true " +
            "ORDER BY ip.sortOrder ASC, ip.displayName ASC")
    List<InstitutionProperty> findSearchablePropertiesByInstitutionType(@Param("institutionTypeId") Long institutionTypeId);

    @Query("SELECT ip FROM InstitutionProperty ip " +
            "WHERE ip.institutionType.id = :institutionTypeId AND ip.isActive = true " +
            "AND ip.isFilterable = true " +
            "ORDER BY ip.sortOrder ASC, ip.displayName ASC")
    List<InstitutionProperty> findFilterablePropertiesByInstitutionType(@Param("institutionTypeId") Long institutionTypeId);

    @Query("SELECT ip FROM InstitutionProperty ip " +
            "WHERE ip.institutionType.id = :institutionTypeId AND ip.isActive = true " +
            "AND ip.showInCard = true " +
            "ORDER BY ip.sortOrder ASC, ip.displayName ASC")
    List<InstitutionProperty> findCardPropertiesByInstitutionType(@Param("institutionTypeId") Long institutionTypeId);

    @Query("SELECT ip FROM InstitutionProperty ip " +
            "WHERE ip.institutionType.id = :institutionTypeId AND ip.isActive = true " +
            "AND ip.showInProfile = true " +
            "ORDER BY ip.sortOrder ASC, ip.displayName ASC")
    List<InstitutionProperty> findProfilePropertiesByInstitutionType(@Param("institutionTypeId") Long institutionTypeId);

    @Query("SELECT ip FROM InstitutionProperty ip " +
            "WHERE ip.institutionType.id = :institutionTypeId AND ip.isActive = true " +
            "AND ip.isRequired = true " +
            "ORDER BY ip.sortOrder ASC, ip.displayName ASC")
    List<InstitutionProperty> findRequiredPropertiesByInstitutionType(@Param("institutionTypeId") Long institutionTypeId);
}
