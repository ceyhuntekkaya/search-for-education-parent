-- ========================================================================================
-- Flyway Migration: Create School Search Hybrid Materialized View
-- Version: V1__create_school_search_hybrid_view.sql
-- ========================================================================================

-- pg_trgm extension (trigram similarity search için)
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- ========================================================================================
-- MATERIALIZED VIEW
-- ========================================================================================

CREATE MATERIALIZED VIEW IF NOT EXISTS school_search_hybrid AS
SELECT
    -- ==================== SCHOOL BASIC ====================
    s.id AS school_id,
    s.name AS school_name,
    s.slug AS school_slug,
    s.description AS school_description,
    s.logo_url AS school_logo_url,
    s.min_age,
    s.max_age,
    s.capacity,
    s.current_student_count,
    s.class_size_average,
    s.curriculum_type,
    s.language_of_instruction,
    s.foreign_languages,
    s.view_count,
    s.rating_average,
    s.rating_count,
    s.like_count,
    s.created_at,
    s.updated_at,
    s.is_active,

    -- ==================== CAMPUS ====================
    c.id AS campus_id,
    c.name AS campus_name,
    c.slug AS campus_slug,
    c.email AS campus_email,
    c.phone AS campus_phone,
    c.website_url AS campus_website_url,
    c.latitude AS campus_latitude,
    c.longitude AS campus_longitude,
    c.established_year,
    c.is_subscribed AS campus_is_subscribed,

    -- ==================== BRAND ====================
    b.id AS brand_id,
    b.name AS brand_name,
    b.slug AS brand_slug,

    -- ==================== INSTITUTION TYPE ====================
    it.id AS institution_type_id,
    it.name AS institution_type_name,
    it.display_name AS institution_type_display_name,

    itg.id AS institution_type_group_id,
    itg.name AS institution_type_group_name,
    itg.display_name AS institution_type_group_display_name,

    -- ==================== LOCATION ====================
    co.id AS country_id,
    co.name AS country_name,

    p.id AS province_id,
    p.name AS province_name,
    p.code AS province_code,

    d.id AS district_id,
    d.name AS district_name,
    d.district_type,

    n.id AS neighborhood_id,
    n.name AS neighborhood_name,
    n.neighborhood_type,

    -- ==================== PRICING ====================
    (
        SELECT sp.monthly_tuition
        FROM school_pricing sp
        WHERE sp.school_id = s.id
          AND sp.is_active = true
        ORDER BY sp.is_current DESC, sp.valid_from DESC
        LIMIT 1
    ) AS current_monthly_tuition,

    (
        SELECT sp.annual_tuition
        FROM school_pricing sp
        WHERE sp.school_id = s.id
          AND sp.is_active = true
        ORDER BY sp.is_current DESC, sp.valid_from DESC
        LIMIT 1
    ) AS current_annual_tuition,

    (
        SELECT sp.registration_fee
        FROM school_pricing sp
        WHERE sp.school_id = s.id
          AND sp.is_active = true
        ORDER BY sp.is_current DESC, sp.valid_from DESC
        LIMIT 1
    ) AS current_registration_fee,

    (
        SELECT sp.currency
        FROM school_pricing sp
        WHERE sp.school_id = s.id
          AND sp.is_active = true
        ORDER BY sp.is_current DESC, sp.valid_from DESC
        LIMIT 1
    ) AS pricing_currency,

    -- ==================== PROPERTIES (DYNAMIC - JSONB) ====================
    (
        SELECT JSONB_AGG(
            JSONB_BUILD_OBJECT(
                'property_id', ip.id,
                'name', ip.name,
                'display_name', ip.display_name,
                'data_type', ip.data_type,
                'text_value', ipv.text_value,
                'number_value', ipv.number_value,
                'boolean_value', ipv.boolean_value,
                'date_value', ipv.date_value,
                'sort_order', ip.sort_order,
                'is_searchable', ip.is_searchable,
                'is_filterable', ip.is_filterable,
                'show_in_card', ip.show_in_card
            ) ORDER BY ip.sort_order
        )
        FROM institution_property_values ipv
        INNER JOIN institution_properties ip ON ipv.property_id = ip.id
        WHERE ipv.school_id = s.id
          AND ipv.is_active = true
    ) AS properties,

    (
        SELECT STRING_AGG(DISTINCT ip.display_name, ', ' ORDER BY ip.display_name)
        FROM institution_property_values ipv
        INNER JOIN institution_properties ip ON ipv.property_id = ip.id
        WHERE ipv.school_id = s.id
          AND ipv.is_active = true
    ) AS property_names_text,

    (
        SELECT COUNT(*)
        FROM institution_property_values ipv
        WHERE ipv.school_id = s.id
          AND ipv.is_active = true
    ) AS property_count,

    -- ==================== CAMPAIGN ====================
    EXISTS(
        SELECT 1 FROM campaign_schools cs
        INNER JOIN campaigns cam ON cs.campaign_id = cam.id
        WHERE cs.school_id = s.id
          AND cam.status = 'ACTIVE'
          AND cam.start_date <= CURRENT_DATE
          AND cam.end_date >= CURRENT_DATE
    ) AS has_active_campaign,

    -- ==================== JSON DETAILS ====================
    JSON_BUILD_OBJECT(
        'address_line1', c.address_line1,
        'address_line2', c.address_line2,
        'postal_code', c.postal_code,
        'fax', c.fax,
        'facebook_url', c.facebook_url,
        'twitter_url', c.twitter_url,
        'instagram_url', c.instagram_url,
        'rating_average', c.rating_average,
        'rating_count', c.rating_count
    ) AS campus_details,

    CASE WHEN b.id IS NOT NULL THEN
        JSON_BUILD_OBJECT(
            'description', b.description,
            'logo_url', b.logo_url,
            'website_url', b.website_url,
            'founded_year', b.founded_year,
            'rating_average', b.rating_average
        )
    ELSE NULL END AS brand_details,

    JSON_BUILD_OBJECT(
        'population', d.population,
        'safety_index', d.safety_index,
        'education_quality_index', d.education_quality_index,
        'public_transport_score', d.public_transport_score,
        'school_count', d.school_count,
        'average_income', d.average_income,
        'socioeconomic_level', d.socioeconomic_level
    ) AS district_stats,

    CASE WHEN n.id IS NOT NULL THEN
        JSON_BUILD_OBJECT(
            'population', n.population,
            'safety_score', n.safety_score,
            'school_quality_index', n.school_quality_index,
            'family_friendliness_score', n.family_friendliness_score,
            'income_level', n.income_level
        )
    ELSE NULL END AS neighborhood_stats,

    (
        SELECT JSON_AGG(
            JSON_BUILD_OBJECT(
                'pricing_id', sp.id,
                'academic_year', sp.academic_year,
                'grade_level', sp.grade_level,
                'monthly_tuition', sp.monthly_tuition,
                'annual_tuition', sp.annual_tuition,
                'registration_fee', sp.registration_fee,
                'is_current', sp.is_current,
                'status', sp.status,
                'sibling_discount_percentage', sp.sibling_discount_percentage
            ) ORDER BY sp.is_current DESC, sp.valid_from DESC
        )
        FROM school_pricing sp
        WHERE sp.school_id = s.id
          AND sp.is_active = true
    ) AS pricings,

    (
        SELECT JSON_AGG(
            JSON_BUILD_OBJECT(
                'campaign_id', cam.id,
                'title', cam.title,
                'short_description', cam.short_description,
                'discount_type', cam.discount_type,
                'discount_percentage', cam.discount_percentage,
                'start_date', cam.start_date,
                'end_date', cam.end_date,
                'badge_text', cam.badge_text
            ) ORDER BY cs.priority DESC
        )
        FROM campaign_schools cs
        INNER JOIN campaigns cam ON cs.campaign_id = cam.id
        WHERE cs.school_id = s.id
          AND cam.status = 'ACTIVE'
          AND cam.start_date <= CURRENT_DATE
          AND cam.end_date >= CURRENT_DATE
    ) AS campaigns,

    (
        SELECT JSON_AGG(
            JSON_BUILD_OBJECT(
                'fee_id', cf.id,
                'fee_name', cf.fee_name,
                'fee_amount', cf.fee_amount,
                'fee_type', cf.fee_type,
                'is_mandatory', cf.is_mandatory
            ) ORDER BY cf.display_order
        )
        FROM custom_fees cf
        WHERE cf.school_id = s.id
          AND cf.is_active = true
    ) AS custom_fees,

    -- ==================== SEARCH TEXT ====================
    CONCAT_WS(' ',
        s.name,
        s.description,
        s.curriculum_type,
        c.name,
        b.name,
        d.name,
        p.name,
        (
            SELECT STRING_AGG(ip.display_name, ' ')
            FROM institution_property_values ipv
            INNER JOIN institution_properties ip ON ipv.property_id = ip.id
            WHERE ipv.school_id = s.id AND ipv.is_active = true
        )
    ) AS search_text

