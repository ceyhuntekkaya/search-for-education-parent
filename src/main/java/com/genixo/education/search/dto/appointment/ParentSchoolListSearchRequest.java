package com.genixo.education.search.dto.appointment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParentSchoolListSearchRequest {

    private String keyword;
    private List<String> statuses;
    private Boolean onlyFavorites;
    private Boolean onlyBlocked;
    private Integer minStarRating;
    private Integer maxStarRating;
    private List<String> tags;
    private String sortBy = "priorityOrder"; // priorityOrder, starRating, addedAt, schoolName
    private String sortDirection = "ASC";
    private Integer page = 0;
    private Integer size = 20;
}