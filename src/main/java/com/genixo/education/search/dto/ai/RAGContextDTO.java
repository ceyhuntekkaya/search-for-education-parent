package com.genixo.education.search.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RAGContextDTO {
    private List<String> availableCities;
    private List<DistrictOption> availableDistricts; // Şehre göre ilçeler
    private List<String> availableInstitutionTypes;
    private List<SchoolPropertyOption> availableSchoolProperties;
    private PriceRangeInfo priceRangeInfo;


    private List<String> availableInstitutionTypeGroups;

    // Kullanıcının mevcut seçimleri
    private String selectedCity;
    private String selectedDistrict;
    private String selectedInstitutionType;
    private List<String> selectedProperties;
    private Double minPrice;
    private Double maxPrice;



    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DistrictOption {
        private String city;
        private List<String> districts;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SchoolPropertyOption {
        private Long id;
        private String name;
        private String category; // Spor, Akademik, Sosyal vb.
    }


}
