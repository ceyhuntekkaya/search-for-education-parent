ALTER TABLE parent_school_list_items
    ADD COLUMN created_by BIGINT,
ADD COLUMN updated_by BIGINT,
ADD COLUMN is_active BOOLEAN DEFAULT TRUE;