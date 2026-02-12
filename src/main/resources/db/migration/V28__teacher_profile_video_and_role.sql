-- Flyway Migration: TeacherProfile video_url ve TEACHER rolü

-- 1. teacher_profiles tablosuna video_url kolonu ekle
ALTER TABLE teacher_profiles ADD COLUMN IF NOT EXISTS video_url VARCHAR(500);

-- 2. user_roles tablosuna TEACHER rolünü ekle (constraint güncellemesi)
ALTER TABLE user_roles DROP CONSTRAINT IF EXISTS user_roles_role_check;

ALTER TABLE user_roles
    ADD CONSTRAINT user_roles_role_check
        CHECK (
            role::text = ANY (
                ARRAY[
                    'USER',
                    'ADMIN',
                    'CANDIDATE',
                    'COMPANY',
                    'SUPPLY',
                    'INSTRUCTOR',
                    'PARTICIPANT',
                    'TEACHER'
                ]::text[]
            )
        );
