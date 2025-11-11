-- Flyway Migration: JSON alanlarını TEXT'e çevirme
-- Versiyon: V2
-- Açıklama: Tüm JSON tipindeki kolonları TEXT tipine dönüştürür

-- institution_types tablosu
ALTER TABLE institution_types
ALTER COLUMN default_properties TYPE TEXT USING default_properties::TEXT;

-- institution_properties tablosu
ALTER TABLE institution_properties
ALTER COLUMN options TYPE TEXT USING options::TEXT;

-- performance_metrics tablosu
ALTER TABLE performance_metrics
ALTER COLUMN additional_metrics TYPE TEXT USING additional_metrics::TEXT;

-- analytics tablosu
ALTER TABLE analytics
ALTER COLUMN custom_metrics TYPE TEXT USING custom_metrics::TEXT;

-- institution_property_values tablosu
ALTER TABLE institution_property_values
ALTER COLUMN json_value TYPE TEXT USING json_value::TEXT;

-- subscription_plans tablosu
ALTER TABLE subscription_plans
ALTER COLUMN features TYPE TEXT USING features::TEXT,
    ALTER COLUMN pricing_tiers TYPE TEXT USING pricing_tiers::TEXT;

-- payments tablosu
ALTER TABLE payments
ALTER COLUMN gateway_response TYPE TEXT USING gateway_response::TEXT;

-- invoices tablosu
ALTER TABLE invoices
ALTER COLUMN e_invoice_response TYPE TEXT USING e_invoice_response::TEXT,
    ALTER COLUMN line_items TYPE TEXT USING line_items::TEXT;

-- survey_questions tablosu
ALTER TABLE survey_questions
ALTER COLUMN options TYPE TEXT USING options::TEXT,
    ALTER COLUMN rating_labels TYPE TEXT USING rating_labels::TEXT;

-- appointment_slots tablosu
ALTER TABLE appointment_slots
ALTER COLUMN excluded_dates TYPE TEXT USING excluded_dates::TEXT;

-- campaigns tablosu
ALTER TABLE campaigns
ALTER COLUMN bonus_features TYPE TEXT USING bonus_features::TEXT,
    ALTER COLUMN free_services TYPE TEXT USING free_services::TEXT,
    ALTER COLUMN gift_items TYPE TEXT USING gift_items::TEXT;

-- campaign_contents tablosu
ALTER TABLE campaign_contents
ALTER COLUMN social_media_platforms TYPE TEXT USING social_media_platforms::TEXT;

-- gallery_items tablosu
ALTER TABLE gallery_items
ALTER COLUMN color_palette TYPE TEXT USING color_palette::TEXT,
    ALTER COLUMN moderation_labels TYPE TEXT USING moderation_labels::TEXT;

-- messages tablosu
ALTER TABLE messages
ALTER COLUMN attachments TYPE TEXT USING attachments::TEXT;

-- posts tablosu
ALTER TABLE posts
ALTER COLUMN media_attachments TYPE TEXT USING media_attachments::TEXT;

-- school_pricing tablosu
ALTER TABLE school_pricing
ALTER COLUMN competitor_analysis TYPE TEXT USING competitor_analysis::TEXT,
    ALTER COLUMN pricing_tiers TYPE TEXT USING pricing_tiers::TEXT;

-- custom_fees tablosu
ALTER TABLE custom_fees
ALTER COLUMN mutually_exclusive_fees TYPE TEXT USING mutually_exclusive_fees::TEXT,
    ALTER COLUMN prerequisite_fees TYPE TEXT USING prerequisite_fees::TEXT,
    ALTER COLUMN required_documents TYPE TEXT USING required_documents::TEXT;

-- search_logs tablosu
ALTER TABLE search_logs
ALTER COLUMN filters_applied TYPE TEXT USING filters_applied::TEXT;

-- survey_responses tablosu
ALTER TABLE survey_responses
ALTER COLUMN browser_info TYPE TEXT USING browser_info::TEXT,
    ALTER COLUMN device_info TYPE TEXT USING device_info::TEXT;

-- survey_question_responses tablosu
ALTER TABLE survey_question_responses
ALTER COLUMN choice_responses TYPE TEXT USING choice_responses::TEXT,
    ALTER COLUMN matrix_responses TYPE TEXT USING matrix_responses::TEXT;