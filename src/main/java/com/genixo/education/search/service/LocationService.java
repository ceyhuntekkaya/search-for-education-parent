package com.genixo.education.search.service;

import com.genixo.education.search.common.exception.BusinessException;
import com.genixo.education.search.common.exception.ResourceNotFoundException;
import com.genixo.education.search.dto.location.*;
import com.genixo.education.search.entity.location.*;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.enumaration.*;
import com.genixo.education.search.repository.location.*;
import com.genixo.education.search.service.auth.JwtService;
import com.genixo.education.search.service.converter.LocationConverterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class LocationService {

    private final CountryRepository countryRepository;
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final NeighborhoodRepository neighborhoodRepository;
    private final LocationConverterService converterService;
    private final JwtService jwtService;

    // ================================ COUNTRY OPERATIONS ================================

    @Cacheable(value = "countries")
    public List<CountryDto> getAllCountries() {
        log.info("Fetching all countries");

        List<Country> countries = countryRepository.findAllByIsActiveTrueOrderBySortOrderAscNameAsc();
        return countries.stream()
                .map(converterService::mapToDto)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "supported_countries")
    public List<CountrySummaryDto> getSupportedCountries() {
        log.info("Fetching supported countries");

        List<Country> countries = countryRepository.findBySupportedTrueAndIsActiveTrueOrderBySortOrderAscNameAsc();
        return countries.stream()
                .map(converterService::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "countries", key = "#id")
    public CountryDto getCountryById(Long id) {
        log.info("Fetching country with ID: {}", id);

        Country country = countryRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Country", id));

        return converterService.mapToDto(country);
    }

    @Cacheable(value = "countries", key = "#isoCode2")
    public CountryDto getCountryByIsoCode(String isoCode2) {
        log.info("Fetching country with ISO code: {}", isoCode2);

        Country country = countryRepository.findByIsoCode2AndIsActiveTrue(isoCode2.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Country not found with ISO code: " + isoCode2));

        return converterService.mapToDto(country);
    }

    @Transactional
    @CacheEvict(value = {"countries", "supported_countries"}, allEntries = true)
    public CountryDto createCountry(CountryDto countryDto, HttpServletRequest request) {
        log.info("Creating new country: {}", countryDto.getName());

        User user = jwtService.getUser(request);
        validateSystemAdminAccess(user);

        // Check ISO code uniqueness
        if (countryRepository.existsByIsoCode2(countryDto.getIsoCode2().toUpperCase())) {
            throw new BusinessException("Country with ISO code already exists: " + countryDto.getIsoCode2());
        }

        if (countryRepository.existsByIsoCode3(countryDto.getIsoCode3().toUpperCase())) {
            throw new BusinessException("Country with ISO3 code already exists: " + countryDto.getIsoCode3());
        }

        Country country = new Country();
        country.setName(countryDto.getName());
        country.setNameEn(countryDto.getNameEn());
        country.setIsoCode2(countryDto.getIsoCode2().toUpperCase());
        country.setIsoCode3(countryDto.getIsoCode3().toUpperCase());
        country.setPhoneCode(countryDto.getPhoneCode());
        country.setCurrencyCode(countryDto.getCurrencyCode());
        country.setCurrencySymbol(countryDto.getCurrencySymbol());
        country.setFlagEmoji(countryDto.getFlagEmoji());
        country.setLatitude(countryDto.getLatitude());
        country.setLongitude(countryDto.getLongitude());
        country.setTimezone(countryDto.getTimezone());
        country.setIsSupported(countryDto.getIsSupported() != null ? countryDto.getIsSupported() : false);
        country.setSortOrder(countryDto.getSortOrder() != null ? countryDto.getSortOrder() : 0);
        country.setCreatedBy(user.getId());

        country = countryRepository.save(country);
        log.info("Country created with ID: {}", country.getId());

        return converterService.mapToDto(country);
    }

    @Transactional
    @CacheEvict(value = {"countries", "supported_countries"}, allEntries = true)
    public CountryDto updateCountry(Long id, LocationUpdateDto updateDto, HttpServletRequest request) {
        log.info("Updating country with ID: {}", id);

        User user = jwtService.getUser(request);
        validateSystemAdminAccess(user);

        Country country = countryRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Country", id));

        if (StringUtils.hasText(updateDto.getName())) {
            country.setName(updateDto.getName());
        }
        if (StringUtils.hasText(updateDto.getNameEn())) {
            country.setNameEn(updateDto.getNameEn());
        }
        if (updateDto.getLatitude() != null) {
            country.setLatitude(updateDto.getLatitude());
        }
        if (updateDto.getLongitude() != null) {
            country.setLongitude(updateDto.getLongitude());
        }
        if (updateDto.getIsActive() != null) {
            country.setIsActive(updateDto.getIsActive());
        }
        if (updateDto.getSortOrder() != null) {
            country.setSortOrder(updateDto.getSortOrder());
        }

        country.setUpdatedBy(user.getId());
        country = countryRepository.save(country);

        log.info("Country updated with ID: {}", id);
        return converterService.mapToDto(country);
    }

    // ================================ PROVINCE OPERATIONS ================================

    @Cacheable(value = "provinces", key = "#countryId")
    public List<ProvinceSummaryDto> getProvincesByCountry(Long countryId) {
        log.info("Fetching provinces for country: {}", countryId);

        List<Province> provinces = provinceRepository.findByCountryIdAndIsActiveTrueOrderBySortOrderAscNameAsc(countryId);
        return provinces.stream()
                .map(converterService::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "provinces", key = "#id")
    public ProvinceDto getProvinceById(Long id) {
        log.info("Fetching province with ID: {}", id);

        Province province = provinceRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Province", id));

        return converterService.mapToDto(province);
    }

    @Cacheable(value = "provinces", key = "#slug")
    public ProvinceDto getProvinceBySlug(String slug) {
        log.info("Fetching province with slug: {}", slug);

        Province province = provinceRepository.findBySlugAndIsActiveTrue(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Province not found with slug: " + slug));

        return converterService.mapToDto(province);
    }

    @Transactional
    @CacheEvict(value = {"provinces", "province_summaries"}, allEntries = true)
    public ProvinceDto createProvince(LocationCreateDto createDto, HttpServletRequest request) {
        log.info("Creating new province: {}", createDto.getName());

        User user = jwtService.getUser(request);
        validateSystemAdminAccess(user);

        Country country = countryRepository.findByIdAndIsActiveTrue(createDto.getParentId())
                .orElseThrow(() -> new ResourceNotFoundException("Country", createDto.getParentId()));

        // Check name uniqueness in country
        if (provinceRepository.existsByNameIgnoreCaseAndCountryIdAndIsActiveTrue(createDto.getName(), createDto.getParentId())) {
            throw new BusinessException("Province name already exists in this country: " + createDto.getName());
        }

        // Check code uniqueness if provided
        if (StringUtils.hasText(createDto.getCode()) &&
                provinceRepository.existsByCodeAndIsActiveTrue(createDto.getCode())) {
            throw new BusinessException("Province code already exists: " + createDto.getCode());
        }

        String slug = generateSlug(createDto.getName());
        if (provinceRepository.existsBySlug(slug)) {
            slug = generateSlug(createDto.getName() + "-" + System.currentTimeMillis());
        }

        Province province = new Province();
        province.setCountry(country);
        province.setName(createDto.getName());
        province.setNameEn(createDto.getNameEn());
        province.setCode(createDto.getCode());
        province.setSlug(slug);
        province.setLatitude(createDto.getLatitude());
        province.setLongitude(createDto.getLongitude());
        province.setDescription(createDto.getDescription());
        province.setCreatedBy(user.getId());

        province = provinceRepository.save(province);
        log.info("Province created with ID: {}", province.getId());

        return converterService.mapToDto(province);
    }

    public List<ProvinceSummaryDto> getMetropolitanProvinces() {
        log.info("Fetching metropolitan provinces");

        List<Province> provinces = provinceRepository.findByIsMetropolitanTrueAndIsActiveTrueOrderByNameAsc();
        return provinces.stream()
                .map(converterService::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    // ================================ DISTRICT OPERATIONS ================================

    @Cacheable(value = "districts", key = "#provinceId")
    public List<DistrictSummaryDto> getDistrictsByProvince(Long provinceId) {
        log.info("Fetching districts for province: {}", provinceId);

        List<District> districts = districtRepository.findByProvinceIdAndIsActiveTrueOrderBySortOrderAscNameAsc(provinceId);
        return districts.stream()
                .map(converterService::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "districts", key = "#id")
    public DistrictDto getDistrictById(Long id) {
        log.info("Fetching district with ID: {}", id);

        District district = districtRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("District", id));

        return converterService.mapToDto(district);
    }

    @Cacheable(value = "districts", key = "#slug")
    public DistrictDto getDistrictBySlug(String slug) {
        log.info("Fetching district with slug: {}", slug);

        District district = districtRepository.findBySlugAndIsActiveTrue(slug)
                .orElseThrow(() -> new ResourceNotFoundException("District not found with slug: " + slug));

        return converterService.mapToDto(district);
    }

    @Transactional
    @CacheEvict(value = {"districts", "district_summaries"}, allEntries = true)
    public DistrictDto createDistrict(LocationCreateDto createDto, HttpServletRequest request) {
        log.info("Creating new district: {}", createDto.getName());

        User user = jwtService.getUser(request);
        validateSystemAdminAccess(user);

        Province province = provinceRepository.findByIdAndIsActiveTrue(createDto.getParentId())
                .orElseThrow(() -> new ResourceNotFoundException("Province", createDto.getParentId()));

        // Check name uniqueness in province
        if (districtRepository.existsByNameIgnoreCaseAndProvinceIdAndIsActiveTrue(createDto.getName(), createDto.getParentId())) {
            throw new BusinessException("District name already exists in this province: " + createDto.getName());
        }

        String slug = generateSlug(createDto.getName());
        if (districtRepository.existsBySlug(slug)) {
            slug = generateSlug(createDto.getName() + "-" + System.currentTimeMillis());
        }

        District district = new District();
        district.setProvince(province);
        district.setName(createDto.getName());
        district.setNameEn(createDto.getNameEn());
        district.setCode(createDto.getCode());
        district.setSlug(slug);
        district.setDistrictType(DistrictType.ILCE); // Default type
        district.setLatitude(createDto.getLatitude());
        district.setLongitude(createDto.getLongitude());
        district.setDescription(createDto.getDescription());
        district.setCreatedBy(user.getId());

        district = districtRepository.save(district);
        log.info("District created with ID: {}", district.getId());

        return converterService.mapToDto(district);
    }

    public List<DistrictSummaryDto> getCentralDistricts(Long provinceId) {
        log.info("Fetching central districts for province: {}", provinceId);

        List<District> districts = districtRepository.findByProvinceIdAndIsCentralTrueAndIsActiveTrueOrderByNameAsc(provinceId);
        return districts.stream()
                .map(converterService::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    // ================================ NEIGHBORHOOD OPERATIONS ================================

    @Cacheable(value = "neighborhoods", key = "#districtId")
    public List<NeighborhoodSummaryDto> getNeighborhoodsByDistrict(Long districtId) {
        log.info("Fetching neighborhoods for district: {}", districtId);

        List<Neighborhood> neighborhoods = neighborhoodRepository.findByDistrictIdAndIsActiveTrueOrderBySortOrderAscNameAsc(districtId);
        return neighborhoods.stream()
                .map(converterService::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "neighborhoods", key = "#id")
    public NeighborhoodDto getNeighborhoodById(Long id) {
        log.info("Fetching neighborhood with ID: {}", id);

        Neighborhood neighborhood = neighborhoodRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Neighborhood", id));

        return converterService.mapToDto(neighborhood);
    }

    @Cacheable(value = "neighborhoods", key = "#slug")
    public NeighborhoodDto getNeighborhoodBySlug(String slug) {
        log.info("Fetching neighborhood with slug: {}", slug);

        Neighborhood neighborhood = neighborhoodRepository.findBySlugAndIsActiveTrue(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Neighborhood not found with slug: " + slug));

        return converterService.mapToDto(neighborhood);
    }

    @Transactional
    @CacheEvict(value = {"neighborhoods", "neighborhood_summaries"}, allEntries = true)
    public NeighborhoodDto createNeighborhood(LocationCreateDto createDto, HttpServletRequest request) {
        log.info("Creating new neighborhood: {}", createDto.getName());

        User user = jwtService.getUser(request);
        validateSystemAdminAccess(user);

        District district = districtRepository.findByIdAndIsActiveTrue(createDto.getParentId())
                .orElseThrow(() -> new ResourceNotFoundException("District", createDto.getParentId()));

        // Check name uniqueness in district
        if (neighborhoodRepository.existsByNameIgnoreCaseAndDistrictIdAndIsActiveTrue(createDto.getName(), createDto.getParentId())) {
            throw new BusinessException("Neighborhood name already exists in this district: " + createDto.getName());
        }

        String slug = generateSlug(createDto.getName());
        if (neighborhoodRepository.existsBySlug(slug)) {
            slug = generateSlug(createDto.getName() + "-" + System.currentTimeMillis());
        }

        Neighborhood neighborhood = new Neighborhood();
        neighborhood.setDistrict(district);
        neighborhood.setName(createDto.getName());
        neighborhood.setNameEn(createDto.getNameEn());
        neighborhood.setCode(createDto.getCode());
        neighborhood.setSlug(slug);
        neighborhood.setNeighborhoodType(NeighborhoodType.MAHALLE); // Default type
        neighborhood.setLatitude(createDto.getLatitude());
        neighborhood.setLongitude(createDto.getLongitude());
        neighborhood.setDescription(createDto.getDescription());
        neighborhood.setCreatedBy(user.getId());

        neighborhood = neighborhoodRepository.save(neighborhood);
        log.info("Neighborhood created with ID: {}", neighborhood.getId());

        return converterService.mapToDto(neighborhood);
    }

    // ================================ SEARCH OPERATIONS ================================

    public Page<LocationSuggestionDto> searchLocations(LocationSearchDto searchDto) {
        log.info("Searching locations with term: {}", searchDto.getSearchTerm());

        Pageable pageable = PageRequest.of(
                searchDto.getPage() != null ? searchDto.getPage() : 0,
                searchDto.getSize() != null ? searchDto.getSize() : 20,
                createLocationSort(searchDto.getSortBy(), searchDto.getSortDirection())
        );

        // Determine which repositories to search based on location type
        if ("NEIGHBORHOOD".equals(searchDto.getLocationType())) {
            Page<Neighborhood> neighborhoods = neighborhoodRepository.searchNeighborhoods(
                    searchDto.getSearchTerm(),
                    searchDto.getDistrictId(),
                    searchDto.getHasSchools(),
                    searchDto.getMinSchoolCount(),
                    searchDto.getMinSocioeconomicLevel(),
                    searchDto.getMinIncomeLevel(),
                    searchDto.getHasMetroStation(),
                    pageable
            );
            return neighborhoods.map(this::mapNeighborhoodToSuggestion);
        } else if ("DISTRICT".equals(searchDto.getLocationType())) {
            Page<District> districts = districtRepository.searchDistricts(
                    searchDto.getSearchTerm(),
                    searchDto.getProvinceId(),
                    searchDto.getHasSchools(),
                    searchDto.getMinSchoolCount(),
                    searchDto.getMinSocioeconomicLevel(),
                    searchDto.getHasMetroStation(),
                    searchDto.getHasUniversity(),
                    pageable
            );
            return districts.map(this::mapDistrictToSuggestion);
        } else if ("PROVINCE".equals(searchDto.getLocationType())) {
            Page<Province> provinces = provinceRepository.searchProvinces(
                    searchDto.getSearchTerm(),
                    searchDto.getCountryId(),
                    searchDto.getHasSchools(),
                    searchDto.getMinSchoolCount(),
                    searchDto.getIsMetropolitan(),
                    searchDto.getHasUniversity(),
                    pageable
            );
            return provinces.map(this::mapProvinceToSuggestion);
        } else {
            // Mixed search across all location types
            return performMixedLocationSearch(searchDto, pageable);
        }
    }

    @Cacheable(value = "location_suggestions", key = "#query")
    public List<LocationSuggestionDto> getLocationSuggestions(String query, int limit) {
        log.info("Getting location suggestions for: {}", query);

        List<LocationSuggestionDto> suggestions = List.of();

        if (StringUtils.hasText(query) && query.length() >= 2) {
            suggestions = neighborhoodRepository.findLocationSuggestions(query, Pageable.ofSize(limit));
        }

        return suggestions;
    }

    // ================================ LOCATION HIERARCHY ================================

    public LocationHierarchyDto getLocationHierarchy(Long neighborhoodId) {
        log.info("Getting location hierarchy for neighborhood: {}", neighborhoodId);

        Neighborhood neighborhood = neighborhoodRepository.findByIdAndIsActiveTrue(neighborhoodId)
                .orElseThrow(() -> new ResourceNotFoundException("Neighborhood", neighborhoodId));

        return LocationHierarchyDto.builder()
                .country(converterService.mapToSummaryDto(neighborhood.getDistrict().getProvince().getCountry()))
                .province(converterService.mapToSummaryDto(neighborhood.getDistrict().getProvince()))
                .district(converterService.mapToSummaryDto(neighborhood.getDistrict()))
                .neighborhood(converterService.mapToSummaryDto(neighborhood))
                .build();
    }

    public LocationHierarchyDto getLocationHierarchyByDistrict(Long districtId) {
        log.info("Getting location hierarchy for district: {}", districtId);

        District district = districtRepository.findByIdAndIsActiveTrue(districtId)
                .orElseThrow(() -> new ResourceNotFoundException("District", districtId));

        return LocationHierarchyDto.builder()
                .country(converterService.mapToSummaryDto(district.getProvince().getCountry()))
                .province(converterService.mapToSummaryDto(district.getProvince()))
                .district(converterService.mapToSummaryDto(district))
                .build();
    }

    // ================================ NEARBY LOCATIONS ================================

    public NearbyLocationsDto getNearbyLocations(Double latitude, Double longitude, Double radiusKm, Integer limit) {
        log.info("Finding locations near coordinates: {}, {} within {} km", latitude, longitude, radiusKm);

        List<Neighborhood> nearbyNeighborhoods = neighborhoodRepository.findNearbyNeighborhoods(
                latitude, longitude, radiusKm, Pageable.ofSize(limit != null ? limit : 50));

        List<LocationDistanceDto> locationDistances = nearbyNeighborhoods.stream()
                .map(neighborhood -> {
                    Double distance = calculateDistance(latitude, longitude,
                            neighborhood.getLatitude(), neighborhood.getLongitude());
                    LocationSuggestionDto suggestion = mapNeighborhoodToSuggestion(neighborhood);

                    return LocationDistanceDto.builder()
                            .location(suggestion)
                            .distanceKm(distance)
                            .estimatedTravelTimeMinutes(calculateTravelTime(distance))
                            .transportationMethod("CAR")
                            .build();
                })
                .sorted((a, b) -> Double.compare(a.getDistanceKm(), b.getDistanceKm()))
                .collect(Collectors.toList());

        return NearbyLocationsDto.builder()
                .center(CoordinatesDto.builder().latitude(latitude).longitude(longitude).build())
                .radiusKm(radiusKm)
                .locations(locationDistances)
                .totalCount(locationDistances.size())
                .build();
    }

    // ================================ STATISTICS ================================

    @Cacheable(value = "location_statistics")
    public LocationStatisticsDto getLocationStatistics() {
        log.info("Fetching location statistics");

        return LocationStatisticsDto.builder()
                .totalCountries(countryRepository.countByIsActiveTrue())
                .supportedCountries(countryRepository.countByIsActiveTrueAndIsSupportedTrue())
                .totalProvinces(provinceRepository.countByIsActiveTrue())
                .metropolitanProvinces(provinceRepository.countByIsActiveTrueAndIsMetropolitanTrue())
                .totalDistricts(districtRepository.countByIsActiveTrue())
                .centralDistricts(districtRepository.countByIsActiveTrueAndIsCentralTrue())
                .totalNeighborhoods(neighborhoodRepository.countByIsActiveTrue())
                .totalSchools(neighborhoodRepository.getTotalSchoolCount())
                .totalStudents(0L) // Would need to be calculated from school entities
                .totalTeachers(0L) // Would need to be calculated from school entities
                .build();
    }

    // ================================ BULK OPERATIONS ================================

    @Transactional
    @CacheEvict(value = {"countries", "provinces", "districts", "neighborhoods"}, allEntries = true)
    public LocationImportResultDto bulkImportLocations(BulkLocationImportDto importDto, HttpServletRequest request) {
        log.info("Starting bulk location import from: {}", importDto.getFileUrl());

        User user = jwtService.getUser(request);
        validateSystemAdminAccess(user);

        String importId = "IMPORT_" + System.currentTimeMillis();

        try {
            // This would be implemented based on the file type and mapping configuration
            // For now, return a placeholder result

            return LocationImportResultDto.builder()
                    .success(true)
                    .importId(importId)
                    .importDate(LocalDateTime.now())
                    .totalRecords(0)
                    .successfulImports(0)
                    .failedImports(0)
                    .skippedRecords(0)
                    .errors(List.of())
                    .warnings(List.of("Bulk import functionality needs to be implemented"))
                    .build();

        } catch (Exception e) {
            log.error("Bulk import failed for import ID: {}", importId, e);

            return LocationImportResultDto.builder()
                    .success(false)
                    .importId(importId)
                    .importDate(LocalDateTime.now())
                    .totalRecords(0)
                    .successfulImports(0)
                    .failedImports(0)
                    .skippedRecords(0)
                    .errors(List.of("Import failed: " + e.getMessage()))
                    .warnings(List.of())
                    .build();
        }
    }

    // ================================ HELPER METHODS ================================

    private void validateSystemAdminAccess(User user) {
        boolean hasSystemRole = user.getUserRoles().stream()
                .anyMatch(userRole -> userRole.getRoleLevel() == RoleLevel.SYSTEM);

        if (!hasSystemRole) {
            throw new BusinessException("System administrator access required for location management");
        }
    }

    private String generateSlug(String input) {
        if (!StringUtils.hasText(input)) {
            return "location-" + System.currentTimeMillis();
        }

        return input.toLowerCase()
                .replaceAll("[çÇ]", "c")
                .replaceAll("[ğĞ]", "g")
                .replaceAll("[ıI]", "i")
                .replaceAll("[öÖ]", "o")
                .replaceAll("[şŞ]", "s")
                .replaceAll("[üÜ]", "u")
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    private Sort createLocationSort(String sortBy, String sortDirection) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        String sortField = switch (sortBy != null ? sortBy.toLowerCase() : "name") {
            case "school_count" -> "schoolCount";
            case "population" -> "population";
            case "sort_order" -> "sortOrder";
            default -> "name";
        };

        return Sort.by(direction, sortField);
    }

    private Page<LocationSuggestionDto> performMixedLocationSearch(LocationSearchDto searchDto, Pageable pageable) {
        // This would perform a combined search across all location types
        // For now, return empty page
        return Page.empty();
    }

    private LocationSuggestionDto mapNeighborhoodToSuggestion(Neighborhood neighborhood) {
        String fullName = String.format("%s, %s, %s",
                neighborhood.getName(),
                neighborhood.getDistrict().getName(),
                neighborhood.getDistrict().getProvince().getName());

        return LocationSuggestionDto.builder()
                .id("N" + neighborhood.getId())
                .name(neighborhood.getName())
                .type("NEIGHBORHOOD")
                .fullName(fullName)
                .latitude(neighborhood.getLatitude())
                .longitude(neighborhood.getLongitude())
                .schoolCount(neighborhood.getSchoolCount())
                .build();
    }

    private LocationSuggestionDto mapDistrictToSuggestion(District district) {
        String fullName = String.format("%s, %s",
                district.getName(),
                district.getProvince().getName());

        return LocationSuggestionDto.builder()
                .id("D" + district.getId())
                .name(district.getName())
                .type("DISTRICT")
                .fullName(fullName)
                .latitude(district.getLatitude())
                .longitude(district.getLongitude())
                .schoolCount(district.getSchoolCount())
                .build();
    }

    private LocationSuggestionDto mapProvinceToSuggestion(Province province) {
        return LocationSuggestionDto.builder()
                .id("P" + province.getId())
                .name(province.getName())
                .type("PROVINCE")
                .fullName(province.getName())
                .latitude(province.getLatitude())
                .longitude(province.getLongitude())
                .schoolCount(province.getSchoolCount())
                .build();
    }

    private Double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            return 0.0;
        }

        final int R = 6371; // Earth's radius in kilometers

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    private Integer calculateTravelTime(Double distanceKm) {
        if (distanceKm == null || distanceKm <= 0) {
            return 0;
        }

        // Estimate travel time by car in city traffic (average 30 km/h)
        return (int) Math.ceil((distanceKm / 30.0) * 60);
    }

    // ================================ UPDATE OPERATIONS ================================

    @Transactional
    @CacheEvict(value = {"provinces", "province_summaries"}, allEntries = true)
    public ProvinceDto updateProvince(Long id, LocationUpdateDto updateDto, HttpServletRequest request) {
        log.info("Updating province with ID: {}", id);

        User user = jwtService.getUser(request);
        validateSystemAdminAccess(user);

        Province province = provinceRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Province", id));

        if (StringUtils.hasText(updateDto.getName())) {
            // Check name uniqueness if changed
            if (!province.getName().equalsIgnoreCase(updateDto.getName()) &&
                    provinceRepository.existsByNameIgnoreCaseAndCountryIdAndIdNotAndIsActiveTrue(
                            updateDto.getName(), province.getCountry().getId(), id)) {
                throw new BusinessException("Province name already exists in this country: " + updateDto.getName());
            }
            province.setName(updateDto.getName());
        }

        if (StringUtils.hasText(updateDto.getNameEn())) {
            province.setNameEn(updateDto.getNameEn());
        }
        if (updateDto.getLatitude() != null) {
            province.setLatitude(updateDto.getLatitude());
        }
        if (updateDto.getLongitude() != null) {
            province.setLongitude(updateDto.getLongitude());
        }
        if (StringUtils.hasText(updateDto.getDescription())) {
            province.setDescription(updateDto.getDescription());
        }
        if (updateDto.getIsActive() != null) {
            province.setIsActive(updateDto.getIsActive());
        }
        if (updateDto.getSortOrder() != null) {
            province.setSortOrder(updateDto.getSortOrder());
        }

        province.setUpdatedBy(user.getId());
        province = provinceRepository.save(province);

        log.info("Province updated with ID: {}", id);
        return converterService.mapToDto(province);
    }

    @Transactional
    @CacheEvict(value = {"districts", "district_summaries"}, allEntries = true)
    public DistrictDto updateDistrict(Long id, LocationUpdateDto updateDto, HttpServletRequest request) {
        log.info("Updating district with ID: {}", id);

        User user = jwtService.getUser(request);
        validateSystemAdminAccess(user);

        District district = districtRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("District", id));

        if (StringUtils.hasText(updateDto.getName())) {
            // Check name uniqueness if changed
            if (!district.getName().equalsIgnoreCase(updateDto.getName()) &&
                    districtRepository.existsByNameIgnoreCaseAndProvinceIdAndIdNotAndIsActiveTrue(
                            updateDto.getName(), district.getProvince().getId(), id)) {
                throw new BusinessException("District name already exists in this province: " + updateDto.getName());
            }
            district.setName(updateDto.getName());
        }

        if (StringUtils.hasText(updateDto.getNameEn())) {
            district.setNameEn(updateDto.getNameEn());
        }
        if (updateDto.getLatitude() != null) {
            district.setLatitude(updateDto.getLatitude());
        }
        if (updateDto.getLongitude() != null) {
            district.setLongitude(updateDto.getLongitude());
        }
        if (StringUtils.hasText(updateDto.getDescription())) {
            district.setDescription(updateDto.getDescription());
        }
        if (updateDto.getIsActive() != null) {
            district.setIsActive(updateDto.getIsActive());
        }
        if (updateDto.getSortOrder() != null) {
            district.setSortOrder(updateDto.getSortOrder());
        }

        district.setUpdatedBy(user.getId());
        district = districtRepository.save(district);

        log.info("District updated with ID: {}", id);
        return converterService.mapToDto(district);
    }

    @Transactional
    @CacheEvict(value = {"neighborhoods", "neighborhood_summaries"}, allEntries = true)
    public NeighborhoodDto updateNeighborhood(Long id, LocationUpdateDto updateDto, HttpServletRequest request) {
        log.info("Updating neighborhood with ID: {}", id);

        User user = jwtService.getUser(request);
        validateSystemAdminAccess(user);

        Neighborhood neighborhood = neighborhoodRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Neighborhood", id));

        if (StringUtils.hasText(updateDto.getName())) {
            // Check name uniqueness if changed
            if (!neighborhood.getName().equalsIgnoreCase(updateDto.getName()) &&
                    neighborhoodRepository.existsByNameIgnoreCaseAndDistrictIdAndIdNotAndIsActiveTrue(
                            updateDto.getName(), neighborhood.getDistrict().getId(), id)) {
                throw new BusinessException("Neighborhood name already exists in this district: " + updateDto.getName());
            }
            neighborhood.setName(updateDto.getName());
        }

        if (StringUtils.hasText(updateDto.getNameEn())) {
            neighborhood.setNameEn(updateDto.getNameEn());
        }
        if (updateDto.getLatitude() != null) {
            neighborhood.setLatitude(updateDto.getLatitude());
        }
        if (updateDto.getLongitude() != null) {
            neighborhood.setLongitude(updateDto.getLongitude());
        }
        if (StringUtils.hasText(updateDto.getDescription())) {
            neighborhood.setDescription(updateDto.getDescription());
        }
        if (updateDto.getIsActive() != null) {
            neighborhood.setIsActive(updateDto.getIsActive());
        }
        if (updateDto.getSortOrder() != null) {
            neighborhood.setSortOrder(updateDto.getSortOrder());
        }

        neighborhood.setUpdatedBy(user.getId());
        neighborhood = neighborhoodRepository.save(neighborhood);

        log.info("Neighborhood updated with ID: {}", id);
        return converterService.mapToDto(neighborhood);
    }

    // ================================ DELETE OPERATIONS ================================

    @Transactional
    @CacheEvict(value = {"countries", "supported_countries"}, allEntries = true)
    public void deleteCountry(Long id, HttpServletRequest request) {
        log.info("Deleting country with ID: {}", id);

        User user = jwtService.getUser(request);
        validateSystemAdminAccess(user);

        Country country = countryRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Country", id));

        // Check if country has active provinces
        if (provinceRepository.existsByCountryIdAndIsActiveTrue(id)) {
            throw new BusinessException("Cannot delete country with active provinces");
        }

        country.setIsActive(false);
        country.setUpdatedBy(user.getId());
        countryRepository.save(country);

        log.info("Country soft deleted with ID: {}", id);
    }

    @Transactional
    @CacheEvict(value = {"provinces", "province_summaries"}, allEntries = true)
    public void deleteProvince(Long id, HttpServletRequest request) {
        log.info("Deleting province with ID: {}", id);

        User user = jwtService.getUser(request);
        validateSystemAdminAccess(user);

        Province province = provinceRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Province", id));

        // Check if province has active districts
        if (districtRepository.existsByProvinceIdAndIsActiveTrue(id)) {
            throw new BusinessException("Cannot delete province with active districts");
        }

        province.setIsActive(false);
        province.setUpdatedBy(user.getId());
        provinceRepository.save(province);

        log.info("Province soft deleted with ID: {}", id);
    }

    @Transactional
    @CacheEvict(value = {"districts", "district_summaries"}, allEntries = true)
    public void deleteDistrict(Long id, HttpServletRequest request) {
        log.info("Deleting district with ID: {}", id);

        User user = jwtService.getUser(request);
        validateSystemAdminAccess(user);

        District district = districtRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("District", id));

        // Check if district has active neighborhoods
        if (neighborhoodRepository.existsByDistrictIdAndIsActiveTrue(id)) {
            throw new BusinessException("Cannot delete district with active neighborhoods");
        }

        district.setIsActive(false);
        district.setUpdatedBy(user.getId());
        districtRepository.save(district);

        log.info("District soft deleted with ID: {}", id);
    }

    @Transactional
    @CacheEvict(value = {"neighborhoods", "neighborhood_summaries"}, allEntries = true)
    public void deleteNeighborhood(Long id, HttpServletRequest request) {
        log.info("Deleting neighborhood with ID: {}", id);

        User user = jwtService.getUser(request);
        validateSystemAdminAccess(user);

        Neighborhood neighborhood = neighborhoodRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Neighborhood", id));

        // Check if neighborhood has active schools (would need school repository for this)
        // For now, we'll allow deletion

        neighborhood.setIsActive(false);
        neighborhood.setUpdatedBy(user.getId());
        neighborhoodRepository.save(neighborhood);

        log.info("Neighborhood soft deleted with ID: {}", id);
    }

    // ================================ SPECIAL QUERIES ================================

    public List<NeighborhoodSummaryDto> getPopularNeighborhoodsForSchools() {
        log.info("Fetching popular neighborhoods for schools");

        List<Neighborhood> neighborhoods = neighborhoodRepository.findTopNeighborhoodsBySchoolCount(Pageable.ofSize(20));
        return neighborhoods.stream()
                .map(converterService::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    public List<DistrictSummaryDto> getHighSocioeconomicDistricts() {
        log.info("Fetching districts with high socioeconomic levels");

        List<District> districts = districtRepository.findByHighSocioeconomicLevels();
        return districts.stream()
                .map(converterService::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    public List<NeighborhoodSummaryDto> getNeighborhoodsWithMetroAccess(Long districtId) {
        log.info("Fetching neighborhoods with metro access for district: {}", districtId);

        List<Neighborhood> neighborhoods = neighborhoodRepository.findByDistrictIdAndHasMetroStationTrueAndIsActiveTrueOrderByNameAsc(districtId);
        return neighborhoods.stream()
                .map(converterService::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    public List<NeighborhoodSummaryDto> getFamilyFriendlyNeighborhoods(Long districtId, Integer minScore) {
        log.info("Fetching family-friendly neighborhoods for district: {} with min score: {}", districtId, minScore);

        List<Neighborhood> neighborhoods = neighborhoodRepository.findFamilyFriendlyNeighborhoods(districtId, minScore);
        return neighborhoods.stream()
                .map(converterService::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    // ================================ VALIDATION METHODS ================================

    public boolean isValidLocationHierarchy(Long countryId, Long provinceId, Long districtId, Long neighborhoodId) {
        log.info("Validating location hierarchy");

        if (neighborhoodId != null) {
            return neighborhoodRepository.existsByValidHierarchy(neighborhoodId, districtId, provinceId, countryId);
        } else if (districtId != null) {
            return districtRepository.existsByValidHierarchy(districtId, provinceId, countryId);
        } else if (provinceId != null) {
            return provinceRepository.existsByValidHierarchy(provinceId, countryId);
        } else {
            return countryId != null && countryRepository.existsByIdAndIsActiveTrue(countryId);
        }
    }

    public boolean hasSchoolsInLocation(String locationType, Long locationId) {
        log.info("Checking if location has schools: {} - {}", locationType, locationId);

        return switch (locationType.toUpperCase()) {
            case "NEIGHBORHOOD" -> neighborhoodRepository.hasActiveSchools(locationId);
            case "DISTRICT" -> districtRepository.hasActiveSchools(locationId);
            case "PROVINCE" -> provinceRepository.hasActiveSchools(locationId);
            default -> false;
        };
    }
}