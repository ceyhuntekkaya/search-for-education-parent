package com.genixo.education.search.repository.insitution;

import com.genixo.education.search.entity.institution.InstitutionPropertyValue;
import com.genixo.education.search.entity.institution.PropertyGroupType;
import com.genixo.education.search.entity.institution.PropertyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface PropertyGroupTypeRepository extends JpaRepository<PropertyGroupType, Long> {

    /**
     * Belirli InstitutionType ID'lerine ait aktif PropertyGroupType'ları getirir
     */
    @Query("SELECT pgt FROM PropertyGroupType pgt WHERE pgt.institutionType.id IN :institutionTypeIds AND pgt.isActive = true")
    List<PropertyGroupType> findByInstitutionTypeIdInAndIsActiveTrue(@Param("institutionTypeIds") List<Long> institutionTypeIds);


    @Query("SELECT pgt FROM PropertyGroupType pgt WHERE pgt.id IN (SELECT s.property.propertyType.propertyGroupType.id FROM InstitutionPropertyValue s) AND pgt.institutionType.id IN :institutionTypeIds AND pgt.isActive = true")
    List<PropertyGroupType> findByInstitutionTypeIdInAndIsActiveTrueWithHas(@Param("institutionTypeIds") List<Long> institutionTypeIds);

    /**
     * Belirli bir InstitutionType'a ait aktif PropertyGroupType'ları getirir
     */
    @Query("SELECT pgt FROM PropertyGroupType pgt WHERE pgt.institutionType.id = :institutionTypeId AND pgt.isActive = true")
    List<PropertyGroupType> findByInstitutionTypeIdAndIsActiveTrue(@Param("institutionTypeId") Long institutionTypeId);

    /**
     * Name'e göre aktif PropertyGroupType getirir
     */
    @Query("SELECT pgt FROM PropertyGroupType pgt WHERE pgt.name = :name AND pgt.isActive = true")
    Optional<PropertyGroupType> findByNameAndIsActiveTrue(@Param("name") String name);

    /**
     * InstitutionType ve name'e göre aktif PropertyGroupType getirir
     */
    @Query("SELECT pgt FROM PropertyGroupType pgt WHERE pgt.institutionType.id = :institutionTypeId AND pgt.name = :name AND pgt.isActive = true")
    Optional<PropertyGroupType> findByInstitutionTypeIdAndNameAndIsActiveTrue(@Param("institutionTypeId") Long institutionTypeId, @Param("name") String name);

    /**
     * Tüm aktif PropertyGroupType'ları getirir
     */
    List<PropertyGroupType> findByIsActiveTrue();

    /**
     * ID'ye göre aktif PropertyGroupType getirir
     */
    Optional<PropertyGroupType> findByIdAndIsActiveTrue(Long id);




    @Query("SELECT g FROM PropertyGroupType g where g.name= :name and  g.institutionType.id = :id")
    List<PropertyGroupType> checkIfExist(@Param("name") String name, @Param("id") Long id );

    @Query("SELECT g.id, g.name FROM PropertyGroupType g where g.institutionType.name= :institutionTypeName order by g.name")
    Map<Long, String> getSchoolPropertyGroups(@Param("institutionTypeName") String institutionTypeName);
}
