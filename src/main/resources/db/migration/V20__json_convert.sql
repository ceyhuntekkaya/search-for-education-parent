-- V{version_number}__change_default_properties_to_text.sql
-- Örnek: V1.2__change_default_properties_to_text.sql

-- institution_type_groups tablosu için değişiklik
ALTER TABLE institution_type_groups
ALTER COLUMN default_properties TYPE TEXT USING default_properties::TEXT;

-- institution_types tablosu için değişiklik
ALTER TABLE institution_types
ALTER COLUMN default_properties TYPE TEXT USING default_properties::TEXT;

