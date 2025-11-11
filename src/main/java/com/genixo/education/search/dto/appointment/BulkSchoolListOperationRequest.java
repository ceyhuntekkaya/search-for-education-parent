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
public class BulkSchoolListOperationRequest {

    private List<Long> schoolIds;
    private String operation; // "FAVORITE", "UNFAVORITE", "BLOCK", "UNBLOCK", "REMOVE", "MOVE"
    private Long targetListId; // For move operation
    private Integer starRating; // For bulk rating
}