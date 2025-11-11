-- 1. Yeni sütunu ekle
ALTER TABLE property_group_types
    ADD COLUMN is_multiple BOOLEAN DEFAULT TRUE;

-- 2. Mevcut tüm kayıtları true yap
UPDATE property_group_types
SET is_multiple = TRUE;

-- 3. (Opsiyonel) Eğer bundan sonra null olmasını istemiyorsan:
ALTER TABLE property_group_types
    ALTER COLUMN is_multiple SET NOT NULL;
