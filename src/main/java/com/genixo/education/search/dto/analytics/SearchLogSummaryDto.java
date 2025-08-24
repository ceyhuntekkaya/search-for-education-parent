package com.genixo.education.search.dto.analytics;

import com.genixo.education.search.enumaration.DeviceType;
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
public class SearchLogSummaryDto {
    private LocalDateTime searchTime;
    private String searchQuery;
    private SearchType searchType;
    private Integer resultsCount;
    private Boolean zeroResults;
    private String clickedSchoolName;
    private String userLocation;
    private DeviceType deviceType;
}