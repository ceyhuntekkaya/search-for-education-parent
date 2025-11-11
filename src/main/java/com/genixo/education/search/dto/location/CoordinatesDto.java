package com.genixo.education.search.dto.location;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoordinatesDto {
    private Double latitude;
    private Double longitude;
}