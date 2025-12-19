

CREATE MATERIALIZED VIEW school_search_materialized_v2 AS
SELECT
    -- ============ TEMEL OKUL BİLGİLERİ ============
    s.id AS school_id,
    s.name AS school_name,
    s.slug AS school_slug,
    s.is_active,

    -- ============ KURUM TİPİ ============
    it.id AS institution_type_id,
    it.name AS institution_type_name,
    it.display_name AS institution_type_display_name,
    it.description AS institution_type_description,
    it.color_code AS institution_type_color,
    it.icon_url AS institution_type_icon,

    -- ============ KAMPÜS BİLGİLERİ ============
    c.id AS campus_id,
    c.name AS campus_name,
    c.slug AS campus_slug,
    c.latitude,
    c.longitude,
    c.address_line1 AS address,
    c.address_line2,
    c.postal_code,
    c.website_url,
    c.established_year AS founded_year,
    c.is_subscribed AS campus_is_subscribed,

    -- ============ YER BİLGİLERİ ============
    n.id AS neighborhood_id,
    n.name AS neighborhood_name,

    d.id AS district_id,
    d.name AS district_name,
    d.slug AS district_slug,
    d.district_type,
    d.is_central AS district_is_central,

    p.id AS province_id,
    p.name AS province_name,
    p.slug AS province_slug,
    p.code AS province_code,
    p.plate_code,

    co.id AS country_id,
    co.name AS country_name,
    co.iso_code_2,

    -- ============ MARKA BİLGİSİ ============
    b.id AS brand_id,
    b.name AS brand_name,
    b.slug AS brand_slug,
    b.logo_url AS brand_logo,
    b.is_active AS brand_is_active,

    -- ============ PUANLAMA VE İSTATİSTİKLER ============
    s.rating_average,
    s.rating_count,
    s.view_count,
    s.like_count,
    s.post_count,

    c.rating_average AS campus_rating_average,
    c.rating_count AS campus_rating_count,
    c.view_count AS campus_view_count,

    -- ============ KAPASİTE BİLGİLERİ ============
    s.capacity AS student_capacity,
    s.current_student_count,
    s.class_size_average,
    s.min_age,
    s.max_age,

    -- ============ ÜCRET BİLGİLERİ ============
    s.monthly_fee,
    s.annual_fee,
    s.registration_fee,

    -- ============ EĞİTİM BİLGİLERİ ============
    s.curriculum_type,
    s.language_of_instruction,
    s.foreign_languages,
    s.extension,

    -- ============ İLETİŞİM BİLGİLERİ ============
    s.email,
    s.phone,
    s.facebook_url,
    s.instagram_url,
    s.twitter_url,
    s.linkedin_url,
    s.youtube_url,

    c.email AS campus_email,
    c.phone AS campus_phone,

    -- ============ GÖRSEL VARLIKLARI ============
    s.logo_url,
    s.cover_image_url,
    c.logo_url AS campus_logo_url,
    c.cover_image_url AS campus_cover_image_url,

    -- ============ META BİLGİLER ============
    s.meta_title,
    s.meta_description,
    s.meta_keywords,

    -- ============ AÇIKLAMALAR ============
    s.description,
    c.description AS campus_description,

    -- ============ ZAMAN BİLGİLERİ ============
    s.created_at,
    s.updated_at,

    -- ============ ÖZELLİKLER (PROPERTIES) - JSON FORMAT ============

    -- Tüm özellikler JSON array olarak
    (
        SELECT json_agg(
                       json_build_object(
                               'property_id', ip.id,
                               'property_name', ip.name,
                               'property_display_name', ip.display_name,
                               'property_type_id', pt.id,
                               'property_type_name', pt.name,
                               'property_type_display_name', pt.display_name,
                               'property_group_id', pgt.id,
                               'property_group_name', pgt.name,
                               'property_group_display_name', pgt.display_name,
                               'data_type', ip.data_type,
                               'text_value', ipv.text_value,
                               'number_value', ipv.number_value,
                               'boolean_value', ipv.boolean_value,
                               'date_value', ipv.date_value,
                               'datetime_value', ipv.datetime_value,
                               'json_value', ipv.json_value,
                               'file_url', ipv.file_url,
                               'is_active', ipv.is_active
                       )
               )
        FROM institution_property_values ipv
                 INNER JOIN institution_properties ip ON ipv.property_id = ip.id
                 INNER JOIN property_types pt ON ip.property_type_id = pt.id
                 INNER JOIN property_group_types pgt ON pt.property_group_type_id = pgt.id
        WHERE ipv.school_id = s.id
          AND ipv.is_active = true
    ) ::jsonb AS properties_json,

    -- Özellik gruplarına göre ayrılmış (filtreleme için)
    (
        SELECT json_object_agg(
                       pgt.name,
                       (
                           SELECT json_agg(
                                          json_build_object(
                                                  'name', ip2.name,
                                                  'display_name', ip2.display_name,
                                                  'value', CASE
                                                               WHEN ip2.data_type = 'TEXT' OR ip2.data_type = 'TEXTAREA' THEN ipv2.text_value
                                                               WHEN ip2.data_type = 'NUMBER' OR ip2.data_type = 'DECIMAL' THEN ipv2.number_value::text
                                                               WHEN ip2.data_type = 'BOOLEAN' THEN ipv2.boolean_value::text
                                                               WHEN ip2.data_type = 'DATE' THEN ipv2.date_value::text
                                                               WHEN ip2.data_type = 'DATETIME' THEN ipv2.datetime_value::text
                                                               WHEN ip2.data_type = 'SELECT' OR ip2.data_type = 'MULTISELECT' THEN ipv2.text_value
                                                               WHEN ip2.data_type = 'FILE' OR ip2.data_type = 'IMAGE' THEN ipv2.file_url
                                                               ELSE ipv2.text_value
                                                      END
                                          )
                                  )
                           FROM institution_property_values ipv2
                                    INNER JOIN institution_properties ip2 ON ipv2.property_id = ip2.id
                                    INNER JOIN property_types pt2 ON ip2.property_type_id = pt2.id
                           WHERE ipv2.school_id = s.id
                             AND pt2.property_group_type_id = pgt.id
                             AND ipv2.is_active = true
                       )
               )
        FROM property_group_types pgt
        WHERE pgt.institution_type_id = s.institution_type_id
          AND EXISTS (
            SELECT 1
            FROM institution_property_values ipv3
                     INNER JOIN institution_properties ip3 ON ipv3.property_id = ip3.id
                     INNER JOIN property_types pt3 ON ip3.property_type_id = pt3.id
            WHERE ipv3.school_id = s.id
              AND pt3.property_group_type_id = pgt.id
              AND ipv3.is_active = true
        )
    ) ::jsonb AS properties_by_group,

    -- Aranabilir özellikler metni (full-text search için)
    (
        SELECT string_agg(
                       COALESCE(ip.display_name, '') || ' ' ||
                       COALESCE(ipv.text_value, '') || ' ' ||
                       COALESCE(ipv.number_value::text, ''),
                       ' '
               )
        FROM institution_property_values ipv
                 INNER JOIN institution_properties ip ON ipv.property_id = ip.id
        WHERE ipv.school_id = s.id
          AND ipv.is_active = true
    ) AS properties_searchable_text,

    -- Özellik sayısı
    (
        SELECT COUNT(*)
        FROM institution_property_values ipv
        WHERE ipv.school_id = s.id
          AND ipv.is_active = true
    ) AS property_count,

    -- ============ ARAMA METNİ VE VECTOR ============
    to_tsvector('turkish',
                COALESCE(s.name, '') || ' ' ||
                COALESCE(c.name, '') || ' ' ||
                COALESCE(n.name, '') || ' ' ||
                COALESCE(d.name, '') || ' ' ||
                COALESCE(p.name, '') || ' ' ||
                COALESCE(b.name, '') || ' ' ||
                COALESCE(it.display_name, '') || ' ' ||
                COALESCE(s.description, '') || ' ' ||
                COALESCE((
                             SELECT string_agg(
                                            COALESCE(ip.display_name, '') || ' ' ||
                                            COALESCE(ipv.text_value, ''),
                                            ' '
                                    )
                             FROM institution_property_values ipv
                                      INNER JOIN institution_properties ip ON ipv.property_id = ip.id
                             WHERE ipv.school_id = s.id AND ipv.is_active = true
                         ), '')
    ) AS search_vector,

    -- Hızlı arama için basit metin
    COALESCE(s.name, '') || ' ' ||
    COALESCE(c.name, '') || ' ' ||
    COALESCE(b.name, '') AS quick_search_text,

    -- ============ SCORE HESAPLAMALARI ============

    (COALESCE(s.view_count, 0) * 0.1 +
     COALESCE(c.view_count, 0) * 0.05 +
     COALESCE(s.rating_average, 0) * 20 +
     COALESCE(s.like_count, 0) * 0.5) AS popularity_score,

    CASE
        WHEN s.rating_count > 50 THEN 100
        WHEN s.rating_count > 20 THEN 75
        WHEN s.rating_count > 10 THEN 50
        WHEN s.rating_count > 0 THEN 25
        ELSE 0
        END AS trust_score,

    (
        COALESCE(s.rating_average, 0) * 15 +
        COALESCE(c.rating_average, 0) * 5 +
        CASE WHEN c.is_subscribed THEN 20 ELSE 0 END +
        CASE WHEN b.id IS NOT NULL THEN 10 ELSE 0 END +
        LEAST(COALESCE(s.rating_count, 0), 30) +
        LEAST(COALESCE(s.view_count, 0) / 100, 10) +
        LEAST(COALESCE(s.post_count, 0), 10)
        ) AS quality_score,

    (COALESCE(s.post_count, 0) * 2 +
     COALESCE(s.like_count, 0)) AS activity_score

