package com.genixo.education.search.dto.ai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FormDataDTO {
    private String city;
    private String district; // Opsiyonel
    private String institutionTypeGroup; // Zorunlu
    private String institutionType; // Zorunlu
    private String schoolPropertyGroup; // Opsiyonel
    private List<String> schoolProperties; // Şartlı zorunlu
    private Double minPrice; // Opsiyonel
    private Double maxPrice; // Opsiyonel
    private String explain;

    // Form durumu
    private List<String> missingFields;
    private String nextStep; // city, district, institutionTypeGroup, institutionType, price, propertyGroup, properties, complete
    private String userMessage; // AI'ın kullanıcıya göstereceği mesaj

    // Doldurulma durumu
    private Boolean cityFilled;
    private Boolean districtFilled;
    private Boolean institutionTypeGroupFilled;
    private Boolean institutionTypeFilled;
    private Boolean propertyGroupFilled;
    private Boolean propertiesFilled;
    private Boolean priceFilled;

    // Form tamamlanma yüzdesi
    private Integer completionPercentage;

    // Minimum gereksinimler sağlandı mı? (city + institutionTypeGroup + institutionType)
    private Boolean meetsMinimumRequirements;
}