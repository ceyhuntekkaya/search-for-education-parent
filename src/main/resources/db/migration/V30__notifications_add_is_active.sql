-- Flyway Migration: notifications tablosuna is_active sütunu ekleme
-- HrNotification entity BaseEntity'den is_active kullanıyor; tabloda eksikti.

ALTER TABLE notifications
    ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE;

COMMENT ON COLUMN notifications.is_active IS 'Kayıt aktif mi (BaseEntity uyumu)';
