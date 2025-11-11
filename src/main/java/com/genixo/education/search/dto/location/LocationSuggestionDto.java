package com.genixo.education.search.dto.location;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationSuggestionDto {
    private String id;
    private String name;
    private String type; // COUNTRY, PROVINCE, DISTRICT, NEIGHBORHOOD
    private String fullName; // "Ankara, Çankaya, Kızılay"
    private Double latitude;
    private Double longitude;
    private Long schoolCount;
    private String relevanceScore;
}