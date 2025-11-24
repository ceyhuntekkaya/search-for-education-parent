package com.genixo.education.search.repository.location;

import com.genixo.education.search.dto.location.LocationSuggestionDto;
import com.genixo.education.search.entity.location.Neighborhood;
import com.genixo.education.search.enumaration.IncomeLevel;
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
public interface NeighborhoodRepository extends JpaRepository<Neighborhood, Long> {
    @Query("SELECT n FROM Neighborhood n WHERE n.district.id = :districtId AND n.isActive = true ORDER BY n.sortOrder ASC, n.name ASC")
    List<Neighborhood> findByDistrictIdAndIsActiveTrueOrderBySortOrderAscNameAsc(@Param("districtId") Long districtId);

    @Query("SELECT n FROM Neighborhood n WHERE n.id = :id AND n.isActive = true")
    Optional<Neighborhood> findByIdAndIsActiveTrue(@Param("id") Long id);

    @Query("SELECT n FROM Neighborhood n WHERE LOWER(n.slug) = LOWER(:slug) AND n.isActive = true")
    Optional<Neighborhood> findBySlugAndIsActiveTrue(@Param("slug") String slug);

    @Query("SELECT CASE WHEN COUNT(n) > 0 THEN true ELSE false END " +
            "FROM Neighborhood n WHERE LOWER(n.name) = LOWER(:name) AND n.district.id = :districtId AND n.isActive = true")
    boolean existsByNameIgnoreCaseAndDistrictIdAndIsActiveTrue(@Param("name") String name, @Param("districtId") Long districtId);

    @Query("SELECT CASE WHEN COUNT(n) > 0 THEN true ELSE false END " +
            "FROM Neighborhood n WHERE LOWER(n.name) = LOWER(:name) AND n.district.id = :districtId AND n.id != :id AND n.isActive = true")
    boolean existsByNameIgnoreCaseAndDistrictIdAndIdNotAndIsActiveTrue(@Param("name") String name, @Param("districtId") Long districtId, @Param("id") Long id);

    @Query("SELECT CASE WHEN COUNT(n) > 0 THEN true ELSE false END FROM Neighborhood n WHERE n.slug = :slug")
    boolean existsBySlug(@Param("slug") String slug);

    @Query("SELECT CASE WHEN COUNT(n) > 0 THEN true ELSE false END " +
            "FROM Neighborhood n WHERE n.district.id = :districtId AND n.isActive = true")
    boolean existsByDistrictIdAndIsActiveTrue(@Param("districtId") Long districtId);

    @Query("SELECT n FROM Neighborhood n WHERE n.district.id = :districtId AND n.isActive = true ORDER BY n.name ASC")
    List<Neighborhood> findByDistrictIdAndHasMetroStationTrueAndIsActiveTrueOrderByNameAsc(@Param("districtId") Long districtId);

    @Query("SELECT COUNT(n) FROM Neighborhood n WHERE n.isActive = true")
    Long countByIsActiveTrue();

    @Query("SELECT COALESCE(SUM(n.schoolCount), 0) FROM Neighborhood n WHERE n.isActive = true")
    Long getTotalSchoolCount();

    // Complex search query for neighborhoods
    @Query("SELECT n FROM Neighborhood n WHERE n.isActive = true " +
            "AND (:searchTerm IS NULL OR " +
            "    LOWER(n.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "    LOWER(n.nameEn) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "AND (:districtId IS NULL OR n.district.id = :districtId) " +
            "AND (:hasSchools IS NULL OR " +
            "    (:hasSchools = true AND n.schoolCount > 0) OR " +
            "    (:hasSchools = false AND (n.schoolCount IS NULL OR n.schoolCount = 0))) " +
            "AND (:minSchoolCount IS NULL OR n.schoolCount >= :minSchoolCount) " +
            "AND (:minSocioeconomicLevel IS NULL OR n.district.socioeconomicLevel >= :minSocioeconomicLevel) " +
            "AND (:minIncomeLevel IS NULL OR n.incomeLevel >= :minIncomeLevel) " +
            "AND (:hasMetroStation IS NULL OR n.hasMetroStation = :hasMetroStation)")
    Page<Neighborhood> searchNeighborhoods(
            @Param("searchTerm") String searchTerm,
            @Param("districtId") Long districtId,
            @Param("hasSchools") Boolean hasSchools,
            @Param("minSchoolCount") Integer minSchoolCount,
            @Param("minSocioeconomicLevel") SocioeconomicLevel minSocioeconomicLevel,
            @Param("minIncomeLevel") IncomeLevel minIncomeLevel,
            @Param("hasMetroStation") Boolean hasMetroStation,
            Pageable pageable);

