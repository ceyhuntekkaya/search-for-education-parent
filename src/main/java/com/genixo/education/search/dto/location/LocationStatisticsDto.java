package com.genixo.education.search.dto.location;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationStatisticsDto {
    private Long totalCountries;
    private Long supportedCountries;
    private Long totalProvinces;
    private Long metropolitanProvinces;
    private Long totalDistricts;
    private Long centralDistricts;
    private Long totalNeighborhoods;
    private Long totalSchools;
    private Long totalStudents;
    private Long totalTeachers;
}