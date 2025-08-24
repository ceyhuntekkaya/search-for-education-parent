package com.genixo.education.search.repository.location;

import com.genixo.education.search.entity.location.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
    @Query("SELECT c FROM Country c WHERE c.isActive = true ORDER BY c.sortOrder ASC, c.name ASC")
    List<Country> findAllByIsActiveTrueOrderBySortOrderAscNameAsc();

    @Query("SELECT c FROM Country c WHERE c.isActive = true AND c.isSupported = true ORDER BY c.sortOrder ASC, c.name ASC")
    List<Country> findBySupportedTrueAndIsActiveTrueOrderBySortOrderAscNameAsc();

    @Query("SELECT c FROM Country c WHERE c.id = :id AND c.isActive = true")
    Optional<Country> findByIdAndIsActiveTrue(@Param("id") Long id);

    @Query("SELECT c FROM Country c WHERE UPPER(c.isoCode2) = UPPER(:isoCode2) AND c.isActive = true")
    Optional<Country> findByIsoCode2AndIsActiveTrue(@Param("isoCode2") String isoCode2);

    @Query("SELECT c FROM Country c WHERE UPPER(c.isoCode3) = UPPER(:isoCode3) AND c.isActive = true")
    Optional<Country> findByIsoCode3AndIsActiveTrue(@Param("isoCode3") String isoCode3);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Country c WHERE UPPER(c.isoCode2) = UPPER(:isoCode2)")
    boolean existsByIsoCode2(@Param("isoCode2") String isoCode2);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Country c WHERE UPPER(c.isoCode3) = UPPER(:isoCode3)")
    boolean existsByIsoCode3(@Param("isoCode3") String isoCode3);

    @Query("SELECT COUNT(c) FROM Country c WHERE c.isActive = true")
    Long countByIsActiveTrue();

    @Query("SELECT COUNT(c) FROM Country c WHERE c.isActive = true AND c.isSupported = true")
    Long countByIsActiveTrueAndIsSupportedTrue();

    @Query("SELECT c FROM Country c WHERE c.isActive = true AND " +
            "(:searchTerm IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.nameEn) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Country> searchCountries(@Param("searchTerm") String searchTerm);

    boolean existsByIdAndIsActiveTrue(Long countryId);
}
