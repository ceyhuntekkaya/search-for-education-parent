package com.genixo.education.search.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormSubmissionDTO {
    private Long id;
    private Long conversationId;
    private Long userId;
    private String city;
    private String district;
    private String institutionType;
    private List<String> schoolProperties;
    private Double minPrice;
    private Double maxPrice;
    private String explain;
    private Integer searchResultsCount;
    private LocalDateTime submittedAt;
}