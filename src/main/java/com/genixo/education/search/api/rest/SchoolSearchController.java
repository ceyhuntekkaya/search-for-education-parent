package com.genixo.education.search.api.rest;


import com.genixo.education.search.dto.institution.SchoolSearchFilterDTO;
import com.genixo.education.search.dto.institution.SchoolSearchResultViewDTO;
import com.genixo.education.search.service.SchoolSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/schools-search")
@RequiredArgsConstructor
@Slf4j
public class SchoolSearchController {

    private final SchoolSearchService schoolSearchService;

    /**
     * Dinamik arama - Tüm filtreler
     * POST /api/v1/schools/search
     *
     * Body örneği:
     * {
     *   "provinceName": "Ankara",
     *   "minPrice": 20000,
     *   "maxPrice": 50000,
     *   "propertyFilters": [
     *     {"name": "has_library", "booleanValue": true}
     *   ],
     *   "sortBy": "rating"
     * }
     */
    @PostMapping
    public ResponseEntity<Page<SchoolSearchResultViewDTO>> search(
            @RequestBody SchoolSearchFilterDTO filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {

        Pageable pageable = PageRequest.of(page, size);
        Page<SchoolSearchResultViewDTO> results = schoolSearchService.search(filter, pageable);

        return ResponseEntity.ok(results);
    }

    /**
     * İl'e göre arama (basit)
     * GET /api/v1/schools/search/by-province?provinceName=Ankara&page=0&size=20
     */
    @GetMapping("/by-province")
    public ResponseEntity<Page<SchoolSearchResultViewDTO>> searchByProvince(
            @RequestParam String provinceName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {

        Pageable pageable = PageRequest.of(page, size);
        Page<SchoolSearchResultViewDTO> results = schoolSearchService.searchByProvince(provinceName, pageable);

        return ResponseEntity.ok(results);
    }

    /**
     * Kompleks arama (URL parametreleri ile)
     * GET /api/v1/schools/search/advanced?provinceName=Ankara&minPrice=20000&maxPrice=50000&propertyName=has_library&propertyValue=true
     */
    @GetMapping("/advanced")
    public ResponseEntity<Page<SchoolSearchResultViewDTO>> advancedSearch(
            @RequestParam String provinceName,
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            @RequestParam String propertyName,
            @RequestParam Boolean propertyValue,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {


        Pageable pageable = PageRequest.of(page, size);
        Page<SchoolSearchResultViewDTO> results = schoolSearchService.searchByProvinceAndPriceAndProperty(
                provinceName, minPrice, maxPrice, propertyName, propertyValue, pageable
        );

        return ResponseEntity.ok(results);
    }

    /**
     * Full-text search
     * GET /api/v1/schools/search/fulltext?keyword=IB+programı+özel+okul
     */
    @GetMapping("/fulltext")
    public ResponseEntity<Page<SchoolSearchResultViewDTO>> fullTextSearch(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {

        Pageable pageable = PageRequest.of(page, size);
        Page<SchoolSearchResultViewDTO> results = schoolSearchService.fullTextSearch(keyword, pageable);

        return ResponseEntity.ok(results);
    }

    /**
     * İsme göre basit arama
     * GET /api/v1/schools/search/by-name?keyword=TED
     */
    @GetMapping("/by-name")
    public ResponseEntity<Page<SchoolSearchResultViewDTO>> searchByName(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {

        Pageable pageable = PageRequest.of(page, size);
        Page<SchoolSearchResultViewDTO> results = schoolSearchService.searchByName(keyword, pageable);

        return ResponseEntity.ok(results);
    }

    /**
     * İstatistikler - İl bazında okul sayıları
     * GET /api/v1/schools/search/stats/provinces
     */
    @GetMapping("/stats/provinces")
    public ResponseEntity<List<SchoolSearchService.ProvinceCountDTO>> getProvinceStats() {
        List<SchoolSearchService.ProvinceCountDTO> stats = schoolSearchService.getProvinceStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * İstatistikler - Fiyat aralıkları
     * GET /api/v1/schools/search/stats/price-ranges
     */
    @GetMapping("/stats/price-ranges")
    public ResponseEntity<List<SchoolSearchService.PriceRangeDTO>> getPriceRangeStats() {
        List<SchoolSearchService.PriceRangeDTO> stats = schoolSearchService.getPriceRangeStatistics();
        return ResponseEntity.ok(stats);
    }
}