package com.genixo.education.search.repository.insitution;

import com.genixo.education.search.entity.institution.InstitutionPropertyValue;
import com.genixo.education.search.entity.institution.PropertyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyTypeRepository extends JpaRepository<InstitutionPropertyValue, Long> {
    @Query("SELECT pt FROM PropertyType pt WHERE pt.propertyGroupType.id IN :propertyGroupTypeIds AND pt.isActive = true")
    List<PropertyType> findByPropertyGroupTypeIdInAndIsActiveTrue(@Param("propertyGroupTypeIds") List<Long> propertyGroupTypeIds);

    /**
     * Belirli bir PropertyGroupType'a ait aktif PropertyType'ları getirir
     */
    @Query("SELECT pt FROM PropertyType pt WHERE pt.propertyGroupType.id = :propertyGroupTypeId AND pt.isActive = true")
    List<PropertyType> findByPropertyGroupTypeIdAndIsActiveTrue(@Param("propertyGroupTypeId") Long propertyGroupTypeId);

    /**
     * Name'e göre aktif PropertyType getirir
     */
    @Query("SELECT pt FROM PropertyType pt WHERE pt.name = :name AND pt.isActive = true")
    Optional<PropertyType> findByNameAndIsActiveTrue(@Param("name") String name);

    /**
     * PropertyGroupType ve name'e göre aktif PropertyType getirir
     */
    @Query("SELECT pt FROM PropertyType pt WHERE pt.propertyGroupType.id = :propertyGroupTypeId AND pt.name = :name AND pt.isActive = true")
    Optional<PropertyType> findByPropertyGroupTypeIdAndNameAndIsActiveTrue(@Param("propertyGroupTypeId") Long propertyGroupTypeId, @Param("name") String name);

    /**
     * Tüm aktif PropertyType'ları getirir
     */
    List<PropertyType> findByIsActiveTrue();

    /**
     * ID'ye göre aktif PropertyType getirir
     */
    Optional<PropertyType> findByIdAndIsActiveTrue(Long id);

    /**
     * Belirli bir InstitutionType'a ait tüm PropertyType'ları getirir (nested query)
     */
    @Query("SELECT pt FROM PropertyType pt WHERE pt.propertyGroupType.institutionType.id = :institutionTypeId AND pt.isActive = true")
    List<PropertyType> findByInstitutionTypeIdAndIsActiveTrue(@Param("institutionTypeId") Long institutionTypeId);
}
