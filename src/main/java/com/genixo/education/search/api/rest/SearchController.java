package com.genixo.education.search.api.rest;

import com.genixo.education.search.dto.supply.SearchResultDto;
import com.genixo.education.search.dto.supply.SearchSuggestionDto;
import com.genixo.education.search.service.supply.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/supply/search")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Search & Filter", description = "APIs for global search and autocomplete in the supply system")
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    @Operation(summary = "Global search", description = "Search across products, suppliers, RFQs, and categories")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Search completed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid search parameters")
    })
    public ResponseEntity<ApiResponse<SearchResultDto>> globalSearch(
            @Parameter(description = "Search query") @RequestParam(required = false) String query,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(required = false, defaultValue = "0") Integer page,
            @Parameter(description = "Page size (max results per category)") @RequestParam(required = false, defaultValue = "10") Integer size,
            HttpServletRequest request) {

        SearchResultDto results = searchService.globalSearch(query, page, size);

        ApiResponse<SearchResultDto> response = ApiResponse.success(results, "Search completed successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/suggestions")
    @Operation(summary = "Get search suggestions", description = "Get autocomplete suggestions for search queries")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Suggestions retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid query parameter")
    })
    public ResponseEntity<ApiResponse<SearchSuggestionDto>> getSearchSuggestions(
            @Parameter(description = "Search query for autocomplete") @RequestParam(required = false) String query,
            HttpServletRequest request) {

        SearchSuggestionDto suggestions = searchService.getSearchSuggestions(query);

        ApiResponse<SearchSuggestionDto> response = ApiResponse.success(suggestions, "Suggestions retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }
}

