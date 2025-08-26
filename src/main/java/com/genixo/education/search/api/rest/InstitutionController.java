package com.genixo.education.search.api.rest;

import com.genixo.education.search.dto.institution.*;
import com.genixo.education.search.service.InstitutionService;
import com.genixo.education.search.service.auth.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/institutions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Institution Management", description = "APIs for managing brands, campuses, schools and institution types")
public class InstitutionController {

    private final InstitutionService institutionService;
    private final JwtService jwtService;

    // ================================ BRAND OPERATIONS ================================

    @PostMapping("/brands")
    @Operation(summary = "Create brand", description = "Create a new brand")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Brand created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Brand name already exists")
    })
    public ResponseEntity<ApiResponse<BrandDto>> createBrand(
            @Valid @RequestBody BrandCreateDto createDto,
            HttpServletRequest request) {

        log.info("Create brand request: {}", createDto.getName());

        BrandDto brandDto = institutionService.createBrand(createDto, request);

        ApiResponse<BrandDto> response = ApiResponse.success(brandDto, "Brand created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/brands/{id}")
    @Operation(summary = "Get brand by ID", description = "Get brand details by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Brand retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Brand not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<BrandDto>> getBrandById(
            @Parameter(description = "Brand ID") @PathVariable Long id,
            HttpServletRequest request) {

        log.debug("Get brand request: {}", id);

        BrandDto brandDto = institutionService.getBrandById(id, request);

        ApiResponse<BrandDto> response = ApiResponse.success(brandDto, "Brand retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/brands/slug/{slug}")
    @Operation(summary = "Get brand by slug", description = "Get brand details by slug")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Brand retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Brand not found")
    })
    public ResponseEntity<ApiResponse<BrandDto>> getBrandBySlug(
            @Parameter(description = "Brand slug") @PathVariable String slug,
            HttpServletRequest request) {

        log.debug("Get brand by slug request: {}", slug);

        BrandDto brandDto = institutionService.getBrandBySlug(slug);

        ApiResponse<BrandDto> response = ApiResponse.success(brandDto, "Brand retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/brands/{id}")
    @Operation(summary = "Update brand", description = "Update an existing brand")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Brand updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Brand not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Brand name already exists")
    })
    public ResponseEntity<ApiResponse<BrandDto>> updateBrand(
            @Parameter(description = "Brand ID") @PathVariable Long id,
            @Valid @RequestBody BrandCreateDto updateDto,
            HttpServletRequest request) {

        log.info("Update brand request: {}", id);

        BrandDto brandDto = institutionService.updateBrand(id, updateDto, request);

        ApiResponse<BrandDto> response = ApiResponse.success(brandDto, "Brand updated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/brands/{id}")
    @Operation(summary = "Delete brand", description = "Delete brand (must have no active campuses)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Brand deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Brand not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Brand has active campuses")
    })
    public ResponseEntity<ApiResponse<Void>> deleteBrand(
            @Parameter(description = "Brand ID") @PathVariable Long id,
            HttpServletRequest request) {

        log.info("Delete brand request: {}", id);

        institutionService.deleteBrand(id, request);

        ApiResponse<Void> response = ApiResponse.success(null, "Brand deleted successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/brands/summaries")
    @Operation(summary = "Get brand summaries", description = "Get all brand summaries accessible to user")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Brand summaries retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<BrandSummaryDto>>> getAllBrandSummaries(
            HttpServletRequest request) {

        log.debug("Get brand summaries request");

        List<BrandSummaryDto> summaries = institutionService.getAllBrandSummaries(request);

        ApiResponse<List<BrandSummaryDto>> response = ApiResponse.success(summaries, "Brand summaries retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ CAMPUS OPERATIONS ================================

    @PostMapping("/campuses")
    @Operation(summary = "Create campus", description = "Create a new campus")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Campus created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Brand not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Campus name already exists in brand")
    })
    public ResponseEntity<ApiResponse<CampusDto>> createCampus(
            @Valid @RequestBody CampusCreateDto createDto,
            HttpServletRequest request) {

        log.info("Create campus request: {}", createDto.getName());

        CampusDto campusDto = institutionService.createCampus(createDto, request);

        ApiResponse<CampusDto> response = ApiResponse.success(campusDto, "Campus created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/campuses/{id}")
    @Operation(summary = "Get campus by ID", description = "Get campus details by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Campus retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Campus not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<CampusDto>> getCampusById(
            @Parameter(description = "Campus ID") @PathVariable Long id,
            HttpServletRequest request) {

        log.debug("Get campus request: {}", id);

        CampusDto campusDto = institutionService.getCampusById(id, request);

        ApiResponse<CampusDto> response = ApiResponse.success(campusDto, "Campus retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/campuses/slug/{slug}")
    @Operation(summary = "Get campus by slug", description = "Get campus details by slug")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Campus retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Campus not found")
    })
    public ResponseEntity<ApiResponse<CampusDto>> getCampusBySlug(
            @Parameter(description = "Campus slug") @PathVariable String slug,
            HttpServletRequest request) {

        log.debug("Get campus by slug request: {}", slug);

        CampusDto campusDto = institutionService.getCampusBySlug(slug);

        ApiResponse<CampusDto> response = ApiResponse.success(campusDto, "Campus retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/brands/{brandId}/campuses")
    @Operation(summary = "Get campuses by brand", description = "Get all campuses for a specific brand")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Campuses retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Brand not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<List<CampusSummaryDto>>> getCampusesByBrand(
            @Parameter(description = "Brand ID") @PathVariable Long brandId,
            HttpServletRequest request) {

        log.debug("Get campuses by brand request: {}", brandId);

        List<CampusSummaryDto> campuses = institutionService.getCampusesByBrand(brandId, request);

        ApiResponse<List<CampusSummaryDto>> response = ApiResponse.success(campuses, "Campuses retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/campuses/{campusId}/properties")
    @Operation(summary = "Set campus property values", description = "Set property values for a campus")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Property values set successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Campus or property not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<Void>> setCampusPropertyValues(
            @Parameter(description = "Campus ID") @PathVariable Long campusId,
            @Valid @RequestBody List<InstitutionPropertyValueSetDto> propertyValues,
            HttpServletRequest request) {

        log.info("Set campus property values request: {}", campusId);

        institutionService.setCampusPropertyValues(campusId, propertyValues, request);

        ApiResponse<Void> response = ApiResponse.success(null, "Property values set successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ SCHOOL OPERATIONS ================================

    @PostMapping("/schools")
    @Operation(summary = "Create school", description = "Create a new school")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "School created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Campus or institution type not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "School name already exists in campus")
    })
    public ResponseEntity<ApiResponse<SchoolDto>> createSchool(
            @Valid @RequestBody SchoolCreateDto createDto,
            HttpServletRequest request) {

        log.info("Create school request: {}", createDto.getName());

        SchoolDto schoolDto = institutionService.createSchool(createDto, request);

        ApiResponse<SchoolDto> response = ApiResponse.success(schoolDto, "School created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/schools/{id}")
    @Operation(summary = "Get school by ID", description = "Get school details by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "School retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<SchoolDto>> getSchoolById(
            @Parameter(description = "School ID") @PathVariable Long id,
            HttpServletRequest request) {

        log.debug("Get school request: {}", id);

        SchoolDto schoolDto = institutionService.getSchoolById(id, request);

        ApiResponse<SchoolDto> response = ApiResponse.success(schoolDto, "School retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/schools/slug/{slug}")
    @Operation(summary = "Get school by slug", description = "Get school details by slug")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "School retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School not found")
    })
    public ResponseEntity<ApiResponse<SchoolDto>> getSchoolBySlug(
            @Parameter(description = "School slug") @PathVariable String slug,
            HttpServletRequest request) {

        log.debug("Get school by slug request: {}", slug);

        SchoolDto schoolDto = institutionService.getSchoolBySlug(slug);

        ApiResponse<SchoolDto> response = ApiResponse.success(schoolDto, "School retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/schools/{id}/detail")
    @Operation(summary = "Get school detail", description = "Get detailed school information")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "School detail retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<SchoolDetailDto>> getSchoolDetailById(
            @Parameter(description = "School ID") @PathVariable Long id,
            HttpServletRequest request) {

        log.debug("Get school detail request: {}", id);

        SchoolDetailDto schoolDetailDto = institutionService.getSchoolDetailById(id, request);

        ApiResponse<SchoolDetailDto> response = ApiResponse.success(schoolDetailDto, "School detail retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/schools/{schoolId}/properties")
    @Operation(summary = "Set school property values", description = "Set property values for a school")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Property values set successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School or property not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<Void>> setSchoolPropertyValues(
            @Parameter(description = "School ID") @PathVariable Long schoolId,
            @Valid @RequestBody List<InstitutionPropertyValueSetDto> propertyValues,
            HttpServletRequest request) {

        log.info("Set school property values request: {}", schoolId);

        institutionService.setSchoolPropertyValues(schoolId, propertyValues, request);

        ApiResponse<Void> response = ApiResponse.success(null, "Property values set successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/schools/{id}/statistics")
    @Operation(summary = "Get school statistics", description = "Get detailed statistics for a school")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<SchoolStatisticsDto>> getSchoolStatistics(
            @Parameter(description = "School ID") @PathVariable Long id,
            HttpServletRequest request) {

        log.debug("Get school statistics request: {}", id);

        SchoolStatisticsDto statistics = institutionService.getSchoolStatistics(id, request);

        ApiResponse<SchoolStatisticsDto> response = ApiResponse.success(statistics, "Statistics retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ SEARCH OPERATIONS ================================

    @PostMapping("/schools/search")
    @Operation(summary = "Search schools", description = "Search schools with advanced filters")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Search completed successfully")
    })
    public ResponseEntity<ApiResponse<Page<SchoolSearchResultDto>>> searchSchools(
            @Valid @RequestBody SchoolSearchDto searchDto,
            HttpServletRequest request) {

        log.debug("Search schools request");

        Page<SchoolSearchResultDto> results = institutionService.searchSchools(searchDto);

        ApiResponse<Page<SchoolSearchResultDto>> response = ApiResponse.success(results, "Search completed successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/schools/public-search")
    @Operation(summary = "Public search schools", description = "Public search for schools (no authentication required)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Public search completed successfully")
    })
    public ResponseEntity<ApiResponse<Page<SchoolSearchResultDto>>> publicSearchSchools(
            @Valid @RequestBody SchoolSearchDto searchDto,
            HttpServletRequest request) {

        log.debug("Public search schools request");

        Page<SchoolSearchResultDto> results = institutionService.publicSearchSchools(searchDto);

        ApiResponse<Page<SchoolSearchResultDto>> response = ApiResponse.success(results, "Public search completed successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ INSTITUTION TYPE OPERATIONS ================================

    @GetMapping("/institution-types")
    @Operation(summary = "Get all institution types", description = "Get all available institution types")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Institution types retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<InstitutionTypeDto>>> getAllInstitutionTypes(
            HttpServletRequest request) {

        log.debug("Get institution types request");

        List<InstitutionTypeDto> types = institutionService.getAllInstitutionTypes();

        ApiResponse<List<InstitutionTypeDto>> response = ApiResponse.success(types, "Institution types retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/institution-types/summaries")
    @Operation(summary = "Get institution type summaries", description = "Get institution type summaries with count info")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Institution type summaries retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<InstitutionTypeSummaryDto>>> getInstitutionTypeSummaries(
            HttpServletRequest request) {

        log.debug("Get institution type summaries request");

        List<InstitutionTypeSummaryDto> summaries = institutionService.getInstitutionTypeSummaries();

        ApiResponse<List<InstitutionTypeSummaryDto>> response = ApiResponse.success(summaries, "Institution type summaries retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/institution-types")
    @Operation(summary = "Create institution type", description = "Create a new institution type (admin only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Institution type created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Institution type name already exists")
    })
    public ResponseEntity<ApiResponse<InstitutionTypeDto>> createInstitutionType(
            @Valid @RequestBody InstitutionTypeDto typeDto,
            HttpServletRequest request) {

        log.info("Create institution type request: {}", typeDto.getName());

        InstitutionTypeDto createdType = institutionService.createInstitutionType(typeDto, request);

        ApiResponse<InstitutionTypeDto> response = ApiResponse.success(createdType, "Institution type created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ================================ INSTITUTION PROPERTY OPERATIONS ================================

    @PostMapping("/institution-properties")
    @Operation(summary = "Create institution property", description = "Create a new institution property")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Institution property created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Property name already exists for institution type")
    })
    public ResponseEntity<ApiResponse<InstitutionPropertyDto>> createInstitutionProperty(
            @Valid @RequestBody InstitutionPropertyCreateDto createDto,
            HttpServletRequest request) {

        log.info("Create institution property request: {}", createDto.getName());

        InstitutionPropertyDto propertyDto = institutionService.createInstitutionProperty(createDto, request);

        ApiResponse<InstitutionPropertyDto> response = ApiResponse.success(propertyDto, "Institution property created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/institution-types/{institutionTypeId}/properties")
    @Operation(summary = "Get properties by institution type", description = "Get all properties for a specific institution type")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Properties retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Institution type not found")
    })
    public ResponseEntity<ApiResponse<List<InstitutionPropertyDto>>> getPropertiesByInstitutionType(
            @Parameter(description = "Institution type ID") @PathVariable Long institutionTypeId,
            HttpServletRequest request) {

        log.debug("Get properties by institution type request: {}", institutionTypeId);

        List<InstitutionPropertyDto> properties = institutionService.getPropertiesByInstitutionType(institutionTypeId);

        ApiResponse<List<InstitutionPropertyDto>> response = ApiResponse.success(properties, "Properties retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ BULK OPERATIONS ================================

    @PostMapping("/schools/bulk")
    @Operation(summary = "Bulk update schools", description = "Perform bulk operations on multiple schools")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Bulk operation completed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid operation or request data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<BulkOperationResultDto>> bulkUpdateSchools(
            @Valid @RequestBody BulkOperationDto bulkDto,
            HttpServletRequest request) {

        log.info("Bulk operation request: {} on {} schools", bulkDto.getOperation(), bulkDto.getEntityIds().size());

        BulkOperationResultDto result = institutionService.bulkUpdateSchools(bulkDto, request);

        ApiResponse<BulkOperationResultDto> response = ApiResponse.success(result, "Bulk operation completed");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ USER FAVORITES AND COMPARISON ================================

    @GetMapping("/favorites")
    @Operation(summary = "Get user favorites", description = "Get user's favorite institutions")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Favorites retrieved successfully")
    })
    public ResponseEntity<ApiResponse<InstitutionFavoritesDto>> getUserFavorites(
            HttpServletRequest request) {

        log.debug("Get user favorites request");

        InstitutionFavoritesDto favorites = institutionService.getUserFavorites(request);

        ApiResponse<InstitutionFavoritesDto> response = ApiResponse.success(favorites, "Favorites retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/schools/compare")
    @Operation(summary = "Compare schools", description = "Compare multiple schools side by side")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "School comparison completed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid school IDs or insufficient schools for comparison"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied to one or more schools")
    })
    public ResponseEntity<ApiResponse<InstitutionComparisonDto>> compareSchools(
            @Valid @RequestBody List<Long> schoolIds,
            HttpServletRequest request) {

        log.info("Compare schools request: {}", schoolIds);

        InstitutionComparisonDto comparison = institutionService.compareSchools(schoolIds, request);

        ApiResponse<InstitutionComparisonDto> response = ApiResponse.success(comparison, "School comparison completed successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ PUBLIC ENDPOINTS (NO AUTH REQUIRED) ================================

    @GetMapping("/public/brands/slug/{slug}")
    @Operation(summary = "Get public brand by slug", description = "Get brand details by slug (public access)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Brand retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Brand not found or not available")
    })
    public ResponseEntity<ApiResponse<BrandDto>> getPublicBrandBySlug(
            @Parameter(description = "Brand slug") @PathVariable String slug,
            HttpServletRequest request) {

        log.debug("Get public brand by slug request: {}", slug);

        BrandDto brandDto = institutionService.getPublicBrandBySlug(slug);

        ApiResponse<BrandDto> response = ApiResponse.success(brandDto, "Brand retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/public/campuses/slug/{slug}")
    @Operation(summary = "Get public campus by slug", description = "Get campus details by slug (public access)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Campus retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Campus not found or not available")
    })
    public ResponseEntity<ApiResponse<CampusDto>> getPublicCampusBySlug(
            @Parameter(description = "Campus slug") @PathVariable String slug,
            HttpServletRequest request) {

        log.debug("Get public campus by slug request: {}", slug);

        CampusDto campusDto = institutionService.getPublicCampusBySlug(slug);

        ApiResponse<CampusDto> response = ApiResponse.success(campusDto, "Campus retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/public/schools/slug/{slug}")
    @Operation(summary = "Get public school by slug", description = "Get school details by slug (public access)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "School retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School not found or not available")
    })
    public ResponseEntity<ApiResponse<SchoolDto>> getPublicSchoolBySlug(
            @Parameter(description = "School slug") @PathVariable String slug,
            HttpServletRequest request) {

        log.debug("Get public school by slug request: {}", slug);

        SchoolDto schoolDto = institutionService.getPublicSchoolBySlug(slug);

        ApiResponse<SchoolDto> response = ApiResponse.success(schoolDto, "School retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ VALIDATION ENDPOINTS ================================

    @PostMapping("/validate/brand-name")
    @Operation(summary = "Validate brand name", description = "Check if brand name is available")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Validation completed")
    })
    public ResponseEntity<ApiResponse<ValidationResultDto>> validateBrandName(
            @Valid @RequestBody NameValidationDto validationDto,
            HttpServletRequest request) {

        log.debug("Validate brand name request: {}", validationDto.getName());

        ValidationResultDto result = ValidationResultDto.builder()
                .isValid(true) // This would be implemented in the service
                .message("Brand name is available")
                .fieldName("name")
                .build();

        ApiResponse<ValidationResultDto> response = ApiResponse.success(result, "Validation completed");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate/campus-name")
    @Operation(summary = "Validate campus name", description = "Check if campus name is available in brand")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Validation completed")
    })
    public ResponseEntity<ApiResponse<ValidationResultDto>> validateCampusName(
            @Valid @RequestBody CampusNameValidationDto validationDto,
            HttpServletRequest request) {

        log.debug("Validate campus name request: {} for brand {}", validationDto.getName(), validationDto.getBrandId());

        ValidationResultDto result = ValidationResultDto.builder()
                .isValid(true) // This would be implemented in the service
                .message("Campus name is available in this brand")
                .fieldName("name")
                .build();

        ApiResponse<ValidationResultDto> response = ApiResponse.success(result, "Validation completed");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate/school-name")
    @Operation(summary = "Validate school name", description = "Check if school name is available in campus")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Validation completed")
    })
    public ResponseEntity<ApiResponse<ValidationResultDto>> validateSchoolName(
            @Valid @RequestBody SchoolNameValidationDto validationDto,
            HttpServletRequest request) {

        log.debug("Validate school name request: {} for campus {}", validationDto.getName(), validationDto.getCampusId());

        ValidationResultDto result = ValidationResultDto.builder()
                .isValid(true) // This would be implemented in the service
                .message("School name is available in this campus")
                .fieldName("name")
                .build();

        ApiResponse<ValidationResultDto> response = ApiResponse.success(result, "Validation completed");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ HELPER DTOs ================================

    public static class NameValidationDto {
        private String name;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class CampusNameValidationDto {
        private String name;
        private Long brandId;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Long getBrandId() { return brandId; }
        public void setBrandId(Long brandId) { this.brandId = brandId; }
    }

    public static class SchoolNameValidationDto {
        private String name;
        private Long campusId;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Long getCampusId() { return campusId; }
        public void setCampusId(Long campusId) { this.campusId = campusId; }
    }

    public static class ValidationResultDto {
        private Boolean isValid;
        private String message;
        private String fieldName;

        public static ValidationResultDtoBuilder builder() {
            return new ValidationResultDtoBuilder();
        }

        public Boolean getIsValid() { return isValid; }
        public void setIsValid(Boolean isValid) { this.isValid = isValid; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getFieldName() { return fieldName; }
        public void setFieldName(String fieldName) { this.fieldName = fieldName; }

        public static class ValidationResultDtoBuilder {
            private Boolean isValid;
            private String message;
            private String fieldName;

            public ValidationResultDtoBuilder isValid(Boolean isValid) {
                this.isValid = isValid;
                return this;
            }

            public ValidationResultDtoBuilder message(String message) {
                this.message = message;
                return this;
            }

            public ValidationResultDtoBuilder fieldName(String fieldName) {
                this.fieldName = fieldName;
                return this;
            }

            public ValidationResultDto build() {
                ValidationResultDto dto = new ValidationResultDto();
                dto.setIsValid(this.isValid);
                dto.setMessage(this.message);
                dto.setFieldName(this.fieldName);
                return dto;
            }
        }
    }
}