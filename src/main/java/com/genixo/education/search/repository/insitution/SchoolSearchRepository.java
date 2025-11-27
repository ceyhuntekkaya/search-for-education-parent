package com.genixo.education.search.repository.insitution;

import com.genixo.education.search.entity.view.SchoolSearchView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SchoolSearchRepository extends JpaRepository<SchoolSearchView, Long> {

    // ==================== BASIT FİLTRELEME ====================

    /**
     * İl'e göre arama
     */
    Page<SchoolSearchView> findByProvinceName(String provinceName, Pageable pageable);

    /**
     * İlçe'ye göre arama
     */
    Page<SchoolSearchView> findByDistrictName(String districtName, Pageable pageable);

    /**
     * İl ve İlçe'ye göre arama
     */
    Page<SchoolSearchView> findByProvinceNameAndDistrictName(
            String provinceName,
            String districtName,
            Pageable pageable
    );

    /**
     * Kurum tipine göre arama
     */
    Page<SchoolSearchView> findByInstitutionTypeId(Long institutionTypeId, Pageable pageable);

    /**
     * Aktif kampanyalı okullar
     */
    Page<SchoolSearchView> findByHasActiveCampaignTrue(Pageable pageable);

    // ==================== FİYAT FİLTRELEME ====================

    /**
     * Fiyat aralığı
     */
    Page<SchoolSearchView> findByCurrentMonthlyTuitionBetween(
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    );

    /**
     * İl + Fiyat aralığı
     */
    Page<SchoolSearchView> findByProvinceNameAndCurrentMonthlyTuitionBetween(
            String provinceName,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    );

    // ==================== PROPERTY FİLTRELEME (JSONB) ====================

    /**
     * Belirli bir property'ye sahip okullar (JSONB query)
     * Örnek: has_library = true
     */
    @Query(value = """
        SELECT * FROM school_search_hybrid
        WHERE properties @> CAST(:propertyFilter AS jsonb)
        """,
            countQuery = """
        SELECT COUNT(*) FROM school_search_hybrid
        WHERE properties @> CAST(:propertyFilter AS jsonb)
        """,
            nativeQuery = true)
    Page<SchoolSearchView> findByProperty(
            @Param("propertyFilter") String propertyFilter,
            Pageable pageable
    );

    /**
     * İl + Property filtreleme
     * Örnek: Ankara + has_library = true
     */
    @Query(value = """
        SELECT * FROM school_search_hybrid
        WHERE province_name = :provinceName
          AND properties @> CAST(:propertyFilter AS jsonb)
        """,
            countQuery = """
        SELECT COUNT(*) FROM school_search_hybrid
        WHERE province_name = :provinceName
          AND properties @> CAST(:propertyFilter AS jsonb)
        """,
            nativeQuery = true)
    Page<SchoolSearchView> findByProvinceAndProperty(
            @Param("provinceName") String provinceName,
            @Param("propertyFilter") String propertyFilter,
            Pageable pageable
    );

    /**
     * Kompleks Filtreleme: İl + Fiyat + Property
     * Senin örneğin: Ankara + 20000-50000 TL + Kütüphane
     */
    @Query(value = """
        SELECT * FROM school_search_hybrid
        WHERE province_name = :provinceName
          AND current_monthly_tuition BETWEEN :minPrice AND :maxPrice
          AND properties @> CAST(:propertyFilter AS jsonb)
        ORDER BY rating_average DESC NULLS LAST
        """,
            countQuery = """
        SELECT COUNT(*) FROM school_search_hybrid
        WHERE province_name = :provinceName
          AND current_monthly_tuition BETWEEN :minPrice AND :maxPrice
          AND properties @> CAST(:propertyFilter AS jsonb)
        """,
            nativeQuery = true)
    Page<SchoolSearchView> findByProvinceAndPriceAndProperty(
            @Param("provinceName") String provinceName,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("propertyFilter") String propertyFilter,
            Pageable pageable
    );

    /**
     * Çoklu Property Filtreleme
     * Örnek: has_library = true VE has_laboratory = true
     */
    @Query(value = """
        SELECT * FROM school_search_hybrid
        WHERE properties @> CAST(:property1Filter AS jsonb)
          AND properties @> CAST(:property2Filter AS jsonb)
        """,
            countQuery = """
        SELECT COUNT(*) FROM school_search_hybrid
        WHERE properties @> CAST(:property1Filter AS jsonb)
          AND properties @> CAST(:property2Filter AS jsonb)
        """,
            nativeQuery = true)
    Page<SchoolSearchView> findByMultipleProperties(
            @Param("property1Filter") String property1Filter,
            @Param("property2Filter") String property2Filter,
            Pageable pageable
    );

    // ==================== FULL-TEXT SEARCH ====================

    /**
     * Türkçe Full-Text Search
     * Örnek: "IB programı özel okul"
     */
    @Query(value = """
        SELECT * FROM school_search_hybrid
        WHERE to_tsvector('turkish', search_text) @@ to_tsquery('turkish', :searchQuery)
        ORDER BY rating_average DESC NULLS LAST
        """,
            countQuery = """
        SELECT COUNT(*) FROM school_search_hybrid
        WHERE to_tsvector('turkish', search_text) @@ to_tsquery('turkish', :searchQuery)
        """,
            nativeQuery = true)
    Page<SchoolSearchView> fullTextSearch(
            @Param("searchQuery") String searchQuery,
            Pageable pageable
    );

    /**
     * LIKE Search (basit isim araması)
     */
    @Query(value = """
        SELECT * FROM school_search_hybrid
        WHERE school_name ILIKE %:keyword%
        ORDER BY rating_average DESC NULLS LAST
        """,
            countQuery = """
        SELECT COUNT(*) FROM school_search_hybrid
        WHERE school_name ILIKE %:keyword%
        """,
            nativeQuery = true)
    Page<SchoolSearchView> searchByName(
            @Param("keyword") String keyword,
            Pageable pageable
    );

    // ==================== İSTATİSTİKLER ====================

    /**
     * İl bazında okul sayıları
     */
    @Query(value = """
        SELECT 
            province_name as name,
            COUNT(*) as count
        FROM school_search_hybrid
        GROUP BY province_name
        ORDER BY count DESC
        """,
            nativeQuery = true)
    List<Object[]> countByProvince();

    /**
     * İlçe bazında okul sayıları (belirli bir ilde)
     */
    @Query(value = """
        SELECT 
            district_name as name,
            COUNT(*) as count
        FROM school_search_hybrid
        WHERE province_name = :provinceName
        GROUP BY district_name
        ORDER BY count DESC
        """,
            nativeQuery = true)
    List<Object[]> countByDistrictInProvince(@Param("provinceName") String provinceName);

    /**
     * Fiyat aralıkları (histogram için)
     */
    @Query(value = """
        SELECT 
            CASE 
                WHEN current_monthly_tuition < 5000 THEN '0-5000'
                WHEN current_monthly_tuition < 10000 THEN '5000-10000'
                WHEN current_monthly_tuition < 20000 THEN '10000-20000'
                WHEN current_monthly_tuition < 50000 THEN '20000-50000'
                ELSE '50000+'
            END as price_range,
            COUNT(*) as count
        FROM school_search_hybrid
        WHERE current_monthly_tuition IS NOT NULL
        GROUP BY price_range
        ORDER BY price_range
        """,
            nativeQuery = true)
    List<Object[]> getPriceRangeDistribution();

    // ==================== DİNAMİK QUERY (Gelişmiş) ====================

    /**
     * Dinamik filtreleme (tüm parametreler opsiyonel)
     * Service layer'da kullanılacak
     */
    @Query(value = """
        SELECT * FROM school_search_hybrid
        WHERE (:provinceName IS NULL OR province_name = :provinceName)
          AND (:districtName IS NULL OR district_name = :districtName)
          AND (:institutionTypeId IS NULL OR institution_type_id = :institutionTypeId)
          AND (:minPrice IS NULL OR current_monthly_tuition >= :minPrice)
          AND (:maxPrice IS NULL OR current_monthly_tuition <= :maxPrice)
          AND (:hasActiveCampaign IS NULL OR has_active_campaign = :hasActiveCampaign)
          AND (:propertyFilter IS NULL OR properties @> CAST(:propertyFilter AS jsonb))
        ORDER BY 
            CASE WHEN :sortBy = 'rating' THEN rating_average END DESC NULLS LAST,
            CASE WHEN :sortBy = 'price_asc' THEN current_monthly_tuition END ASC NULLS LAST,
            CASE WHEN :sortBy = 'price_desc' THEN current_monthly_tuition END DESC NULLS LAST,
            CASE WHEN :sortBy = 'newest' THEN created_at END DESC
        """,
            countQuery = """
        SELECT COUNT(*) FROM school_search_hybrid
        WHERE (:provinceName IS NULL OR province_name = :provinceName)
          AND (:districtName IS NULL OR district_name = :districtName)
          AND (:institutionTypeId IS NULL OR institution_type_id = :institutionTypeId)
          AND (:minPrice IS NULL OR current_monthly_tuition >= :minPrice)
          AND (:maxPrice IS NULL OR current_monthly_tuition <= :maxPrice)
          AND (:hasActiveCampaign IS NULL OR has_active_campaign = :hasActiveCampaign)
          AND (:propertyFilter IS NULL OR properties @> CAST(:propertyFilter AS jsonb))
        """,
            nativeQuery = true)
    Page<SchoolSearchView> dynamicSearch(
            @Param("provinceName") String provinceName,
            @Param("districtName") String districtName,
            @Param("institutionTypeId") Long institutionTypeId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("hasActiveCampaign") Boolean hasActiveCampaign,
            @Param("propertyFilter") String propertyFilter,
            @Param("sortBy") String sortBy,
            Pageable pageable
    );
}