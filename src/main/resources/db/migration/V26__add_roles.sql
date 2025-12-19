-- ROLE için eski constraint'i kaldır
ALTER TABLE user_roles
DROP CONSTRAINT IF EXISTS user_roles_role_check;

-- ROLE_LEVEL için eski constraint'i kaldır
ALTER TABLE user_roles
DROP CONSTRAINT IF EXISTS user_roles_role_level_check;

-- ROLE için yeni constraint
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
    'PARTICIPANT'
    ]::text[]
    )
    );

-- ROLE_LEVEL için yeni constraint
ALTER TABLE user_roles
    ADD CONSTRAINT user_roles_role_level_check
        CHECK (
            role_level::text = ANY (
    ARRAY[
    'BRAND',
    'CAMPUS',
    'SCHOOL',
    'SYSTEM',
    'INDIVIDUAL',
    'INSTITUTION'
    ]::text[]
    )
    );
