package com.genixo.education.search.service.supply;

import com.genixo.education.search.dto.supply.SearchResultDto;
import com.genixo.education.search.dto.supply.SearchSuggestionDto;
import com.genixo.education.search.entity.supply.Category;
import com.genixo.education.search.entity.supply.Product;
import com.genixo.education.search.entity.supply.RFQ;
import com.genixo.education.search.entity.supply.Supplier;
import com.genixo.education.search.enumaration.ProductStatus;
import com.genixo.education.search.repository.supply.CategoryRepository;
import com.genixo.education.search.repository.supply.ProductRepository;
import com.genixo.education.search.repository.supply.RFQRepository;
import com.genixo.education.search.repository.supply.SupplierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SearchService {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final RFQRepository rfqRepository;
    private final CategoryRepository categoryRepository;

    // Maximum results per category for global search
    private static final int MAX_RESULTS_PER_CATEGORY = 10;
    private static final int MAX_SUGGESTIONS = 10;

    public SearchResultDto globalSearch(String query, Integer page, Integer size) {
        log.info("Performing global search with query: '{}'", query);

        if (query == null || query.trim().isEmpty()) {
            return SearchResultDto.builder()
                    .query("")
                    .totalResults(0L)
                    .productCount(0L)
                    .supplierCount(0L)
                    .rfqCount(0L)
                    .categoryCount(0L)
                    .products(new ArrayList<>())
                    .suppliers(new ArrayList<>())
                    .rfqs(new ArrayList<>())
                    .categories(new ArrayList<>())
                    .build();
        }

        String searchTerm = query.trim();
        Pageable pageable = PageRequest.of(
                page != null && page >= 0 ? page : 0,
                size != null && size > 0 ? size : MAX_RESULTS_PER_CATEGORY
        );

        // Search products
        Page<Product> productPage = productRepository.searchProducts(
                searchTerm, null, null, ProductStatus.ACTIVE, null, null, pageable
        );
        List<SearchResultDto.ProductSearchResult> products = productPage.getContent().stream()
                .map(this::mapToProductSearchResult)
                .collect(Collectors.toList());

        // Search suppliers
        Page<Supplier> supplierPage = supplierRepository.searchSuppliers(
                searchTerm, true, pageable
        );
        List<SearchResultDto.SupplierSearchResult> suppliers = supplierPage.getContent().stream()
                .map(this::mapToSupplierSearchResult)
                .collect(Collectors.toList());

        // Search RFQs (only published ones)
        Page<RFQ> rfqPage = rfqRepository.searchRFQs(
                searchTerm, null, null, null, pageable
        );
        List<SearchResultDto.RFQSearchResult> rfqs = rfqPage.getContent().stream()
                .map(this::mapToRFQSearchResult)
                .collect(Collectors.toList());

        // Search categories
        Page<Category> categoryPage = categoryRepository.searchCategories(
                searchTerm, true, null, pageable
        );
        List<SearchResultDto.CategorySearchResult> categories = categoryPage.getContent().stream()
                .map(this::mapToCategorySearchResult)
                .collect(Collectors.toList());

        return SearchResultDto.builder()
                .query(searchTerm)
                .totalResults(productPage.getTotalElements() + supplierPage.getTotalElements() +
                        rfqPage.getTotalElements() + categoryPage.getTotalElements())
                .productCount(productPage.getTotalElements())
                .supplierCount(supplierPage.getTotalElements())
                .rfqCount(rfqPage.getTotalElements())
                .categoryCount(categoryPage.getTotalElements())
                .products(products)
                .suppliers(suppliers)
                .rfqs(rfqs)
                .categories(categories)
                .build();
    }

    public SearchSuggestionDto getSearchSuggestions(String query) {
        log.info("Getting search suggestions for query: '{}'", query);

        if (query == null || query.trim().isEmpty()) {
            return SearchSuggestionDto.builder()
                    .query("")
                    .suggestions(new ArrayList<>())
                    .build();
        }

        String searchTerm = query.trim();
        Pageable pageable = PageRequest.of(0, MAX_SUGGESTIONS);

        List<SearchSuggestionDto.SuggestionItem> suggestions = new ArrayList<>();

        // Get product suggestions
        Page<Product> productPage = productRepository.searchProducts(
                searchTerm, null, null, ProductStatus.ACTIVE, null, null, pageable
        );
        productPage.getContent().stream()
                .limit(3)
                .forEach(product -> {
                    String additionalInfo = product.getCategory() != null
                            ? "Category: " + product.getCategory().getName()
                            : "";
                    suggestions.add(SearchSuggestionDto.SuggestionItem.builder()
                            .text(product.getName())
                            .type("product")
                            .id(product.getId())
                            .additionalInfo(additionalInfo)
                            .build());
                });

        // Get supplier suggestions
        Page<Supplier> supplierPage = supplierRepository.searchSuppliers(
                searchTerm, true, pageable
        );
        supplierPage.getContent().stream()
                .limit(3)
                .forEach(supplier -> {
                    suggestions.add(SearchSuggestionDto.SuggestionItem.builder()
                            .text(supplier.getCompanyName())
                            .type("supplier")
                            .id(supplier.getId())
                            .additionalInfo("Supplier")
                            .build());
                });

        // Get RFQ suggestions
        Page<RFQ> rfqPage = rfqRepository.searchRFQs(
                searchTerm, null, null, null, pageable
        );
        rfqPage.getContent().stream()
                .limit(2)
                .forEach(rfq -> {
                    String additionalInfo = rfq.getCompany() != null
                            ? "Company: " + rfq.getCompany().getName()
                            : "";
                    suggestions.add(SearchSuggestionDto.SuggestionItem.builder()
                            .text(rfq.getTitle())
                            .type("rfq")
                            .id(rfq.getId())
                            .additionalInfo(additionalInfo)
                            .build());
                });

        // Get category suggestions
        Page<Category> categoryPage = categoryRepository.searchCategories(
                searchTerm, true, null, pageable
        );
        categoryPage.getContent().stream()
                .limit(2)
                .forEach(category -> {
                    suggestions.add(SearchSuggestionDto.SuggestionItem.builder()
                            .text(category.getName())
                            .type("category")
                            .id(category.getId())
                            .additionalInfo("Category")
                            .build());
                });

        return SearchSuggestionDto.builder()
                .query(searchTerm)
                .suggestions(suggestions)
                .build();
    }

    // ================================ HELPER METHODS ================================

    private SearchResultDto.ProductSearchResult mapToProductSearchResult(Product product) {
        String matchType = "name"; // Default, could be enhanced to detect actual match field
        if (product.getSku() != null && product.getSku().toLowerCase().contains(product.getName().toLowerCase())) {
            matchType = "sku";
        } else if (product.getDescription() != null && product.getDescription().toLowerCase().contains(product.getName().toLowerCase())) {
            matchType = "description";
        }

        return SearchResultDto.ProductSearchResult.builder()
                .id(product.getId())
                .name(product.getName())
                .sku(product.getSku())
                .description(product.getDescription())
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .supplierCompanyName(product.getSupplier() != null ? product.getSupplier().getCompanyName() : null)
                .mainImageUrl(product.getMainImageUrl())
                .matchType(matchType)
                .build();
    }

    private SearchResultDto.SupplierSearchResult mapToSupplierSearchResult(Supplier supplier) {
        String matchType = "companyName"; // Default

        return SearchResultDto.SupplierSearchResult.builder()
                .id(supplier.getId())
                .companyName(supplier.getCompanyName())
                .email(supplier.getEmail())
                .phone(supplier.getPhone())
                .description(supplier.getDescription())
                .matchType(matchType)
                .build();
    }

    private SearchResultDto.RFQSearchResult mapToRFQSearchResult(RFQ rfq) {
        String matchType = "title"; // Default

        return SearchResultDto.RFQSearchResult.builder()
                .id(rfq.getId())
                .title(rfq.getTitle())
                .description(rfq.getDescription())
                .companyName(rfq.getCompany() != null ? rfq.getCompany().getName() : null)
                .status(rfq.getStatus() != null ? rfq.getStatus().name() : null)
                .matchType(matchType)
                .build();
    }

    private SearchResultDto.CategorySearchResult mapToCategorySearchResult(Category category) {
        String matchType = "name"; // Default

        return SearchResultDto.CategorySearchResult.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .matchType(matchType)
                .build();
    }
}

