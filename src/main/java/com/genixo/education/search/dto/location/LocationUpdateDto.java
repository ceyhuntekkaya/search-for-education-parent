package com.genixo.education.search.dto.location;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationUpdateDto {
    private String name;
    private String nameEn;
    private Double latitude;
    private Double longitude;
    private String description;
    private Boolean isActive;
    private Integer sortOrder;
}