FROM
    schools s
        INNER JOIN campuses c ON s.campus_id = c.id
        INNER JOIN institution_types it ON s.institution_type_id = it.id
        LEFT JOIN neighborhoods n ON c.neighborhood_id = n.id
        LEFT JOIN districts d ON c.district_id = d.id
        LEFT JOIN provinces p ON c.province_id = p.id
        LEFT JOIN countries co ON c.country_id = co.id
        LEFT JOIN brands b ON c.brand_id = b.id

WHERE
    s.is_active = true
  AND c.is_active = true;

-- ============================================================================
-- İndeksler
-- ============================================================================

CREATE UNIQUE INDEX idx_school_search_v2_id
    ON school_search_materialized_v2(school_id);

CREATE INDEX idx_school_search_v2_vector
    ON school_search_materialized_v2 USING GIN(search_vector);

CREATE INDEX idx_school_search_v2_province
    ON school_search_materialized_v2(province_slug);

CREATE INDEX idx_school_search_v2_district
    ON school_search_materialized_v2(district_slug);

CREATE INDEX idx_school_search_v2_brand
    ON school_search_materialized_v2(brand_slug)
    WHERE brand_slug IS NOT NULL;

CREATE INDEX idx_school_search_v2_quality
    ON school_search_materialized_v2(quality_score DESC);