FROM schools s
LEFT JOIN campuses c ON s.campus_id = c.id
LEFT JOIN brands b ON c.brand_id = b.id
LEFT JOIN institution_types it ON s.institution_type_id = it.id
LEFT JOIN institution_type_groups itg ON it.institution_type_group_id = itg.id
LEFT JOIN districts d ON c.district_id = d.id
LEFT JOIN provinces p ON d.province_id = p.id
LEFT JOIN countries co ON c.country_id = co.id
LEFT JOIN neighborhoods n ON c.neighborhood_id = n.id

WHERE s.is_active = true;

-- ========================================================================================
-- INDEXES
-- ========================================================================================

CREATE UNIQUE INDEX IF NOT EXISTS idx_school_search_hybrid_id ON school_search_hybrid(school_id);
CREATE INDEX IF NOT EXISTS idx_school_search_hybrid_province ON school_search_hybrid(province_id);
CREATE INDEX IF NOT EXISTS idx_school_search_hybrid_district ON school_search_hybrid(district_id);
CREATE INDEX IF NOT EXISTS idx_school_search_hybrid_province_name ON school_search_hybrid(province_name);
CREATE INDEX IF NOT EXISTS idx_school_search_hybrid_district_name ON school_search_hybrid(district_name);
CREATE INDEX IF NOT EXISTS idx_school_search_hybrid_inst_type ON school_search_hybrid(institution_type_id);
CREATE INDEX IF NOT EXISTS idx_school_search_hybrid_monthly_tuition ON school_search_hybrid(current_monthly_tuition);
CREATE INDEX IF NOT EXISTS idx_school_search_hybrid_annual_tuition ON school_search_hybrid(current_annual_tuition);
CREATE INDEX IF NOT EXISTS idx_school_search_hybrid_rating ON school_search_hybrid(rating_average DESC);
CREATE INDEX IF NOT EXISTS idx_school_search_hybrid_name ON school_search_hybrid(school_name);
CREATE INDEX IF NOT EXISTS idx_school_search_hybrid_campaign ON school_search_hybrid(has_active_campaign) WHERE has_active_campaign = true;
CREATE INDEX IF NOT EXISTS idx_school_search_hybrid_fulltext ON school_search_hybrid USING gin(to_tsvector('turkish', search_text));
CREATE INDEX IF NOT EXISTS idx_school_search_hybrid_name_trgm ON school_search_hybrid USING gin(school_name gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_school_search_hybrid_properties ON school_search_hybrid USING gin(properties jsonb_path_ops);
CREATE INDEX IF NOT EXISTS idx_school_search_hybrid_prov_price ON school_search_hybrid(province_id, current_monthly_tuition);
CREATE INDEX IF NOT EXISTS idx_school_search_hybrid_dist_price ON school_search_hybrid(district_id, current_monthly_tuition);

-- ========================================================================================
-- HELPER FUNCTION
-- ========================================================================================

CREATE OR REPLACE FUNCTION refresh_school_search_hybrid()
RETURNS void AS $$
BEGIN
    REFRESH MATERIALIZED VIEW CONCURRENTLY school_search_hybrid;
    RAISE NOTICE 'School search hybrid materialized view refreshed at %', NOW();
END;
$$ LANGUAGE plpgsql;

-- ========================================================================================
-- STATS VIEW
-- ========================================================================================

CREATE OR REPLACE VIEW school_search_stats AS
SELECT
    COUNT(*) as total_schools,
    COUNT(*) FILTER (WHERE has_active_campaign = true) as schools_with_campaigns,
    COUNT(*) FILTER (WHERE current_monthly_tuition IS NOT NULL) as schools_with_pricing,
    AVG(current_monthly_tuition) as avg_monthly_tuition,
    MIN(current_monthly_tuition) as min_monthly_tuition,
    MAX(current_monthly_tuition) as max_monthly_tuition,
    AVG(property_count) as avg_properties_per_school,
    COUNT(DISTINCT province_id) as total_provinces,
    COUNT(DISTINCT district_id) as total_districts
FROM school_search_hybrid;

-- ========================================================================================
-- COMMENT
-- ========================================================================================

COMMENT ON MATERIALIZED VIEW school_search_hybrid IS
'School search hybrid materialized view - Fully dynamic with JSONB properties.
Optimized for search and filtering operations.
Refresh with: SELECT refresh_school_search_hybrid();';