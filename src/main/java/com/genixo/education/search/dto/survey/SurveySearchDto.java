package com.genixo.education.search.dto.survey;

import com.genixo.education.search.enumaration.SurveyTriggerEvent;
import com.genixo.education.search.enumaration.SurveyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveySearchDto {
    private String searchTerm;
    private List<SurveyType> surveyTypes;
    private List<SurveyTriggerEvent> triggerEvents;
    private Boolean isActive;
    private Boolean isAnonymous;
    private Boolean isMandatory;
    private Boolean showResultsToPublic;
    private LocalDateTime createdFrom;
    private LocalDateTime createdTo;
    private Long minResponses;
    private Long maxResponses;
    private Double minCompletionRate;
    private Double maxCompletionRate;
    private Double minAverageRating;
    private Double maxAverageRating;

    // Sorting
    private String sortBy; // CREATED_DATE, TITLE, RESPONSE_COUNT, COMPLETION_RATE, AVERAGE_RATING
    private String sortDirection;

    // Pagination
    private Integer page;
    private Integer size;
}