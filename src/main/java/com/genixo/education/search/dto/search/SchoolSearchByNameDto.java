package com.genixo.education.search.dto.search;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * AI Search DTO - Name bazlı arama
 * AI'dan gelen istekler için (ID yerine name kullanır)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolSearchByNameDto {

    // ZORUNLU - AI mutlaka bunları vermeli
    private String institutionTypeName;  // "LİSE", "ORTAOKUL", etc.
    private String provinceName;         // "ANKARA", "İSTANBUL", etc.

    // OPSIYONEL - Null olabilir
    private String districtName;         // "ÇANKAYA", null olabilir
    private String neighborhoodName;     // "BİLKENT", null olabilir

    // Yaş aralığı
    private Integer minAge;              // 1
    private Integer maxAge;              // 80

    // Ücret aralığı
    private Double minFee;               // 311930
    private Double maxFee;               // 767232

    // Eğitim bilgileri
    private String curriculumType;       // "" veya "IB", "MEB", etc.
    private String languageOfInstruction; // "" veya "İNGİLİZCE", "TÜRKÇE", etc.

    // Kalite filtreleri
    private Double minRating;            // 2
    private Boolean isSubscribed;        // null olabilir

    // Özellikler - Display name'lerle
    private List<String> propertyFilters; // ["BASKETBOL KULÜBÜ", "DRAMA", "İNGİLİZCE", "IB PROGRAMI"]

    // Genel arama terimi
    private String searchTerm;           // "" olabilir

    // Sıralama
    private String sortBy;               // "name", "rating", "price", "created"
    private String sortDirection;        // "asc", "desc"

    // Pagination
    private Integer page;                // 0
    private Integer size;                // 12

    /**
     * Boş string'leri null'a çevir
     */
    public void normalizeEmptyStrings() {
        if (districtName != null && districtName.trim().isEmpty()) {
            districtName = null;
        }
        if (neighborhoodName != null && neighborhoodName.trim().isEmpty()) {
            neighborhoodName = null;
        }
        if (curriculumType != null && curriculumType.trim().isEmpty()) {
            curriculumType = null;
        }
        if (languageOfInstruction != null && languageOfInstruction.trim().isEmpty()) {
            languageOfInstruction = null;
        }
        if (searchTerm != null && searchTerm.trim().isEmpty()) {
            searchTerm = null;
        }
    }

    /**
     * Zorunlu alanlar dolu mu kontrol et
     */
    public boolean hasRequiredFields() {
        return institutionTypeName != null && !institutionTypeName.trim().isEmpty()
                && provinceName != null && !provinceName.trim().isEmpty();
    }
}