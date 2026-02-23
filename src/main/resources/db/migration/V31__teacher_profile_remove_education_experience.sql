-- Teacher profile'dan basit eğitim/tecrübe alanları kaldırılıyor.
-- Detaylı eğitim ve tecrübe listeleri ayrı tablolarla eklenecek.
ALTER TABLE teacher_profiles DROP COLUMN IF EXISTS education_level;
ALTER TABLE teacher_profiles DROP COLUMN IF EXISTS experience_years;
