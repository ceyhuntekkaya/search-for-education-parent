ALTER TABLE custom_fees
    ADD COLUMN school_id BIGINT;

UPDATE custom_fees cf
SET school_id = sp.school_id
    FROM school_pricing sp
WHERE cf.school_pricing_id = sp.id;

ALTER TABLE custom_fees
    ALTER COLUMN school_id SET NOT NULL;

ALTER TABLE custom_fees
    ADD CONSTRAINT fk_custom_fees_school
        FOREIGN KEY (school_id) REFERENCES schools(id);

ALTER TABLE custom_fees DROP CONSTRAINT IF EXISTS fk_custom_fees_school_pricing;
ALTER TABLE custom_fees DROP CONSTRAINT IF EXISTS fk_custom_fees_created_by_user;

ALTER TABLE custom_fees DROP COLUMN IF EXISTS school_pricing_id;
ALTER TABLE custom_fees DROP COLUMN IF EXISTS created_by_user_id;