    // Location suggestions for autocomplete
    @Query("SELECT new com.genixo.education.search.dto.location.LocationSuggestionDto(" +
            "CONCAT('N', n.id), n.name, 'NEIGHBORHOOD', " +
            "CONCAT(n.name, ', ', n.district.name, ', ', n.district.province.name), " +
            "n.latitude, n.longitude, n.schoolCount, '1.0') " +
            "FROM Neighborhood n WHERE n.isActive = true AND " +
            "LOWER(n.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "ORDER BY n.schoolCount DESC, n.name ASC")
    List<LocationSuggestionDto> findLocationSuggestions(@Param("query") String query, Pageable pageable);

    @Query(value = "SELECT n FROM Neighborhood n WHERE n.isActive = true AND " +
            "n.latitude IS NOT NULL AND n.longitude IS NOT NULL AND " +
            "(6371 * acos(cos(radians(:latitude)) * cos(radians(n.latitude)) * " +
            "cos(radians(n.longitude) - radians(:longitude)) + " +
            "sin(radians(:latitude)) * sin(radians(n.latitude)))) <= :radiusKm " +
            "ORDER BY (6371 * acos(cos(radians(:latitude)) * cos(radians(n.latitude)) * " +
            "cos(radians(n.longitude) - radians(:longitude)) + " +
            "sin(radians(:latitude)) * sin(radians(n.latitude)))) ASC")
    List<Neighborhood> findNearbyNeighborhoods(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radiusKm") Double radiusKm,
            Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(n) > 0 THEN true ELSE false END " +
            "FROM Neighborhood n WHERE n.id = :neighborhoodId AND n.district.id = :districtId " +
            "AND n.district.province.id = :provinceId AND n.district.province.country.id = :countryId AND n.isActive = true")
    boolean existsByValidHierarchy(@Param("neighborhoodId") Long neighborhoodId, @Param("districtId") Long districtId,
                                   @Param("provinceId") Long provinceId, @Param("countryId") Long countryId);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
            "FROM School s WHERE s.campus.neighborhood.id = :neighborhoodId AND s.isActive = true")
    boolean hasActiveSchools(@Param("neighborhoodId") Long neighborhoodId);

    // Top neighborhoods by school count
    @Query("SELECT n FROM Neighborhood n WHERE n.isActive = true AND n.schoolCount > 0 " +
            "ORDER BY n.schoolCount DESC, n.name ASC")
    List<Neighborhood> findTopNeighborhoodsBySchoolCount(Pageable pageable);

    // Family-friendly neighborhoods
    @Query("SELECT n FROM Neighborhood n WHERE n.district.id = :districtId AND n.isActive = true " +
            "AND n.familyFriendlinessScore >= :minScore " +
            "ORDER BY n.familyFriendlinessScore DESC, n.name ASC")
    List<Neighborhood> findFamilyFriendlyNeighborhoods(@Param("districtId") Long districtId, @Param("minScore") Integer minScore);

    // High-income neighborhoods
    @Query("SELECT n FROM Neighborhood n WHERE n.isActive = true AND " +
            "n.incomeLevel IN ('HIGH', 'VERY_HIGH', 'LUXURY') " +
            "ORDER BY n.incomeLevel DESC, n.averagePropertyPrice DESC, n.name ASC")
    List<Neighborhood> findHighIncomeNeighborhoods();

    // Neighborhoods with good school access
    @Query("SELECT n FROM Neighborhood n WHERE n.isActive = true AND " +
            "n.educationAccessibilityScore >= :minScore AND n.schoolCount > 0 " +
            "ORDER BY n.educationAccessibilityScore DESC, n.schoolCount DESC")
    List<Neighborhood> findNeighborhoodsWithGoodSchoolAccess(@Param("minScore") Integer minScore);

    // Neighborhoods with high safety scores
    @Query("SELECT n FROM Neighborhood n WHERE n.isActive = true AND " +
            "n.safetyScore >= :minScore " +
            "ORDER BY n.safetyScore DESC, n.name ASC")
    List<Neighborhood> findSafeNeighborhoods(@Param("minScore") Integer minScore);

    // Neighborhoods with public transport access
    @Query("SELECT n FROM Neighborhood n WHERE n.isActive = true AND " +
            "(n.hasMetroStation = true OR n.publicTransportFrequency >= :minFrequency) " +
            "ORDER BY n.hasMetroStation DESC, n.publicTransportFrequency DESC, n.name ASC")
    List<Neighborhood> findNeighborhoodsWithGoodTransport(@Param("minFrequency") Integer minFrequency);

    // Neighborhoods with amenities
    @Query("SELECT n FROM Neighborhood n WHERE n.isActive = true AND " +
            "n.hasShoppingCenter = true AND n.hasHospital = true AND n.hasPark = true " +
            "ORDER BY (n.restaurantCount + n.cafeCount + n.bankCount) DESC, n.name ASC")
    List<Neighborhood> findNeighborhoodsWithGoodAmenities();

