package com.genixo.education.search.dto.webinar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventCategorySummaryDto {
    private Long id;
    private String name;
    private String slug;
    private String icon;
}
