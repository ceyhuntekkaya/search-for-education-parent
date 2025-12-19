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
public class SearchResultDto {

    private String query;
    private Long totalResults;
    private Long productCount;
    private Long supplierCount;
    private Long rfqCount;
    private Long categoryCount;

    private List<ProductSearchResult> products;
    private List<SupplierSearchResult> suppliers;
    private List<RFQSearchResult> rfqs;
    private List<CategorySearchResult> categories;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductSearchResult {
        private Long id;
        private String name;
        private String sku;
        private String description;
        private String categoryName;
        private String supplierCompanyName;
        private String mainImageUrl;
        private String matchType; // "name", "sku", "description"
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SupplierSearchResult {
        private Long id;
        private String companyName;
        private String email;
        private String phone;
        private String description;
        private String matchType; // "companyName", "email", "description"
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RFQSearchResult {
        private Long id;
        private String title;
        private String description;
        private String companyName;
        private String status;
        private String matchType; // "title", "description"
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategorySearchResult {
        private Long id;
        private String name;
        private String description;
        private String matchType; // "name", "description"
    }
}