CREATE INDEX idx_school_search_v2_rating
    ON school_search_materialized_v2(rating_average DESC NULLS LAST);

-- JSON özellikler için GIN index (filtreleme için)
CREATE INDEX idx_school_search_v2_properties_json
    ON school_search_materialized_v2 USING GIN(properties_json);

CREATE INDEX idx_school_search_v2_properties_by_group
    ON school_search_materialized_v2 USING GIN(properties_by_group);

-- ============================================================================
-- Refresh Function
-- ============================================================================

CREATE OR REPLACE FUNCTION refresh_school_search_v2()
RETURNS void AS $$
BEGIN
    REFRESH MATERIALIZED VIEW CONCURRENTLY school_search_materialized_v2;
    RAISE NOTICE 'School search v2 materialized view refreshed at %', NOW();
END;
$$ LANGUAGE plpgsql;

-- ============================================================================
-- KULLANIM ÖRNEKLERİ
-- ============================================================================

-- 1. Tüm özellikleri göster
-- SELECT
--     school_name,
--     properties_json
-- FROM school_search_materialized_v2
-- WHERE school_id = 7;

-- 2. Belirli bir özelliğe sahip okullar (JSON query)
-- SELECT school_name, properties_json
-- FROM school_search_materialized_v2
-- WHERE properties_json @> '[{"property_name": "has_swimming_pool", "boolean_value": true}]'::jsonb;

-- 3. Özellik grubuna göre filtreleme
-- SELECT school_name, properties_by_group
-- FROM school_search_materialized_v2
-- WHERE properties_by_group ? 'FACILITIES';

-- 4. Özellik adında arama
-- SELECT school_name
-- FROM school_search_materialized_v2
-- WHERE properties_searchable_text ILIKE '%yüzme havuzu%';

-- 5. Özellikleri parse ederek kullanma
-- SELECT
--     school_name,
--     jsonb_array_elements(properties_json::jsonb)->>'property_display_name' as property,
--     jsonb_array_elements(properties_json::jsonb)->>'text_value' as value
-- FROM school_search_materialized_v2
-- WHERE school_id = 7;

COMMENT ON MATERIALIZED VIEW school_search_materialized_v2 IS
'Okul araması için geliştirilmiş view - institution properties dahil.
properties_json: Tüm özellikler detaylı JSON formatında
properties_by_group: Özellikler grup bazında organize edilmiş
properties_searchable_text: Özellikler içinde metin araması için';