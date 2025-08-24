package com.genixo.education.search.dto.analytics;

import com.genixo.education.search.enumaration.DeviceType;
import com.genixo.education.search.enumaration.SearchIntent;
import com.genixo.education.search.enumaration.SearchType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchLogDto {
    private Long id;
    private String sessionId;
    private String searchQuery;
    private String cleanedQuery;
    private LocalDateTime searchTime;
    private SearchType searchType;
    private Integer resultsCount;
    private Boolean zeroResults;
    private Integer responseTimeMs;

    // Search filters
    private String filtersApplied; // JSON
    private String sortOrder;
    private Integer pageNumber;
    private Integer resultsPerPage;

    // User behavior
    private Integer clickedResultPosition;
    private Long clickedSchoolId;
    private String clickedSchoolName;
    private Integer timeToClickSeconds;
    private Boolean refinedSearch;
    private Boolean abandonedSearch;

    // Geographic context
    private String userLocation;
    private Integer searchRadiusKm;
    private String ipAddress;

    // Device context
    private DeviceType deviceType;
    private String userAgent;

    // Search intent
    private SearchIntent searchIntent;
    private Double confidenceScore;

    // Search suggestions
    private Boolean autocompleteUsed;
    private String suggestionSelected;

    // A/B testing
    private String experimentId;
    private String variant;

    private LocalDateTime createdAt;
}