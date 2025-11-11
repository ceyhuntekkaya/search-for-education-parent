-- 1️⃣ user_roles tablosuna subscription_id kolonunu ekle
ALTER TABLE user_roles
    ADD COLUMN subscription_id BIGINT;

-- 2️⃣ subscription ile foreign key ilişkisi kur
ALTER TABLE user_roles
    ADD CONSTRAINT fk_user_roles_subscription
        FOREIGN KEY (subscription_id)
            REFERENCES subscriptions (id)
            ON DELETE SET NULL;

-- 3️⃣ user_roles ile schools arasında ManyToMany ilişkisi için join tablosu oluştur
CREATE TABLE user_roles_schools (
                                    user_role_id BIGINT NOT NULL,
                                    school_id BIGINT NOT NULL,
                                    PRIMARY KEY (user_role_id, school_id),
                                    CONSTRAINT fk_user_roles_schools_user_role
                                        FOREIGN KEY (user_role_id)
                                            REFERENCES user_roles (id)
                                            ON DELETE CASCADE,
                                    CONSTRAINT fk_user_roles_schools_school
                                        FOREIGN KEY (school_id)
                                            REFERENCES schools (id)
                                            ON DELETE CASCADE
);

-- 4️⃣ (Opsiyonel) Performans için index ekle
CREATE INDEX idx_user_roles_subscription_id ON user_roles (subscription_id);
CREATE INDEX idx_user_roles_schools_user_role_id ON user_roles_schools (user_role_id);
CREATE INDEX idx_user_roles_schools_school_id ON user_roles_schools (school_id);
