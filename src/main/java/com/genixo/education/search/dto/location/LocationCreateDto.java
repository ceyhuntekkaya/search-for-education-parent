package com.genixo.education.search.dto.location;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationCreateDto {
    private String name;
    private String nameEn;
    private String code;
    private Double latitude;
    private Double longitude;
    private String description;
    private Long parentId; // Province for District, District for Neighborhood etc.
}