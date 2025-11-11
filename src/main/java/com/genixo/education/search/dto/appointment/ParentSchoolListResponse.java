package com.genixo.education.search.dto.appointment;


import com.genixo.education.search.enumaration.ListStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParentSchoolListResponse {

    private Long id;
    private String listName;
    private String description;
    private ListStatus status;
    private Boolean isDefault;
    private String colorCode;
    private String icon;
    private String notes;
    private Integer schoolCount;
    private LocalDateTime lastAccessedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Summary info
    private Integer favoriteCount;
    private Integer blockedCount;
    private Integer visitPlannedCount;
    private Integer visitCompletedCount;
}