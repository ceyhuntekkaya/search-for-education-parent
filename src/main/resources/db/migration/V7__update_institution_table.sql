-- Flyway migration script
-- File name: V{version_number}__add_property_type_id_to_institution_properties.sql
-- Example: V1_1__add_property_type_id_to_institution_properties.sql

-- Add property_type_id column to institution_properties table
ALTER TABLE institution_properties
    ADD COLUMN property_type_id BIGINT;

-- Add foreign key constraint (assuming property_types table exists)
ALTER TABLE institution_properties
    ADD CONSTRAINT fk_institution_properties_property_type
        FOREIGN KEY (property_type_id) REFERENCES property_types(id);

-- Create index for better query performance
CREATE INDEX idx_institution_properties_property_type_id
    ON institution_properties(property_type_id);

-- Optional: Update existing records with default property_type_id if needed
-- UPDATE institution_properties SET property_type_id = 1 WHERE property_type_id IS NULL;

-- If you want to make it NOT NULL after populating data:
-- ALTER TABLE institution_properties ALTER COLUMN property_type_id SET NOT NULL;