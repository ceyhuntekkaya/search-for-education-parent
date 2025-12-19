package com.genixo.education.search.api.rest;



import com.genixo.education.search.dto.institution.SchoolSearchDto;
import com.genixo.education.search.dto.institution.SchoolSearchResultDto;
import com.genixo.education.search.dto.search.SchoolSearchByNameDto;
import com.genixo.education.search.service.search.SchoolSearchDualService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * School Search Controller - DUAL MODE
 * Frontend ve AI için ayrı endpoint'ler
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/schools/search")
@RequiredArgsConstructor
@Tag(name = "School Search", description = "Okul arama API'leri (Frontend & AI)")
public class SchoolSearchDualController {

    private final SchoolSearchDualService searchService;

    // ============================================================================
    // FRONTEND ENDPOINTS (ID BAZLI)
    // ============================================================================

    /**
     * Frontend için ID bazlı arama
     * POST /api/v1/schools/search/by-ids
     */
    @PostMapping("/by-ids")
    @Operation(
            summary = "Frontend için ID bazlı arama",
            description = "Frontend'den gelen ID'lerle (institutionTypeId, provinceId, etc.) okul arama"
    )
    public ResponseEntity<Page<SchoolSearchResultDto>> searchByIds(
            @RequestBody SchoolSearchDto searchDto) {

        log.info("Frontend search request: institutionTypes={}, province={}",
                searchDto.getInstitutionTypeIds(),
                searchDto.getProvinceId());

        try {
            Page<SchoolSearchResultDto> results = searchService.searchByIds(searchDto);

            log.info("Frontend search completed: {} results", results.getTotalElements());

            return ResponseEntity.ok(results);

        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Search error", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Frontend için coğrafi arama
     * POST /api/v1/schools/search/nearby-by-ids
     */
    @PostMapping("/nearby-by-ids")
    @Operation(
            summary = "Frontend için yakındaki okullar",
            description = "Koordinat ve institution type ID'ye göre yakındaki okulları bulur"
    )
    public ResponseEntity<Page<SchoolSearchResultDto>> searchNearbyByIds(
            @RequestBody SchoolSearchDto searchDto) {

        log.info("Frontend nearby search: lat={}, lon={}, radius={}",
                searchDto.getLatitude(),
                searchDto.getLongitude(),
                searchDto.getRadiusKm());

        try {
            Page<SchoolSearchResultDto> results = searchService.searchNearbyByIds(searchDto);

            log.info("Frontend nearby search completed: {} results", results.getTotalElements());

            return ResponseEntity.ok(results);

        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Nearby search error", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ============================================================================
    // AI ENDPOINTS (NAME BAZLI)
    // ============================================================================

    /**
     * AI için Name bazlı arama
     * POST /api/v1/schools/search/by-names
     */
    @PostMapping("/by-names")
    @Operation(
            summary = "AI için Name bazlı arama",
            description = "AI'dan gelen name'lerle (institutionTypeName, provinceName, etc.) okul arama"
    )
    public ResponseEntity<Page<SchoolSearchResultDto>> searchByNames(
            @RequestBody SchoolSearchByNameDto searchDto) {

        log.info("AI search request: institutionType={}, province={}, properties={}",
                searchDto.getInstitutionTypeName(),
                searchDto.getProvinceName(),
                searchDto.getPropertyFilters());

        try {
            Page<SchoolSearchResultDto> results = searchService.searchByNames(searchDto);

            log.info("AI search completed: {} results", results.getTotalElements());

            return ResponseEntity.ok(results);

        } catch (IllegalArgumentException e) {
            log.error("AI validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("AI search error", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * AI için coğrafi arama
     * POST /api/v1/schools/search/nearby-by-names
     */
    @PostMapping("/nearby-by-names")
    @Operation(
            summary = "AI için yakındaki okullar",
            description = "Koordinat ve institution type name'e göre yakındaki okulları bulur"
    )
    public ResponseEntity<Page<SchoolSearchResultDto>> searchNearbyByNames(
            @RequestBody SchoolSearchByNameDto searchDto,
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(required = false, defaultValue = "10.0") Double radiusKm) {

        log.info("AI nearby search: lat={}, lon={}, radius={}, institutionType={}",
                latitude, longitude, radiusKm, searchDto.getInstitutionTypeName());

        try {
            Page<SchoolSearchResultDto> results = searchService.searchNearbyByNames(
                    searchDto,
                    latitude,
                    longitude,
                    radiusKm
            );

            log.info("AI nearby search completed: {} results", results.getTotalElements());

            return ResponseEntity.ok(results);

        } catch (IllegalArgumentException e) {
            log.error("AI validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("AI nearby search error", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ============================================================================
    // HEALTH CHECK
    // ============================================================================

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    @Operation(summary = "Search servis sağlık kontrolü")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("School Search Dual Service is running!");
    }
}