package com.genixo.education.search.api.rest;

import com.genixo.education.search.dto.location.*;
import com.genixo.education.search.service.LocationService;
import com.genixo.education.search.service.auth.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Location Management", description = "APIs for managing countries, provinces, districts, neighborhoods and geographic data")
public class LocationController {

    private final LocationService locationService;
    private final JwtService jwtService;

    // ================================ COUNTRY OPERATIONS ================================

    @GetMapping("/countries")
    @Operation(summary = "Get all countries", description = "Get all active countries")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Countries retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<CountryDto>>> getAllCountries(
            HttpServletRequest request) {

        log.debug("Get all countries request");

        List<CountryDto> countries = locationService.getAllCountries();

        ApiResponse<List<CountryDto>> response = ApiResponse.success(countries, "Countries retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/countries/supported")
    @Operation(summary = "Get supported countries", description = "Get all supported countries for the platform")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Supported countries retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<CountrySummaryDto>>> getSupportedCountries(
            HttpServletRequest request) {

        log.debug("Get supported countries request");

        List<CountrySummaryDto> countries = locationService.getSupportedCountries();

        ApiResponse<List<CountrySummaryDto>> response = ApiResponse.success(countries, "Supported countries retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/countries/{id}")
    @Operation(summary = "Get country by ID", description = "Get country details by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Country retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Country not found")
    })
    public ResponseEntity<ApiResponse<CountryDto>> getCountryById(
            @Parameter(description = "Country ID") @PathVariable Long id,
            HttpServletRequest request) {

        log.debug("Get country request: {}", id);

        CountryDto countryDto = locationService.getCountryById(id);

        ApiResponse<CountryDto> response = ApiResponse.success(countryDto, "Country retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/countries/iso/{isoCode}")
    @Operation(summary = "Get country by ISO code", description = "Get country details by ISO 2-letter code")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Country retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Country not found")
    })
    public ResponseEntity<ApiResponse<CountryDto>> getCountryByIsoCode(
            @Parameter(description = "ISO 2-letter country code") @PathVariable String isoCode,
            HttpServletRequest request) {

        log.debug("Get country by ISO code request: {}", isoCode);

        CountryDto countryDto = locationService.getCountryByIsoCode(isoCode);

        ApiResponse<CountryDto> response = ApiResponse.success(countryDto, "Country retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/countries")
    @Operation(summary = "Create country", description = "Create a new country (system admin only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Country created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "System admin access required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "ISO code already exists")
    })
    public ResponseEntity<ApiResponse<CountryDto>> createCountry(
            @Valid @RequestBody CountryDto countryDto,
            HttpServletRequest request) {

        log.info("Create country request: {}", countryDto.getName());

        CountryDto createdCountry = locationService.createCountry(countryDto, request);

        ApiResponse<CountryDto> response = ApiResponse.success(createdCountry, "Country created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/countries/{id}")
    @Operation(summary = "Update country", description = "Update country details (system admin only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Country updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Country not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "System admin access required")
    })
    public ResponseEntity<ApiResponse<CountryDto>> updateCountry(
            @Parameter(description = "Country ID") @PathVariable Long id,
            @Valid @RequestBody LocationUpdateDto updateDto,
            HttpServletRequest request) {

        log.info("Update country request: {}", id);

        CountryDto countryDto = locationService.updateCountry(id, updateDto, request);

        ApiResponse<CountryDto> response = ApiResponse.success(countryDto, "Country updated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/countries/{id}")
    @Operation(summary = "Delete country", description = "Soft delete a country (system admin only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Country deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Country not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "System admin access required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Country has active provinces")
    })
    public ResponseEntity<ApiResponse<Void>> deleteCountry(
            @Parameter(description = "Country ID") @PathVariable Long id,
            HttpServletRequest request) {

        log.info("Delete country request: {}", id);

        locationService.deleteCountry(id, request);

        ApiResponse<Void> response = ApiResponse.success(null, "Country deleted successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ PROVINCE OPERATIONS ================================

    @GetMapping("/countries/{countryId}/provinces")
    @Operation(summary = "Get provinces by country", description = "Get all provinces for a specific country")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Provinces retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Country not found")
    })
    public ResponseEntity<ApiResponse<List<ProvinceSummaryDto>>> getProvincesByCountry(
            @Parameter(description = "Country ID") @PathVariable Long countryId,
            HttpServletRequest request) {

        log.debug("Get provinces by country request: {}", countryId);

        List<ProvinceSummaryDto> provinces = locationService.getProvincesByCountry(countryId);

        ApiResponse<List<ProvinceSummaryDto>> response = ApiResponse.success(provinces, "Provinces retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/provinces/{id}")
    @Operation(summary = "Get province by ID", description = "Get province details by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Province retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Province not found")
    })
    public ResponseEntity<ApiResponse<ProvinceDto>> getProvinceById(
            @Parameter(description = "Province ID") @PathVariable Long id,
            HttpServletRequest request) {

        log.debug("Get province request: {}", id);

        ProvinceDto provinceDto = locationService.getProvinceById(id);

        ApiResponse<ProvinceDto> response = ApiResponse.success(provinceDto, "Province retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/provinces/slug/{slug}")
    @Operation(summary = "Get province by slug", description = "Get province details by slug")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Province retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Province not found")
    })
    public ResponseEntity<ApiResponse<ProvinceDto>> getProvinceBySlug(
            @Parameter(description = "Province slug") @PathVariable String slug,
            HttpServletRequest request) {

        log.debug("Get province by slug request: {}", slug);

        ProvinceDto provinceDto = locationService.getProvinceBySlug(slug);

        ApiResponse<ProvinceDto> response = ApiResponse.success(provinceDto, "Province retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/provinces/metropolitan")
    @Operation(summary = "Get metropolitan provinces", description = "Get all metropolitan provinces")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Metropolitan provinces retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<ProvinceSummaryDto>>> getMetropolitanProvinces(
            HttpServletRequest request) {

        log.debug("Get metropolitan provinces request");

        List<ProvinceSummaryDto> provinces = locationService.getMetropolitanProvinces();

        ApiResponse<List<ProvinceSummaryDto>> response = ApiResponse.success(provinces, "Metropolitan provinces retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/provinces")
    @Operation(summary = "Create province", description = "Create a new province (system admin only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Province created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "System admin access required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Country not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Province name already exists in country")
    })
    public ResponseEntity<ApiResponse<ProvinceDto>> createProvince(
            @Valid @RequestBody LocationCreateDto createDto,
            HttpServletRequest request) {

        log.info("Create province request: {}", createDto.getName());

        ProvinceDto provinceDto = locationService.createProvince(createDto, request);

        ApiResponse<ProvinceDto> response = ApiResponse.success(provinceDto, "Province created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/provinces/{id}")
    @Operation(summary = "Update province", description = "Update province details (system admin only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Province updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Province not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "System admin access required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Province name already exists in country")
    })
    public ResponseEntity<ApiResponse<ProvinceDto>> updateProvince(
            @Parameter(description = "Province ID") @PathVariable Long id,
            @Valid @RequestBody LocationUpdateDto updateDto,
            HttpServletRequest request) {

        log.info("Update province request: {}", id);

        ProvinceDto provinceDto = locationService.updateProvince(id, updateDto, request);

        ApiResponse<ProvinceDto> response = ApiResponse.success(provinceDto, "Province updated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/provinces/{id}")
    @Operation(summary = "Delete province", description = "Soft delete a province (system admin only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Province deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Province not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "System admin access required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Province has active districts")
    })
    public ResponseEntity<ApiResponse<Void>> deleteProvince(
            @Parameter(description = "Province ID") @PathVariable Long id,
            HttpServletRequest request) {

        log.info("Delete province request: {}", id);

        locationService.deleteProvince(id, request);

        ApiResponse<Void> response = ApiResponse.success(null, "Province deleted successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ DISTRICT OPERATIONS ================================

    @GetMapping("/provinces/{provinceId}/districts")
    @Operation(summary = "Get districts by province", description = "Get all districts for a specific province")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Districts retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Province not found")
    })
    public ResponseEntity<ApiResponse<List<DistrictSummaryDto>>> getDistrictsByProvince(
            @Parameter(description = "Province ID") @PathVariable Long provinceId,
            HttpServletRequest request) {

        log.debug("Get districts by province request: {}", provinceId);

        List<DistrictSummaryDto> districts = locationService.getDistrictsByProvince(provinceId);

        ApiResponse<List<DistrictSummaryDto>> response = ApiResponse.success(districts, "Districts retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/districts/{id}")
    @Operation(summary = "Get district by ID", description = "Get district details by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "District retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "District not found")
    })
    public ResponseEntity<ApiResponse<DistrictDto>> getDistrictById(
            @Parameter(description = "District ID") @PathVariable Long id,
            HttpServletRequest request) {

        log.debug("Get district request: {}", id);

        DistrictDto districtDto = locationService.getDistrictById(id);

        ApiResponse<DistrictDto> response = ApiResponse.success(districtDto, "District retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/districts/slug/{slug}")
    @Operation(summary = "Get district by slug", description = "Get district details by slug")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "District retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "District not found")
    })
    public ResponseEntity<ApiResponse<DistrictDto>> getDistrictBySlug(
            @Parameter(description = "District slug") @PathVariable String slug,
            HttpServletRequest request) {

        log.debug("Get district by slug request: {}", slug);

        DistrictDto districtDto = locationService.getDistrictBySlug(slug);

        ApiResponse<DistrictDto> response = ApiResponse.success(districtDto, "District retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/provinces/{provinceId}/districts/central")
    @Operation(summary = "Get central districts", description = "Get central districts for a province")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Central districts retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Province not found")
    })
    public ResponseEntity<ApiResponse<List<DistrictSummaryDto>>> getCentralDistricts(
            @Parameter(description = "Province ID") @PathVariable Long provinceId,
            HttpServletRequest request) {

        log.debug("Get central districts request for province: {}", provinceId);

        List<DistrictSummaryDto> districts = locationService.getCentralDistricts(provinceId);

        ApiResponse<List<DistrictSummaryDto>> response = ApiResponse.success(districts, "Central districts retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/districts/high-socioeconomic")
    @Operation(summary = "Get high socioeconomic districts", description = "Get districts with high socioeconomic levels")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "High socioeconomic districts retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<DistrictSummaryDto>>> getHighSocioeconomicDistricts(
            HttpServletRequest request) {

        log.debug("Get high socioeconomic districts request");

        List<DistrictSummaryDto> districts = locationService.getHighSocioeconomicDistricts();

        ApiResponse<List<DistrictSummaryDto>> response = ApiResponse.success(districts, "High socioeconomic districts retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/districts")
    @Operation(summary = "Create district", description = "Create a new district (system admin only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "District created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "System admin access required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Province not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "District name already exists in province")
    })
    public ResponseEntity<ApiResponse<DistrictDto>> createDistrict(
            @Valid @RequestBody LocationCreateDto createDto,
            HttpServletRequest request) {

        log.info("Create district request: {}", createDto.getName());

        DistrictDto districtDto = locationService.createDistrict(createDto, request);

        ApiResponse<DistrictDto> response = ApiResponse.success(districtDto, "District created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/districts/{id}")
    @Operation(summary = "Update district", description = "Update district details (system admin only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "District updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "District not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "System admin access required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "District name already exists in province")
    })
    public ResponseEntity<ApiResponse<DistrictDto>> updateDistrict(
            @Parameter(description = "District ID") @PathVariable Long id,
            @Valid @RequestBody LocationUpdateDto updateDto,
            HttpServletRequest request) {

        log.info("Update district request: {}", id);

        DistrictDto districtDto = locationService.updateDistrict(id, updateDto, request);

        ApiResponse<DistrictDto> response = ApiResponse.success(districtDto, "District updated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/districts/{id}")
    @Operation(summary = "Delete district", description = "Soft delete a district (system admin only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "District deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "District not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "System admin access required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "District has active neighborhoods")
    })
    public ResponseEntity<ApiResponse<Void>> deleteDistrict(
            @Parameter(description = "District ID") @PathVariable Long id,
            HttpServletRequest request) {

        log.info("Delete district request: {}", id);

        locationService.deleteDistrict(id, request);

        ApiResponse<Void> response = ApiResponse.success(null, "District deleted successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ NEIGHBORHOOD OPERATIONS ================================

    @GetMapping("/districts/{districtId}/neighborhoods")
    @Operation(summary = "Get neighborhoods by district", description = "Get all neighborhoods for a specific district")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Neighborhoods retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "District not found")
    })
    public ResponseEntity<ApiResponse<List<NeighborhoodSummaryDto>>> getNeighborhoodsByDistrict(
            @Parameter(description = "District ID") @PathVariable Long districtId,
            HttpServletRequest request) {

        log.debug("Get neighborhoods by district request: {}", districtId);

        List<NeighborhoodSummaryDto> neighborhoods = locationService.getNeighborhoodsWithMetroAccess(districtId);

        ApiResponse<List<NeighborhoodSummaryDto>> response = ApiResponse.success(neighborhoods, "Metro neighborhoods retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/districts/{districtId}/neighborhoods/family-friendly")
    @Operation(summary = "Get family-friendly neighborhoods", description = "Get neighborhoods suitable for families with high scores")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Family-friendly neighborhoods retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "District not found")
    })
    public ResponseEntity<ApiResponse<List<NeighborhoodSummaryDto>>> getFamilyFriendlyNeighborhoods(
            @Parameter(description = "District ID") @PathVariable Long districtId,
            @Parameter(description = "Minimum family score (1-10)") @RequestParam(defaultValue = "7") Integer minScore,
            HttpServletRequest request) {

        log.debug("Get family-friendly neighborhoods request for district: {} with min score: {}", districtId, minScore);

        List<NeighborhoodSummaryDto> neighborhoods = locationService.getFamilyFriendlyNeighborhoods(districtId, minScore);

        ApiResponse<List<NeighborhoodSummaryDto>> response = ApiResponse.success(neighborhoods, "Family-friendly neighborhoods retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/neighborhoods")
    @Operation(summary = "Create neighborhood", description = "Create a new neighborhood (system admin only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Neighborhood created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "System admin access required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "District not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Neighborhood name already exists in district")
    })
    public ResponseEntity<ApiResponse<NeighborhoodDto>> createNeighborhood(
            @Valid @RequestBody LocationCreateDto createDto,
            HttpServletRequest request) {

        log.info("Create neighborhood request: {}", createDto.getName());

        NeighborhoodDto neighborhoodDto = locationService.createNeighborhood(createDto, request);

        ApiResponse<NeighborhoodDto> response = ApiResponse.success(neighborhoodDto, "Neighborhood created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/neighborhoods/{id}")
    @Operation(summary = "Update neighborhood", description = "Update neighborhood details (system admin only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Neighborhood updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Neighborhood not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "System admin access required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Neighborhood name already exists in district")
    })
    public ResponseEntity<ApiResponse<NeighborhoodDto>> updateNeighborhood(
            @Parameter(description = "Neighborhood ID") @PathVariable Long id,
            @Valid @RequestBody LocationUpdateDto updateDto,
            HttpServletRequest request) {

        log.info("Update neighborhood request: {}", id);

        NeighborhoodDto neighborhoodDto = locationService.updateNeighborhood(id, updateDto, request);

        ApiResponse<NeighborhoodDto> response = ApiResponse.success(neighborhoodDto, "Neighborhood updated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/neighborhoods/{id}")
    @Operation(summary = "Delete neighborhood", description = "Soft delete a neighborhood (system admin only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Neighborhood deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Neighborhood not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "System admin access required")
    })
    public ResponseEntity<ApiResponse<Void>> deleteNeighborhood(
            @Parameter(description = "Neighborhood ID") @PathVariable Long id,
            HttpServletRequest request) {

        log.info("Delete neighborhood request: {}", id);

        locationService.deleteNeighborhood(id, request);

        ApiResponse<Void> response = ApiResponse.success(null, "Neighborhood deleted successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ SEARCH OPERATIONS ================================

    @PostMapping("/search")
    @Operation(summary = "Search locations", description = "Search locations with advanced filters")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Search completed successfully")
    })
    public ResponseEntity<ApiResponse<Page<LocationSuggestionDto>>> searchLocations(
            @Valid @RequestBody LocationSearchDto searchDto,
            HttpServletRequest request) {

        log.debug("Search locations request");

        Page<LocationSuggestionDto> locations = locationService.searchLocations(searchDto);

        ApiResponse<Page<LocationSuggestionDto>> response = ApiResponse.success(locations, "Search completed successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/suggestions")
    @Operation(summary = "Get location suggestions", description = "Get autocomplete suggestions for location search")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Suggestions retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<LocationSuggestionDto>>> getLocationSuggestions(
            @Parameter(description = "Search query") @RequestParam String query,
            @Parameter(description = "Maximum results") @RequestParam(defaultValue = "10") Integer limit,
            HttpServletRequest request) {

        log.debug("Get location suggestions request: {}", query);

        List<LocationSuggestionDto> suggestions = locationService.getLocationSuggestions(query, limit);

        ApiResponse<List<LocationSuggestionDto>> response = ApiResponse.success(suggestions, "Suggestions retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ LOCATION HIERARCHY ================================

    @GetMapping("/hierarchy/neighborhoods/{neighborhoodId}")
    @Operation(summary = "Get location hierarchy by neighborhood", description = "Get complete location hierarchy for a neighborhood")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Hierarchy retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Neighborhood not found")
    })
    public ResponseEntity<ApiResponse<LocationHierarchyDto>> getLocationHierarchy(
            @Parameter(description = "Neighborhood ID") @PathVariable Long neighborhoodId,
            HttpServletRequest request) {

        log.debug("Get location hierarchy request for neighborhood: {}", neighborhoodId);

        LocationHierarchyDto hierarchy = locationService.getLocationHierarchy(neighborhoodId);

        ApiResponse<LocationHierarchyDto> response = ApiResponse.success(hierarchy, "Hierarchy retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/hierarchy/districts/{districtId}")
    @Operation(summary = "Get location hierarchy by district", description = "Get location hierarchy for a district")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Hierarchy retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "District not found")
    })
    public ResponseEntity<ApiResponse<LocationHierarchyDto>> getLocationHierarchyByDistrict(
            @Parameter(description = "District ID") @PathVariable Long districtId,
            HttpServletRequest request) {

        log.debug("Get location hierarchy request for district: {}", districtId);

        LocationHierarchyDto hierarchy = locationService.getLocationHierarchyByDistrict(districtId);

        ApiResponse<LocationHierarchyDto> response = ApiResponse.success(hierarchy, "Hierarchy retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ NEARBY LOCATIONS ================================

    @GetMapping("/nearby")
    @Operation(summary = "Get nearby locations", description = "Find locations within a radius from coordinates")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Nearby locations retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid coordinates")
    })
    public ResponseEntity<ApiResponse<NearbyLocationsDto>> getNearbyLocations(
            @Parameter(description = "Latitude") @RequestParam Double latitude,
            @Parameter(description = "Longitude") @RequestParam Double longitude,
            @Parameter(description = "Radius in kilometers") @RequestParam(defaultValue = "10.0") Double radiusKm,
            @Parameter(description = "Maximum results") @RequestParam(defaultValue = "50") Integer limit,
            HttpServletRequest request) {

        log.debug("Get nearby locations request: {}, {} within {} km", latitude, longitude, radiusKm);

        NearbyLocationsDto nearbyLocations = locationService.getNearbyLocations(latitude, longitude, radiusKm, limit);

        ApiResponse<NearbyLocationsDto> response = ApiResponse.success(nearbyLocations, "Nearby locations retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ STATISTICS ================================

    @GetMapping("/statistics")
    @Operation(summary = "Get location statistics", description = "Get overall location statistics")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    })
    public ResponseEntity<ApiResponse<LocationStatisticsDto>> getLocationStatistics(
            HttpServletRequest request) {

        log.debug("Get location statistics request");

        LocationStatisticsDto statistics = locationService.getLocationStatistics();

        ApiResponse<LocationStatisticsDto> response = ApiResponse.success(statistics, "Statistics retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ VALIDATION ================================

    @GetMapping("/validate/hierarchy")
    @Operation(summary = "Validate location hierarchy", description = "Validate if location hierarchy is correct")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Validation completed")
    })
    public ResponseEntity<ApiResponse<LocationValidationResultDto>> validateLocationHierarchy(
            @Parameter(description = "Country ID") @RequestParam(required = false) Long countryId,
            @Parameter(description = "Province ID") @RequestParam(required = false) Long provinceId,
            @Parameter(description = "District ID") @RequestParam(required = false) Long districtId,
            @Parameter(description = "Neighborhood ID") @RequestParam(required = false) Long neighborhoodId,
            HttpServletRequest request) {

        log.debug("Validate location hierarchy request");

        boolean isValid = locationService.isValidLocationHierarchy(countryId, provinceId, districtId, neighborhoodId);

        LocationValidationResultDto result = LocationValidationResultDto.builder()
                .isValid(isValid)
                .message(isValid ? "Location hierarchy is valid" : "Invalid location hierarchy")
                .countryId(countryId)
                .provinceId(provinceId)
                .districtId(districtId)
                .neighborhoodId(neighborhoodId)
                .build();

        ApiResponse<LocationValidationResultDto> response = ApiResponse.success(result, "Validation completed");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate/schools")
    @Operation(summary = "Check if location has schools", description = "Check if a location has active schools")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Check completed")
    })
    public ResponseEntity<ApiResponse<LocationSchoolCheckDto>> checkLocationHasSchools(
            @Parameter(description = "Location type (NEIGHBORHOOD, DISTRICT, PROVINCE)") @RequestParam String locationType,
            @Parameter(description = "Location ID") @RequestParam Long locationId,
            HttpServletRequest request) {

        log.debug("Check location has schools: {} - {}", locationType, locationId);

        boolean hasSchools = locationService.hasSchoolsInLocation(locationType, locationId);

        LocationSchoolCheckDto result = LocationSchoolCheckDto.builder()
                .locationType(locationType)
                .locationId(locationId)
                .hasSchools(hasSchools)
                .message(hasSchools ? "Location has active schools" : "No active schools found in location")
                .build();

        ApiResponse<LocationSchoolCheckDto> response = ApiResponse.success(result, "Check completed");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ BULK OPERATIONS ================================

    @PostMapping("/bulk/import")
    @Operation(summary = "Bulk import locations", description = "Import locations from file (system admin only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "202", description = "Import request accepted"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "System admin access required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid import data")
    })
    public ResponseEntity<ApiResponse<LocationImportResultDto>> bulkImportLocations(
            @Valid @RequestBody BulkLocationImportDto importDto,
            HttpServletRequest request) {

        log.info("Bulk import locations request from: {}", importDto.getFileUrl());

        LocationImportResultDto result = locationService.bulkImportLocations(importDto, request);

        ApiResponse<LocationImportResultDto> response = ApiResponse.success(result, "Import request processed");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    // ================================ HELPER DTOs ================================

    @Setter
    @Getter
    public static class LocationValidationResultDto {
        private Boolean isValid;
        private String message;
        private Long countryId;
        private Long provinceId;
        private Long districtId;
        private Long neighborhoodId;

        public static LocationValidationResultDtoBuilder builder() {
            return new LocationValidationResultDtoBuilder();
        }

        public static class LocationValidationResultDtoBuilder {
            private Boolean isValid;
            private String message;
            private Long countryId;
            private Long provinceId;
            private Long districtId;
            private Long neighborhoodId;

            public LocationValidationResultDtoBuilder isValid(Boolean isValid) {
                this.isValid = isValid;
                return this;
            }

            public LocationValidationResultDtoBuilder message(String message) {
                this.message = message;
                return this;
            }

            public LocationValidationResultDtoBuilder countryId(Long countryId) {
                this.countryId = countryId;
                return this;
            }

            public LocationValidationResultDtoBuilder provinceId(Long provinceId) {
                this.provinceId = provinceId;
                return this;
            }

            public LocationValidationResultDtoBuilder districtId(Long districtId) {
                this.districtId = districtId;
                return this;
            }

            public LocationValidationResultDtoBuilder neighborhoodId(Long neighborhoodId) {
                this.neighborhoodId = neighborhoodId;
                return this;
            }

            public LocationValidationResultDto build() {
                LocationValidationResultDto dto = new LocationValidationResultDto();
                dto.setIsValid(this.isValid);
                dto.setMessage(this.message);
                dto.setCountryId(this.countryId);
                dto.setProvinceId(this.provinceId);
                dto.setDistrictId(this.districtId);
                dto.setNeighborhoodId(this.neighborhoodId);
                return dto;
            }
        }
    }

    @Setter
    @Getter
    public static class LocationSchoolCheckDto {
        private String locationType;
        private Long locationId;
        private Boolean hasSchools;
        private String message;

        public static LocationSchoolCheckDtoBuilder builder() {
            return new LocationSchoolCheckDtoBuilder();
        }

        public static class LocationSchoolCheckDtoBuilder {
            private String locationType;
            private Long locationId;
            private Boolean hasSchools;
            private String message;

            public LocationSchoolCheckDtoBuilder locationType(String locationType) {
                this.locationType = locationType;
                return this;
            }

            public LocationSchoolCheckDtoBuilder locationId(Long locationId) {
                this.locationId = locationId;
                return this;
            }

            public LocationSchoolCheckDtoBuilder hasSchools(Boolean hasSchools) {
                this.hasSchools = hasSchools;
                return this;
            }

            public LocationSchoolCheckDtoBuilder message(String message) {
                this.message = message;
                return this;
            }

            public LocationSchoolCheckDto build() {
                LocationSchoolCheckDto dto = new LocationSchoolCheckDto();
                dto.setLocationType(this.locationType);
                dto.setLocationId(this.locationId);
                dto.setHasSchools(this.hasSchools);
                dto.setMessage(this.message);
                return dto;
            }
        }
    }


    @GetMapping("/neighborhoods/{id}")
    @Operation(summary = "Get neighborhood by ID", description = "Get neighborhood details by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Neighborhood retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Neighborhood not found")
    })
    public ResponseEntity<ApiResponse<NeighborhoodDto>> getNeighborhoodById(
            @Parameter(description = "Neighborhood ID") @PathVariable Long id,
            HttpServletRequest request) {

        log.debug("Get neighborhood request: {}", id);

        NeighborhoodDto neighborhoodDto = locationService.getNeighborhoodById(id);

        ApiResponse<NeighborhoodDto> response = ApiResponse.success(neighborhoodDto, "Neighborhood retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/neighborhoods/slug/{slug}")
    @Operation(summary = "Get neighborhood by slug", description = "Get neighborhood details by slug")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Neighborhood retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Neighborhood not found")
    })
    public ResponseEntity<ApiResponse<NeighborhoodDto>> getNeighborhoodBySlug(
            @Parameter(description = "Neighborhood slug") @PathVariable String slug,
            HttpServletRequest request) {

        log.debug("Get neighborhood by slug request: {}", slug);

        NeighborhoodDto neighborhoodDto = locationService.getNeighborhoodBySlug(slug);

        ApiResponse<NeighborhoodDto> response = ApiResponse.success(neighborhoodDto, "Neighborhood retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/neighborhoods/popular")
    @Operation(summary = "Get popular neighborhoods", description = "Get neighborhoods popular for schools")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Popular neighborhoods retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<NeighborhoodSummaryDto>>> getPopularNeighborhoodsForSchools(
            HttpServletRequest request) {

        log.debug("Get popular neighborhoods request");

        List<NeighborhoodSummaryDto> neighborhoods = locationService.getPopularNeighborhoodsForSchools();

        ApiResponse<List<NeighborhoodSummaryDto>> response = ApiResponse.success(neighborhoods, "Popular neighborhoods retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/districts/{districtId}/neighborhoods/metro")
    @Operation(summary = "Get neighborhoods with metro access", description = "Get neighborhoods with metro station access")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Metro neighborhoods retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "District not found")
    })
    public ResponseEntity<ApiResponse<List<NeighborhoodSummaryDto>>> getNeighborhoodsWithMetroAccess(
            @Parameter(description = "District ID") @PathVariable Long districtId,
            HttpServletRequest request) {

        log.debug("Get neighborhoods with metro access request for district: {}", districtId);

        List<NeighborhoodSummaryDto> neighborhoods = locationService.getNeighborhoodsByDistrict(districtId);

        ApiResponse<List<NeighborhoodSummaryDto>> response = ApiResponse.success(neighborhoods, "Neighborhoods retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }
}
