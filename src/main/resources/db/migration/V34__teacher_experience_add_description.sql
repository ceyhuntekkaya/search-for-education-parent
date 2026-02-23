-- Tecrübe kaydına görev tanımı / açıklama alanı
ALTER TABLE teacher_experience ADD COLUMN description TEXT;

COMMENT ON COLUMN teacher_experience.description IS 'Görev tanımı veya iş açıklaması (metin)';
