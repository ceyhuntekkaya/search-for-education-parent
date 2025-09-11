package com.genixo.education.search.repository.insitution;

import com.genixo.education.search.dto.institution.InstitutionTypeSummaryDto;
import com.genixo.education.search.entity.institution.InstitutionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstitutionTypeRepository extends JpaRepository<InstitutionType, Long> {
    @Query("SELECT it FROM InstitutionType it WHERE it.isActive = true AND it.id = :id")
    Optional<InstitutionType> findByIdAndIsActiveTrue(@Param("id") Long id);

    @Query("SELECT CASE WHEN COUNT(it) > 0 THEN true ELSE false END " +
            "FROM InstitutionType it WHERE LOWER(it.name) = LOWER(:name) AND it.isActive = true")
    boolean existsByNameIgnoreCase(@Param("name") String name);

    @Query("SELECT it FROM InstitutionType it WHERE it.isActive = true ORDER BY it.sortOrder ASC, it.name ASC")
    List<InstitutionType> findAllByIsActiveTrueOrderBySortOrderAscNameAsc();

    @Query("SELECT new com.genixo.education.search.dto.institution.InstitutionTypeSummaryDto(" +
            "it.id, it.name, it.displayName, it.iconUrl, it.colorCode, " +
            "COALESCE((SELECT COUNT(s) FROM School s WHERE s.institutionType.id = it.id AND s.isActive = true), 0L)) " +
            "FROM InstitutionType it WHERE it.isActive = true ORDER BY it.sortOrder ASC, it.displayName ASC")
    List<InstitutionTypeSummaryDto> findInstitutionTypeSummaries();

    @Query("SELECT it FROM InstitutionType it WHERE it.isActive = true AND " +
            "LOWER(it.displayName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<InstitutionType> searchByDisplayName(@Param("searchTerm") String searchTerm);



    List<InstitutionType> findByIsActiveTrue();


    Optional<InstitutionType> findByNameAndIsActiveTrue(String name);

    @Query("SELECT it FROM InstitutionType it WHERE it.isActive = true ORDER BY it.sortOrder ASC, it.name ASC")
    List<InstitutionType> findAllActiveOrderBySortOrder();

    List<InstitutionType> findByIdInAndIsActiveTrue(List<Long> ids);
}
