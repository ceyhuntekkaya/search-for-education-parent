package com.genixo.education.search.entity.analytics;

import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.enumaration.DeviceType;
import com.genixo.education.search.enumaration.SearchIntent;
import com.genixo.education.search.enumaration.SearchType;
import com.genixo.education.search.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "search_logs")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class SearchLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // Null if anonymous

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "search_query", nullable = false)
    private String searchQuery;

    @Column(name = "cleaned_query")
    private String cleanedQuery; // Normalized version

    @Column(name = "search_time", nullable = false)
    private LocalDateTime searchTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "search_type", nullable = false)
    private SearchType searchType;

    @Column(name = "results_count")
    private Integer resultsCount = 0;

    @Column(name = "zero_results")
    private Boolean zeroResults = false;

    @Column(name = "response_time_ms")
    private Integer responseTimeMs;

    // Search filters applied
    @Column(name = "filters_applied")
    private String filtersApplied;

    @Column(name = "sort_order")
    private String sortOrder;

    @Column(name = "page_number")
    private Integer pageNumber = 1;

    @Column(name = "results_per_page")
    private Integer resultsPerPage = 10;

    // User behavior
    @Column(name = "clicked_result_position")
    private Integer clickedResultPosition;

    @Column(name = "clicked_school_id")
    private Long clickedSchoolId;

    @Column(name = "time_to_click_seconds")
    private Integer timeToClickSeconds;

    @Column(name = "refined_search")
    private Boolean refinedSearch = false; // Did user search again?

    @Column(name = "abandoned_search")
    private Boolean abandonedSearch = false;

    // Geographic context
    @Column(name = "user_location")
    private String userLocation;

    @Column(name = "search_radius_km")
    private Integer searchRadiusKm;

    @Column(name = "ip_address")
    private String ipAddress;

    // Device context
    @Enumerated(EnumType.STRING)
    @Column(name = "device_type")
    private DeviceType deviceType;

    @Column(name = "user_agent")
    private String userAgent;

    // Search intent classification (can be set by ML)
    @Enumerated(EnumType.STRING)
    @Column(name = "search_intent")
    private SearchIntent searchIntent;

    @Column(name = "confidence_score")
    private Double confidenceScore;

    // Search suggestions
    @Column(name = "autocomplete_used")
    private Boolean autocompleteUsed = false;

    @Column(name = "suggestion_selected")
    private String suggestionSelected;

    // A/B testing
    @Column(name = "experiment_id")
    private String experimentId;

    @Column(name = "variant")
    private String variant;
}
