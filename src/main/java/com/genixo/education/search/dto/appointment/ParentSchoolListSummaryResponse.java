package com.genixo.education.search.dto.appointment;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParentSchoolListSummaryResponse {

    private Long id;
    private String listName;
    private String colorCode;
    private String icon;
    private Integer schoolCount;
    private Boolean isDefault;
    private LocalDateTime lastAccessedAt;
}