    // Investment attractive neighborhoods
    @Query("SELECT n FROM Neighborhood n WHERE n.isActive = true AND " +
            "n.investmentAttractiveness >= :minScore AND n.developmentPotential IN ('HIGH', 'MEDIUM') " +
            "ORDER BY n.investmentAttractiveness DESC, n.propertyDemandLevel DESC")
    List<Neighborhood> findInvestmentAttractive(@Param("minScore") Integer minScore);

    // Green and clean neighborhoods
    @Query("SELECT n FROM Neighborhood n WHERE n.isActive = true AND " +
            "n.greenSpacePercentage >= :minGreenSpace AND n.cleanlinessScore >= :minCleanliness " +
            "ORDER BY n.greenSpacePercentage DESC, n.cleanlinessScore DESC")
    List<Neighborhood> findGreenAndCleanNeighborhoods(@Param("minGreenSpace") Double minGreenSpace,
                                                      @Param("minCleanliness") Integer minCleanliness);

    // Demographics queries
    @Query("SELECT n FROM Neighborhood n WHERE n.isActive = true AND " +
            "n.familyWithChildrenPercentage >= :minFamilyPercentage " +
            "ORDER BY n.familyWithChildrenPercentage DESC")
    List<Neighborhood> findFamilyOrientedNeighborhoods(@Param("minFamilyPercentage") Double minFamilyPercentage);

    @Query("SELECT n FROM Neighborhood n WHERE n.isActive = true AND " +
            "n.youngProfessionalPercentage >= :minProfessionalPercentage " +
            "ORDER BY n.youngProfessionalPercentage DESC")
    List<Neighborhood> findYoungProfessionalNeighborhoods(@Param("minProfessionalPercentage") Double minProfessionalPercentage);

    // Housing type queries
    @Query("SELECT n FROM Neighborhood n WHERE n.isActive = true AND " +
            "n.housingType = :housingType ORDER BY n.name ASC")
    List<Neighborhood> findByHousingType(@Param("housingType") String housingType);

    @Query("SELECT n FROM Neighborhood n WHERE n.isActive = true AND " +
            "n.isGatedCommunity = true ORDER BY n.averagePropertyPrice DESC")
    List<Neighborhood> findGatedCommunities();

    @Query("SELECT n FROM Neighborhood n WHERE n.isActive = true AND " +
            "n.isHistorical = true ORDER BY n.name ASC")
    List<Neighborhood> findHistoricalNeighborhoods();

    // Price range queries
    @Query("SELECT n FROM Neighborhood n WHERE n.isActive = true AND " +
            "n.averagePropertyPrice BETWEEN :minPrice AND :maxPrice " +
            "ORDER BY n.averagePropertyPrice ASC")
    List<Neighborhood> findByPropertyPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);

    @Query("SELECT n FROM Neighborhood n WHERE n.isActive = true AND " +
            "n.averageRentPrice BETWEEN :minRent AND :maxRent " +
            "ORDER BY n.averageRentPrice ASC")
    List<Neighborhood> findByRentPriceRange(@Param("minRent") Double minRent, @Param("maxRent") Double maxRent);

    // Statistical queries
    @Query("SELECT AVG(n.averagePropertyPrice) FROM Neighborhood n WHERE n.isActive = true AND n.averagePropertyPrice IS NOT NULL")
    Double getAveragePropertyPrice();

    @Query("SELECT AVG(n.averageRentPrice) FROM Neighborhood n WHERE n.isActive = true AND n.averageRentPrice IS NOT NULL")
    Double getAverageRentPrice();

    @Query("SELECT MIN(n.averagePropertyPrice), MAX(n.averagePropertyPrice) FROM Neighborhood n WHERE n.isActive = true AND n.averagePropertyPrice IS NOT NULL")
    Object[] getPropertyPriceRange();

    @Query("SELECT n.incomeLevel, COUNT(n) FROM Neighborhood n WHERE n.isActive = true AND n.incomeLevel IS NOT NULL GROUP BY n.incomeLevel ORDER BY n.incomeLevel")
    List<Object[]> getIncomeLevelDistribution();

    @Query("SELECT n.district.province.name, COUNT(n) FROM Neighborhood n WHERE n.isActive = true GROUP BY n.district.province.name ORDER BY COUNT(n) DESC")
    List<Object[]> getNeighborhoodCountByProvince();

    @Query("SELECT n FROM Neighborhood n WHERE n.name = :name and n.district.id = :id ")
    List<Neighborhood> checkIfExist(@Param("name") String name, @Param("id") Long id);
}
