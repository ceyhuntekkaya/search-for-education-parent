package com.genixo.education.search.repository.insitution;

import com.genixo.education.search.entity.institution.InstitutionTypeGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface InstitutionTypeGroupRepository extends JpaRepository<InstitutionTypeGroup, Long> {

    @Query("SELECT g FROM InstitutionTypeGroup g WHERE g.isActive = true ORDER BY g.sortOrder ASC, g.name ASC")
    List<InstitutionTypeGroup> findByIsActiveTrueOrderBySortOrderAscNameAsc();

    @Query("SELECT g FROM InstitutionTypeGroup g WHERE g.isActive = true AND g.id = :id")
    Optional<InstitutionTypeGroup> findByIdAndIsActiveTrue(@Param("id") Long id);

    @Query("SELECT CASE WHEN COUNT(g) > 0 THEN true ELSE false END " +
            "FROM InstitutionTypeGroup g WHERE LOWER(g.name) = LOWER(CAST(:name AS string)) AND g.isActive = true")
    boolean existsByNameIgnoreCaseAndIsActiveTrue(@Param("name") String name);

    @Query("SELECT CASE WHEN COUNT(g) > 0 THEN true ELSE false END " +
            "FROM InstitutionTypeGroup g WHERE LOWER(g.name) = LOWER(CAST(:name AS string)) " +
            "AND g.id != :id AND g.isActive = true")
    boolean existsByNameIgnoreCaseAndIdNotAndIsActiveTrue(@Param("name") String name, @Param("id") Long id);

    @Query("SELECT g FROM InstitutionTypeGroup g where g.name= :name")
    List<InstitutionTypeGroup> checkIfExist(@Param("name") String name);

    @Query("SELECT g.name FROM InstitutionTypeGroup g order by g.name")
    List<String> getAllInstitutionTypeGroupNames();
}
