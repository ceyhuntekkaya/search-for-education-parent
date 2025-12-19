package com.genixo.education.search.dto.supply;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchSuggestionDto {

    private String query;
    private List<SuggestionItem> suggestions;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SuggestionItem {
        private String text;
        private String type; // "product", "supplier", "rfq", "category"
        private Long id;
        private String additionalInfo; // e.g., "Supplier: ABC Company" or "Category: Electronics"
    }
}

