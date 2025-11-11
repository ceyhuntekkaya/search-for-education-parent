-- V2__add_slot_at_column_to_appointment_slots.sql

-- 1. Adım: Geçici olarak nullable ekle
ALTER TABLE appointment_slots
    ADD COLUMN slot_at TIMESTAMP WITHOUT TIME ZONE;

-- 2. Adım: Gerekirse mevcut kayıtlar için geçici değer ata (örneğin oluşturulma tarihi)
UPDATE appointment_slots
SET slot_at = created_at
WHERE slot_at IS NULL;

-- 3. Adım: Şimdi zorunlu hale getir
ALTER TABLE appointment_slots
    ALTER COLUMN slot_at SET NOT NULL;
