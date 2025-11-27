package com.genixo.education.search.service;

import com.genixo.education.search.dto.institution.SchoolSearchFilterDTO;
import com.genixo.education.search.dto.institution.SchoolSearchResultViewDTO;
import com.genixo.education.search.entity.view.SchoolSearchView;
import com.genixo.education.search.repository.insitution.SchoolSearchRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SchoolSearchService {

    private final SchoolSearchRepository schoolSearchRepository;
    private final EntityManager entityManager;

    /**
     * Dinamik arama - Tüm filtreler opsiyonel
     */
    public Page<SchoolSearchResultViewDTO> search(SchoolSearchFilterDTO filter, Pageable pageable) {

        // Eğer property filter varsa, özel sorgu kullan
        if (filter.getPropertyFilters() != null && !filter.getPropertyFilters().isEmpty()) {
            return searchWithMultipleProperties(filter, pageable);
        }

        // Property yoksa normal dinamik sorgu
        Page<SchoolSearchView> results = schoolSearchRepository.dynamicSearch(
                filter.getProvinceName(),
                filter.getDistrictName(),
                filter.getInstitutionTypeId(),
                filter.getMinPrice(),
                filter.getMaxPrice(),
                filter.getHasActiveCampaign(),
                null, // propertyFilter yok
                filter.getSortBy() != null ? filter.getSortBy() : "rating",
                pageable
        );

        return results.map(this::toDTO);
    }

    /**
     * Çoklu property ile arama (20 property bile olabilir)
     */
    private Page<SchoolSearchResultViewDTO> searchWithMultipleProperties(
            SchoolSearchFilterDTO filter,
            Pageable pageable
    ) {
        // Base query oluştur
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT * FROM school_search_hybrid WHERE 1=1 ");

        // Province filter
        if (filter.getProvinceName() != null) {
            queryBuilder.append("AND province_name = :provinceName ");
        }

        // District filter
        if (filter.getDistrictName() != null) {
            queryBuilder.append("AND district_name = :districtName ");
        }

        // Institution type filter
        if (filter.getInstitutionTypeId() != null) {
            queryBuilder.append("AND institution_type_id = :institutionTypeId ");
        }

        // Price filters (0 veya null ise fiyat filtresini hiç uygulama)
        boolean hasPriceFilter = false;
        if (filter.getMinPrice() != null && filter.getMinPrice().compareTo(BigDecimal.ZERO) > 0) {
            queryBuilder.append("AND current_monthly_tuition >= :minPrice ");
            hasPriceFilter = true;
        }
        if (filter.getMaxPrice() != null && filter.getMaxPrice().compareTo(BigDecimal.ZERO) > 0) {
            queryBuilder.append("AND current_monthly_tuition <= :maxPrice ");
            hasPriceFilter = true;
        }
        // Eğer fiyat filtresi varsa, NULL değerleri hariç tut
        // Eğer fiyat filtresi yoksa, NULL olanları da göster (adam için fiyat önemli değil)

        // Campaign filter
        if (filter.getHasActiveCampaign() != null) {
            queryBuilder.append("AND has_active_campaign = :hasActiveCampaign ");
        }

        // Property filters (her biri için ayrı JSONB kontrol)
        if (filter.getPropertyFilters() != null) {
            for (int i = 0; i < filter.getPropertyFilters().size(); i++) {
                queryBuilder.append("AND properties @> CAST(:propertyFilter" + i + " AS jsonb) ");
            }
        }

        // Sorting
        String sortBy = filter.getSortBy() != null ? filter.getSortBy() : "rating";
        switch (sortBy) {
            case "price_asc":
                queryBuilder.append("ORDER BY current_monthly_tuition ASC NULLS LAST ");
                break;
            case "price_desc":
                queryBuilder.append("ORDER BY current_monthly_tuition DESC NULLS LAST ");
                break;
            case "newest":
                queryBuilder.append("ORDER BY created_at DESC ");
                break;
            case "rating":
            default:
                queryBuilder.append("ORDER BY rating_average DESC NULLS LAST ");
                break;
        }

        // Native query oluştur
        Query nativeQuery = entityManager.createNativeQuery(queryBuilder.toString(), SchoolSearchView.class);

        // Parametreleri set et
        if (filter.getProvinceName() != null) {
            nativeQuery.setParameter("provinceName", filter.getProvinceName());
        }
        if (filter.getDistrictName() != null) {
            nativeQuery.setParameter("districtName", filter.getDistrictName());
        }
        if (filter.getInstitutionTypeId() != null) {
            nativeQuery.setParameter("institutionTypeId", filter.getInstitutionTypeId());
        }
        if (filter.getMinPrice() != null && filter.getMinPrice().compareTo(BigDecimal.ZERO) > 0) {
            nativeQuery.setParameter("minPrice", filter.getMinPrice());
        }
        if (filter.getMaxPrice() != null && filter.getMaxPrice().compareTo(BigDecimal.ZERO) > 0) {
            nativeQuery.setParameter("maxPrice", filter.getMaxPrice());
        }
        if (filter.getHasActiveCampaign() != null) {
            nativeQuery.setParameter("hasActiveCampaign", filter.getHasActiveCampaign());
        }

        // Property parametrelerini set et
        if (filter.getPropertyFilters() != null) {
            for (int i = 0; i < filter.getPropertyFilters().size(); i++) {
                SchoolSearchFilterDTO.PropertyFilter pf = filter.getPropertyFilters().get(i);
                String jsonbFilter = buildSinglePropertyFilter(pf.getName(), pf.getBooleanValue());
                nativeQuery.setParameter("propertyFilter" + i, jsonbFilter);
            }
        }

        // Pagination
        nativeQuery.setFirstResult((int) pageable.getOffset());
        nativeQuery.setMaxResults(pageable.getPageSize());

        // Execute query
        @SuppressWarnings("unchecked")
        List<SchoolSearchView> results = nativeQuery.getResultList();

        // Count query (total elements için)
        String countQuery = queryBuilder.toString().replace("SELECT * FROM", "SELECT COUNT(*) FROM");
        countQuery = countQuery.substring(0, countQuery.indexOf("ORDER BY")); // ORDER BY'ı kaldır

        Query countNativeQuery = entityManager.createNativeQuery(countQuery);

        // Count query parametreleri
        if (filter.getProvinceName() != null) {
            countNativeQuery.setParameter("provinceName", filter.getProvinceName());
        }
        if (filter.getDistrictName() != null) {
            countNativeQuery.setParameter("districtName", filter.getDistrictName());
        }
        if (filter.getInstitutionTypeId() != null) {
            countNativeQuery.setParameter("institutionTypeId", filter.getInstitutionTypeId());
        }
        if (filter.getMinPrice() != null && filter.getMinPrice().compareTo(BigDecimal.ZERO) > 0) {
            countNativeQuery.setParameter("minPrice", filter.getMinPrice());
        }
        if (filter.getMaxPrice() != null && filter.getMaxPrice().compareTo(BigDecimal.ZERO) > 0) {
            countNativeQuery.setParameter("maxPrice", filter.getMaxPrice());
        }
        if (filter.getHasActiveCampaign() != null) {
            countNativeQuery.setParameter("hasActiveCampaign", filter.getHasActiveCampaign());
        }
        if (filter.getPropertyFilters() != null) {
            for (int i = 0; i < filter.getPropertyFilters().size(); i++) {
                SchoolSearchFilterDTO.PropertyFilter pf = filter.getPropertyFilters().get(i);
                String jsonbFilter = buildSinglePropertyFilter(pf.getName(), pf.getBooleanValue());
                countNativeQuery.setParameter("propertyFilter" + i, jsonbFilter);
            }
        }

        Long total = ((Number) countNativeQuery.getSingleResult()).longValue();

        // Page oluştur
        Page<SchoolSearchView> page = new PageImpl<>(results, pageable, total);
        return page.map(this::toDTO);
    }

    /**
     * İl'e göre arama
     */
    public Page<SchoolSearchResultViewDTO> searchByProvince(String provinceName, Pageable pageable) {
        Page<SchoolSearchView> results = schoolSearchRepository.findByProvinceName(provinceName, pageable);
        return results.map(this::toDTO);
    }

    /**
     * Kompleks arama: İl + Fiyat + Property
     * Senin örneğin: Ankara + 20000-50000 TL + Kütüphane
     */
    public Page<SchoolSearchResultViewDTO> searchByProvinceAndPriceAndProperty(
            String provinceName,
            java.math.BigDecimal minPrice,
            java.math.BigDecimal maxPrice,
            String propertyName,
            Boolean propertyValue,
            Pageable pageable
    ) {
        String propertyFilter = buildSinglePropertyFilter(propertyName, propertyValue);

        Page<SchoolSearchView> results = schoolSearchRepository.findByProvinceAndPriceAndProperty(
                provinceName, minPrice, maxPrice, propertyFilter, pageable
        );

        return results.map(this::toDTO);
    }

    /**
     * Full-text search
     */
    public Page<SchoolSearchResultViewDTO> fullTextSearch(String keyword, Pageable pageable) {
        // Türkçe karakterleri düzelt ve & ile birleştir
        String searchQuery = keyword.trim().replaceAll("\\s+", " & ");

        Page<SchoolSearchView> results = schoolSearchRepository.fullTextSearch(searchQuery, pageable);
        return results.map(this::toDTO);
    }

    /**
     * İsme göre basit arama (LIKE)
     */
    public Page<SchoolSearchResultViewDTO> searchByName(String keyword, Pageable pageable) {
        Page<SchoolSearchView> results = schoolSearchRepository.searchByName(keyword, pageable);
        return results.map(this::toDTO);
    }

    /**
     * İstatistikler - İl bazında okul sayıları
     */
    public List<ProvinceCountDTO> getProvinceStatistics() {
        List<Object[]> results = schoolSearchRepository.countByProvince();
        List<ProvinceCountDTO> stats = new ArrayList<>();

        for (Object[] row : results) {
            stats.add(new ProvinceCountDTO(
                    (String) row[0],  // province_name
                    ((Number) row[1]).longValue()  // count
            ));
        }

        return stats;
    }

    /**
     * İstatistikler - Fiyat aralıkları
     */
    public List<PriceRangeDTO> getPriceRangeStatistics() {
        List<Object[]> results = schoolSearchRepository.getPriceRangeDistribution();
        List<PriceRangeDTO> stats = new ArrayList<>();

        for (Object[] row : results) {
            stats.add(new PriceRangeDTO(
                    (String) row[0],  // price_range
                    ((Number) row[1]).longValue()  // count
            ));
        }

        return stats;
    }

    // ==================== HELPER METHODS ====================

    /**
     * Tek bir property için JSONB filter
     */
    private String buildSinglePropertyFilter(String propertyName, Boolean booleanValue) {
        if (propertyName == null) {
            return null;
        }

        if (booleanValue != null) {
            // Boolean property
            return String.format("[{\"name\": \"%s\", \"boolean_value\": %s}]",
                    propertyName, booleanValue);
        }

        return null;
    }

    /**
     * Entity -> DTO dönüşümü
     */
    private SchoolSearchResultViewDTO toDTO(SchoolSearchView view) {
        SchoolSearchResultViewDTO dto = new SchoolSearchResultViewDTO();

        // Basic info
        dto.setSchoolId(view.getSchoolId());
        dto.setSchoolName(view.getSchoolName());
        dto.setSchoolSlug(view.getSchoolSlug());
        dto.setSchoolDescription(view.getSchoolDescription());
        dto.setSchoolLogoUrl(view.getSchoolLogoUrl());

        // Location
        dto.setProvinceName(view.getProvinceName());
        dto.setDistrictName(view.getDistrictName());
        dto.setNeighborhoodName(view.getNeighborhoodName());

        // Campus
        dto.setCampusName(view.getCampusName());
        dto.setCampusLatitude(view.getCampusLatitude());
        dto.setCampusLongitude(view.getCampusLongitude());

        // Institution Type
        dto.setInstitutionTypeDisplayName(view.getInstitutionTypeDisplayName());

        // Pricing
        dto.setCurrentMonthlyTuition(view.getCurrentMonthlyTuition());
        dto.setCurrentAnnualTuition(view.getCurrentAnnualTuition());
        dto.setCurrentRegistrationFee(view.getCurrentRegistrationFee());
        dto.setPricingCurrency(view.getPricingCurrency());

        // Statistics
        dto.setRatingAverage(view.getRatingAverage());
        dto.setRatingCount(view.getRatingCount());
        dto.setViewCount(view.getViewCount());

        // Properties
        dto.setPropertyNamesText(view.getPropertyNamesText());
        dto.setPropertyCount(view.getPropertyCount());
        dto.setProperties(view.getProperties()); // JSON string

        // Campaign
        dto.setHasActiveCampaign(view.getHasActiveCampaign());

        // JSON details (ihtiyaç olursa)
        dto.setCampusDetails(view.getCampusDetails());
        dto.setDistrictStats(view.getDistrictStats());

        return dto;
    }

    // ==================== INNER DTOs ====================

    @lombok.Data
    @lombok.AllArgsConstructor
    public static class ProvinceCountDTO {
        private String provinceName;
        private Long count;
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    public static class PriceRangeDTO {
        private String priceRange;
        private Long count;
    }
}