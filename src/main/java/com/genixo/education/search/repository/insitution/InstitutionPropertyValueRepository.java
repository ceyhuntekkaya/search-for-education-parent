package com.genixo.education.search.repository.insitution;

import com.genixo.education.search.entity.institution.InstitutionPropertyValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstitutionPropertyValueRepository extends JpaRepository<InstitutionPropertyValue, Long> {
    @Query("SELECT ipv FROM InstitutionPropertyValue ipv " +
            "WHERE ipv.property.id = :propertyId AND ipv.school.id = :schoolId AND ipv.isActive = true")
    Optional<InstitutionPropertyValue> findByPropertyIdAndSchoolIdAndIsActiveTrue(
            @Param("propertyId") Long propertyId,
            @Param("schoolId") Long schoolId);

    @Query("SELECT ipv FROM InstitutionPropertyValue ipv " +
            "WHERE ipv.property.id = :propertyId AND ipv.campus.id = :campusId AND ipv.isActive = true")
    Optional<InstitutionPropertyValue> findByPropertyIdAndCampusIdAndIsActiveTrue(
            @Param("propertyId") Long propertyId,
            @Param("campusId") Long campusId);

    @Query("SELECT ipv FROM InstitutionPropertyValue ipv " +
            "WHERE ipv.school.id = :schoolId AND ipv.isActive = true " +
            "ORDER BY ipv.property.sortOrder ASC, ipv.property.displayName ASC")
    List<InstitutionPropertyValue> findBySchoolIdAndIsActiveTrue(@Param("schoolId") Long schoolId);

    @Query("SELECT ipv FROM InstitutionPropertyValue ipv " +
            "WHERE ipv.campus.id = :campusId AND ipv.isActive = true " +
            "ORDER BY ipv.property.sortOrder ASC, ipv.property.displayName ASC")
    List<InstitutionPropertyValue> findByCampusIdAndIsActiveTrue(@Param("campusId") Long campusId);

    @Query("SELECT ipv FROM InstitutionPropertyValue ipv " +
            "WHERE ipv.school.id = :schoolId AND ipv.property.showInCard = true AND ipv.isActive = true " +
            "ORDER BY ipv.property.sortOrder ASC, ipv.property.displayName ASC")
    List<InstitutionPropertyValue> findCardValuesBySchoolId(@Param("schoolId") Long schoolId);

    @Query("SELECT ipv FROM InstitutionPropertyValue ipv " +
            "JOIN FETCH ipv.property p " +  // EAGER fetch ekle
            "WHERE ipv.school.id = :schoolId " +
            "AND p.showInProfile = true " +
            "AND ipv.isActive = true " +
            "ORDER BY p.sortOrder ASC, p.displayName ASC")
    List<InstitutionPropertyValue> findProfileValuesBySchoolId(@Param("schoolId") Long schoolId);

    @Query("SELECT ipv FROM InstitutionPropertyValue ipv " +
            "WHERE ipv.campus.id = :campusId AND ipv.property.showInCard = true AND ipv.isActive = true " +
            "ORDER BY ipv.property.sortOrder ASC, ipv.property.displayName ASC")
    List<InstitutionPropertyValue> findCardValuesByCampusId(@Param("campusId") Long campusId);

    @Query("SELECT ipv FROM InstitutionPropertyValue ipv " +
            "WHERE ipv.campus.id = :campusId AND ipv.property.showInProfile = true AND ipv.isActive = true " +
            "ORDER BY ipv.property.sortOrder ASC, ipv.property.displayName ASC")
    List<InstitutionPropertyValue> findProfileValuesByCampusId(@Param("campusId") Long campusId);

    @Query("SELECT ipv FROM InstitutionPropertyValue ipv " +
            "WHERE ipv.property.id IN :propertyIds AND ipv.school.id = :schoolId AND ipv.isActive = true")
    List<InstitutionPropertyValue> findByPropertyIdsAndSchoolId(
            @Param("propertyIds") List<Long> propertyIds,
            @Param("schoolId") Long schoolId);

    @Query("SELECT ipv FROM InstitutionPropertyValue ipv " +
            "WHERE ipv.property.id IN :propertyIds AND ipv.campus.id = :campusId AND ipv.isActive = true")
    List<InstitutionPropertyValue> findByPropertyIdsAndCampusId(
            @Param("propertyIds") List<Long> propertyIds,
            @Param("campusId") Long campusId);

    @Query("DELETE FROM InstitutionPropertyValue ipv WHERE ipv.school.id = :schoolId")
    void deleteBySchoolId(@Param("schoolId") Long schoolId);

    @Query("DELETE FROM InstitutionPropertyValue ipv WHERE ipv.campus.id = :campusId")
    void deleteByCampusId(@Param("campusId") Long campusId);

    // Search by property values
    @Query("SELECT DISTINCT ipv.school.id FROM InstitutionPropertyValue ipv " +
            "WHERE ipv.property.id = :propertyId AND ipv.isActive = true " +
            "AND (:textValue IS NULL OR LOWER(ipv.textValue) LIKE LOWER(CONCAT('%', :textValue, '%'))) " +
            "AND (:numberValue IS NULL OR ipv.numberValue = :numberValue) " +
            "AND (:booleanValue IS NULL OR ipv.booleanValue = :booleanValue)")
    List<Long> findSchoolIdsByPropertyValue(
            @Param("propertyId") Long propertyId,
            @Param("textValue") String textValue,
            @Param("numberValue") Double numberValue,
            @Param("booleanValue") Boolean booleanValue);

    @Query("SELECT ipv FROM InstitutionPropertyValue ipv " +
            "JOIN FETCH ipv.property ip " +
            "JOIN FETCH ip.propertyType " +
            "WHERE ipv.school.id = :schoolId")
    List<InstitutionPropertyValue> findBySchoolIdWithProperty(@Param("schoolId") Long schoolId);
}
