-- V2__Insert_Demo_Location_And_User_Data.sql
-- eğitimara.com için kapsamlı demo verileri
-- Coğrafi hiyerarşi: countries > provinces > districts > neighborhoods > users

-- ======= COUNTRIES =======
INSERT INTO countries (
    currency_code, is_active, is_supported, iso_code_2, iso_code_3,
    latitude, longitude, sort_order, created_at, created_by, updated_at, updated_by,
    currency_symbol, flag_emoji, name, name_en, phone_code, timezone
) VALUES
    (
        'TRY', true, true, 'TR', 'TUR',
        39.9334, 32.8597, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1,
        '₺', '🇹🇷', 'Türkiye', 'Turkey', '+90', 'Europe/Istanbul'
    );

-- ======= PROVINCES =======
INSERT INTO provinces (
    area_km2, education_index, elevation_m, gdp_per_capita, has_airport, has_metro, has_university,
    is_active, is_metropolitan, latitude, literacy_rate, longitude, sort_order, unemployment_rate,
    country_id, created_at, created_by, updated_at, updated_by,
    population, school_count, student_count, teacher_count,
    area_code, code, description, meta_description, meta_title, name, name_en,
    plate_code, postal_code_prefix, region, slug, time_zone, traffic_density
) VALUES
-- İstanbul
(
    5343.0, 0.85, 100, 28500.0, true, true, true,
    true, true, 41.0082, 0.98, 28.9784, 1, 0.11,
    1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1,
    15840900, 8650, 2450000, 145000,
    '212', '34', 'Türkiye''nin en büyük şehri ve ekonomi merkezi',
    'İstanbul ili hakkında detaylı bilgiler', 'İstanbul İli',
    'İstanbul', 'Istanbul', '34', '34', 'Marmara', 'istanbul', 'Europe/Istanbul', 'ÇOKAĞIR'
),
-- Ankara
(
    25437.0, 0.88, 938, 22000.0, true, true, true,
    true, true, 39.9334, 0.97, 32.8597, 2, 0.09,
    1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1,
    5663322, 4200, 1150000, 68000,
    '312', '06', 'Türkiye''nin başkenti',
    'Ankara ili hakkında detaylı bilgiler', 'Ankara İli',
    'Ankara', 'Ankara', '06', '06', 'İç Anadolu', 'ankara', 'Europe/Istanbul', 'AĞIR'
),
-- İzmir
(
    11973.0, 0.82, 120, 20500.0, true, true, true,
    true, true, 38.4192, 0.96, 27.1287, 3, 0.12,
    1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1,
    4394694, 3850, 980000, 58000,
    '232', '35', 'Ege Bölgesi''nin incisi',
    'İzmir ili hakkında detaylı bilgiler', 'İzmir İli',
    'İzmir', 'Izmir', '35', '35', 'Ege', 'izmir', 'Europe/Istanbul', 'ORTA'
);

-- ======= DISTRICTS =======
INSERT INTO districts (
    air_quality_index, annual_rainfall_mm, area_km2, average_family_size, average_income, average_temperature,
    birth_rate, cost_of_living_index, cultural_center_count, density_per_km2, distance_to_airport_km,
    distance_to_city_center_km, education_quality_index, elderly_population_percentage, elevation_m,
    has_bus_terminal, has_metro_station, has_train_station, hospital_count, humidity_percentage,
    is_active, is_central, is_coastal, latitude, literacy_rate, longitude, park_count,
    property_price_index, public_transport_score, safety_index, shopping_mall_count, sort_order,
    sports_facility_count, youth_population_percentage, created_at, created_by, updated_at, updated_by,
    population, private_school_count, province_id, public_school_count, school_count, university_count,
    climate_type, code, description, district_type, meta_description, meta_title, name, name_en,
    noise_level, postal_code, slug, socioeconomic_level, traffic_congestion_level
) VALUES
-- İstanbul - Şişli
(
    65.0, 850.0, 35.2, 3.2, 18500.0, 15.5,
    0.012, 1.35, 25, 22500.0, 45.0,
    8.0, 0.88, 0.18, 180,
    true, true, false, 15, 0.72,
    true, true, false, 41.0600, 0.99, 28.9869, 18,
    1.45, 9, 0.82, 8, 1,
    12, 0.28, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1,
    267000, 85, 1, 145, 230, 3,
    'Akdeniz', '34367', 'İstanbul''un merkezi ilçelerinden biri', 'MERKEZ',
    'Şişli ilçesi hakkında bilgiler', 'Şişli İlçesi', 'Şişli', 'Sisli',
    'YÜKSEK', '34367', 'sisli', 'UPPER_MIDDLE', 'AĞIR'
),
-- İstanbul - Kadıköy
(
    58.0, 920.0, 25.1, 2.8, 22000.0, 16.2,
    0.010, 1.25, 35, 18200.0, 35.0,
    12.0, 0.92, 0.22, 25,
    true, true, true, 22, 0.68,
    true, false, true, 40.9833, 0.98, 29.0333, 28,
    1.65, 10, 0.88, 12, 2,
    18, 0.32, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1,
    467000, 120, 1, 185, 305, 5,
    'Akdeniz', '34710', 'İstanbul''un kültür merkezi', 'MERKEZ',
    'Kadıköy ilçesi hakkında bilgiler', 'Kadıköy İlçesi', 'Kadıköy', 'Kadikoy',
    'ORTA', '34710', 'kadikoy', 'HIGH', 'ORTA'
),
-- Ankara - Çankaya
(
    72.0, 420.0, 482.0, 3.1, 19500.0, 12.8,
    0.011, 1.15, 28, 1850.0, 25.0,
    0.0, 0.91, 0.20, 850,
    true, true, false, 18, 0.58,
    true, true, false, 39.9208, 0.98, 32.8541, 35,
    1.25, 8, 0.91, 6, 3,
    22, 0.35, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1,
    945000, 180, 2, 285, 465, 8,
    'Karasal', '06100', 'Ankara''nın merkez ilçesi', 'MERKEZ',
    'Çankaya ilçesi hakkında bilgiler', 'Çankaya İlçesi', 'Çankaya', 'Cankaya',
    'ORTA', '06100', 'cankaya', 'HIGH', 'ORTA'
),
-- İzmir - Konak
(
    68.0, 720.0, 28.5, 2.9, 16800.0, 18.5,
    0.009, 1.08, 22, 12500.0, 18.0,
    0.0, 0.85, 0.25, 2,
    true, true, true, 12, 0.65,
    true, true, true, 38.4237, 0.97, 27.1428, 15,
    1.15, 7, 0.85, 4, 4,
    8, 0.30, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1,
    365000, 95, 3, 125, 220, 2,
    'Akdeniz', '35220', 'İzmir''in merkez ilçesi', 'MERKEZ',
    'Konak ilçesi hakkında bilgiler', 'Konak İlçesi', 'Konak', 'Konak',
    'YÜKSEK', '35220', 'konak', 'MIDDLE', 'AĞIR'
);

-- ======= NEIGHBORHOODS =======
INSERT INTO neighborhoods (
    air_quality_score, area_km2, average_age, average_property_price, average_rent_price,
    bank_count, bus_accessibility_minutes, cafe_count, cleanliness_score, commute_to_business_districts_minutes,
    density_per_km2, education_accessibility_score, elderly_percentage, elevation_m, family_friendliness_score,
    family_with_children_percentage, green_space_percentage, has_cultural_center, has_hospital, has_kindergarten,
    has_library, has_metro_station, has_park, has_shopping_center, has_sports_facility, highway_accessibility_minutes,
    investment_attractiveness, is_active, is_commercial_center, is_gated_community, is_historical, is_industrial,
    is_residential, latitude, longitude, main_road_accessibility_minutes, metro_accessibility_minutes,
    pharmacy_count, property_price_per_m2, public_transport_frequency, restaurant_count, safety_score,
    school_preference_score, school_quality_index, social_life_score, sort_order, student_percentage,
    supermarket_count, walkability_score, young_professional_percentage, created_at, created_by, updated_at, updated_by,
    population, preschool_count, private_school_count, public_school_count, school_count, district_id,
    code, description, development_level, development_potential, housing_type, income_level, name, name_en,
    neighborhood_type, noise_level, parking_availability, postal_code, property_demand_level, slug
) VALUES
-- Şişli - Maslak
(
    60, 2.8, 38.5, 2850000.0, 8500.0,
    12, 5, 45, 85, 15,
    18500.0, 95, 0.15, 180, 80,
    0.25, 0.15, true, true, true,
    true, true, true, true, true, 8,
    95, true, true, false, false, false,
    true, 41.1069, 28.9958, 2, 2,
    8, 18500.0, 3, 85, 88,
    92, 0.88, 90, 1, 0.35,
    15, 92, 0.42, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1,
    52000, 8, 15, 12, 27, 1,
    'MSL001', 'İstanbul''un iş merkezi', 'MODERN', 'Yüksek gelişim potansiyeli',
    'MIXED', 'HIGH', 'Maslak', 'Maslak', 'MAHALLE',
    'YÜKSEK', 'SINIRLI', '34485', 'YÜKSEK', 'maslak'
),
-- Kadıköy - Moda
(
    72, 1.2, 42.0, 3200000.0, 12000.0,
    8, 8, 65, 90, 25,
    22000.0, 88, 0.28, 25, 85,
    0.30, 0.25, true, false, true,
    true, true, true, false, true, 15,
    88, true, false, false, true, false,
    true, 40.9833, 29.0333, 3, 8,
    6, 22000.0, 5, 125, 92,
    88, 0.85, 95, 2, 0.28,
    8, 95, 0.45, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1,
    28000, 5, 8, 6, 14, 2,
    'MOD001', 'Tarihi ve kültürel merkez', 'MATURE', 'Koruma altında',
    'APARTMENT', 'VERY_HIGH', 'Moda', 'Moda', 'SEMT',
    'ORTA', 'ZAYIF', '34742', 'YÜKSEK', 'moda'
),
-- Çankaya - Kızılay
(
    75, 3.5, 35.2, 1850000.0, 6500.0,
    15, 3, 85, 82, 10,
    15000.0, 92, 0.18, 850, 78,
    0.22, 0.12, true, true, true,
    true, true, true, true, true, 5,
    85, true, true, false, false, false,
    true, 39.9208, 32.8541, 1, 3,
    12, 12500.0, 2, 158, 85,
    90, 0.90, 92, 3, 0.38,
    22, 88, 0.48, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1,
    48000, 12, 18, 15, 33, 3,
    'KZL001', 'Ankara''nın kalbi', 'DEVELOPED', 'Yenileme projeleri',
    'MIXED', 'UPPER_MIDDLE', 'Kızılay', 'Kizilay', 'MERKEZ',
    'YÜKSEK', 'ORTA', '06420', 'YÜKSEK', 'kizilay'
),
-- Konak - Alsancak
(
    70, 2.1, 40.8, 2100000.0, 7500.0,
    10, 6, 55, 88, 20,
    16500.0, 85, 0.25, 2, 82,
    0.28, 0.18, true, true, true,
    true, false, true, true, true, 12,
    82, true, true, false, true, false,
    true, 38.4369, 27.1476, 2, 15,
    9, 15500.0, 8, 95, 88,
    85, 0.82, 88, 4, 0.32,
    12, 90, 0.40, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1,
    35000, 6, 10, 8, 18, 4,
    'ALS001', 'İzmir''in merkezi', 'MATURE', 'Kentsel dönüşüm',
    'APARTMENT', 'HIGH', 'Alsancak', 'Alsancak', 'SEMT',
    'ORTA', 'ORTA', '35220', 'YÜKSEK', 'alsancak'
);

-- ======= USERS =======
INSERT INTO users (
    is_active, is_email_verified, is_phone_verified, latitude, longitude,
    country_id, created_at, created_by, district_id, last_login_at,
    neighborhood_id, password_reset_expires_at, province_id, updated_at, updated_by,
    address_line1, address_line2, email, email_verification_token,
    first_name, last_name, password, password_reset_token,
    phone, phone_verification_code, postal_code, profile_image_url, user_type
) VALUES
-- Admin Kullanıcı (INSTITUTION_USER) - Maslak, İstanbul
(
    true, true, true, 41.1069, 28.9958,
    1, CURRENT_TIMESTAMP, 1, 1, CURRENT_TIMESTAMP,
    1, null, 1, CURRENT_TIMESTAMP, 1,
    'Maslak Mahallesi, Büyükdere Caddesi No:171', 'A Blok Kat:15',
    'admin@egitimara.com', null,
    'Ahmet', 'Yılmaz', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhka',
    null, '+905551234567', null, '34485',
    'https://egitimara.com/images/profiles/admin.jpg', 'INSTITUTION_USER'
),
-- Şirket Kullanıcısı (INSTITUTION_USER) - Moda, İstanbul
(
    true, true, true, 40.9833, 29.0333,
    1, CURRENT_TIMESTAMP, 1, 2, CURRENT_TIMESTAMP,
    2, null, 1, CURRENT_TIMESTAMP, 1,
    'Moda Mahallesi, Bahariye Caddesi No:25', 'B Blok Kat:8',
    'sirket@egitimara.com', null,
    'Zeynep', 'Kaya', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhka',
    null, '+905551234568', null, '34742',
    'https://egitimara.com/images/profiles/company.jpg', 'INSTITUTION_USER'
),
-- Aday Kullanıcısı (PARENT) - Kızılay, Ankara
(
    true, true, true, 39.9208, 32.8541,
    1, CURRENT_TIMESTAMP, 1, 3, CURRENT_TIMESTAMP,
    3, null, 2, CURRENT_TIMESTAMP, 1,
    'Kızılay Mahallesi, Atatürk Bulvarı No:45', 'Daire:12',
    'aday@egitimara.com', null,
    'Mehmet', 'Demir', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhka',
    null, '+905551234569', null, '06420',
    'https://egitimara.com/images/profiles/candidate.jpg', 'PARENT'
),
-- Normal Kullanıcı (PARENT) - Alsancak, İzmir
(
    true, true, true, 38.4369, 27.1476,
    1, CURRENT_TIMESTAMP, 1, 4, CURRENT_TIMESTAMP,
    4, null, 3, CURRENT_TIMESTAMP, 1,
    'Alsancak Mahallesi, Kıbrıs Şehitleri Caddesi No:80', null,
    'user@egitimara.com', null,
    'Ayşe', 'Özkan', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhka',
    null, '+905551234570', null, '35220',
    'https://egitimara.com/images/profiles/user.jpg', 'PARENT'
);

-- ======= USER ROLES =======
INSERT INTO user_roles (
    is_active, created_at, created_by, expires_at, updated_at, updated_by,
    user_id, role, role_level
) VALUES
-- Admin Rolü - Sistem seviyesi
(
    true, CURRENT_TIMESTAMP, 1, null, CURRENT_TIMESTAMP, 1,
    1, 'ADMIN', 'SYSTEM'
),
-- Şirket Rolü - Marka seviyesi
(
    true, CURRENT_TIMESTAMP, 1, null, CURRENT_TIMESTAMP, 1,
    2, 'COMPANY', 'BRAND'
),
-- Aday Rolü - Okul seviyesi
(
    true, CURRENT_TIMESTAMP, 1, null, CURRENT_TIMESTAMP, 1,
    3, 'CANDIDATE', 'SCHOOL'
),
-- Normal Kullanıcı Rolü - Kampüs seviyesi
(
    true, CURRENT_TIMESTAMP, 1, null, CURRENT_TIMESTAMP, 1,
    4, 'USER', 'CAMPUS'
);

-- ======= USER ROLE PERMISSIONS =======
-- Admin kullanıcısına tüm yetkiler
INSERT INTO user_role_permissions (user_role_id, permissions) VALUES
                                                                  (1, 'APPROVAL'),
                                                                  (1, 'USER_CREATE'),
                                                                  (1, 'GENERAL'),
                                                                  (1, 'FINANCE_OPERATION'),
                                                                  (1, 'ACCOUNTING_OPERATION'),
                                                                  (1, 'DELIVERY_OPERATION'),
                                                                  (1, 'CUSTOMER_OPERATION'),
                                                                  (1, 'OFFER_OPERATION'),
                                                                  (1, 'ORDER_OPERATION'),
                                                                  (1, 'SUPPLIER_OPERATION'),
                                                                  (1, 'TRANSPORTATION_OPERATION'),
                                                                  (1, 'DELIVERY_DOCUMENT'),
                                                                  (1, 'SETTING');

-- Şirket kullanıcısına operasyon yetkilerini ver
INSERT INTO user_role_permissions (user_role_id, permissions) VALUES
                                                                  (2, 'GENERAL'),
                                                                  (2, 'CUSTOMER_OPERATION'),
                                                                  (2, 'OFFER_OPERATION'),
                                                                  (2, 'ORDER_OPERATION'),
                                                                  (2, 'DELIVERY_OPERATION'),
                                                                  (2, 'FINANCE_OPERATION'),
                                                                  (2, 'ACCOUNTING_OPERATION');

-- Aday kullanıcısına müşteri yetkilerini ver
INSERT INTO user_role_permissions (user_role_id, permissions) VALUES
                                                                  (3, 'GENERAL'),
                                                                  (3, 'CUSTOMER_OPERATION'),
                                                                  (3, 'ORDER_OPERATION');

-- Normal kullanıcıya temel yetkiyi ver
INSERT INTO user_role_permissions (user_role_id, permissions) VALUES
    (4, 'GENERAL');

-- ======= USER ROLE DEPARTMENTS =======
-- Admin kullanıcısına tüm departmanlar
INSERT INTO user_role_departments (user_role_id, departments) VALUES
                                                                  (1, 'AUTHOR'),
                                                                  (1, 'GRADER'),
                                                                  (1, 'SUPERVISOR'),
                                                                  (1, 'MANAGEMENT'),
                                                                  (1, 'IT'),
                                                                  (1, 'AUTHOR_REVIEWER'),
                                                                  (1, 'ADMIN'),
                                                                  (1, 'REVIEWER');

-- Şirket kullanıcısına yönetim departmanlarını ata
INSERT INTO user_role_departments (user_role_id, departments) VALUES
                                                                  (2, 'MANAGEMENT'),
                                                                  (2, 'SUPERVISOR'),
                                                                  (2, 'REVIEWER'),
                                                                  (2, 'AUTHOR_REVIEWER');

-- Aday kullanıcısına içerik departmanlarını ata
INSERT INTO user_role_departments (user_role_id, departments) VALUES
                                                                  (3, 'AUTHOR'),
                                                                  (3, 'GRADER'),
                                                                  (3, 'REVIEWER');

-- Normal kullanıcıya temel departmanı ata
INSERT INTO user_role_departments (user_role_id, departments) VALUES
    (4, 'AUTHOR');

-- ======= BRANDS =======
INSERT INTO brands (
    founded_year, is_active, rating_average, created_at, created_by, updated_at, updated_by,
    rating_count, view_count, cover_image_url, description, email, facebook_url, instagram_url,
    linkedin_url, logo_url, meta_description, meta_keywords, meta_title, name, phone,
    slug, twitter_url, website_url, youtube_url
) VALUES
-- Eğitim Dünyası Koleji
(
    1995, true, 4.6, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1,
    2847, 45682, 'https://egitimara.com/images/brands/egitim-dunyasi-cover.jpg',
    'Eğitim Dünyası Koleji, 1995 yılından bu yana kaliteli eğitim anlayışı ile öğrencilerini geleceğe hazırlamaktadır. Okul öncesinden liseye kadar tüm kademelerde modern eğitim yaklaşımları ile hizmet vermektedir.',
    'info@egitimdunyasi.edu.tr', 'https://facebook.com/egitimdunyasikolejleri',
    'https://instagram.com/egitimdunyasikolej', 'https://linkedin.com/company/egitim-dunyasi',
    'https://egitimara.com/images/brands/egitim-dunyasi-logo.jpg',
    'Eğitim Dünyası Koleji - Kaliteli eğitimin adresi', 'eğitim, okul, kolej, özel okul',
    'Eğitim Dünyası Koleji', 'Eğitim Dünyası Koleji', '+902121234567',
    'egitim-dunyasi-koleji', 'https://twitter.com/egitimdunyasi',
    'https://www.egitimdunyasi.edu.tr', 'https://youtube.com/egitimdunyasikolej'
),
-- Bilim Sanat Eğitim Kurumları
(
    2001, true, 4.8, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1,
    1956, 38291, 'https://egitimara.com/images/brands/bilim-sanat-cover.jpg',
    'Bilim Sanat Eğitim Kurumları, bilim ve sanatı harmanlayan eğitim anlayışı ile öğrencilerinin yaratıcılığını ve analitik düşünce becerilerini geliştirmektedir. 2001 yılından bu yana sektöre öncülük etmektedir.',
    'iletisim@bilimsanat.edu.tr', 'https://facebook.com/bilimsanateğitim',
    'https://instagram.com/bilimsanatokullari', 'https://linkedin.com/company/bilim-sanat-egitim',
    'https://egitimara.com/images/brands/bilim-sanat-logo.jpg',
    'Bilim Sanat Eğitim Kurumları - Bilim ve sanatın buluştuğu nokta', 'bilim, sanat, eğitim, özel okul',
    'Bilim Sanat Eğitim Kurumları', 'Bilim Sanat Eğitim Kurumları', '+903121567890',
    'bilim-sanat-egitim-kurumlari', 'https://twitter.com/bilimsanategitim',
    'https://www.bilimsanat.edu.tr', 'https://youtube.com/bilimsanategitim'
),
-- Gelişim Koleji
(
    1988, true, 4.4, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1,
    3124, 52847, 'https://egitimara.com/images/brands/gelisim-cover.jpg',
    'Gelişim Koleji, 35 yıllık deneyimi ile Türkiye''nin en köklü eğitim kurumlarından biridir. Geleneksel değerleri modern eğitim yöntemleriyle harmanlayarak öğrencilerini hayata hazırlamaktadır.',
    'bilgi@gelisimkoleji.edu.tr', 'https://facebook.com/gelisimkoleji',
    'https://instagram.com/gelisimkoleji', 'https://linkedin.com/company/gelisim-koleji',
    'https://egitimara.com/images/brands/gelisim-logo.jpg',
    'Gelişim Koleji - 35 yıllık eğitim deneyimi', 'gelişim, kolej, eğitim, özel okul',
    'Gelişim Koleji', 'Gelişim Koleji', '+902321892345',
    'gelisim-koleji', 'https://twitter.com/gelisimkoleji',
    'https://www.gelisimkoleji.edu.tr', 'https://youtube.com/gelisimkoleji'
);

-- ======= INSTITUTION TYPES =======
INSERT INTO institution_types (
    is_active, sort_order, created_at, created_by, updated_at, updated_by,
    color_code, default_properties, description, display_name, icon_url, name
) VALUES
-- Anaokulu
(
    true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1,
    '#FFB6C1', '{"minAge": 2, "maxAge": 6, "hasPlayground": true, "hasNapTime": true}',
    'Okul öncesi eğitim veren kurumlar', 'Anaokulu',
    'https://egitimara.com/icons/preschool.svg', 'PRESCHOOL'
),
-- İlkokul
(
    true, 2, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1,
    '#87CEEB', '{"minAge": 6, "maxAge": 10, "gradeCount": 4, "hasLibrary": true}',
    'İlköğretimin ilk kademesi', 'İlkokul',
    'https://egitimara.com/icons/primary.svg', 'PRIMARY_SCHOOL'
),
-- Ortaokul
(
    true, 3, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1,
    '#98FB98', '{"minAge": 10, "maxAge": 14, "gradeCount": 4, "hasLaboratory": true}',
    'İlköğretimin ikinci kademesi', 'Ortaokul',
    'https://egitimara.com/icons/middle.svg', 'MIDDLE_SCHOOL'
),
-- Lise
(
    true, 4, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1,
    '#DDA0DD', '{"minAge": 14, "maxAge": 18, "gradeCount": 4, "hasCareerGuidance": true}',
    'Ortaöğretim kurumu', 'Lise',
    'https://egitimara.com/icons/high-school.svg', 'HIGH_SCHOOL'
);

-- ======= INSTITUTION PROPERTIES =======
INSERT INTO institution_properties (
    is_active, is_filterable, is_required, is_searchable, max_length, max_value, min_length, min_value,
    show_in_card, show_in_profile, sort_order, created_at, created_by, updated_at, updated_by,
    institution_type_id, data_type, default_value, description, display_name, name, options, regex_pattern
) VALUES
-- Anaokulu özellikleri
(
    true, true, true, true, null, 200, null, 10,
    true, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1,
    1, 'NUMBER', '50', 'Okuldaki toplam öğrenci sayısı', 'Öğrenci Sayısı', 'student_count', null, null
),
(
    true, true, false, false, null, null, null, null,
    true, true, 2, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1,
    1, 'BOOLEAN', 'true', 'Oyun alanı var mı?', 'Oyun Alanı', 'has_playground', null, null
),
-- İlkokul özellikleri
(
    true, true, true, true, null, 800, null, 50,
    true, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1,
    2, 'NUMBER', '200', 'Okuldaki toplam öğrenci sayısı', 'Öğrenci Sayısı', 'student_count', null, null
),
(
    true, true, false, false, null, null, null, null,
    true, true, 2, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1,
    2, 'BOOLEAN', 'true', 'Kütüphane var mı?', 'Kütüphane', 'has_library', null, null
),
-- Ortaokul özellikleri
(
    true, true, true, true, null, 600, null, 100,
    true, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1,
    3, 'NUMBER', '300', 'Okuldaki toplam öğrenci sayısı', 'Öğrenci Sayısı', 'student_count', null, null
),
(
    true, true, false, false, null, null, null, null,
    true, true, 2, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1,
    3, 'BOOLEAN', 'true', 'Laboratuvar var mı?', 'Laboratuvar', 'has_laboratory', null, null
),
-- Lise özellikleri
(
    true, true, true, true, null, 1000, null, 200,
    true, true, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1,
    4, 'NUMBER', '500', 'Okuldaki toplam öğrenci sayısı', 'Öğrenci Sayısı', 'student_count', null, null
),
(
    true, true, true, false, null, null, null, null,
    true, true, 2, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1,
    4, 'SELECT', 'Fen', 'Okulun ana bölüm türü', 'Bölüm Türü', 'department_type',
    '["Fen", "Sosyal", "Anadolu", "Meslek"]', null
);

-- ======= CAMPUSES =======
INSERT INTO campuses (
    established_year, is_active, is_subscribed, latitude, longitude, rating_average,
    brand_id, country_id, created_at, created_by, district_id, updated_at, updated_by,
    neighborhood_id, province_id, rating_count, view_count, address_line1, address_line2,
    cover_image_url, description, email, facebook_url, fax, instagram_url, linkedin_url,
    logo_url, meta_description, meta_keywords, meta_title, name, phone, postal_code,
    slug, twitter_url, website_url, youtube_url
) VALUES
-- Eğitim Dünyası Maslak Kampüsü
(
    1998, true, true, 41.1069, 28.9958, 4.7,
    1, 1, CURRENT_TIMESTAMP, 1, 1, CURRENT_TIMESTAMP, 1,
    1, 1, 1847, 23456, 'Maslak Mahallesi, Ahi Evran Caddesi No:25', 'Maslak Plaza Yanı',
    'https://egitimara.com/images/campuses/egitim-dunyasi-maslak-cover.jpg',
    'Eğitim Dünyası Koleji Maslak Kampüsü, modern eğitim anlayışı ile İstanbul''un kalbinde hizmet vermektedir.',
    'maslak@egitimdunyasi.edu.tr', 'https://facebook.com/egitimdunyasimaslak',
    '+902122345679', 'https://instagram.com/egitimdunyasimaslak', 'https://linkedin.com/company/egitim-dunyasi-maslak',
    'https://egitimara.com/images/campuses/egitim-dunyasi-maslak-logo.jpg',
    'Eğitim Dünyası Maslak Kampüsü - Modern eğitimin adresi', 'eğitim, maslak, kampüs, özel okul',
    'Eğitim Dünyası Maslak Kampüsü', 'Eğitim Dünyası Maslak Kampüsü', '+902121234567',
    '34485', 'egitim-dunyasi-maslak-kampusu', 'https://twitter.com/egitimdunyasimaslak',
    'https://maslak.egitimdunyasi.edu.tr', 'https://youtube.com/egitimdunyasimaslak'
),
-- Bilim Sanat Kızılay Kampüsü
(
    2003, true, true, 39.9208, 32.8541, 4.9,
    2, 1, CURRENT_TIMESTAMP, 1, 3, CURRENT_TIMESTAMP, 1,
    3, 2, 1456, 18742, 'Kızılay Mahallesi, Ziya Gökalp Caddesi No:45', 'Opera Yanı',
    'https://egitimara.com/images/campuses/bilim-sanat-kizilay-cover.jpg',
    'Bilim Sanat Eğitim Kurumları Kızılay Kampüsü, başkentin merkezinde bilim ve sanata adanmış eğitim veriyor.',
    'kizilay@bilimsanat.edu.tr', 'https://facebook.com/bilimsanatkizilay',
    '+903125678901', 'https://instagram.com/bilimsanatkizilay', 'https://linkedin.com/company/bilim-sanat-kizilay',
    'https://egitimara.com/images/campuses/bilim-sanat-kizilay-logo.jpg',
    'Bilim Sanat Kızılay Kampüsü - Bilim ve sanatın buluşma noktası', 'bilim, sanat, kızılay, ankara',
    'Bilim Sanat Kızılay Kampüsü', 'Bilim Sanat Kızılay Kampüsü', '+903121567890',
    '06420', 'bilim-sanat-kizilay-kampusu', 'https://twitter.com/bilimsanatkizilay',
    'https://kizilay.bilimsanat.edu.tr', 'https://youtube.com/bilimsanatkizilay'
),
-- Gelişim Alsancak Kampüsü
(
    1992, true, true, 38.4369, 27.1476, 4.5,
    3, 1, CURRENT_TIMESTAMP, 1, 4, CURRENT_TIMESTAMP, 1,
    4, 3, 2156, 31287, 'Alsancak Mahallesi, Cumhuriyet Bulvarı No:156', 'Kültürpark Karşısı',
    'https://egitimara.com/images/campuses/gelisim-alsancak-cover.jpg',
    'Gelişim Koleji Alsancak Kampüsü, İzmir''in kültür merkezinde 30 yıldır eğitim hizmeti sunmaktadır.',
    'alsancak@gelisimkoleji.edu.tr', 'https://facebook.com/gelisimalsancak',
    '+902323456789', 'https://instagram.com/gelisimalsancak', 'https://linkedin.com/company/gelisim-alsancak',
    'https://egitimara.com/images/campuses/gelisim-alsancak-logo.jpg',
    'Gelişim Alsancak Kampüsü - 30 yıllık eğitim geleneği', 'gelişim, alsancak, izmir, eğitim',
    'Gelişim Alsancak Kampüsü', 'Gelişim Alsancak Kampüsü', '+902321892345',
    '35220', 'gelisim-alsancak-kampusu', 'https://twitter.com/gelisimalsancak',
    'https://alsancak.gelisimkoleji.edu.tr', 'https://youtube.com/gelisimalsancak'
),
-- Eğitim Dünyası Moda Kampüsü
(
    2005, true, true, 40.9833, 29.0333, 4.6,
    1, 1, CURRENT_TIMESTAMP, 1, 2, CURRENT_TIMESTAMP, 1,
    2, 1, 1234, 19865, 'Moda Mahallesi, Bahariye Caddesi No:78', 'Tarihi Moda İskelesi Yanı',
    'https://egitimara.com/images/campuses/egitim-dunyasi-moda-cover.jpg',
    'Eğitim Dünyası Koleji Moda Kampüsü, tarihi Moda semtinde butik eğitim anlayışı ile hizmet vermektedir.',
    'moda@egitimdunyasi.edu.tr', 'https://facebook.com/egitimdunyasimoda',
    '+902163456789', 'https://instagram.com/egitimdunyasimoda', 'https://linkedin.com/company/egitim-dunyasi-moda',
    'https://egitimara.com/images/campuses/egitim-dunyasi-moda-logo.jpg',
    'Eğitim Dünyası Moda Kampüsü - Tarihi atmosferde modern eğitim', 'eğitim, moda, kadıköy, kampüs',
    'Eğitim Dünyası Moda Kampüsü', 'Eğitim Dünyası Moda Kampüsü', '+902121234568',
    '34742', 'egitim-dunyasi-moda-kampusu', 'https://twitter.com/egitimdunyasimoda',
    'https://moda.egitimdunyasi.edu.tr', 'https://youtube.com/egitimdunyasimoda'
);

-- ======= SCHOOLS =======
INSERT INTO schools (
    annual_fee, capacity, class_size_average, current_student_count, is_active, max_age, min_age,
    monthly_fee, rating_average, registration_fee, campus_id, created_at, created_by, updated_at, updated_by,
    institution_type_id, like_count, post_count, rating_count, view_count, cover_image_url,
    curriculum_type, description, email, extension, foreign_languages, language_of_instruction,
    logo_url, meta_description, meta_keywords, meta_title, name, phone, slug
) VALUES
-- Eğitim Dünyası Maslak - Anaokulu
(
    45000.0, 80, 15, 68, true, 6, 2,
    4500.0, 4.8, 2500.0, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1,
    1, 156, 89, 234, 5642, 'https://egitimara.com/images/schools/egitim-dunyasi-maslak-anaokulu.jpg',
    'Montessori', 'Modern eğitim anlayışı ile çocukların yaratıcılığını geliştiren anaokulu programı.',
    'anaokulu.maslak@egitimdunyasi.edu.tr', '101', 'İngilizce', 'Türkçe',
    'https://egitimara.com/images/schools/egitim-dunyasi-anaokulu-logo.jpg',
    'Eğitim Dünyası Maslak Anaokulu - Çocukların ilk adımı', 'anaokulu, montessori, maslak',
    'Eğitim Dünyası Maslak Anaokulu', 'Eğitim Dünyası Maslak Anaokulu', '+902121234567',
    'egitim-dunyasi-maslak-anaokulu'
),
-- Eğitim Dünyası Maslak - İlkokul
(
    65000.0, 320, 22, 287, true, 10, 6,
    6000.0, 4.7, 3500.0, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1,
    2, 423, 156, 567, 12847, 'https://egitimara.com/images/schools/egitim-dunyasi-maslak-ilkokul.jpg',
    'Cambridge', 'İngilizce yoğun eğitim programı ile çocukların temel becerilerini geliştiren ilkokul.',
    'ilkokul.maslak@egitimdunyasi.edu.tr', '102', 'İngilizce, Almanca', 'Türkçe',
    'https://egitimara.com/images/schools/egitim-dunyasi-ilkokul-logo.jpg',
    'Eğitim Dünyası Maslak İlkokul - Güçlü temeller', 'ilkokul, cambridge, maslak',
    'Eğitim Dünyası Maslak İlkokul', 'Eğitim Dünyası Maslak İlkokul', '+902121234567',
    'egitim-dunyasi-maslak-ilkokul'
),
-- Bilim Sanat Kızılay - Ortaokul
(
    78000.0, 240, 18, 216, true, 14, 10,
    7200.0, 4.9, 4000.0, 2, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1,
    3, 678, 234, 892, 15632, 'https://egitimara.com/images/schools/bilim-sanat-kizilay-ortaokul.jpg',
    'STEAM', 'Bilim, teknoloji, mühendislik, sanat ve matematik odaklı eğitim programı.',
    'ortaokul.kizilay@bilimsanat.edu.tr', '201', 'İngilizce, Fransızca', 'Türkçe',
    'https://egitimara.com/images/schools/bilim-sanat-ortaokul-logo.jpg',
    'Bilim Sanat Kızılay Ortaokul - STEAM eğitimi', 'ortaokul, steam, bilim, sanat',
    'Bilim Sanat Kızılay Ortaokul', 'Bilim Sanat Kızılay Ortaokul', '+903121567890',
    'bilim-sanat-kizilay-ortaokul'
),
-- Bilim Sanat Kızılay - Lise
(
    95000.0, 200, 20, 178, true, 18, 14,
    8500.0, 4.8, 5000.0, 2, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1,
    4, 523, 187, 724, 18947, 'https://egitimara.com/images/schools/bilim-sanat-kizilay-lise.jpg',
    'IB', 'International Baccalaureate programı ile dünya standartlarında lise eğitimi.',
    'lise.kizilay@bilimsanat.edu.tr', '301', 'İngilizce, Fransızca, Almanca', 'İngilizce',
    'https://egitimara.com/images/schools/bilim-sanat-lise-logo.jpg',
    'Bilim Sanat Kızılay Lise - IB World School', 'lise, ib, uluslararası',
    'Bilim Sanat Kızılay Lise', 'Bilim Sanat Kızılay Lise', '+903121567890',
    'bilim-sanat-kizilay-lise'
),
-- Gelişim Alsancak - Anaokulu
(
    38000.0, 60, 12, 54, true, 6, 2,
    3800.0, 4.4, 2000.0, 3, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1,
    1, 89, 45, 167, 3784, 'https://egitimara.com/images/schools/gelisim-alsancak-anaokulu.jpg',
    'Geleneksel', 'Türk kültür değerleri ile modern eğitimi harmanlayan anaokulu programı.',
    'anaokulu.alsancak@gelisimkoleji.edu.tr', '401', 'İngilizce', 'Türkçe',
    'https://egitimara.com/images/schools/gelisim-anaokulu-logo.jpg',
    'Gelişim Alsancak Anaokulu - Değerlerle büyüyen çocuklar', 'anaokulu, geleneksel, değerler',
    'Gelişim Alsancak Anaokulu', 'Gelişim Alsancak Anaokulu', '+902321892345',
    'gelisim-alsancak-anaokulu'
),
-- Eğitim Dünyası Moda - İlkokul
(
    72000.0, 180, 18, 162, true, 10, 6,
    6800.0, 4.6, 3800.0, 4, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1,
    2, 267, 98, 356, 8973, 'https://egitimara.com/images/schools/egitim-dunyasi-moda-ilkokul.jpg',
    'Sanat Ağırlıklı', 'Sanat ve estetik eğitime özel önem veren, yaratıcılığı destekleyen ilkokul programı.',
    'ilkokul.moda@egitimdunyasi.edu.tr', '501', 'İngilizce, İtalyanca', 'Türkçe',
    'https://egitimara.com/images/schools/egitim-dunyasi-sanat-logo.jpg',
    'Eğitim Dünyası Moda İlkokul - Sanatta yetenekli çocuklar', 'ilkokul, sanat, yaratıcılık',
    'Eğitim Dünyası Moda İlkokul', 'Eğitim Dünyası Moda İlkokul', '+902121234568',
    'egitim-dunyasi-moda-ilkokul'
);

-- ======= INSTITUTION PROPERTY VALUES =======
INSERT INTO institution_property_values (
    boolean_value, is_active, number_value, campus_id, created_at, created_by, updated_at, updated_by,
    property_id, school_id, text_value
) VALUES
-- Eğitim Dünyası Maslak Anaokulu özellikleri
(null, true, 68, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1, 1, 1, null), -- Öğrenci sayısı
(true, true, null, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1, 2, 1, null), -- Oyun alanı var

-- Eğitim Dünyası Maslak İlkokul özellikleri
(null, true, 287, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1, 3, 2, null), -- Öğrenci sayısı
(true, true, null, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1, 4, 2, null), -- Kütüphane var

-- Bilim Sanat Kızılay Ortaokul özellikleri
(null, true, 216, 2, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1, 5, 3, null), -- Öğrenci sayısı
(true, true, null, 2, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1, 6, 3, null), -- Laboratuvar var

-- Bilim Sanat Kızılay Lise özellikleri
(null, true, 178, 2, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1, 7, 4, null), -- Öğrenci sayısı
(null, true, null, 2, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1, 8, 4, 'Fen'), -- Bölüm türü

-- Gelişim Alsancak Anaokulu özellikleri
(null, true, 54, 3, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1, 1, 5, null), -- Öğrenci sayısı
(true, true, null, 3, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1, 2, 5, null), -- Oyun alanı var

-- Eğitim Dünyası Moda İlkokul özellikleri
(null, true, 162, 4, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1, 3, 6, null), -- Öğrenci sayısı
(true, true, null, 4, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1, 4, 6, null); -- Kütüphane var

-- ======= USER INSTITUTION ACCESS =======
INSERT INTO user_institution_access (
    is_active, created_at, created_by, entity_id, expires_at, granted_at, updated_at, updated_by,
    user_id, access_type
) VALUES
-- Admin kullanıcısına tüm markalara erişim
(true, CURRENT_TIMESTAMP, 1, 1, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, 'BRAND'),
(true, CURRENT_TIMESTAMP, 1, 2, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, 'BRAND'),
(true, CURRENT_TIMESTAMP, 1, 3, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, 'BRAND'),

-- Admin kullanıcısına tüm kampüslere erişim
(true, CURRENT_TIMESTAMP, 1, 1, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, 'CAMPUS'),
(true, CURRENT_TIMESTAMP, 1, 2, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, 'CAMPUS'),
(true, CURRENT_TIMESTAMP, 1, 3, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, 'CAMPUS'),
(true, CURRENT_TIMESTAMP, 1, 4, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, 'CAMPUS'),

-- Admin kullanıcısına tüm okullara erişim
(true, CURRENT_TIMESTAMP, 1, 1, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, 'SCHOOL'),
(true, CURRENT_TIMESTAMP, 1, 2, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, 'SCHOOL'),
(true, CURRENT_TIMESTAMP, 1, 3, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, 'SCHOOL'),
(true, CURRENT_TIMESTAMP, 1, 4, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, 'SCHOOL'),
(true, CURRENT_TIMESTAMP, 1, 5, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, 'SCHOOL'),
(true, CURRENT_TIMESTAMP, 1, 6, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, 'SCHOOL'),

-- Şirket kullanıcısına Eğitim Dünyası markasına erişim
(true, CURRENT_TIMESTAMP, 1, 1, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 2, 'BRAND'),

-- Şirket kullanıcısına Eğitim Dünyası kampüslerine erişim
(true, CURRENT_TIMESTAMP, 1, 1, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 2, 'CAMPUS'),
(true, CURRENT_TIMESTAMP, 1, 4, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 2, 'CAMPUS'),

-- Şirket kullanıcısına Eğitim Dünyası okullarına erişim
(true, CURRENT_TIMESTAMP, 1, 1, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 2, 'SCHOOL'),
(true, CURRENT_TIMESTAMP, 1, 2, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 2, 'SCHOOL'),
(true, CURRENT_TIMESTAMP, 1, 6, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 2, 'SCHOOL'),

-- Aday kullanıcısına Bilim Sanat markasına erişim (aday olarak başvuracağı)
(true, CURRENT_TIMESTAMP, 1, 2, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 3, 'BRAND'),

-- Aday kullanıcısına Bilim Sanat Kızılay kampüsüne erişim
(true, CURRENT_TIMESTAMP, 1, 2, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 3, 'CAMPUS'),

-- Aday kullanıcısına Bilim Sanat lise okuluna erişim
(true, CURRENT_TIMESTAMP, 1, 4, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 3, 'SCHOOL'),

-- Normal kullanıcıya Gelişim markasına erişim (çocuğunu kaydetmek için)
(true, CURRENT_TIMESTAMP, 1, 3, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 4, 'BRAND'),

-- Normal kullanıcıya Gelişim Alsancak kampüsüne erişim
(true, CURRENT_TIMESTAMP, 1, 3, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 4, 'CAMPUS'),

-- Normal kullanıcıya Gelişim anaokuluna erişim
(true, CURRENT_TIMESTAMP, 1, 5, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 4, 'SCHOOL');



-- V3__Insert_Demo_Subscription_And_Payment_Data.sql
-- eğitimara.com için abonelik ve ödeme sistemi demo verileri

-- ======= SUBSCRIPTION PLANS =======
INSERT INTO subscription_plans (
    has_analytics, has_api_access, has_custom_domain, has_priority_support, has_white_label,
    is_active, is_popular, is_visible, max_appointments_per_month, max_gallery_items,
    max_posts_per_month, max_schools, max_users, price, sort_order, storage_gb, trial_days,
    created_at, created_by, updated_at, updated_by, billing_period, description, display_name,
    features, name, pricing_tiers
) VALUES
-- Starter Plan
(
    false, false, false, false, false,
    true, false, true, 50, 20,
    10, 1, 5, 99.00, 1, 5, 14,
    CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1, 'MONTHLY',
    'Küçük okullar için başlangıç paketi. Temel özellikler ve sınırlı kullanım ile eğitim yönetiminin ilk adımı.',
    'Başlangıç Planı',
    '{"basicReporting": true, "emailSupport": true, "mobileApp": false, "customBranding": false, "advancedAnalytics": false}',
    'STARTER',
    '{"monthly": 99, "yearly": 990, "discount": 10}'
),
-- Professional Plan
(
    true, false, true, true, false,
    true, true, true, 200, 100,
    50, 3, 25, 299.00, 2, 25, 14,
    CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1, 'MONTHLY',
    'Orta ölçekli okullar için kapsamlı çözüm. Gelişmiş analitikler ve öncelikli destek ile eğitim yönetimini profesyonelleştirin.',
    'Profesyonel Plan',
    '{"basicReporting": true, "emailSupport": true, "mobileApp": true, "customBranding": true, "advancedAnalytics": true, "prioritySupport": true, "customDomain": true}',
    'PROFESSIONAL',
    '{"monthly": 299, "yearly": 2990, "discount": 16}'
),
-- Enterprise Plan
(
    true, true, true, true, true,
    true, false, true, 1000, 500,
    200, 10, 100, 599.00, 3, 100, 30,
    CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1, 'MONTHLY',
    'Büyük eğitim kurumları için tam donanımlı çözüm. API erişimi, white-label ve sınırsız özellikler ile kurumsal ihtiyaçları karşılar.',
    'Kurumsal Plan',
    '{"basicReporting": true, "emailSupport": true, "mobileApp": true, "customBranding": true, "advancedAnalytics": true, "prioritySupport": true, "customDomain": true, "apiAccess": true, "whiteLabel": true, "dedicatedManager": true}',
    'ENTERPRISE',
    '{"monthly": 599, "yearly": 5990, "discount": 20}'
),
-- Annual Starter
(
    false, false, false, false, false,
    true, false, true, 50, 20,
    10, 1, 5, 990.00, 4, 5, 14,
    CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1, 'YEARLY',
    'Yıllık ödeme ile %10 indirimli başlangıç paketi.',
    'Yıllık Başlangıç',
    '{"basicReporting": true, "emailSupport": true, "mobileApp": false, "customBranding": false, "yearlyDiscount": 10}',
    'STARTER_YEARLY',
    '{"yearly": 990, "monthlyEquivalent": 82.5, "savings": 198}'
),
-- Annual Professional
(
    true, false, true, true, false,
    true, true, true, 200, 100,
    50, 3, 25, 2990.00, 5, 25, 14,
    CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1, 'YEARLY',
    'Yıllık ödeme ile %16 indirimli profesyonel paket.',
    'Yıllık Profesyonel',
    '{"basicReporting": true, "emailSupport": true, "mobileApp": true, "customBranding": true, "advancedAnalytics": true, "prioritySupport": true, "customDomain": true, "yearlyDiscount": 16}',
    'PROFESSIONAL_YEARLY',
    '{"yearly": 2990, "monthlyEquivalent": 249.17, "savings": 598}'
);

-- ======= COUPONS =======
INSERT INTO coupons (
    discount_value, is_active, is_public, max_discount_amount, min_order_amount,
    usage_count, usage_limit, user_usage_limit, created_at, created_by, updated_at, updated_by,
    valid_from, valid_until, applicable_plans, code, description, discount_type, name, terms,
    applicable_plan_ids
) VALUES
-- Hoşgeldin Kuponu
(
    25.00, true, true, 500.00, 99.00,
    142, 1000, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '90 days', 'STARTER,PROFESSIONAL',
    'HOSGELDIN25', 'Yeni üyelere özel %25 indirim', 'PERCENTAGE',
    'Hoşgeldin İndirimi', 'Sadece ilk abonelik için geçerli. Diğer kampanyalarla birleştirilemez.',
    '{1,2,4,5}'
),
-- Eğitim Haftası Özel
(
    50.00, true, true, 300.00, 200.00,
    67, 500, 2, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1,
    CURRENT_TIMESTAMP - INTERVAL '10 days', CURRENT_TIMESTAMP + INTERVAL '20 days', 'ALL',
    'EGITIM50', 'Eğitim Haftası özel indirimi', 'FIXED',
    'Eğitim Haftası Kampanyası', '24-30 Kasım Eğitim Haftasına özel. Yıllık planlar için geçerli.',
    '{1,2,3,4,5}'
),
-- Kurumsal Kampanya
(
    15.00, true, false, 1000.00, 500.00,
    23, 100, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '60 days', 'ENTERPRISE',
    'KURUMSAL15', 'Kurumsal müşteriler için özel indirim', 'PERCENTAGE',
    'Kurumsal Müşteri İndirimi', 'Sadece kurumsal plan için geçerli. Satış temsilcisi onayı gerekir.',
    '{3}'
);

-- ======= SUBSCRIPTIONS =======
INSERT INTO subscriptions (
    auto_renew, current_gallery_items, current_month_appointments, current_month_posts,
    current_schools_count, current_users_count, discount_amount, discount_percentage,
    is_active, price, campus_id, canceled_at, created_at, created_by, end_date, grace_period_end,
    next_billing_date, start_date, storage_used_mb, subscription_plan_id, trial_end_date,
    updated_at, updated_by, billing_address, billing_email, billing_name, billing_phone,
    cancellation_reason, coupon_code, currency, status, tax_number, tax_office
) VALUES
-- Eğitim Dünyası Maslak - Professional Plan (Aktif)
(
    true, 45, 156, 28,
    2, 18, 74.75, 25.00,
    true, 224.25, 1, null, CURRENT_TIMESTAMP - INTERVAL '45 days', 2, CURRENT_TIMESTAMP + INTERVAL '15 days', null,
    CURRENT_TIMESTAMP + INTERVAL '15 days', CURRENT_TIMESTAMP - INTERVAL '45 days', 15680, 2, CURRENT_TIMESTAMP - INTERVAL '31 days',
    CURRENT_TIMESTAMP, 2, 'Maslak Mahallesi, Ahi Evran Caddesi No:25, Şişli/İstanbul',
    'muhasebe@egitimdunyasi.edu.tr', 'Eğitim Dünyası Koleji Maslak Kampüsü', '+902121234567',
    null, 'HOSGELDIN25', 'TRY', 'ACTIVE', '1234567890', 'Şişli Vergi Dairesi'
),
-- Bilim Sanat Kızılay - Enterprise Plan (Aktif)
(
    true, 125, 287, 89,
    2, 45, 89.85, 15.00,
    true, 509.15, 2, null, CURRENT_TIMESTAMP - INTERVAL '120 days', 2, CURRENT_TIMESTAMP + INTERVAL '240 days', null,
    CURRENT_TIMESTAMP + INTERVAL '240 days', CURRENT_TIMESTAMP - INTERVAL '120 days', 42580, 3, null,
    CURRENT_TIMESTAMP, 2, 'Kızılay Mahallesi, Ziya Gökalp Caddesi No:45, Çankaya/Ankara',
    'mali@bilimsanat.edu.tr', 'Bilim Sanat Eğitim Kurumları', '+903121567890',
    null, 'KURUMSAL15', 'TRY', 'ACTIVE', '0987654321', 'Çankaya Vergi Dairesi'
),
-- Gelişim Alsancak - Starter Plan (Trial)
(
    false, 8, 24, 5,
    1, 6, null, null,
    true, 99.00, 3, null, CURRENT_TIMESTAMP - INTERVAL '5 days', 4, CURRENT_TIMESTAMP + INTERVAL '9 days', CURRENT_TIMESTAMP + INTERVAL '16 days',
    CURRENT_TIMESTAMP + INTERVAL '9 days', CURRENT_TIMESTAMP - INTERVAL '5 days', 2450, 1, CURRENT_TIMESTAMP + INTERVAL '9 days',
    CURRENT_TIMESTAMP, 4, 'Alsancak Mahallesi, Cumhuriyet Bulvarı No:156, Konak/İzmir',
    'info@gelisimkoleji.edu.tr', 'Gelişim Koleji Alsancak Kampüsü', '+902321892345',
    null, null, 'TRY', 'TRIAL', '1122334455', 'Konak Vergi Dairesi'
),
-- Eğitim Dünyası Moda - Professional Yearly (Aktif)
(
    true, 67, 98, 34,
    1, 22, 478.40, 16.00,
    true, 2511.60, 4, null, CURRENT_TIMESTAMP - INTERVAL '180 days', 2, CURRENT_TIMESTAMP + INTERVAL '185 days', null,
    CURRENT_TIMESTAMP + INTERVAL '185 days', CURRENT_TIMESTAMP - INTERVAL '180 days', 18920, 5, null,
    CURRENT_TIMESTAMP, 2, 'Moda Mahallesi, Bahariye Caddesi No:78, Kadıköy/İstanbul',
    'finans@egitimdunyasi.edu.tr', 'Eğitim Dünyası Koleji Moda Kampüsü', '+902121234568',
    null, null, 'TRY', 'ACTIVE', '1234567891', 'Kadıköy Vergi Dairesi'
);

-- ======= PAYMENTS =======
INSERT INTO payments (
    amount, is_active, refund_amount, created_at, created_by, due_date, payment_date,
    period_end, period_start, refund_date, subscription_id, updated_at, updated_by,
    card_brand, card_holder_name, card_last_four, currency, description, external_payment_id,
    failure_reason, gateway_name, gateway_response, gateway_transaction_id, payment_method,
    payment_reference, payment_status, refund_reason
) VALUES
-- Eğitim Dünyası Maslak - İlk ödeme
(
    224.25, true, null, CURRENT_TIMESTAMP - INTERVAL '45 days', 2, CURRENT_TIMESTAMP - INTERVAL '45 days', CURRENT_TIMESTAMP - INTERVAL '45 days',
    CURRENT_TIMESTAMP + INTERVAL '15 days', CURRENT_TIMESTAMP - INTERVAL '45 days', null, 1, CURRENT_TIMESTAMP - INTERVAL '45 days', 2,
    'Visa', 'Ahmet Yılmaz', '4532', 'TRY', 'Eğitim Dünyası Maslak - Profesyonel Plan', 'iyzc_12345678901',
    null, 'IYZICO', '{"status": "success", "payment_id": "12345678901", "conversation_id": "EDM001"}', 'TXN_12345678901', 'CREDIT_CARD',
    'PAY_EDM_20250827_001', 'COMPLETED', null
),
-- Eğitim Dünyası Maslak - Yenileme ödemesi
(
    299.00, true, null, CURRENT_TIMESTAMP - INTERVAL '15 days', 2, CURRENT_TIMESTAMP - INTERVAL '15 days', CURRENT_TIMESTAMP - INTERVAL '15 days',
    CURRENT_TIMESTAMP + INTERVAL '15 days', CURRENT_TIMESTAMP - INTERVAL '15 days', null, 1, CURRENT_TIMESTAMP - INTERVAL '15 days', 2,
    'Visa', 'Ahmet Yılmaz', '4532', 'TRY', 'Eğitim Dünyası Maslak - Profesyonel Plan Yenileme', 'iyzc_12345678902',
    null, 'IYZICO', '{"status": "success", "payment_id": "12345678902", "conversation_id": "EDM002"}', 'TXN_12345678902', 'CREDIT_CARD',
    'PAY_EDM_20250827_002', 'COMPLETED', null
),
-- Bilim Sanat Kızılay - Kurumsal ödeme
(
    509.15, true, null, CURRENT_TIMESTAMP - INTERVAL '120 days', 2, CURRENT_TIMESTAMP - INTERVAL '120 days', CURRENT_TIMESTAMP - INTERVAL '115 days',
    CURRENT_TIMESTAMP + INTERVAL '240 days', CURRENT_TIMESTAMP - INTERVAL '120 days', null, 2, CURRENT_TIMESTAMP - INTERVAL '115 days', 2,
    'Mastercard', 'Zeynep Kaya', '5467', 'TRY', 'Bilim Sanat Kızılay - Kurumsal Plan', 'iyzc_23456789012',
    null, 'IYZICO', '{"status": "success", "payment_id": "23456789012", "conversation_id": "BSK001"}', 'TXN_23456789012', 'CREDIT_CARD',
    'PAY_BSK_20250827_001', 'COMPLETED', null
),
-- Bilim Sanat Kızılay - Aylık yenileme
(
    599.00, true, null, CURRENT_TIMESTAMP - INTERVAL '90 days', 2, CURRENT_TIMESTAMP - INTERVAL '90 days', CURRENT_TIMESTAMP - INTERVAL '90 days',
    CURRENT_TIMESTAMP + INTERVAL '270 days', CURRENT_TIMESTAMP - INTERVAL '90 days', null, 2, CURRENT_TIMESTAMP - INTERVAL '90 days', 2,
    'Mastercard', 'Zeynep Kaya', '5467', 'TRY', 'Bilim Sanat Kızılay - Kurumsal Plan Yenileme', 'iyzc_23456789013',
    null, 'IYZICO', '{"status": "success", "payment_id": "23456789013", "conversation_id": "BSK002"}', 'TXN_23456789013', 'CREDIT_CARD',
    'PAY_BSK_20250827_002', 'COMPLETED', null
),
-- Eğitim Dünyası Moda - Yıllık ödeme
(
    2511.60, true, null, CURRENT_TIMESTAMP - INTERVAL '180 days', 2, CURRENT_TIMESTAMP - INTERVAL '180 days', CURRENT_TIMESTAMP - INTERVAL '178 days',
    CURRENT_TIMESTAMP + INTERVAL '185 days', CURRENT_TIMESTAMP - INTERVAL '180 days', null, 4, CURRENT_TIMESTAMP - INTERVAL '178 days', 2,
    'Visa', 'Ahmet Yılmaz', '4532', 'TRY', 'Eğitim Dünyası Moda - Yıllık Profesyonel Plan', 'iyzc_34567890123',
    null, 'IYZICO', '{"status": "success", "payment_id": "34567890123", "conversation_id": "EDModa001"}', 'TXN_34567890123', 'CREDIT_CARD',
    'PAY_EDModa_20250827_001', 'COMPLETED', null
),
-- Gelişim Alsancak - Deneme süresi sonu (Beklemede)
(
    99.00, true, null, CURRENT_TIMESTAMP + INTERVAL '9 days', 4, CURRENT_TIMESTAMP + INTERVAL '9 days', null,
    CURRENT_TIMESTAMP + INTERVAL '39 days', CURRENT_TIMESTAMP + INTERVAL '9 days', null, 3, CURRENT_TIMESTAMP, 4,
    null, 'Ayşe Özkan', null, 'TRY', 'Gelişim Alsancak - Başlangıç Plan (Trial Sonu)', null,
    null, null, null, null, 'CREDIT_CARD',
    'PAY_GA_20250827_001', 'PENDING', null
);

-- ======= INVOICES =======
INSERT INTO invoices (
    discount_amount, generate_e_invoice, is_active, subtotal, tax_amount, tax_rate, total_amount,
    created_at, created_by, due_date, e_invoice_sent_at, invoice_date, payment_id, pdf_generated_at,
    period_end, period_start, subscription_id, updated_at, updated_by, billing_address,
    billing_email, billing_name, billing_phone, currency, description, e_invoice_response,
    e_invoice_status, e_invoice_uuid, invoice_number, invoice_status, line_items, notes,
    pdf_file_url, tax_number, tax_office
) VALUES
-- Eğitim Dünyası Maslak - İlk fatura
(
    74.75, true, true, 224.25, 40.37, 18.00, 264.62,
    CURRENT_TIMESTAMP - INTERVAL '45 days', 2, CURRENT_TIMESTAMP - INTERVAL '45 days', CURRENT_TIMESTAMP - INTERVAL '44 days', CURRENT_TIMESTAMP - INTERVAL '45 days', 1, CURRENT_TIMESTAMP - INTERVAL '44 days',
    CURRENT_TIMESTAMP + INTERVAL '15 days', CURRENT_TIMESTAMP - INTERVAL '45 days', 1, CURRENT_TIMESTAMP - INTERVAL '44 days', 2,
    'Maslak Mahallesi, Ahi Evran Caddesi No:25, Şişli/İstanbul',
    'muhasebe@egitimdunyasi.edu.tr', 'Eğitim Dünyası Koleji Maslak Kampüsü', '+902121234567',
    'TRY', 'Profesyonel Plan - Aylık Abonelik',
    '{"uuid": "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "status": "approved", "sent_date": "2025-07-13T10:30:00Z"}',
    'SENT', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'EDM-2025-0001', 'PAID',
    '[{"description": "Profesyonel Plan - Aylık", "quantity": 1, "unit_price": 299.00, "discount": 74.75, "total": 224.25}]',
    'Hoşgeldin kampanyası ile %25 indirim uygulanmıştır.',
    'https://egitimara.com/invoices/EDM-2025-0001.pdf', '1234567890', 'Şişli Vergi Dairesi'
),
-- Eğitim Dünyası Maslak - Yenileme faturası
(
    null, true, true, 299.00, 53.82, 18.00, 352.82,
    CURRENT_TIMESTAMP - INTERVAL '15 days', 2, CURRENT_TIMESTAMP - INTERVAL '15 days', CURRENT_TIMESTAMP - INTERVAL '14 days', CURRENT_TIMESTAMP - INTERVAL '15 days', 2, CURRENT_TIMESTAMP - INTERVAL '14 days',
    CURRENT_TIMESTAMP + INTERVAL '15 days', CURRENT_TIMESTAMP - INTERVAL '15 days', 1, CURRENT_TIMESTAMP - INTERVAL '14 days', 2,
    'Maslak Mahallesi, Ahi Evran Caddesi No:25, Şişli/İstanbul',
    'muhasebe@egitimdunyasi.edu.tr', 'Eğitim Dünyası Koleji Maslak Kampüsü', '+902121234567',
    'TRY', 'Profesyonel Plan - Aylık Yenileme',
    '{"uuid": "b2c3d4e5-f6g7-8901-bcde-f23456789012", "status": "approved", "sent_date": "2025-08-13T09:15:00Z"}',
    'SENT', 'b2c3d4e5-f6g7-8901-bcde-f23456789012', 'EDM-2025-0002', 'PAID',
    '[{"description": "Profesyonel Plan - Aylık", "quantity": 1, "unit_price": 299.00, "discount": 0, "total": 299.00}]',
    'Normal fiyat, kampanya süresi dolmuştur.',
    'https://egitimara.com/invoices/EDM-2025-0002.pdf', '1234567890', 'Şişli Vergi Dairesi'
),
-- Bilim Sanat Kızılay - Kurumsal fatura
(
    89.85, true, true, 509.15, 91.65, 18.00, 600.80,
    CURRENT_TIMESTAMP - INTERVAL '115 days', 2, CURRENT_TIMESTAMP - INTERVAL '120 days', CURRENT_TIMESTAMP - INTERVAL '114 days', CURRENT_TIMESTAMP - INTERVAL '115 days', 3, CURRENT_TIMESTAMP - INTERVAL '114 days',
    CURRENT_TIMESTAMP + INTERVAL '240 days', CURRENT_TIMESTAMP - INTERVAL '120 days', 2, CURRENT_TIMESTAMP - INTERVAL '114 days', 2,
    'Kızılay Mahallesi, Ziya Gökalp Caddesi No:45, Çankaya/Ankara',
    'mali@bilimsanat.edu.tr', 'Bilim Sanat Eğitim Kurumları', '+903121567890',
    'TRY', 'Kurumsal Plan - Aylık Abonelik',
    '{"uuid": "c3d4e5f6-g7h8-9012-cdef-345678901234", "status": "approved", "sent_date": "2025-05-03T14:22:00Z"}',
    'SENT', 'c3d4e5f6-g7h8-9012-cdef-345678901234', 'BSK-2025-0001', 'PAID',
    '[{"description": "Kurumsal Plan - Aylık", "quantity": 1, "unit_price": 599.00, "discount": 89.85, "total": 509.15}]',
    'Kurumsal müşteri indirimi %15 uygulanmıştır.',
    'https://egitimara.com/invoices/BSK-2025-0001.pdf', '0987654321', 'Çankaya Vergi Dairesi'
),
-- Eğitim Dünyası Moda - Yıllık fatura
(
    478.40, true, true, 2511.60, 452.09, 18.00, 2963.69,
    CURRENT_TIMESTAMP - INTERVAL '178 days', 2, CURRENT_TIMESTAMP - INTERVAL '180 days', CURRENT_TIMESTAMP - INTERVAL '177 days', CURRENT_TIMESTAMP - INTERVAL '178 days', 5, CURRENT_TIMESTAMP - INTERVAL '177 days',
    CURRENT_TIMESTAMP + INTERVAL '185 days', CURRENT_TIMESTAMP - INTERVAL '180 days', 4, CURRENT_TIMESTAMP - INTERVAL '177 days', 2,
    'Moda Mahallesi, Bahariye Caddesi No:78, Kadıköy/İstanbul',
    'finans@egitimdunyasi.edu.tr', 'Eğitim Dünyası Koleji Moda Kampüsü', '+902121234568',
    'TRY', 'Profesyonel Plan - Yıllık Abonelik',
    '{"uuid": "d4e5f6g7-h8i9-0123-defg-456789012345", "status": "approved", "sent_date": "2025-02-08T11:45:00Z"}',
    'SENT', 'd4e5f6g7-h8i9-0123-defg-456789012345', 'EDModa-2025-0001', 'PAID',
    '[{"description": "Profesyonel Plan - Yıllık", "quantity": 1, "unit_price": 2990.00, "discount": 478.40, "total": 2511.60}]',
    'Yıllık ödeme indirimi %16 uygulanmıştır.',
    'https://egitimara.com/invoices/EDModa-2025-0001.pdf', '1234567891', 'Kadıköy Vergi Dairesi'
),
-- Gelişim Alsancak - Bekleyen fatura (Trial sonu)
(
    null, false, true, 99.00, 17.82, 18.00, 116.82,
    CURRENT_TIMESTAMP, 4, CURRENT_TIMESTAMP + INTERVAL '9 days', null, CURRENT_TIMESTAMP, 6, null,
    CURRENT_TIMESTAMP + INTERVAL '39 days', CURRENT_TIMESTAMP + INTERVAL '9 days', 3, CURRENT_TIMESTAMP, 4,
    'Alsancak Mahallesi, Cumhuriyet Bulvarı No:156, Konak/İzmir',
    'info@gelisimkoleji.edu.tr', 'Gelişim Koleji Alsancak Kampüsü', '+902321892345',
    'TRY', 'Başlangıç Plan - İlk Aylık Ödeme',
    null, null, null, 'GA-2025-0001', 'SENT',
    '[{"description": "Başlangıç Plan - Aylık", "quantity": 1, "unit_price": 99.00, "discount": 0, "total": 99.00}]',
    'Trial süresi sonu, ilk ödeme.',
    null, '1122334455', 'Konak Vergi Dairesi'
);




-- V4__Insert_Demo_Appointment_Data.sql
-- eğitimara.com için randevu yönetim sistemi demo verileri

-- ======= APPOINTMENT SLOTS =======
INSERT INTO appointment_slots (
    id, advance_booking_hours, cancellation_hours, capacity, duration_minutes, end_time, is_active,
    is_recurring, max_advance_booking_days, online_meeting_available, preparation_required,
    requires_approval, start_time, valid_from, valid_until, created_at, created_by, updated_at, updated_by,
    school_id, staff_user_id, appointment_type, day_of_week, description, excluded_dates,
    location, preparation_notes, title
) VALUES
-- Eğitim Dünyası Maslak Anaokulu - Bilgi toplantısı (Pazartesi)
(
    1,24, 4, 8, 60, '11:00:00', true,
    true, 30, true, true,
    false, '10:00:00', '2025-01-01', '2025-12-31', CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1,
    1, 1, 'INFORMATION_MEETING', 'MONDAY', 'Anaokulu hakkında detaylı bilgi toplantısı',
    '["2025-01-01", "2025-04-23", "2025-05-01", "2025-05-19", "2025-08-30", "2025-10-29", "2025-12-25"]',
    'Konferans Salonu A', 'Veliler çocuklarını getirebilir. Sunum materyalleri hazırlanacak.', 'Anaokulu Tanıtım Toplantısı'
),
-- Eğitim Dünyası Maslak Anaokulu - Okul turu (Çarşamba)
(
    2,48, 6, 4, 45, '15:30:00', true,
    true, 14, false, false,
    true, '14:45:00', '2025-01-01', '2025-12-31', CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1,
    1, 1, 'SCHOOL_TOUR', 'WEDNESDAY', 'Okul tesislerinin gezilmesi ve sınıfların incelenmesi',
    '["2025-01-01", "2025-04-23", "2025-05-01", "2025-05-19", "2025-08-30", "2025-10-29", "2025-12-25"]',
    'Ana Giriş', 'Rahat ayakkabı giyiniz. Çocukların güvenliği için velilerin yanında bulunması zorunludur.', 'Okul Turu'
),
-- Eğitim Dünyası Maslak İlkokul - Kayıt görüşmesi (Salı)
(
    3,72, 8, 2, 90, '16:00:00', true,
    true, 21, true, true,
    true, '14:30:00', '2025-01-01', '2025-12-31', CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1,
    2, 2, 'ENROLLMENT_INTERVIEW', 'TUESDAY', 'Öğrenci kayıt değerlendirme görüşmesi',
    '["2025-01-01", "2025-04-23", "2025-05-01", "2025-05-19", "2025-08-30", "2025-10-29", "2025-12-25"]',
    'Müdür Yardımcısı Odası', 'Öğrencinin önceki okul raporları ve sağlık raporu getirilmelidir.', 'Kayıt Görüşmesi'
),
-- Bilim Sanat Kızılay Ortaokul - Veli görüşmesi (Perşembe)
(
    4,24, 4, 6, 30, '17:00:00', true,
    true, 14, true, false,
    false, '16:30:00', '2025-01-01', '2025-12-31', CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1,
    3, 3, 'PARENT_MEETING', 'THURSDAY', 'Veli-öğretmen görüşmeleri',
    '["2025-01-01", "2025-04-23", "2025-05-01", "2025-05-19", "2025-08-30", "2025-10-29", "2025-12-25"]',
    'Öğretmenler Odası', null, 'Veli Görüşmesi'
),
-- Bilim Sanat Kızılay Lise - Online danışmanlık (Cuma)
(
    5, 12, 2, 12, 45, '18:00:00', true,
    true, 7, true, false,
    false, '17:15:00', '2025-01-01', '2025-12-31', CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1,
    4, 3, 'ONLINE_MEETING', 'FRIDAY', 'Lise tercihi ve kariyer danışmanlığı',
    '["2025-01-01", "2025-04-23", "2025-05-01", "2025-05-19", "2025-08-30", "2025-10-29", "2025-12-25"]',
    'Online', 'Zoom toplantısı bağlantısı randevu onayı sonrası gönderilecektir.', 'Kariyer Danışmanlığı'
),
-- Gelişim Alsancak Anaokulu - Oryantasyon (Cumartesi)
(
    6, 48, 12, 10, 120, '12:00:00', true,
    false, 14, false, true,
    true, '10:00:00', '2025-09-01', '2025-09-30', CURRENT_TIMESTAMP, 4, CURRENT_TIMESTAMP, 4,
    5, 4, 'ORIENTATION', 'SATURDAY', 'Yeni öğrenciler için oryantasyon programı',
    '["2025-09-07", "2025-09-14"]',
    'Etkinlik Salonu', 'Çocuklarınız için atıştırmalık ve oyuncak getirebilirsiniz.', 'Yeni Öğrenci Oryantasyonu'
),
-- Eğitim Dünyası Moda İlkokul - Değerlendirme (Çarşamba)
(
    7,96, 24, 3, 75, '15:00:00', true,
    true, 30, false, true,
    true, '13:45:00', '2025-01-01', '2025-12-31', CURRENT_TIMESTAMP, 2, CURRENT_TIMESTAMP, 2,
    6, 2, 'ASSESSMENT', 'WEDNESDAY', 'Öğrenci yetenek ve seviye değerlendirmesi',
    '["2025-01-01", "2025-04-23", "2025-05-01", "2025-05-19", "2025-08-30", "2025-10-29", "2025-12-25"]',
    'Değerlendirme Odası', 'Çocuğunuzun sevdiği bir oyuncağını getirebilirsiniz. Değerlendirme oyun tarzında yapılmaktadır.', 'Öğrenci Değerlendirmesi'
);

-- ======= APPOINTMENTS =======
INSERT INTO appointments (
    id, appointment_date, end_time, enrollment_likelihood, follow_up_date, follow_up_required,
    is_active, is_online, reschedule_count, start_time, student_age, student_birth_date,
    actual_end_time, actual_start_time, appointment_slot_id, canceled_at, canceled_by,
    confirmed_at, confirmed_by, created_at, created_by, parent_user_id, reminder_sent_at,
    rescheduled_from_id, rescheduled_to_id, school_id, staff_user_id, survey_completed_at,
    survey_sent_at, updated_at, updated_by, appointment_number, appointment_type, canceled_by_type,
    cancellation_reason, current_school, description, grade_interested, internal_notes,
    location, meeting_id, meeting_password, meeting_url, next_steps, notes, outcome,
    outcome_notes, parent_email, parent_name, parent_phone, special_requests, status,
    student_gender, student_name, title
) VALUES
-- Tamamlanmış randevu - Eğitim Dünyası Maslak Anaokulu
(
    1, '2025-08-20', '11:00:00', 85, '2025-09-15', true,
    true, false, 0, '10:00:00', 4, '2021-03-15',
    '2025-08-20 11:05:00', '2025-08-20 10:02:00', 1, null, null,
    '2025-08-18 09:30:00', 1, '2025-08-15 14:22:00', 4, 4, '2025-08-19 18:00:00',
    null, null, 1, 1, '2025-08-21 16:45:00',
    '2025-08-20 14:30:00', '2025-08-21 16:45:00', 4, 'APT-EDM-2025-0001', 'INFORMATION_MEETING', null,
    null, 'Özel Çiçekler Anaokulu', 'Anaokulu programı ve Montessori eğitimi hakkında bilgi almak istiyorum.',
    'Anaokulu', 'Aile çok ilgili ve çocuk sosyal. Kayıt olma ihtimali yüksek.',
    'Konferans Salonu A', null, null, null, 'Kayıt formlarını gönder, kampüs turu ayarla',
    'Çok güzel bir toplantıydı. Öğretmenlerle tanışmak istiyoruz.', 'INTERESTED',
    'Ailevi değerlere önem veriyorlar. Fiyat konusunda endişe yok.', 'ayse.ozkan@email.com',
    'Ayşe Özkan', '+905551234570', 'Çocuğumuz hafif astım problemi yaşıyor.',
    'COMPLETED', 'Kız', 'Elif Özkan', 'Anaokulu Tanıtım Toplantısı'
),
-- Onaylanmış randevu - Eğitim Dünyası Maslak İlkokul
(
    2,'2025-08-30', '16:00:00', 75, null, false,
    true, true, 0, '14:30:00', 6, '2019-01-10',
    null, null, 3, null, null,
    '2025-08-25 11:15:00', 2, '2025-08-23 16:45:00', 3, 4,null,
    null, null, 2, 2, null,
    null, '2025-08-25 11:15:00', 2, 'APT-EDM-2025-0002', 'ENROLLMENT_INTERVIEW', null,
    null, 'Bahçeşehir İlkokulu', 'İlkokul kayıt görüşmesi ve müfredat hakkında bilgi',
    '1. Sınıf', 'Çocuk oldukça zeki ve aktif. İngilizce ilgisi var.',
    'Müdür Yardımcısı Odası', 'zoom-123-456', 'edupass123', 'https://zoom.us/j/123456789',
    'Kayıt belgelerini hazırla', 'Online görüşme tercih ediyoruz.', null, null,
    'mehmet.demir@email.com', 'Mehmet Demir', '+905551234569',
    'Çocuk disleksiye tanısı var, bu konuda desteğinizi merak ediyoruz.',
    'CONFIRMED', 'Erkek', 'Ali Demir', 'Kayıt Görüşmesi'
),
-- Bekleyen randevu - Bilim Sanat Kızılay Ortaokul
(
    3, '2025-08-29', '17:00:00', 60, null, false,
    true, false, 1, '16:30:00', 11, '2014-05-20',
    null, null, 4, null, null,
    null, null, '2025-08-26 10:30:00', 1, 4,null,
    null, null, 3, 3, null,
    null, '2025-08-26 10:30:00', 1, 'APT-BSK-2025-0001', 'PARENT_MEETING', null,
    null, 'Çankaya Ortaokulu', 'Matematik dersindeki performans hakkında görüşme',
    '6. Sınıf', 'Öğrenci matematik konularında zorlanıyor.',
    'Öğretmenler Odası', null, null, null, null,
    'Matematik öğretmeni ile görüşme talep ediyoruz.', null, null,
    'zeynep.kaya@email.com', 'Zeynep Kaya', '+905551234568',
    null, 'PENDING', 'Erkek', 'Emre Kaya', 'Veli Görüşmesi'
),
-- İptal edilmiş randevu - Bilim Sanat Kızılay Lise
(
    4,'2025-08-25', '18:00:00', null, null, false,
    true, true, 0, '17:15:00', 15, '2010-11-30',
    null, null, 5, '2025-08-24 14:20:00', 4,
    '2025-08-22 16:00:00', 3, '2025-08-20 09:45:00', 4, 4,null,
    null, null, 4, 3, null,
    null, '2025-08-24 14:20:00', 4, 'APT-BSK-2025-0002', 'ONLINE_MEETING', 'PARENT',
    'Aile programı değişti', 'Gelişim Koleji Lise', 'Lise tercihi ve kariyer planlaması danışmanlığı',
    '9. Sınıf', null, 'Online', 'zoom-789-012', 'careerpass456', 'https://zoom.us/j/789012345',
    null, 'Randevu tekrar planlanacak.', null, null,
    'ayse.ozkan@email.com', 'Ayşe Özkan', '+905551234570',
    'Çocuğumuz mühendislik alanında kariyer planlamak istiyor.',
    'CANCELLED', 'Kız', 'Selin Özkan', 'Kariyer Danışmanlığı'
),
-- Devam eden randevu - Eğitim Dünyası Moda İlkokul
(
    5,    CURRENT_DATE, '15:00:00', null, null, false,
    true, false, 0, '13:45:00', 7, '2018-09-12',
    CURRENT_TIMESTAMP, null, 7, null, null,
    CURRENT_TIMESTAMP - INTERVAL '2 hours', 2, CURRENT_TIMESTAMP - INTERVAL '3 days', 2, 4,CURRENT_TIMESTAMP - INTERVAL '1 day',
    null, null, 6, 2, null,
    null, CURRENT_TIMESTAMP - INTERVAL '2 hours', 2, 'APT-EDModa-2025-0001', 'ASSESSMENT', null,
    null, 'Kadıköy İlkokulu', 'Öğrenci seviye değerlendirmesi ve sınıf yerleştirme',
    '2. Sınıf', 'Çocuk sanat konularında oldukça yetenekli.',
    'Değerlendirme Odası', null, null, null, null,
    'Sanat ağırlıklı programa uygun mu değerlendiriyoruz.', null, null,
    'ahmet.yilmaz@email.com', 'Ahmet Yılmaz', '+905551234567',
    'Çocuğumuz resim yapmayı çok seviyor.', 'IN_PROGRESS', 'Erkek', 'Ege Yılmaz', 'Öğrenci Değerlendirmesi'
),
-- Gelecek randevu - Gelişim Alsancak Anaokulu (Oryantasyon)
(
    6, '2025-09-07', '12:00:00', null, null, false,
    true, false, 0, '10:00:00', 3, '2022-01-08',
    null, null, 6, null, null,
    '2025-08-26 13:45:00', 4, '2025-08-24 11:20:00', 4, 4,null,
    null, null, 5, 4, null,
    null, '2025-08-26 13:45:00', 4, 'APT-GA-2025-0001', 'ORIENTATION', null,
    null, 'Ev ortamında bakım', 'Yeni öğrenci oryantasyon programına katılım',
    'Anaokulu', 'İlk kez okula başlayacak.',
    'Etkinlik Salonu', null, null, null, 'Oryantasyon sonrası adaptasyon planı hazırla',
    'İlk okul deneyimi olacak, heyecanlı ve kaygılı.', null, null,
    'ayse.ozkan@email.com', 'Ayşe Özkan', '+905551234570',
    'Çocuğumuz çok utangaç, adapte olması zaman alabilir.',
    'CONFIRMED', 'Kız', 'Zeynep Özkan', 'Yeni Öğrenci Oryantasyonu'
);

-- ======= APPOINTMENT NOTES =======
INSERT INTO appointment_notes (
    is_active, is_important, is_private, appointment_id, attachment_size, author_user_id,
    created_at, created_by, note_date, updated_at, updated_by, attachment_name, attachment_type,
    attachment_url, note, note_type
) VALUES
-- Tamamlanan randevu notları
(
    true, true, false, 1, null, 1,
    '2025-08-20 11:10:00', 1, '2025-08-20 11:10:00', '2025-08-20 11:10:00', 1,
    null, null, null,
    'Aile çok olumlu yaklaştı. Çocuk sosyal ve uyumlu görünüyor. Montessori eğitimi konusunda detaylı bilgi verdik. Fiyat konusunda problem yok gibi görünüyor.',
    'OUTCOME'
),
(
    true, false, true, 1, null, 1,
    '2025-08-20 14:35:00', 1, '2025-08-20 14:35:00', '2025-08-20 14:35:00', 1,
    null, null, null,
    'Çocukta hafif astım problemi var. Hemşire hanımla konuşulması gerekiyor. Acil durum protokolü hakkında bilgilendirilecekler.',
    'INTERNAL'
),
-- Gelecek randevu hazırlık notu
(
    true, false, false, 2, 245680, 2,
    '2025-08-27 09:15:00', 2, '2025-08-27 09:15:00', '2025-08-27 09:15:00', 2,
    'disleksi_destek_programi.pdf', 'application/pdf', 'https://egitimara.com/documents/disleksi_destek_programi.pdf',
    'Öğrencinin disleksi tanısı mevcut. Özel eğitim koordinatörümüzle görüşme ayarlandı. Aileve destek program broşürü hazırlandı.',
    'PREPARATION'
),
-- İptal edilen randevu notu
(
    true, false, false, 4, null, 3,
    '2025-08-24 14:25:00', 3, '2025-08-24 14:25:00', '2025-08-24 14:25:00', 3,
    null, null, null,
    'Veli aile programında değişiklik olduğunu belirtti. Yeniden randevu almak istiyorlar. Takip edilecek.',
    'CANCELLATION'
),
-- Devam eden randevu progress notu
(
    true, true, false, 5, null, 2,
    CURRENT_TIMESTAMP, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2,
    null, null, null,
    'Çocuğun sanatsal yetenekleri gerçekten dikkat çekici. Resim ve müzik konularında üstün performans gösteriyor. Sanat ağırlıklı programa çok uygun.',
    'GENERAL'
);

-- ======= APPOINTMENT PARTICIPANTS =======
INSERT INTO appointment_participants (
    is_active, appointment_id, arrival_time, created_at, created_by, departure_time,
    updated_at, updated_by, user_id, attendance_status, email, name, notes, participant_type, phone
) VALUES
-- Tamamlanan randevu katılımcıları
(
    true, 1, '2025-08-20 09:58:00', '2025-08-15 14:22:00', 4, '2025-08-20 11:08:00',
    '2025-08-20 11:08:00', 4, 4, 'ATTENDED', 'ayse.ozkan@email.com', 'Ayşe Özkan',
    'Zamanında geldi, çok ilgiliydi.', 'PARENT', '+905551234570'
),
(
    true, 1, '2025-08-20 09:58:00', '2025-08-15 14:22:00', 4, '2025-08-20 11:08:00',
    '2025-08-20 11:08:00', 4, null, 'ATTENDED', null, 'Elif Özkan',
    'Çok şirin ve sosyal bir çocuk.', 'STUDENT', null
),
(
    true, 1, '2025-08-20 10:00:00', '2025-08-20 10:00:00', 1, '2025-08-20 11:05:00',
    '2025-08-20 11:05:00', 1, 1, 'ATTENDED', 'admin@egitimara.com', 'Ahmet Yılmaz',
    'Sunum başarılıydı.', 'SCHOOL_STAFF', '+902121234567'
),
-- Onaylanmış randevu katılımcıları (gelecek)
(
    true, 2, null, '2025-08-23 16:45:00', 3, null,
    '2025-08-25 11:15:00', 2, 3, 'CONFIRMED', 'mehmet.demir@email.com', 'Mehmet Demir',
    'Online katılım sağlayacak.', 'PARENT', '+905551234569'
),
(
    true, 2, null, '2025-08-23 16:45:00', 3, null,
    '2025-08-25 11:15:00', 2, null, 'CONFIRMED', null, 'Ali Demir',
    'Disleksi tanısı olan öğrenci.', 'STUDENT', null
),
(
    true, 2, null, '2025-08-25 11:15:00', 2, null,
    '2025-08-25 11:15:00', 2, 2, 'CONFIRMED', 'sirket@egitimara.com', 'Zeynep Kaya',
    'Müdür Yardımcısı - Kayıt görüşmesi yapacak.', 'SCHOOL_STAFF', '+902121234568'
),
-- Bekleyen randevu katılımcıları
(
    true, 3, null, '2025-08-26 10:30:00', 1, null,
    '2025-08-26 10:30:00', 1, 2, 'EXPECTED', 'zeynep.kaya@email.com', 'Zeynep Kaya',
    null, 'PARENT', '+905551234568'
),
(
    true, 3, null, '2025-08-26 10:30:00', 1, null,
    '2025-08-26 10:30:00', 1, 3, 'EXPECTED', 'aday@egitimara.com', 'Mehmet Demir',
    'Matematik öğretmeni.', 'SCHOOL_STAFF', '+905551234569'
),
-- Devam eden randevu katılımcıları
(
    true, 5, CURRENT_TIMESTAMP - INTERVAL '5 minutes', CURRENT_TIMESTAMP - INTERVAL '3 days', 2, null,
    CURRENT_TIMESTAMP - INTERVAL '5 minutes', 2, 1, 'ATTENDED', 'ahmet.yilmaz@email.com', 'Ahmet Yılmaz',
    'Değerlendirmeye katılıyor.', 'PARENT', '+905551234567'
),
(
    true, 5, CURRENT_TIMESTAMP - INTERVAL '3 minutes', CURRENT_TIMESTAMP - INTERVAL '3 days', 2, null,
    CURRENT_TIMESTAMP - INTERVAL '3 minutes', 2, null, 'ATTENDED', null, 'Ege Yılmaz',
    'Sanat konularında çok yetenekli.', 'STUDENT', null
),
(
    true, 5, CURRENT_TIMESTAMP - INTERVAL '10 minutes', CURRENT_TIMESTAMP - INTERVAL '3 days', 2, null,
    CURRENT_TIMESTAMP - INTERVAL '10 minutes', 2, 2, 'ATTENDED', 'sirket@egitimara.com', 'Zeynep Kaya',
    'Değerlendirme uzmanı.', 'SCHOOL_STAFF', '+902121234568'
),
-- Gelecek oryantasyon randevu katılımcıları
(
    true, 6, null, '2025-08-24 11:20:00', 4, null,
    '2025-08-26 13:45:00', 4, 4, 'CONFIRMED', 'ayse.ozkan@email.com', 'Ayşe Özkan',
    'Oryantasyon programına katılacak.', 'PARENT', '+905551234570'
),
(
    true, 6, null, '2025-08-24 11:20:00', 4, null,
    '2025-08-26 13:45:00', 4, null, 'CONFIRMED', null, 'Zeynep Özkan',
    'İlk okul deneyimi, utangaç.', 'STUDENT', null
),
(
    true, 6, null, '2025-08-26 13:45:00', 4, null,
    '2025-08-26 13:45:00', 4, 4, 'CONFIRMED', 'user@egitimara.com', 'Ayşe Özkan',
    'Oryantasyon koordinatörü.', 'SCHOOL_STAFF', '+902321892345'
);




-- V5__Insert_Demo_School_Pricing_Data.sql
-- eğitimara.com için okul fiyatlandırma sistemi demo verileri

-- ======= SCHOOL PRICING =======
INSERT INTO school_pricing (
    id, activity_fee, annual_tuition, application_fee, art_fee, book_fee, cafeteria_fee, cancellation_fee,
    down_payment_amount, down_payment_percentage, early_payment_discount_percentage, enrollment_fee,
    exam_fee, extended_day_fee, graduation_fee, highlight_total_cost, installment_amount, installment_count,
    insurance_fee, is_active, is_current, laboratory_fee, language_course_fee, late_payment_penalty_percentage,
    library_fee, loyalty_discount_percentage, maintenance_fee, merit_based_aid_available, monthly_tuition,
    multi_year_discount_percentage, music_fee, need_based_aid_available, private_lesson_fee, registration_fee,
    security_fee, semester_tuition, show_detailed_breakdown, show_financial_aid_info, show_payment_options,
    sibling_discount_percentage, sports_fee, summer_school_fee, technology_fee, total_annual_cost,
    total_monthly_cost, total_one_time_fees, transportation_fee, tutoring_fee, uniform_fee, valid_from,
    valid_until, version, winter_camp_fee, withdrawal_refund_percentage, approved_at, approved_by,
    created_at, created_by, created_by_user_id, previous_pricing_id, school_id, updated_at, updated_by,
    academic_year, approval_notes, class_level, competitor_analysis, currency, fee_breakdown_notes,
    grade_level, internal_notes, market_position, payment_frequency, payment_terms, pricing_tiers,
    public_description, refund_policy, status
) VALUES
-- Eğitim Dünyası Maslak Anaokulu (2025-2026)
(
    1,2500.00, 54000.00, 500.00, 1200.00, 800.00, 3600.00, 1000.00,
    15000.00, 25.00, 5.00, 2500.00,
    0.00, 2400.00, 800.00, true, 4500.00, 12,
    600.00, true, true, 0.00, 1800.00, 2.00,
    400.00, 3.00, 1200.00, true, 4500.00,
    5.00, 1500.00, true, 0.00, 2500.00,
    800.00, 27000.00, true, true, true,
    10.00, 1800.00, 3500.00, 1500.00, 64500.00,
    5375.00, 8300.00, 2200.00, 0.00, 1200.00, '2025-09-01',
    '2026-08-31', 1, 2800.00, 50.00, '2025-06-15 10:30:00', 1,
    '2025-06-10 14:20:00', 1, 1, null, 1, '2025-06-15 10:30:00', 1,
    '2025-2026', 'Fiyatlar piyasa araştırması sonrası onaylandı.', 'Küçük Yaş',
    '{"competitors": [{"name": "ABC Anaokulu", "price": 52000, "notes": "Benzer kalite"}, {"name": "XYZ Kreş", "price": 48000, "notes": "Daha basit program"}]}',
    'TRY', 'Montessori eğitim programı maliyetleri dahil. Organik beslenme ek ücret.',
    'Anaokulu', 'Premium segment hedefleniyor. Bölgedeki en kaliteli anaokulu konumundayız.',
    'Premium', 'MONTHLY', 'Aylık ödemeler ayın 5''ine kadar yapılmalıdır. Geç ödeme %2 faiz uygulanır.',
    '{"basic": {"monthly": 4500, "annual": 54000}, "premium": {"monthly": 5200, "annual": 62400, "includes": ["extended_day", "private_lessons"]}}',
    'Montessori eğitim metoduyla çocuklarınızın doğal gelişimini destekliyoruz. Organik beslenme, sanat atölyeleri ve İngilizce programı dahil.',
    'Kayıt iptalinde ilk taksit iade edilmez. Akademik yıl başlamadan önce iptal durumunda %50 iade yapılır.',
    'ACTIVE'
),
-- Eğitim Dünyası Maslak İlkokul (2025-2026)
(
    2,3200.00, 78000.00, 800.00, 1800.00, 1200.00, 4800.00, 1500.00,
    20000.00, 25.00, 3.00, 3500.00,
    500.00, 3600.00, 1200.00, true, 6500.00, 12,
    800.00, true, true, 1500.00, 2400.00, 2.00,
    600.00, 3.00, 1800.00, true, 6500.00,
    5.00, 2200.00, true, 200.00, 3500.00,
    1200.00, 39000.00, true, true, true,
    10.00, 2400.00, 4200.00, 2000.00, 95200.00,
    7933.33, 12800.00, 3000.00, 800.00, 1500.00, '2025-09-01',
    '2026-08-31', 1, 3800.00, 50.00, '2025-06-20 11:45:00', 2,
    '2025-06-18 09:15:00', 2, 2, null, 2, '2025-06-20 11:45:00', 2,
    '2025-2026', 'Cambridge programı maliyetleri eklendi.', '1-4. Sınıf',
    '{"competitors": [{"name": "Bahçeşehir İlkokul", "price": 82000, "notes": "Daha pahalı"}, {"name": "Özel Çiçek İlkokul", "price": 65000, "notes": "Daha ekonomik"}]}',
    'TRY', 'Cambridge müfredatı ve yoğun İngilizce programı maliyetleri dahil.',
    '1-4. Sınıf', 'Rekabet analizi yapıldı, orta-üst segment fiyatlandırması uygun görüldü.',
    'Mid-Premium', 'MONTHLY', 'Yıllık ödeme %3 indirim. Kardeş indirimi %10. Akademik başarı bursu mevcut.',
    '{"standard": {"monthly": 6500, "annual": 78000}, "cambridge_plus": {"monthly": 7200, "annual": 86400, "includes": ["extra_english", "stem"]}}',
    'Cambridge Primary programı ile uluslararası standartlarda eğitim. STEM laboratuvarları ve sanat atölyeleri ile çocuklarınızın yeteneklerini keşfedin.',
    'Akademik yıl başlamadan 30 gün öncesine kadar iptal durumunda %70 iade. Sonrasında iade yapılmaz.',
    'ACTIVE'
),
-- Bilim Sanat Kızılay Ortaokul (2025-2026)
(
    3,4500.00, 96000.00, 1000.00, 2500.00, 1500.00, 5400.00, 2000.00,
    25000.00, 25.00, 4.00, 4000.00,
    800.00, 4200.00, 1500.00, true, 8000.00, 12,
    1000.00, true, true, 2500.00, 3200.00, 2.50,
    800.00, 5.00, 2200.00, true, 8000.00,
    7.00, 3000.00, true, 400.00, 4000.00,
    1500.00, 48000.00, true, true, true,
    12.00, 3500.00, 5800.00, 2800.00, 118700.00,
    9891.67, 16700.00, 3800.00, 1200.00, 1800.00, '2025-09-01',
    '2026-08-31', 1, 4500.00, 60.00, '2025-07-02 14:22:00', 3,
    '2025-06-28 16:40:00', 3, 3, null, 3, '2025-07-02 14:22:00', 3,
    '2025-2026', 'STEAM program maliyetleri analiz edildi. Uygun fiyat seviyesi.',
    '5-8. Sınıf',
    '{"competitors": [{"name": "TED Ankara Koleji", "price": 125000, "notes": "Premium segment"}, {"name": "Bilkent Ortaokul", "price": 105000, "notes": "Benzer kalite"}]}',
    'TRY', 'STEAM eğitimi, robotik atölyesi ve bilim olimpiyatları hazırlık programı dahil.',
    '5-8. Sınıf', 'Ankara bölgesindeki en güçlü STEAM programına sahibiz. Fiyat konumlandırması rekabetçi.',
    'Premium', 'MONTHLY', 'Burs imkanları mevcut. Akademik başarı %15 indirim. Ailevi zorluk durumunda ödeme planı.',
    '{"steam": {"monthly": 8000, "annual": 96000}, "advanced_steam": {"monthly": 9200, "annual": 110400, "includes": ["robotics", "ai_basics", "3d_design"]}}',
    'STEAM metodolojisi ile bilim, teknoloji, mühendislik, sanat ve matematiği entegre eden yenilikçi eğitim programı.',
    'Akademik yıl öncesi iptal %60 iade. İlk dönem sonrası iptal durumunda kalan dönem %40 iadesi.',
    'ACTIVE'
),
-- Bilim Sanat Kızılay Lise (2025-2026)
(
    4, 6000.00, 128000.00, 1500.00, 3500.00, 2000.00, 6000.00, 3000.00,
    35000.00, 25.00, 5.00, 6000.00,
    1200.00, 5000.00, 2500.00, true, 10667.00, 12,
    1500.00, true, true, 3500.00, 4800.00, 3.00,
    1200.00, 5.00, 3000.00, true, 10667.00,
    8.00, 4500.00, true, 600.00, 6000.00,
    2000.00, 64000.00, true, true, true,
    15.00, 4500.00, 7200.00, 4000.00, 164200.00,
    13683.33, 23700.00, 4500.00, 2000.00, 2500.00, '2025-09-01',
    '2026-08-31', 1, 6500.00, 70.00, '2025-07-10 15:55:00', 3,
    '2025-07-05 10:20:00', 3, 3, null, 4, '2025-07-10 15:55:00', 3,
    '2025-2026', 'IB Diploma programı ek maliyetleri onaylandı.', '9-12. Sınıf',
    '{"competitors": [{"name": "Bilkent Lise", "price": 155000, "notes": "Benzer IB program"}, {"name": "Özel Tevfik Fikret", "price": 135000, "notes": "Güçli akademik kadro"}]}',
    'TRY', 'IB Diploma programı, üniversite hazırlık kursu ve kariyer danışmanlığı dahil.',
    '9-12. Sınıf', 'IB World School statümüz ile uluslararası tanınırlığımız fiyatlandırmayı destekliyor.',
    'Premium', 'MONTHLY', 'IB sınav ücretleri ayrı. Üniversite başvuru danışmanlığı dahil. Merit burs %20''ye kadar.',
    '{"ib_standard": {"monthly": 10667, "annual": 128000}, "ib_premium": {"monthly": 12000, "annual": 144000, "includes": ["sat_prep", "university_counseling", "summer_programs"]}}',
    'IB Diploma programı ile dünya üniversitelerine hazırlık. Uluslararası standartlarda eğitim ve kariyer danışmanlığı.',
    'IB programı özel koşullara tabidir. Akademik yıl başladıktan sonra iade yapılmaz.',
    'ACTIVE'
),
-- Gelişim Alsancak Anaokulu (2025-2026) - Daha ekonomik
(
    5,1800.00, 38000.00, 300.00, 800.00, 600.00, 2400.00, 800.00,
    10000.00, 25.00, 3.00, 1500.00,
    0.00, 1800.00, 600.00, true, 3167.00, 12,
    400.00, true, true, 0.00, 1200.00, 2.00,
    300.00, 5.00, 800.00, true, 3167.00,
    3.00, 1000.00, true, 0.00, 1500.00,
    600.00, 19000.00, true, true, true,
    15.00, 1200.00, 2500.00, 1000.00, 46100.00,
    3841.67, 5400.00, 1600.00, 0.00, 800.00, '2025-09-01',
    '2026-08-31', 1, 2000.00, 40.00, '2025-06-25 13:10:00', 4,
    '2025-06-20 11:35:00', 4, 4, null, 5, '2025-06-25 13:10:00', 4,
    '2025-2026', 'Maliyet etkin fiyatlandırma stratejisi onaylandı.', 'Küçük-Orta Yaş',
    '{"competitors": [{"name": "Mutlu Çocuklar Kreş", "price": 35000, "notes": "Benzer seviye"}, {"name": "Güneş Anaokulu", "price": 42000, "notes": "Biraz daha pahalı"}]}',
    'TRY', 'Geleneksel değerler ve modern eğitim yaklaşımının birleşimi. Uygun fiyatlı kaliteli eğitim.',
    'Anaokulu', '35 yıllık deneyimimiz ve uygun fiyat politikamız ile ailelere değer sunuyoruz.',
    'Mid-Market', 'MONTHLY', 'Kardeş indirimi %15. Peşin ödeme %3 indirim. Esnek ödeme planları mevcuttur.',
    '{"standard": {"monthly": 3167, "annual": 38000}, "extended": {"monthly": 3600, "annual": 43200, "includes": ["extended_day", "weekend_activities"]}}',
    '35 yıllık deneyimimizle geleneksel değerleri modern eğitim yaklaşımıyla harmanlıyoruz. Aile bütçelerine uygun kaliteli eğitim.',
    'Akademik yıl öncesi iptal %40 iade. Akademik yıl başladıktan sonra aylık bazda hesap yapılır.',
    'ACTIVE'
),
-- Eğitim Dünyası Moda İlkokul (2025-2026) - Sanat ağırlıklı premium
(
    6, 4800.00, 86400.00, 1000.00, 4500.00, 1400.00, 5200.00, 1800.00,
    22000.00, 25.00, 4.00, 4200.00,
    600.00, 4000.00, 1400.00, true, 7200.00, 12,
    900.00, true, true, 2000.00, 3600.00, 2.50,
    700.00, 4.00, 2000.00, true, 7200.00,
    6.00, 3800.00, true, 300.00, 4200.00,
    1400.00, 43200.00, true, true, true,
    12.00, 2800.00, 4800.00, 2400.00, 109000.00,
    9083.33, 16500.00, 3200.00, 1000.00, 1600.00, '2025-09-01',
    '2026-08-31', 1, 4200.00, 55.00, '2025-07-08 12:25:00', 2,
    '2025-07-03 08:50:00', 2, 2, null, 6, '2025-07-08 12:25:00', 2,
    '2025-2026', 'Sanat ağırlıklı program premium fiyatlandırması uygun.', '1-4. Sınıf',
    '{"competitors": [{"name": "Mimar Sinan Sanat İlkokulu", "price": 92000, "notes": "Benzer sanat programı"}, {"name": "Pera Sanat Okulu", "price": 78000, "notes": "Daha sınırlı program"}]}',
    'TRY', 'Sanat atölyeleri, müze gezileri, profesyonel sanatçı eğitmenler dahil. Premium sanat eğitimi.',
    '1-4. Sınıf', 'Moda semtindeki lokasyonumuz ve sanat odaklı programımız ile niche pazarda lideriz.',
    'Premium', 'MONTHLY', 'Sanat malzeme ücreti dahil. Sergi ve performans masrafları okul tarafından karşılanır.',
    '{"art_focused": {"monthly": 7200, "annual": 86400}, "art_intensive": {"monthly": 8400, "annual": 100800, "includes": ["private_art_lessons", "professional_mentoring", "exhibition_participation"]}}',
    'Sanat ağırlıklı eğitim programımızla çocukların yaratıcılığını ve estetik algısını geliştiriyoruz. Profesyonel sanat eğitmenleri ile nitelikli eğitim.',
    'Sanat programı özel koşulları nedeniyle akademik yıl başladıktan sonra iade yapılmaz.',
    'ACTIVE'
);

-- ======= CUSTOM FEES =======
INSERT INTO custom_fees (
    advance_notice_days, applies_to_existing_students, applies_to_new_students, average_payment_delay_days,
    collection_rate, discount_eligible, display_on_invoice, display_order, documentation_required,
    due_date_offset_days, fee_amount, installment_allowed, is_active, is_mandatory, is_refundable,
    late_fee_percentage, max_installments, maximum_age, minimum_age, parent_notification_required,
    requires_approval, scholarship_applicable, students_charged, students_paid, total_collected,
    valid_from, valid_until, approved_at, approved_by, created_at, created_by, created_by_user_id,
    school_pricing_id, updated_at, updated_by, applies_to_grades, approval_notes, fee_description,
    fee_frequency, fee_name, fee_policy, fee_type, mutually_exclusive_fees, prerequisite_fees,
    required_documents, status
) VALUES
-- Eğitim Dünyası Maslak Anaokulu - Doğum günü partisi ücreti
(
    7, true, true, 3.5, 0.95, false, true, 1, false,
    0, 750.00, false, true, false, false,
    0.0, 1, 6, 2, true,
    false, false, 24, 22, 16500.00,
    '2025-09-01', '2026-08-31', '2025-08-15 14:30:00', 1, '2025-08-10 09:20:00', 1, 1,
    1, '2025-08-15 14:30:00', 1, 'Tüm sınıflar', 'Okul içi doğum günü kutlaması onaylandı.',
    'Sınıf içinde doğum günü kutlaması için dekorasyon, pasta ve oyun aktivitesi dahil özel ücret.',
    'ONE_TIME', 'Doğum Günü Kutlama Ücreti',
    'Ailelerden 1 hafta önceden bildirim istenir. Sınıf mevcudu max 15 çocuk için uygulanır.',
    'SPECIAL_EVENT', null, null, null, 'ACTIVE'
),
-- Eğitim Dünyası Maslak İlkokul - Yaz okulu ücreti
(
    30, false, true, 5.2, 0.88, true, true, 2, true,
    15, 4200.00, true, true, false, true,
    2.0, 3, 10, 6, true,
    true, true, 45, 38, 159600.00,
    '2026-06-15', '2026-08-30', '2025-04-20 11:15:00', 2, '2025-04-15 16:45:00', 2, 2,
    2, '2025-04-20 11:15:00', 2, '1-4. Sınıf', 'Yaz okulu programı müdürlük tarafından onaylandı.',
    '8 haftalık yaz okulu programı. STEM aktiviteleri, İngilizce kampı ve spor etkinlikleri dahil.',
    'ONE_TIME', 'Yaz Okulu Ücreti',
    'Minimum 20 öğrenci katılımı gerekir. İptal durumunda program başlamadan 1 hafte öncesine kadar %50 iade.',
    'SUMMER_PROGRAM', '["winter_camp_fee"]', null,
    '["health_certificate", "swimming_permission"]', 'ACTIVE'
),
-- Bilim Sanat Kızılay Ortaokul - Robotik kulüp ücreti
(
    14, true, true, 2.8, 0.92, false, true, 3, false,
    7, 1800.00, false, true, false, false,
    0.0, 1, 14, 10, true,
    false, true, 32, 29, 52200.00,
    '2025-10-01', '2026-05-31', '2025-09-20 13:40:00', 3, '2025-09-15 10:05:00', 3, 3,
    3, '2025-09-20 13:40:00', 3, '5-8. Sınıf', 'Robotik kulübü için gerekli malzeme maliyeti hesaplandı.',
    'Robotik kulübü üyelik ücreti. Arduino kitleri, sensörler ve programlama yazılımları dahil.',
    'SEMESTER', 'Robotik Kulübü Ücreti',
    'Dönem başında ödenir. Malzemeler öğrenci sorumluluğundadır. Kayıp/hasarlı malzeme ek ücrete tabidir.',
    'CLUB', null, null, null, 'ACTIVE'
),
-- Bilim Sanat Kızılay Lise - IB sınav ücreti
(
    60, true, false, 8.1, 1.00, false, true, 1, true,
    30, 3200.00, false, true, true, false,
    0.0, 1, 18, 16, true,
    false, false, 28, 28, 89600.00,
    '2026-01-01', '2026-05-31', '2025-11-15 09:25:00', 3, '2025-11-10 14:55:00', 3, 3,
    4, '2025-11-15 09:25:00', 3, '11-12. Sınıf', 'IB Organization tarafından belirlenen resmi sınav ücreti.',
    'IB Diploma sınavları için uluslararası organizasyona ödenecek resmi sınav ücreti.',
    'ONE_TIME', 'IB Diploma Sınav Ücreti',
    'IB Organization resmi ücreti. Öğrenci sınava girmeme durumunda iade edilmez.',
    'EXAMINATION', null, null,
    '["ib_registration_form", "passport_copy"]', 'ACTIVE'
),
-- Gelişim Alsancak Anaokulu - Geziler için ek ücret
(
    10, true, true, 4.0, 0.90, true, true, 4, false,
    5, 350.00, false, true, false, true,
    0.0, 1, 6, 3, true,
    false, true, 48, 42, 14700.00,
    '2025-09-01', '2026-08-31', '2025-08-20 15:20:00', 4, '2025-08-18 12:40:00', 4, 4,
    5, '2025-08-20 15:20:00', 4, 'Tüm sınıflar', 'Ayda 1 gezi için uygun maliyetli ücretlendirme.',
    'Aylık eğitsel gezi ücreti. Müze, hayvanat bahçesi, çocuk tiyatrosu gibi etkinlikler dahil.',
    'MONTHLY', 'Eğitsel Gezi Ücreti',
    'Ayda minimum 1 gezi düzenlenir. Hava koşulları nedeniyle iptal durumunda ertesi aya aktarılır.',
    'FIELD_TRIP', null, null, '["parent_consent"]', 'ACTIVE'
),
-- Eğitim Dünyası Moda İlkokul - Sanat malzeme ücreti
(
    15, true, true, 6.3, 0.85, false, true, 5, false,
    10, 2200.00, true, true, false, false,
    1.5, 4, 10, 6, true,
    false, false, 58, 49, 107800.00,
    '2025-09-01', '2026-08-31', '2025-08-25 16:10:00', 2, '2025-08-20 11:25:00', 2, 2,
    6, '2025-08-25 16:10:00', 2, '1-4. Sınıf', 'Sanat atölyeleri için premium malzeme maliyeti.',
    'Sanat atölyelerinde kullanılacak profesyonel boyalar, fırçalar, kağıtlar ve özel malzemeler.',
    'SEMESTER', 'Sanat Malzeme Ücreti',
    'Dönem başında ödenir. Kişisel sanat dosyası ve portfolyo hazırlama dahil.',
    'ART', null, null, null, 'ACTIVE'
);

-- ======= PRICE HISTORY =======
INSERT INTO price_history (
    advance_notice_days, affected_students_count, can_rollback, change_amount, change_percentage,
    effective_date, is_active, new_value, old_value, parents_notified, revenue_impact,
    approved_at, approved_by, change_date, changed_by_user_id, created_at, created_by,
    notification_date, rollback_deadline, rolled_back_at, rolled_back_by, school_pricing_id,
    updated_at, updated_by, change_notes, change_type, competitive_analysis, field_name,
    notification_method, reason, rollback_reason
) VALUES
-- Eğitim Dünyası Maslak Anaokulu - 2024'ten 2025'e fiyat artışı
(
    45, 68, false, 4000.00, 8.00,
    '2025-09-01', true, 54000.00, 50000.00, true, 272000.00,
    '2025-06-15 10:30:00', 1, '2025-06-10 14:20:00', 1, '2025-06-10 14:20:00', 1,
    '2025-07-15 09:00:00', null, null, null, 1,
    '2025-06-15 10:30:00', 1,
    'Enflasyon oranı ve Montessori program geliştirme maliyetleri nedeniyle artış gerekli görüldü.',
    'INFLATION_ADJUSTMENT',
    'Benzer kalitedeki anaokullarının ortalama %10 artış yaptığı görüldü. Bizim artışımız %8 ile rekabetçi kaldı.',
    'INFLATION_ADJUSTMENT',
    'Rekabet analizinde bölgedeki premium anaokullarının %8-12 arasında artış yaptığı tespit edildi.',
    'annual_tuition', 'Email + SMS + Veli toplantısı. Enflasyon ve kalite iyileştirme maliyetleri'
),
-- Eğitim Dünyası Maslak İlkokul - Teknoloji ücreti eklenmesi
(
    30, 287, false, 2000.00, null,
    '2025-09-01', true, 2000.00, 0.00, true, 574000.00,
    '2025-06-20 11:45:00', 2, '2025-06-18 09:15:00', 2, '2025-06-18 09:15:00', 2,
    '2025-07-20 10:30:00', null, null, null, 2,
    '2025-06-20 11:45:00', 2,
    'iPad Pro ve eğitsel yazılım lisansları için yeni teknoloji ücreti eklendi.',
    'NEW_FEE',
    'Diğer Cambridge okulları benzer teknoloji ücretleri uyguluyor (1800-2500 TL arası).',
    'technology_fee', 'Veli toplantısı + Yazılı bilgilendirme',
    'Dijital eğitim altyapısı güçlendirme', null
),
-- Bilim Sanat Kızılay Lise - IB ücreti güncellemesi
(
    90, 178, false, 800.00, 3.13,
    '2026-01-01', true, 26400.00, 25600.00, true, 142400.00,
    '2025-07-10 15:55:00', 3, '2025-07-05 10:20:00', 3, '2025-07-05 10:20:00', 3,
    '2025-10-01 08:00:00', null, null, null, 4,
    '2025-07-10 15:55:00', 3,
    'IB Organization''ın resmi ücret artışı nedeniyle güncelleme yapıldı.',
    'REGULATORY_CHANGE',
    'Tüm IB okulları aynı artışı uygulamak zorunda. Uluslararası standart.',
    'semester_tuition', 'Email + Resmi duyuru',
    'IB Organization resmi ücret artışı', null
),
-- Gelişim Alsancak Anaokulu - Erken ödeme indirimi artışı
(
    15, 54, true, -500.00, -1.30,
    '2025-09-01', true, 37500.00, 38000.00, true, -27000.00,
    '2025-06-25 13:10:00', 4, '2025-06-20 11:35:00', 4, '2025-06-20 11:35:00', 4,
    '2025-07-05 14:00:00', '2025-08-20 23:59:59', null, null, 5,
    '2025-06-25 13:10:00', 4,
    'Yeni öğrenci çekebilmek için erken ödeme indirimini %3''ten %5''e çıkardık.',
    'PROMOTION',
    'Bölgedeki diğer okullar da benzer promosyonlar uyguluyor. Rekabet gereği.',
    'annual_tuition', 'Telefon + WhatsApp',
    'Rekabet avantajı sağlama', null
),
-- Eğitim Dünyası Moda İlkokul - Sanat malzeme ücreti ayarlaması
(
    20, 162, false, 400.00, 22.22,
    '2026-01-15', true, 2200.00, 1800.00, true, 64800.00,
    '2025-07-08 12:25:00', 2, '2025-07-03 08:50:00', 2, '2025-07-03 08:50:00', 2,
    '2025-12-01 09:30:00', null, null, null, 6,
    '2025-07-08 12:25:00', 2,
    'Premium sanat malzemesi tedarikçi fiyat artışları nedeniyle güncelleme gerekli.',
    'COST_ADJUSTMENT',
    'Sanat malzemesi maliyetleri %25 arttı. Bizim artışımız %22 ile makul seviyede.',
    'art_fee', 'Veli toplantısı + Email',
    'Tedarikçi maliyet artışları', null
),
-- Bilim Sanat Kızılay Ortaokul - Laboratuvar ücreti iptal edilmesi
(
    30, 216, true, -1500.00, -100.00,
    '2025-09-01', true, 0.00, 1500.00, true, -324000.00,
    '2025-07-02 14:22:00', 3, '2025-06-28 16:40:00', 3, '2025-06-28 16:40:00', 3,
    '2025-08-01 10:00:00', '2025-08-25 23:59:59', null, null, 3,
    '2025-07-02 14:22:00', 3,
    'Ana ücrete dahil edilmesi kararlaştırıldı. Şeffaflık için ayrı ücret kaldırıldı.',
    'REMOVED_FEE',
    'Velilerden gelen geri bildirimler doğrultusunda ücret yapısı basitleştirildi.',
    'laboratory_fee', 'Veli anketi + Bilgilendirme toplantısı',
    'Ücret yapısını basitleştirme', null
);



-- V6__Insert_Demo_Survey_Data.sql
-- eğitimara.com için anket ve değerlendirme sistemi demo verileri

-- ======= SURVEYS =======
INSERT INTO surveys (
    average_completion_time_seconds, average_rating, expires_after_days, is_active, is_anonymous,
    is_mandatory, max_reminders, reminder_delay_hours, send_delay_hours, show_results_to_public,
    created_at, created_by, total_completed, total_sent, total_started, updated_at, updated_by,
    completion_redirect_url, custom_css, description, email_body, email_subject, email_template_id,
    header_image_url, logo_url, primary_color, survey_type, thank_you_message, title,
    trigger_event, welcome_message
) VALUES
-- Randevu sonrası geri bildirim anketi
(
    245, 4.3, 7, true, false,
    false, 2, 48, 4, false,
    CURRENT_TIMESTAMP - INTERVAL '60 days', 1, 156, 234, 189, CURRENT_TIMESTAMP - INTERVAL '5 days', 1,
    'https://egitimara.com/thank-you', '.survey-container { font-family: "Segoe UI", sans-serif; }',
    'Randevu deneyiminiz hakkındaki görüşleriniz bizim için çok değerli. Lütfen birkaç dakika ayırarak sorularımızı yanıtlayın.',
    'Merhaba {parent_name}, {school_name} ziyaretiniz hakkındaki görüşlerinizi merak ediyoruz. Anketimizi tamamlamanız sadece 3-4 dakika sürecek.',
    'Randevu Deneyiminizi Değerlendirin - {school_name}', 'appointment_feedback_template',
    'https://egitimara.com/images/survey-header.jpg', 'https://egitimara.com/images/logo.png',
    '#2C5282', 'APPOINTMENT_FEEDBACK',
    'Değerli görüşleriniz için teşekkür ederiz! Geri bildirimleriniz hizmet kalitemizi artırmak için kullanılacaktır.',
    'Randevu Geri Bildirim Anketi', 'APPOINTMENT_COMPLETED',
    'Ziyaretiniz hakkındaki deneyimlerinizi paylaşın. Görüşleriniz gelecekteki velilerimize yardımcı olacak.'
),
-- Okul genel değerlendirme anketi
(
    380, 4.1, 14, true, true,
    false, 3, 72, 8, true,
    CURRENT_TIMESTAMP - INTERVAL '90 days', 1, 89, 145, 112, CURRENT_TIMESTAMP - INTERVAL '10 days', 1,
    'https://egitimara.com/survey-complete', '.rating-stars { color: #FFD700; font-size: 1.5em; }',
    'Okulumuz hakkındaki genel değerlendirmenizi almak istiyoruz. Bu anket tamamen anonim olup sonuçlar herkese açık paylaşılacaktır.',
    'Okulumuz hakkındaki düşüncelerinizi paylaşın. Anonim olan bu ankette dürüst görüşleriniz çok önemli.',
    'Okul Değerlendirme Anketi - Görüşlerinizi Paylaşın', 'school_rating_template',
    'https://egitimara.com/images/school-survey-header.jpg', 'https://egitimara.com/images/logo.png',
    '#38A169', 'SCHOOL_RATING',
    'Anketimizi tamamladığınız için çok teşekkürler. Değerlendirmeleriniz diğer ailelerle paylaşılacaktır.',
    'Okul Değerlendirme Anketi', 'MANUAL_SEND',
    'Bu okul hakkındaki deneyimlerinizi ve düşüncelerinizi bizimle paylaşın. Görüşleriniz diğer ailelere yol gösterecektir.'
),
-- Kayıt sonrası memnuniyet anketi
(
    180, 4.5, 10, true, false,
    true, 1, 24, 2, false,
    CURRENT_TIMESTAMP - INTERVAL '45 days', 2, 67, 78, 74, CURRENT_TIMESTAMP - INTERVAL '2 days', 2,
    'https://egitimara.com/enrollment-thanks', null,
    'Kayıt süreciniz tamamlandı! Bu süreçteki deneyiminizi değerlendirmenizi rica ediyoruz.',
    'Tebrikler! {student_name} kayıt işlemi tamamlandı. Kayıt sürecindeki deneyiminizi değerlendirin.',
    'Kayıt Süreci Değerlendirmesi - {school_name}', 'enrollment_feedback_template',
    null, 'https://egitimara.com/images/logo.png',
    '#9F7AEA', 'ENROLLMENT_FEEDBACK',
    'Geri bildiriminiz için teşekkürler! İyi bir başlangıç için sizinle iletişimde kalacağız.',
    'Kayıt Süreci Memnuniyet Anketi', 'ENROLLMENT_COMPLETED',
    'Kayıt sürecindeki deneyiminizi değerlendirerek gelecekteki ailelere yardımcı olun.'
),
-- Hizmet kalitesi değerlendirme anketi
(
    420, 3.9, 21, true, true,
    false, 2, 96, 12, true,
    CURRENT_TIMESTAMP - INTERVAL '30 days', 3, 234, 345, 289, CURRENT_TIMESTAMP - INTERVAL '3 days', 3,
    'https://egitimara.com/service-feedback', '.question-block { margin: 20px 0; padding: 15px; }',
    'Okulumuzun sunduğu hizmetlerin kalitesini değerlendirin. Bu değerlendirme sonuçları hizmet iyileştirmelerimizde kullanılacaktır.',
    'Okulumuzun hizmet kalitesi hakkındaki görüşlerinizi öğrenmek istiyoruz. Lütfen değerlendirmenizi yapın.',
    'Hizmet Kalitesi Değerlendirmesi', 'service_quality_template',
    'https://egitimara.com/images/service-header.jpg', 'https://egitimara.com/images/logo.png',
    '#E53E3E', 'SERVICE_QUALITY',
    'Değerlendirmeniz tamamlandı. Görüşleriniz hizmet kalitemizi artırmaya yardımcı olacak.',
    'Hizmet Kalitesi Anketi', 'PERIODIC',
    'Okulumuzun sunduğu hizmetleri farklı kategorilerde değerlendirin.'
);

-- ======= SURVEY QUESTIONS =======
INSERT INTO survey_questions (
    allow_other_option, average_rating, is_active, is_required, rating_scale_max, rating_scale_min,
    rating_scale_step, sort_order, text_max_length, text_min_length, created_at, created_by,
    show_if_question_id, skip_count, survey_id, total_responses, updated_at, updated_by,
    custom_css_class, description, help_text, image_url, options, other_option_label,
    placeholder_text, question_text, question_type, rating_category, rating_labels,
    show_if_answer, show_if_condition
) VALUES
-- Randevu anketi soruları
(
    false, 4.2, true, true, 5, 1,
    1, 1, null, null, CURRENT_TIMESTAMP - INTERVAL '60 days', 1,
    null, 12, 1, 156, CURRENT_TIMESTAMP - INTERVAL '5 days', 1,
    'overall-rating', null, '5 yıldız en yüksek puan', null,
    null, null, null,
    'Genel olarak randevu deneyiminizi nasıl değerlendiriyorsunuz?', 'RATING_STAR',
    'OVERALL_SATISFACTION', '{"1": "Çok Kötü", "2": "Kötü", "3": "Orta", "4": "İyi", "5": "Mükemmel"}',
    null, null
),
(
    false, 4.1, true, true, 5, 1,
    1, 2, null, null, CURRENT_TIMESTAMP - INTERVAL '60 days', 1,
    null, 8, 1, 148, CURRENT_TIMESTAMP - INTERVAL '5 days', 1,
    'staff-rating', null, null, null,
    null, null, null,
    'Personelimizin tutumu ve yardımseverliğini nasıl buluyorsunuz?', 'RATING_STAR',
    'STAFF_FRIENDLINESS', '{"1": "Çok Kötü", "2": "Kötü", "3": "Orta", "4": "İyi", "5": "Mükemmel"}',
    null, null
),
(
    true, null, true, false, null, null,
    null, 3, null, null, CURRENT_TIMESTAMP - INTERVAL '60 days', 1,
    null, 23, 1, 133, CURRENT_TIMESTAMP - INTERVAL '5 days', 1,
    'enrollment-likelihood', null, null, null,
    '["Kesinlikle kaydolurum", "Büyük ihtimalle kaydolurum", "Kararsızım", "Muhtemelen kaydolmam", "Kesinlikle kaydolmam"]',
    'Diğer (açıklayın)', null,
    'Bu okula kayıt yaptırma ihtimaliniz nedir?', 'SINGLE_CHOICE',
    'RECOMMENDATION', null, null, null
),
(
    false, null, true, false, null, null,
    null, 4, 500, 10, CURRENT_TIMESTAMP - INTERVAL '60 days', 1,
    null, 45, 1, 111, CURRENT_TIMESTAMP - INTERVAL '5 days', 1,
    'feedback-text', null, null, null,
    null, null, 'Önerilerinizi buraya yazabilirsiniz...',
    'Okulumuz hakkında eklemek istediğiniz görüşleriniz var mı?', 'TEXT_LONG',
    'CUSTOM', null, null, null
),

-- Okul değerlendirme anketi soruları
(
    false, 4.0, true, true, 5, 1,
    1, 1, null, null, CURRENT_TIMESTAMP - INTERVAL '90 days', 1,
    null, 15, 2, 89, CURRENT_TIMESTAMP - INTERVAL '10 days', 1,
    'academic-quality', null, null, null,
    null, null, null,
    'Okulun akademik kalitesini nasıl değerlendiriyorsunuz?', 'RATING_STAR',
    'ACADEMIC_QUALITY', '{"1": "Çok Zayıf", "2": "Zayıf", "3": "Orta", "4": "İyi", "5": "Mükemmel"}',
    null, null
),
(
    false, 3.8, true, true, 5, 1,
    1, 2, null, null, CURRENT_TIMESTAMP - INTERVAL '90 days', 1,
    null, 12, 2, 77, CURRENT_TIMESTAMP - INTERVAL '10 days', 1,
    'facilities-rating', null, null, null,
    null, null, null,
    'Okul tesisleri ve fiziki imkanları nasıl?', 'RATING_STAR',
    'FACILITIES', '{"1": "Çok Yetersiz", "2": "Yetersiz", "3": "Yeterli", "4": "İyi", "5": "Çok İyi"}',
    null, null
),
(
    false, null, true, true, null, null,
    null, 3, null, null, CURRENT_TIMESTAMP - INTERVAL '90 days', 1,
    null, 8, 2, 81, CURRENT_TIMESTAMP - INTERVAL '10 days', 1,
    'recommendation', null, null, null,
    null, null, null,
    'Bu okulu başka ailelere tavsiye eder misiniz?', 'YES_NO',
    'RECOMMENDATION', null, null, null
),
(
    true, null, true, false, null, null,
    null, 4, null, null, CURRENT_TIMESTAMP - INTERVAL '90 days', 1,
    3, 18, 2, 71, CURRENT_TIMESTAMP - INTERVAL '10 days', 1,
    'why-recommend', null, null, null,
    '["Akademik kalite", "Öğretmen kadrosu", "Fiziki imkanlar", "Sosyal aktiviteler", "Okul kültürü", "Fiyat"]',
    'Diğer nedenler', null,
    'Tavsiye etme nedeniniz nedir?', 'MULTIPLE_CHOICE',
    'CUSTOM', null, 'true', 'EQUALS'
),

-- Kayıt anketi soruları
(
    false, 4.4, true, true, 5, 1,
    1, 1, null, null, CURRENT_TIMESTAMP - INTERVAL '45 days', 2,
    null, 5, 3, 67, CURRENT_TIMESTAMP - INTERVAL '2 days', 2,
    'enrollment-process', null, null, null,
    null, null, null,
    'Kayıt süreci ne kadar kolay ve anlaşılırdı?', 'RATING_STAR',
    'OVERALL_SATISFACTION', '{"1": "Çok Zor", "2": "Zor", "3": "Orta", "4": "Kolay", "5": "Çok Kolay"}',
    null, null
),
(
    false, 4.6, true, true, 5, 1,
    1, 2, null, null, CURRENT_TIMESTAMP - INTERVAL '45 days', 2,
    null, 3, 3, 64, CURRENT_TIMESTAMP - INTERVAL '2 days', 2,
    'communication-rating', null, null, null,
    null, null, null,
    'Kayıt sürecinde iletişim kalitesi nasıldı?', 'RATING_STAR',
    'COMMUNICATION', '{"1": "Çok Kötü", "2": "Kötü", "3": "Orta", "4": "İyi", "5": "Mükemmel"}',
    null, null
),

-- Hizmet kalitesi anketi soruları
(
    false, 3.9, true, true, 10, 1,
    1, 1, null, null, CURRENT_TIMESTAMP - INTERVAL '30 days', 3,
    null, 25, 4, 234, CURRENT_TIMESTAMP - INTERVAL '3 days', 3,
    'cleanliness-scale', null, '1-10 arası değerlendirin', null,
    null, null, null,
    'Okulun temizlik durumunu 1-10 arası puanlayın', 'RATING_SCALE',
    'CLEANLINESS', '{"1": "Çok Kirli", "10": "Çok Temiz"}',
    null, null
),
(
    false, 4.2, true, true, 10, 1,
    1, 2, null, null, CURRENT_TIMESTAMP - INTERVAL '30 days', 3,
    null, 18, 4, 216, CURRENT_TIMESTAMP - INTERVAL '3 days', 3,
    'safety-scale', null, null, null,
    null, null, null,
    'Okul güvenliği konusunda ne kadar emin hissediyorsunuz?', 'RATING_SCALE',
    'SAFETY', '{"1": "Hiç Güvenli Değil", "10": "Tamamen Güvenli"}',
    null, null
);

-- ======= SURVEY RESPONSES =======
INSERT INTO survey_responses (
    cleanliness_rating, communication_rating, completion_time_seconds, facilities_rating, is_active,
    likelihood_to_enroll, overall_rating, reminder_count, staff_rating, would_recommend,
    appointment_id, completed_at, created_at, created_by, invitation_opened_at, invitation_sent_at,
    last_reminder_sent_at, respondent_user_id, school_id, started_at, submitted_at, survey_id,
    updated_at, updated_by, browser_info, complaints, device_info, general_feedback, ip_address,
    respondent_email, respondent_name, respondent_phone, response_token, status, suggestions, user_agent
) VALUES
-- Tamamlanan randevu anketi cevapları
(
    null, 4.0, 187, null, true,
    90, 5.0, 0, 4.0, true,
    1, '2025-08-21 10:30:00', '2025-08-20 14:35:00', null, '2025-08-20 14:40:00', '2025-08-20 14:35:00',
    null, 4, 1, '2025-08-21 10:27:00', '2025-08-21 10:30:00', 1,
    '2025-08-21 10:30:00', null, '{"name": "Chrome", "version": "91.0"}', null, '{"type": "desktop", "os": "Windows 10"}',
    'Çok memnun kaldık, kayıt olmaya karar verdik.', '178.240.15.123',
    'ayse.ozkan@email.com', 'Ayşe Özkan', '+905551234570', 'RSP-EDM-001-20250821', 'SUBMITTED',
    'Öğretmenlerle tanışma imkanı olursa güzel olur.', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
),
(
    null, 4.0, 245, null, true,
    75, 4.0, 1, 5.0, true,
    2, '2025-08-28 16:45:00', '2025-08-27 09:20:00', null, '2025-08-27 09:25:00', '2025-08-27 09:20:00',
    '2025-08-28 08:00:00', 3, 2, '2025-08-28 16:40:00', '2025-08-28 16:45:00', 1,
    '2025-08-28 16:45:00', null, '{"name": "Safari", "version": "14.1"}', null, '{"type": "mobile", "os": "iOS 14"}',
    'İlkokul programı çok kapsamlı görünüyor.', '178.240.22.84',
    'mehmet.demir@email.com', 'Mehmet Demir', '+905551234569', 'RSP-EDM-002-20250828', 'SUBMITTED',
    'Online eğitim altyapısı hakkında daha detaylı bilgi alabilir miyiz?', 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_6 like Mac OS X)'
),
(
    null, 3.0, 298, null, true,
    40, 3.0, 0, 3.0, false,
    3, '2025-08-30 11:20:00', '2025-08-29 18:15:00', null, '2025-08-29 18:20:00', '2025-08-29 18:15:00',
    null, 2, 3, '2025-08-30 11:15:00', '2025-08-30 11:20:00', 1,
    '2025-08-30 11:20:00', null, '{"name": "Firefox", "version": "89.0"}', 'Randevu saatleri daha esnek olabilir.',
    '{"type": "desktop", "os": "macOS"}', 'Okul güzel ama fiyatlar biraz yüksek.',
    '178.240.8.156', 'zeynep.kaya@email.com', 'Zeynep Kaya', '+905551234568',
    'RSP-BSK-001-20250830', 'SUBMITTED', 'Burs imkanları hakkında bilgi verilmesi.', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)'
),

-- Okul değerlendirme anketi cevapları
(
    4.0, null, 356, 4.0, true,
    null, 4.0, 2, null, true,
    null, '2025-08-25 14:20:00', '2025-08-20 10:30:00', null, '2025-08-20 10:35:00', '2025-08-20 10:30:00',
    '2025-08-23 09:00:00', null, 1, '2025-08-25 14:15:00', '2025-08-25 14:20:00', 2,
    '2025-08-25 14:20:00', null, '{"name": "Edge", "version": "91.0"}', null,
    '{"type": "tablet", "os": "Android 11"}', 'Çocuğum bu okulda çok mutlu.',
    '178.240.19.75', 'anonim@example.com', 'Anonim Veli', null,
    'RSP-ANON-001-20250825', 'SUBMITTED', 'Sanat dersleri daha çeşitli olabilir.', 'Mozilla/5.0 (Linux; Android 11; SM-T870)'
),
(
    3.0, null, 412, 3.0, true,
    null, 3.0, 1, null, true,
    null, '2025-08-22 09:45:00', '2025-08-18 16:20:00', null, '2025-08-18 16:25:00', '2025-08-18 16:20:00',
    '2025-08-21 10:00:00', null, 2, '2025-08-22 09:40:00', '2025-08-22 09:45:00', 2,
    '2025-08-22 09:45:00', null, '{"name": "Chrome", "version": "92.0"}', 'Kafeterya yemekleri pahalı.',
    '{"type": "desktop", "os": "Ubuntu 20.04"}', 'Akademik başarı iyi ama sosyal etkinlikler az.',
    '178.240.31.92', 'anonim@example.com', 'Anonim Veli', null,
    'RSP-ANON-002-20250822', 'SUBMITTED', 'Daha fazla sosyal etkinlik düzenlenebilir.', 'Mozilla/5.0 (X11; Linux x86_64)'
),

-- Kayıt anketi cevapları
(
    null, 5.0, 145, null, true,
    null, 5.0, 0, null, null,
    null, '2025-08-26 13:15:00', '2025-08-26 13:10:00', null, '2025-08-26 13:12:00', '2025-08-26 13:10:00',
    null, 4, 5, '2025-08-26 13:13:00', '2025-08-26 13:15:00', 3,
    '2025-08-26 13:15:00', null, '{"name": "Chrome", "version": "91.0"}', null,
    '{"type": "mobile", "os": "Android 12"}', 'Kayıt işlemi çok hızlı ve kolayda.',
    '178.240.45.67', 'ayse.ozkan@email.com', 'Ayşe Özkan', '+905551234570',
    'RSP-GA-001-20250826', 'SUBMITTED', 'Online kayıt formu çok pratikti.', 'Mozilla/5.0 (Linux; Android 12; Pixel 5)'
);

-- ======= SURVEY QUESTION RESPONSES =======
INSERT INTO survey_question_responses (
    boolean_response, confidence_level, date_response, is_active, number_response, rating_response,
    response_order, response_time_seconds, revision_count, time_response, was_skipped,
    created_at, created_by, datetime_response, file_size, question_id, survey_response_id,
    updated_at, updated_by, choice_responses, file_name, file_type, file_url, matrix_responses,
    other_text, skip_reason, text_response
) VALUES
-- Ayşe Özkan'ın randevu anketi cevapları
(
    null, 9, null, true, null, 5,
    1, 8, 0, null, false,
    '2025-08-21 10:28:00', null, null, null, 1, 1,
    '2025-08-21 10:28:00', null, null, null, null, null, null,
    null, null, null
),
(
    null, 8, null, true, null, 4,
    2, 12, 0, null, false,
    '2025-08-21 10:28:30', null, null, null, 2, 1,
    '2025-08-21 10:28:30', null, null, null, null, null, null,
    null, null, null
),
(
    null, 9, null, true, null, null,
    3, 45, 1, null, false,
    '2025-08-21 10:29:15', null, null, null, 3, 1,
    '2025-08-21 10:29:15', null, '["Kesinlikle kaydolurum"]', null, null, null, null,
    null, null, null
),
(
    null, null, null, true, null, null,
    4, 85, 0, null, false,
    '2025-08-21 10:30:00', null, null, null, 4, 1,
    '2025-08-21 10:30:00', null, null, null, null, null, null,
    null, null, 'Montessori eğitimi gerçekten etkileyiciydi. Öğretmenler çok samimi ve bilgili. Çocuğum için ideal bir ortam.'
),

-- Mehmet Demir'in randevu anketi cevapları
(
    null, 8, null, true, null, 4,
    1, 15, 0, null, false,
    '2025-08-28 16:41:00', null, null, null, 1, 2,
    '2025-08-28 16:41:00', null, null, null, null, null, null,
    null, null, null
),
(
    null, 10, null, true, null, 5,
    2, 10, 0, null, false,
    '2025-08-28 16:41:30', null, null, null, 2, 2,
    '2025-08-28 16:41:30', null, null, null, null, null, null,
    null, null, null
),
(
    null, 7, null, true, null, null,
    3, 65, 0, null, false,
    '2025-08-28 16:42:35', null, null, null, 3, 2,
    '2025-08-28 16:42:35', null, '["Büyük ihtimalle kaydolurum"]', null, null, null, null,
    null, null, null
),
(
    null, null, null, true, null, null,
    4, 120, 2, null, false,
    '2025-08-28 16:44:35', null, null, null, 4, 2,
    '2025-08-28 16:44:35', null, null, null, null, null, null,
    null, null, 'Cambridge programı çok kapsamlı. Disleksi desteği konusunda verilen bilgiler gerçekten faydalıydı. Online eğitim seçenekleri de var mı?'
),

-- Anonim okul değerlendirmesi cevapları
(
    null, 8, null, true, null, 4,
    1, 25, 0, null, false,
    '2025-08-25 14:16:00', null, null, null, 5, 4,
    '2025-08-25 14:16:00', null, null, null, null, null, null,
    null, null, null
),
(
    null, 8, null, true, null, 4,
    2, 18, 0, null, false,
    '2025-08-25 14:16:30', null, null, null, 6, 4,
    '2025-08-25 14:16:30', null, null, null, null, null, null,
    null, null, null
),
(
    true, 9, null, true, null, null,
    3, 8, 0, null, false,
    '2025-08-25 14:17:00', null, null, null, 7, 4,
    '2025-08-25 14:17:00', null, null, null, null, null, null,
    null, null, null
),
(
    null, null, null, true, null, null,
    4, 90, 0, null, false,
    '2025-08-25 14:18:30', null, null, null, 8, 4,
    '2025-08-25 14:18:30', null, '["Akademik kalite", "Öğretmen kadrosu"]', null, null, null, null,
    null, null, null
),

-- Kayıt anketi cevapları
(
    null, 10, null, true, null, 5,
    1, 12, 0, null, false,
    '2025-08-26 13:13:30', null, null, null, 9, 6,
    '2025-08-26 13:13:30', null, null, null, null, null, null,
    null, null, null
),
(
    null, 10, null, true, null, 5,
    2, 8, 0, null, false,
    '2025-08-26 13:14:00', null, null, null, 10, 6,
    '2025-08-26 13:14:00', null, null, null, null, null, null,
    null, null, null
);

-- ======= SURVEY RATINGS =======
INSERT INTO survey_ratings (
    is_active, is_flagged, is_public, is_verified, rating_value, weight, created_at, created_by,
    flagged_at, flagged_by, school_id, survey_response_id, updated_at, updated_by, verified_at,
    verified_by, flag_reason, moderator_notes, rating_category, rating_text
) VALUES
-- Eğitim Dünyası Maslak Anaokulu değerlendirmeleri
(
    true, false, true, true, 5, 1.0, '2025-08-21 10:30:00', null,
    null, null, 1, 1, '2025-08-22 09:15:00', 1, '2025-08-22 09:15:00',
    1, null, 'Pozitif geri bildirim, güvenilir kaynak', 'OVERALL_SATISFACTION', 'Mükemmel'
),
(
    true, false, true, true, 4, 1.0, '2025-08-21 10:30:00', null,
    null, null, 1, 1, '2025-08-22 09:15:00', 1, '2025-08-22 09:15:00',
    1, null, null, 'STAFF_FRIENDLINESS', 'İyi'
),
-- Eğitim Dünyası Maslak İlkokul değerlendirmeleri
(
    true, false, true, true, 4, 1.0, '2025-08-28 16:45:00', null,
    null, null, 2, 2, '2025-08-29 10:20:00', 2, '2025-08-29 10:20:00',
    2, null, 'Detaylı geri bildirim', 'OVERALL_SATISFACTION', 'İyi'
),
(
    true, false, true, true, 5, 1.0, '2025-08-28 16:45:00', null,
    null, null, 2, 2, '2025-08-29 10:20:00', 2, '2025-08-29 10:20:00',
    2, null, null, 'STAFF_FRIENDLINESS', 'Mükemmel'
),
-- Bilim Sanat Kızılay Ortaokul değerlendirmeleri
(
    true, false, true, false, 3, 1.0, '2025-08-30 11:20:00', null,
    null, null, 3, 3, '2025-08-30 11:20:00', null, null,
    null, null, 'Doğrulama bekliyor', 'OVERALL_SATISFACTION', 'Orta'
),
(
    true, false, true, false, 3, 1.0, '2025-08-30 11:20:00', null,
    null, null, 3, 3, '2025-08-30 11:20:00', null, null,
    null, null, null, 'STAFF_FRIENDLINESS', 'Orta'
),
-- Anonim okul değerlendirmeleri
(
    true, false, true, true, 4, 0.8, '2025-08-25 14:20:00', null,
    null, null, 1, 4, '2025-08-26 11:30:00', 1, '2025-08-26 11:30:00',
    1, null, 'Anonim değerlendirme, düşük ağırlık', 'ACADEMIC_QUALITY', 'İyi'
),
(
    true, false, true, true, 4, 0.8, '2025-08-25 14:20:00', null,
    null, null, 1, 4, '2025-08-26 11:30:00', 1, '2025-08-26 11:30:00',
    1, null, null, 'FACILITIES', 'İyi'
),
(
    true, false, true, true, 3, 0.8, '2025-08-22 09:45:00', null,
    null, null, 2, 5, '2025-08-23 14:15:00', 2, '2025-08-23 14:15:00',
    2, null, 'Yapıcı eleştiri', 'ACADEMIC_QUALITY', 'Orta'
),
(
    true, false, true, true, 3, 0.8, '2025-08-22 09:45:00', null,
    null, null, 2, 5, '2025-08-23 14:15:00', 2, '2025-08-23 14:15:00',
    2, null, null, 'FACILITIES', 'Orta'
),
-- Gelişim Alsancak kayıt değerlendirmesi
(
    true, false, false, true, 5, 1.0, '2025-08-26 13:15:00', null,
    null, null, 5, 6, '2025-08-27 08:45:00', 4, '2025-08-27 08:45:00',
    4, null, 'Kayıt süreci memnuniyeti', 'OVERALL_SATISFACTION', 'Mükemmel'
),
(
    true, false, false, true, 5, 1.0, '2025-08-26 13:15:00', null,
    null, null, 5, 6, '2025-08-27 08:45:00', 4, '2025-08-27 08:45:00',
    4, null, null, 'COMMUNICATION', 'Mükemmel'
);



-- V7__Insert_Demo_Content_Management_Data.sql
-- eğitimara.com için içerik yönetim sistemi demo verileri

-- ======= GALLERIES =======
INSERT INTO galleries (
    allow_comments, allow_downloads, is_active, is_featured, sort_order, brand_id, campus_id,
    created_at, created_by, created_by_user_id, download_count, item_count, school_id,
    total_size_bytes, updated_at, updated_by, view_count, cover_image_url, description,
    gallery_type, meta_description, meta_title, slug, tags, title, visibility
) VALUES
-- Eğitim Dünyası Maslak kampüs turu
(
    true, false, true, true, 1, 1, 1,
    CURRENT_TIMESTAMP - INTERVAL '30 days', 1, 1, 0, 24, null,
    45680234, CURRENT_TIMESTAMP - INTERVAL '5 days', 1, 1847, 'https://egitimara.com/images/galleries/covers/maslak-tour.jpg',
    'Eğitim Dünyası Maslak Kampüsü''nün detaylı turu. Sınıflar, laboratuvarlar, oyun alanları ve tüm tesislerimizi keşfedin.',
    'SCHOOL_TOUR', 'Eğitim Dünyası Maslak Kampüsü sanal turu', 'Maslak Kampüsü Turu - Eğitim Dünyası',
    'egitim-dunyasi-maslak-kampus-turu', 'kampüs,tur,maslak,tesisler', 'Maslak Kampüsü Sanal Turu', 'PUBLIC'
),
-- Bilim Sanat etkinlik fotoğrafları
(
    true, true, true, true, 2, 2, 2,
    CURRENT_TIMESTAMP - INTERVAL '15 days', 3, 3, 156, 18, null,
    28945123, CURRENT_TIMESTAMP - INTERVAL '2 days', 3, 892, 'https://egitimara.com/images/galleries/covers/bilim-sanat-etkinlik.jpg',
    'Bilim Fuarı 2025 etkinliğinden kareler. Öğrencilerimizin STEAM projelerini ve bilimsel çalışmalarını görün.',
    'EVENTS', 'Bilim Sanat Kızılay Kampüsü Bilim Fuarı fotoğrafları', 'Bilim Fuarı 2025 - Bilim Sanat',
    'bilim-sanat-kizilay-bilim-fuari-2025', 'bilim,fuar,STEAM,proje,etkinlik', 'Bilim Fuarı 2025 Fotoğrafları', 'PUBLIC'
),
-- Gelişim Koleji mezuniyet töreni
(
    false, false, true, false, 3, 3, 3,
    CURRENT_TIMESTAMP - INTERVAL '45 days', 4, 4, 0, 32, null,
    67234567, CURRENT_TIMESTAMP - INTERVAL '45 days', 4, 567, 'https://egitimara.com/images/galleries/covers/gelisim-mezuniyet.jpg',
    '2025 yılı mezuniyet törenimizden unutulmaz anlar. Mezun öğrencilerimizin gurur verici başarı hikayesi.',
    'GRADUATION', 'Gelişim Koleji 2025 mezuniyet töreni fotoğrafları', 'Mezuniyet Töreni 2025 - Gelişim Koleji',
    'gelisim-koleji-mezuniyet-2025', 'mezuniyet,tören,başarı,gurur', '2025 Mezuniyet Töreni', 'REGISTERED_ONLY'
),
-- Eğitim Dünyası Moda sanat galerisi
(
    true, true, true, true, 4, 1, 4,
    CURRENT_TIMESTAMP - INTERVAL '10 days', 2, 2, 67, 45, 6,
    34567890, CURRENT_TIMESTAMP - INTERVAL '1 day', 2, 1234, 'https://egitimara.com/images/galleries/covers/moda-sanat.jpg',
    'Öğrencilerimizin sanat çalışmaları ve yaratıcı projelerinden oluşan özel koleksiyon.',
    'STUDENT_WORK', 'Eğitim Dünyası Moda öğrenci sanat çalışmaları', 'Öğrenci Sanat Galerisi - Moda Kampüsü',
    'egitim-dunyasi-moda-sanat-galerisi', 'sanat,öğrenci,yaratıcılık,resim', 'Öğrenci Sanat Galerisi', 'PUBLIC'
),
-- Bilim Sanat laboratuvar
(
    false, false, true, false, 5, 2, 2,
    CURRENT_TIMESTAMP - INTERVAL '60 days', 3, 3, 0, 28, 4,
    52341234, CURRENT_TIMESTAMP - INTERVAL '30 days', 3, 345, 'https://egitimara.com/images/galleries/covers/lab-facilities.jpg',
    'Modern laboratuvar tesislerimiz ve bilimsel ekipmanlarımızın tanıtımı.',
    'LABORATORY', 'Bilim Sanat Kızılay laboratuvar tesisleri', 'Laboratuvar Tesisleri - Bilim Sanat',
    'bilim-sanat-laboratuvar-tesisleri', 'laboratuvar,bilim,ekipman,tesis', 'Laboratuvar Tesislerimiz', 'PUBLIC'
);

-- ======= GALLERY ITEMS =======
INSERT INTO gallery_items (
    bitrate, duration_seconds, frame_rate, height, is_active, is_cover, is_featured,
    is_flagged, is_moderated, latitude, longitude, moderation_score, sort_order, width,
    created_at, created_by, download_count, file_size_bytes, gallery_id, like_count,
    processed_at, taken_at, updated_at, updated_by, uploaded_by_user_id, view_count,
    alt_text, aspect_ratio, camera_make, camera_model, color_palette, description,
    file_name, file_url, flag_reason, item_type, location_name, mime_type,
    moderation_labels, original_file_name, processing_error, processing_status,
    tags, thumbnail_url, title, video_codec, video_format
) VALUES
-- Maslak kampüs turu resimleri
(
    null, null, null, 1080, true, true, true,
    false, true, 41.1069, 28.9958, 0.95, 1, 1920,
    CURRENT_TIMESTAMP - INTERVAL '30 days', 1, 0, 2456789, 1, 234,
    CURRENT_TIMESTAMP - INTERVAL '29 days', CURRENT_TIMESTAMP - INTERVAL '30 days', CURRENT_TIMESTAMP - INTERVAL '5 days', 1, 1, 1847,
    'Maslak Kampüsü ana giriş ve bahçe alanı', '16:9', 'Canon', 'EOS R5',
    '{"primary": "#4A90E2", "secondary": "#7ED321", "accent": "#F5A623"}',
    'Kampüsümüzün göz alıcı girişi ve yeşil alanları', 'maslak_entrance_01.jpg',
    'https://egitimara.com/images/galleries/maslak-tour/entrance-01.jpg', null, 'IMAGE',
    'Ana Giriş', 'image/jpeg', '{"safe": 0.99, "adult": 0.01}', 'IMG_0001.JPG',
    null, 'COMPLETED', 'giriş,bahçe,yeşil,alan',
    'https://egitimara.com/images/galleries/maslak-tour/thumbs/entrance-01-thumb.jpg',
    'Kampüs Ana Girişi', null, null
),
(
    null, null, null, 1080, true, false, false,
    false, true, null, null, 0.92, 2, 1920,
    CURRENT_TIMESTAMP - INTERVAL '29 days', 1, 0, 3567890, 1, 89,
    CURRENT_TIMESTAMP - INTERVAL '28 days', CURRENT_TIMESTAMP - INTERVAL '29 days', CURRENT_TIMESTAMP - INTERVAL '5 days', 1, 1, 456,
    'Anaokulu sınıfı içi görünümü ve Montessori materyalleri', '16:9', 'Canon', 'EOS R5',
    '{"primary": "#FFB6C1", "secondary": "#98FB98", "accent": "#FFD700"}',
    'Montessori eğitim materyalleri ile donatılmış sınıfımız', 'maslak_classroom_preschool.jpg',
    'https://egitimara.com/images/galleries/maslak-tour/classroom-preschool.jpg', null, 'IMAGE',
    'Anaokulu Sınıfı', 'image/jpeg', '{"safe": 0.98, "adult": 0.02}', 'IMG_0002.JPG',
    null, 'COMPLETED', 'sınıf,anaokulu,montessori,eğitim',
    'https://egitimara.com/images/galleries/maslak-tour/thumbs/classroom-preschool-thumb.jpg',
    'Anaokulu Sınıfımız', null, null
),

-- Bilim fuarı video
(
    5000, 185, 30.0, 1080, true, false, true,
    false, true, null, null, 0.94, 1, 1920,
    CURRENT_TIMESTAMP - INTERVAL '15 days', 3, 156, 45678901, 2, 312,
    CURRENT_TIMESTAMP - INTERVAL '14 days', CURRENT_TIMESTAMP - INTERVAL '15 days', CURRENT_TIMESTAMP - INTERVAL '2 days', 3, 3, 892,
    'Bilim Fuarı 2025 etkinlik videosu - öğrenci proje sunumları', '16:9', 'Sony', 'FX6',
    '{"primary": "#2E86C1", "secondary": "#F39C12", "accent": "#E74C3C"}',
    'Öğrencilerimizin STEAM projelerini sundukları heyecan verici anlar',
    'bilim_fuari_2025_highlights.mp4', 'https://egitimara.com/videos/galleries/bilim-fuari-highlights.mp4',
    null, 'VIDEO', 'Etkinlik Salonu', 'video/mp4',
    '{"safe": 0.97, "violence": 0.02, "adult": 0.01}', 'bilim_fuari_video_01.mp4',
    null, 'COMPLETED', 'bilim,fuar,proje,STEAM,öğrenci',
    'https://egitimara.com/videos/galleries/thumbs/bilim-fuari-thumb.jpg',
    'Bilim Fuarı Özet Videosu', 'h264', 'mp4'
),

-- Sanat galerisi çalışmaları
(
    null, null, null, 1200, true, true, true,
    false, true, null, null, 0.96, 1, 900,
    CURRENT_TIMESTAMP - INTERVAL '10 days', 2, 67, 1234567, 4, 78,
    CURRENT_TIMESTAMP - INTERVAL '9 days', CURRENT_TIMESTAMP - INTERVAL '10 days', CURRENT_TIMESTAMP - INTERVAL '1 day', 2, 2, 1234,
    '7 yaşındaki öğrencimizin yağlı boya tablosu - İstanbul Manzarası', '3:4', 'iPhone', '13 Pro',
    '{"primary": "#FF6B6B", "secondary": "#4ECDC4", "accent": "#45B7D1"}',
    'Genç sanatçımızın İstanbul''u yorumladığı muhteşem çalışma',
    'student_painting_istanbul.jpg', 'https://egitimara.com/images/galleries/moda-art/student-painting-01.jpg',
    null, 'IMAGE', 'Sanat Atölyesi', 'image/jpeg',
    '{"safe": 0.99, "adult": 0.01}', 'sanat_calismasi_01.jpg',
    null, 'COMPLETED', 'sanat,resim,öğrenci,yaratıcılık',
    'https://egitimara.com/images/galleries/moda-art/thumbs/student-painting-01-thumb.jpg',
    'İstanbul Manzarası - Öğrenci Çalışması', null, null
);

-- ======= MESSAGES =======
INSERT INTO messages (
    follow_up_required, has_attachments, is_active, request_appointment, request_callback,
    resolution_time_hours, response_time_hours, satisfaction_rating, student_age,
    assigned_to_user_id, created_at, created_by, first_response_at, follow_up_date,
    last_response_at, read_at, read_by, resolved_at, resolved_by, satisfaction_date,
    school_id, sender_user_id, updated_at, updated_by, attachments, content, enrollment_year,
    follow_up_notes, grade_interested, internal_notes, ip_address, message_type,
    preferred_contact_method, preferred_contact_time, priority, reference_number,
    satisfaction_feedback, sender_email, sender_name, sender_phone, source_page, status,
    student_name, subject, tags, user_agent, utm_campaign, utm_medium, utm_source
) VALUES
-- Kayıt talebi mesajı
(
    false, false, true, true, false,
    2.5, 0.75, 5, 5,
    1, CURRENT_TIMESTAMP - INTERVAL '3 days', null, CURRENT_TIMESTAMP - INTERVAL '2 days 23 hours', null,
    CURRENT_TIMESTAMP - INTERVAL '2 days 22 hours', CURRENT_TIMESTAMP - INTERVAL '2 days 23 hours', 1, CURRENT_TIMESTAMP - INTERVAL '2 days 22 hours', 1, CURRENT_TIMESTAMP - INTERVAL '1 day',
    1, null, CURRENT_TIMESTAMP - INTERVAL '1 day', 1, null,
    'Merhaba, 5 yaşındaki kızım için anaokuluna kayıt yaptırmak istiyorum. Montessori eğitimi hakkında detaylı bilgi alabilir miyim? Kampüsünüzü ziyaret etmek için randevu alabilir miyiz?',
    '2025-2026', 'Randevu ayarlandı, kampüs turu için hazırlık yapılacak', 'Anaokulu',
    'Veliden çok olumlu geri dönüş aldık. Kayıt olma ihtimali yüksek.', '178.240.15.123', 'ENROLLMENT_INQUIRY',
    'Email', '09:00-17:00', 'HIGH', 'MSG-EDM-20250824-001',
    'Çok hızlı yanıt aldık, teşekkürler!', 'fatma.yilmaz@email.com', 'Fatma Yılmaz', '+905551112233',
    '/anaokulu-bilgileri', 'RESOLVED', 'Aylin Yılmaz', 'Anaokulu Kayıt Bilgileri',
    'anaokulu,kayıt,montessori', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'spring_enrollment', 'organic', 'google'
),
-- Şikayet mesajı
(
    true, true, true, false, true,
    null, 4.2, null, 12,
    3, CURRENT_TIMESTAMP - INTERVAL '1 day', null, CURRENT_TIMESTAMP - INTERVAL '20 hours', CURRENT_TIMESTAMP + INTERVAL '3 days',
    CURRENT_TIMESTAMP - INTERVAL '20 hours', CURRENT_TIMESTAMP - INTERVAL '20 hours', 3, null, null, null,
    3, 2, CURRENT_TIMESTAMP - INTERVAL '20 hours', 3, '[{"filename": "screenshot.png", "size": 245670}]',
    'Çocuğumun matematik dersi ile ilgili bir sorun yaşıyoruz. Öğretmenle iletişim kurmakta zorlanıyoruz. Konuyu görüşmek için acil bir görüşme talep ediyoruz.',
    '2025-2026', 'Matematik öğretmeni ile veli görüşmesi ayarlanacak', '6. Sınıf',
    'Veli haklı, öğretmen iletişim konusunda eksik. Ders koordinatörü ile görüşülecek.', '178.240.22.84', 'COMPLAINT',
    'Telefon', '18:00-20:00', 'URGENT', 'MSG-BSK-20250826-005',
    null, 'mehmet.kara@email.com', 'Mehmet Kara', '+905552223344',
    '/iletisim', 'IN_PROGRESS', 'Emre Kara', 'Matematik Dersi Sorunu',
    'matematik,öğretmen,iletişim,şikayet', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0)', null, 'direct', 'website'
),
-- Genel bilgi talebi
(
    false, false, true, false, false,
    1.0, 0.5, 4, 8,
    4, CURRENT_TIMESTAMP - INTERVAL '5 hours', null, CURRENT_TIMESTAMP - INTERVAL '4 hours 30 minutes', null,
    CURRENT_TIMESTAMP - INTERVAL '4 hours 30 minutes', CURRENT_TIMESTAMP - INTERVAL '4 hours 30 minutes', 4, CURRENT_TIMESTAMP - INTERVAL '4 hours 30 minutes', 4, CURRENT_TIMESTAMP - INTERVAL '2 hours',
    5, null, CURRENT_TIMESTAMP - INTERVAL '2 hours', 4, null,
    'Okulunuzun yaz okulu programı hakkında bilgi alabilir miyim? Çocuğum 2. sınıfa geçecek, hangi aktiviteler var?',
    '2025-2026', null, '2. Sınıf', 'Broşür gönderildi, olumlu geri dönüş alındı.',
    '178.240.8.156', 'GENERAL_INQUIRY', 'WhatsApp', '14:00-16:00', 'NORMAL', 'MSG-GA-20250827-012',
    'Hızlı yanıt için teşekkürler', 'ayşe.demir@email.com', 'Ayşe Demir', '+905553334455',
    '/yaz-okulu', 'RESOLVED', 'Zeynep Demir', 'Yaz Okulu Programı Bilgileri',
    'yaz,okulu,program,aktivite', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', 'summer_programs', 'social', 'facebook'
),
-- Teknik destek talebi
(
    false, false, true, false, false,
    0.3, 0.1, 5, null,
    2, CURRENT_TIMESTAMP - INTERVAL '2 hours', null, CURRENT_TIMESTAMP - INTERVAL '1 hour 50 minutes', null,
    CURRENT_TIMESTAMP - INTERVAL '1 hour 50 minutes', CURRENT_TIMESTAMP - INTERVAL '1 hour 50 minutes', 2, CURRENT_TIMESTAMP - INTERVAL '1 hour 50 minutes', 2, CURRENT_TIMESTAMP - INTERVAL '1 hour',
    2, 2, CURRENT_TIMESTAMP - INTERVAL '1 hour', 2, null,
    'Öğrenci portalına giriş yapamıyorum. Şifremi sıfırlamaya çalıştım ama email gelmiyor. Yardımcı olabilir misiniz?',
    null, null, null, 'Email ayarları kontrol edildi, sorun çözüldü.', '178.240.31.92', 'TECHNICAL_SUPPORT',
    'Email', 'Anında', 'NORMAL', 'MSG-EDModa-20250827-008',
    'Çok hızlı çözüm, mükemmel!', 'zeynep.kaya@email.com', 'Zeynep Kaya', '+905554445566',
    '/ogrenci-portal', 'RESOLVED', null, 'Portal Giriş Sorunu',
    'portal,giriş,şifre,teknik', 'Mozilla/5.0 (Linux; Android 11)', null, 'direct', 'website'
);

-- ======= POSTS =======
INSERT INTO posts (
    allow_comments, allow_likes, average_read_time_seconds, engagement_score, flag_count,
    is_active, is_featured, is_flagged, is_moderated, is_pinned, latitude, longitude,
    video_duration_seconds, author_user_id, click_count, comment_count, created_at, created_by,
    expires_at, impression_count, like_count, moderated_at, moderated_by, pin_expires_at,
    published_at, reach_count, scheduled_at, school_id, share_count, updated_at, updated_by,
    view_count, call_to_action, content, cta_url, external_url, featured_image_url, hashtags,
    location_name, media_attachments, meta_description, meta_title, moderation_notes, post_type,
    slug, status, tags, title, video_thumbnail_url, video_url
) VALUES
-- Bilim Fuarı duyurusu
(
    true, true, 87, 8.5, 0,
    true, true, false, true, false, 39.9208, 32.8541,
    null, 3, 234, 18, CURRENT_TIMESTAMP - INTERVAL '15 days', 3,
    null, 3456, 156, CURRENT_TIMESTAMP - INTERVAL '14 days', 3, null,
    CURRENT_TIMESTAMP - INTERVAL '14 days', 2890, null, 3, 67, CURRENT_TIMESTAMP - INTERVAL '2 days', 3,
    3456, 'Detayları İncele',
    '🔬 Bilim Fuarı 2025 başarıyla tamamlandı! Öğrencilerimizin STEAM projelerindeki yaratıcılık ve bilimsel yaklaşım hepimizi gururlandırdı. Robotik kodlamadan kimya deneyimlerine, 3D tasarımdan çevre projelerine kadar geniş bir yelpazede 47 proje sergilendi. Velilerimizin ve misafirlerimizin büyük ilgisiyle karşılaştık!',
    '/bilim-fuari-2025', null, 'https://egitimara.com/images/posts/bilim-fuari-cover.jpg',
    '#BilimFuarı2025 #STEAM #Eğitim #BilimSanat', 'Bilim Sanat Kızılay Kampüsü',
    '[{"type": "image", "url": "https://egitimara.com/images/posts/bilim-fuari-1.jpg"}, {"type": "image", "url": "https://egitimara.com/images/posts/bilim-fuari-2.jpg"}]',
    'Bilim Sanat Kızılay Kampüsü Bilim Fuarı 2025 etkinlik özeti', 'Bilim Fuarı 2025 - Bilim Sanat',
    'İçerik onaylandı, paylaşıma uygun', 'EVENT', 'bilim-fuari-2025-basarili', 'PUBLISHED',
    'bilim,fuar,STEAM,proje,başarı', 'Bilim Fuarı 2025 Büyük Başarı!', null, null
),
-- Mezuniyet töreni
(
    true, true, 125, 9.2, 0,
    true, true, false, true, true, null, null,
    null, 4, 567, 34, CURRENT_TIMESTAMP - INTERVAL '45 days', 4,
    null, 4567, 289, CURRENT_TIMESTAMP - INTERVAL '44 days', 4, CURRENT_TIMESTAMP + INTERVAL '30 days',
    CURRENT_TIMESTAMP - INTERVAL '44 days', 3890, null, 5, 123, CURRENT_TIMESTAMP - INTERVAL '43 days', 4,
    4567, 'Fotoğrafları Görüntüle',
    '🎓 2025 Mezuniyet Törenimiz Gerçekleşti! 35 yıllık deneyimimizle yetiştirdiğimiz değerli öğrencilerimiz, yeni hayatlarına "merhaba" dedi. Ailelerinin gözlerindeki gururu, öğretmenlerimizin sevincini paylaştık. Her birinin parlak bir gelecek için sağlam temellere sahip olduğuna inanıyoruz. Mezun öğrencilerimize başarılar dileriz! 👨‍🎓👩‍🎓',
    '/mezuniyet-galerisi', null, 'https://egitimara.com/images/posts/mezuniyet-cover.jpg',
    '#Mezuniyet2025 #GelişimKoleji #Başarı #Gurur', 'Gelişim Koleji Alsancak Kampüsü',
    '[{"type": "gallery", "gallery_id": 3, "count": 32}]',
    'Gelişim Koleji 2025 mezuniyet töreni kutlaması', 'Mezuniyet Töreni 2025 - Gelişim Koleji',
    'Duygusal içerik, sabitleme onaylandı', 'CELEBRATION', 'mezuniyet-toreni-2025', 'PUBLISHED',
    'mezuniyet,tören,başarı,öğrenci', '2025 Mezuniyet Törenimiz Gerçekleşti!', null, null
),
-- Sanat sergisi duyurusu
(
    true, true, 95, 7.8, 0,
    true, false, false, true, false, null, null,
    null, 2, 345, 23, CURRENT_TIMESTAMP - INTERVAL '10 days', 2,
    null, 2345, 89, CURRENT_TIMESTAMP - INTERVAL '9 days', 2, null,
    CURRENT_TIMESTAMP - INTERVAL '9 days', 1890, null, 6, 45, CURRENT_TIMESTAMP - INTERVAL '1 day', 2,
    2345, 'Sergimizi Ziyaret Edin',
    '🎨 Öğrenci Sanat Sergimiz Açıldı! Moda Kampüsü''müzde sanat ağırlıklı eğitim programımızın meyvelerini görmenin zamanı geldi. 1-4. sınıf öğrencilerimizin resim, heykel ve dijital sanat çalışmalarından oluşan sergimiz ziyarete açık. Genç sanatçılarımızın yaratıcılığını keşfedin, ilham alın! 🖼️',
    '/sanat-sergisi', null, 'https://egitimara.com/images/posts/sanat-sergisi-cover.jpg',
    '#SanatSergisi #ÖğrenciSanatı #Yaratıcılık #EğitimDünyası', 'Eğitim Dünyası Moda Kampüsü',
    '[{"type": "gallery", "gallery_id": 4, "count": 45}]',
    'Eğitim Dünyası Moda Kampüsü öğrenci sanat sergisi duyurusu', 'Öğrenci Sanat Sergisi - Moda Kampüsü',
    'Sanat içeriği uygun', 'ANNOUNCEMENT', 'ogrenci-sanat-sergisi-2025', 'PUBLISHED',
    'sanat,sergi,öğrenci,yaratıcılık', 'Öğrenci Sanat Sergimiz Açıldı!', null, null
),
-- Başarı hikayesi
(
    true, true, 156, 6.9, 0,
    true, false, false, true, false, null, null,
    null, 1, 123, 15, CURRENT_TIMESTAMP - INTERVAL '7 days', 1,
    null, 1234, 67, CURRENT_TIMESTAMP - INTERVAL '6 days', 1, null,
    CURRENT_TIMESTAMP - INTERVAL '6 days', 985, null, 1, 28, CURRENT_TIMESTAMP - INTERVAL '6 days', 1,
    1234, null,
    '🏆 Uluslararası Matematik Olimpiyatı''nda Büyük Başarı! 4. sınıf öğrencimiz Ege Yılmaz, Türkiye finallerinde üçüncülük ödülü kazandı. Sanat ağırlıklı programımızın yanı sıra analitik düşünce becerileri de geliştiren yaklaşımımızın sonucu. Tebrikler Ege! 🥉📐',
    null, null, 'https://egitimara.com/images/posts/matematik-basari.jpg',
    '#MatematikOlimpiyatı #Başarı #EğitimDünyası #Gurur', 'Eğitim Dünyası Maslak Kampüsü',
    '[{"type": "image", "url": "https://egitimara.com/images/posts/matematik-medal.jpg"}]',
    'Eğitim Dünyası öğrencisi matematik olimpiyatında başarı', 'Matematik Olimpiyatı Başarısı - Eğitim Dünyası',
    'Başarı haberi onaylandı', 'ACHIEVEMENT', 'matematik-olimpiyati-basarisi', 'PUBLISHED',
    'matematik,olimpiyat,başarı,öğrenci', 'Matematik Olimpiyatında Büyük Başarı!', null, null
),
-- Etkinlik duyurusu
(
    true, true, 68, 5.4, 0,
    true, false, false, true, false, null, null,
    null, 3, 89, 7, CURRENT_TIMESTAMP - INTERVAL '3 days', 3,
    CURRENT_TIMESTAMP + INTERVAL '10 days', 890, 34, CURRENT_TIMESTAMP - INTERVAL '2 days', 3, null,
    CURRENT_TIMESTAMP - INTERVAL '2 days', 612, null, 4, 12, CURRENT_TIMESTAMP - INTERVAL '2 days', 3,
    890, 'Kayıt Ol',
    'Robotik Kodlama Atölyemiz başlıyor! 5-8. sınıf öğrencilerimiz için Arduino ve Python programlama eğitimi. Hafta sonu 2 saatlik oturumlar halinde. Kayıtlar sınırlı, acele edin!',
    '/robotik-kayit', null, 'https://egitimara.com/images/posts/robotik-atolye.jpg',
    '#Robotik #Kodlama #STEAM #BilimSanat', 'Bilim Sanat Kızılay Kampüsü',
    '[{"type": "image", "url": "https://egitimara.com/images/posts/arduino-setup.jpg"}]',
    'Bilim Sanat robotik kodlama atölyesi kayıt duyurusu', 'Robotik Kodlama Atölyesi - Bilim Sanat',
    'Etkinlik duyurusu uygun', 'EVENT', 'robotik-kodlama-atolyesi', 'PUBLISHED',
    'robotik,kodlama,arduino,python', 'Robotik Kodlama Atölyesi Başlıyor!', null, null
);

-- ======= POST COMMENTS =======
INSERT INTO post_comments (
    flag_count, is_active, is_edited, is_flagged, is_moderated, created_at, created_by,
    edited_at, like_count, moderated_at, moderated_by, parent_comment_id, post_id,
    reply_count, updated_at, updated_by, user_id, content, moderation_reason, status,
    user_agent, user_ip
) VALUES
-- Bilim fuarı gönderisi yorumları
(
    0, true, false, false, true, CURRENT_TIMESTAMP - INTERVAL '13 days', null,
    null, 12, CURRENT_TIMESTAMP - INTERVAL '12 days', 3, null, 1,
    2, CURRENT_TIMESTAMP - INTERVAL '12 days', null, 4, 'Harika bir etkinlikti! Çocuğumun projesi çok beğenildi, teşekkürler.',
    null, 'APPROVED', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', '178.240.15.123'
),
(
    0, true, false, false, true, CURRENT_TIMESTAMP - INTERVAL '12 days', null,
    null, 5, CURRENT_TIMESTAMP - INTERVAL '11 days', 3, 1, 1,
    0, CURRENT_TIMESTAMP - INTERVAL '11 days', null, 3, 'Biz de çok memnun kaldık. Gelecek yıl daha da büyük olacağına eminiz!',
    null, 'APPROVED', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0)', '178.240.22.84'
),
(
    0, true, false, false, true, CURRENT_TIMESTAMP - INTERVAL '11 days', null,
    null, 3, CURRENT_TIMESTAMP - INTERVAL '10 days', 3, 1, 1,
    0, CURRENT_TIMESTAMP - INTERVAL '10 days', null, 2, 'Öğretmenler gerçekten çok emek vermiş, ellerinize sağlık.',
    null, 'APPROVED', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', '178.240.8.156'
),

-- Mezuniyet gönderisi yorumları
(
    0, true, false, false, true, CURRENT_TIMESTAMP - INTERVAL '43 days', null,
    null, 8, CURRENT_TIMESTAMP - INTERVAL '42 days', 4, null, 2,
    1, CURRENT_TIMESTAMP - INTERVAL '42 days', null, 4, 'Çok duygulu bir törenide. 35 yıllık deneyiminiz hissediliyor.',
    null, 'APPROVED', 'Mozilla/5.0 (Linux; Android 11)', '178.240.31.92'
),
(
    0, true, false, false, true, CURRENT_TIMESTAMP - INTERVAL '42 days', null,
    null, 2, CURRENT_TIMESTAMP - INTERVAL '41 days', 4, 4, 2,
    0, CURRENT_TIMESTAMP - INTERVAL '41 days', null, 1, 'Teşekkür ederiz. Her mezunumuzla gurur duyuyoruz.',
    null, 'APPROVED', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', '178.240.45.67'
),

-- Sanat sergisi yorumları
(
    0, true, false, false, true, CURRENT_TIMESTAMP - INTERVAL '8 days', null,
    null, 6, CURRENT_TIMESTAMP - INTERVAL '7 days', 2, null, 3,
    0, CURRENT_TIMESTAMP - INTERVAL '7 days', null, 3, 'Çocukların yaratıcılığı gerçekten şaşırtıcı! Mutlaka gelin görün.',
    null, 'APPROVED', 'Mozilla/5.0 (Safari; iPhone)', '178.240.19.75'
);

-- ======= POST LIKES =======
INSERT INTO post_likes (
    is_active, created_at, created_by, liked_at, post_id, updated_at, updated_by,
    user_id, device_type, reaction_type, user_agent, user_ip
) VALUES
-- Bilim fuarı beğenileri
(
    true, CURRENT_TIMESTAMP - INTERVAL '14 days', null, CURRENT_TIMESTAMP - INTERVAL '14 days', 1,
    CURRENT_TIMESTAMP - INTERVAL '14 days', null, 4, 'desktop', 'LIKE',
    'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', '178.240.15.123'
),
(
    true, CURRENT_TIMESTAMP - INTERVAL '13 days', null, CURRENT_TIMESTAMP - INTERVAL '13 days', 1,
    CURRENT_TIMESTAMP - INTERVAL '13 days', null, 2, 'mobile', 'LOVE',
    'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0)', '178.240.22.84'
),
(
    true, CURRENT_TIMESTAMP - INTERVAL '12 days', null, CURRENT_TIMESTAMP - INTERVAL '12 days', 1,
    CURRENT_TIMESTAMP - INTERVAL '12 days', null, 1, 'tablet', 'CONGRATULATIONS',
    'Mozilla/5.0 (iPad; CPU OS 15_0)', '178.240.8.156'
),

-- Mezuniyet beğenileri
(
    true, CURRENT_TIMESTAMP - INTERVAL '43 days', null, CURRENT_TIMESTAMP - INTERVAL '43 days', 2,
    CURRENT_TIMESTAMP - INTERVAL '43 days', null, 1, 'desktop', 'LOVE',
    'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', '178.240.45.67'
),
(
    true, CURRENT_TIMESTAMP - INTERVAL '42 days', null, CURRENT_TIMESTAMP - INTERVAL '42 days', 2,
    CURRENT_TIMESTAMP - INTERVAL '42 days', null, 3, 'mobile', 'CONGRATULATIONS',
    'Mozilla/5.0 (Linux; Android 11)', '178.240.31.92'
),

-- Matematik başarısı beğenileri
(
    true, CURRENT_TIMESTAMP - INTERVAL '6 days', null, CURRENT_TIMESTAMP - INTERVAL '6 days', 4,
    CURRENT_TIMESTAMP - INTERVAL '6 days', null, 2, 'desktop', 'CONGRATULATIONS',
    'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', '178.240.19.75'
),
(
    true, CURRENT_TIMESTAMP - INTERVAL '5 days', null, CURRENT_TIMESTAMP - INTERVAL '5 days', 4,
    CURRENT_TIMESTAMP - INTERVAL '5 days', null, 4, 'mobile', 'WOW',
    'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0)', '178.240.15.123'
);


-- V8__Insert_Demo_Campaign_Data.sql
-- eğitimara.com için pazarlama kampanyası sistemi demo verileri

-- ======= CAMPAIGNS =======
INSERT INTO campaigns (
    conversion_rate, discount_amount, discount_percentage, early_bird_end_date, end_date,
    enrollment_end_date, enrollment_start_date, free_trial_days, is_active, is_featured,
    is_public, max_discount_amount, min_purchase_amount, payment_deadline_days, per_school_limit,
    per_user_limit, priority, registration_deadline, requires_approval, send_notifications,
    sort_order, start_date, target_age_max, target_age_min, target_new_students_only,
    target_sibling_discount, usage_count, usage_limit, application_count, approved_at,
    approved_by, click_count, conversion_count, created_at, created_by, created_by_user_id,
    updated_at, updated_by, view_count, academic_year, badge_color, badge_text,
    banner_image_url, bonus_features, campaign_type, cta_text, cta_url, description,
    discount_type, email_template_id, exclusions, fine_print, free_services, gift_items,
    installment_options, meta_description, meta_keywords, meta_title, notification_message,
    promo_code, refund_policy, short_description, slug, sms_template_id, status,
    target_audience, target_grade_levels, terms_and_conditions, thumbnail_image_url,
    title, video_url
) VALUES
-- Erken kuş kayıt kampanyası
(
    15.2, null, 20.0, '2025-07-31', '2025-08-31',
    '2025-08-15', '2025-06-01', null, true, true,
    true, 15000.00, 30000.00, 30, null,
    1, 1, '2025-08-15', false, true,
    1, '2025-06-01', 7, 3, true,
    false, 89, 200, 156, '2025-05-25 14:30:00',
    1, 2847, 89, '2025-05-20 10:15:00', 1, 1,
    '2025-08-15 09:20:00', 1, 2847, '2025-2026', '#FF6B35', 'ERKEN KAYIT %20 İNDİRİM',
    'https://egitimara.com/images/campaigns/early-bird-banner.jpg',
    '["free_orientation", "free_trial_week", "welcome_package"]', 'EARLY_BIRD',
    'Hemen Kayıt Ol', '/erken-kayit-formu',
    '2025-2026 akademik yılı için erken kayıt kampanyası! 31 Temmuz''a kadar kayıt yaptıran veliler %20 indirimden faydalanır. Üstelik ücretsiz oryantasyon programı ve hoş geldin paketi hediye!',
    'PERCENTAGE', 'early_bird_template', 'Mevcut öğrenciler kampanya kapsamı dışındadır.',
    'Kampanya 31 Temmuz 2025 tarihinde sona erer. İndirim sadece ilk yıl için geçerlidir. İptal durumunda normal ücret tarifesi uygulanır.',
    '["orientation_program", "school_supplies_package", "uniform_fitting"]',
    '["welcome_bag", "school_calendar", "parent_handbook"]', '3, 6, 9, 12 taksit seçenekleri',
    'Erken kayıt kampanyası ile %20 indirim fırsatı kaçmaz!', 'erken kayıt, indirim, okul, eğitim',
    'Erken Kayıt Kampanyası - %20 İndirim', 'Erken kayıt kampanyasından faydalanmak için son günler!',
    'ERKENKAYIT20', 'Kampanya süresi içinde iptal durumunda %50 iade yapılır.',
    'Yeni öğrenci kayıtları için %20 erken kayıt indirimi', 'erken-kayit-kampanyasi-2025',
    'early_bird_sms', 'ACTIVE', 'NEW_STUDENTS', 'Anaokulu, İlkokul',
    'Kampanya 31 Temmuz 2025 tarihinde sona erer. Sadece yeni öğrenci kayıtları için geçerlidir. Diğer kampanyalarla birleştirilemez.',
    'https://egitimara.com/images/campaigns/early-bird-thumb.jpg',
    'Erken Kayıt Kampanyası - %20 İndirim Fırsatı!', 'https://youtube.com/watch?v=erkenkayit2025'
),
-- Kardeş indirimi kampanyası
(
    8.7, null, 15.0, null, '2026-08-31',
    null, null, null, true, false,
    true, 12000.00, 25000.00, null, null,
    3, 2, null, false, true,
    2, '2025-09-01', 18, 3, false,
    true, 34, 100, 67, '2025-08-10 16:45:00',
    2, 1256, 34, '2025-08-05 11:30:00', 2, 2,
    '2025-08-20 14:10:00', 2, 1256, '2025-2026', '#4ECDC4', 'KARDEŞ İNDİRİMİ %15',
    'https://egitimara.com/images/campaigns/sibling-discount-banner.jpg',
    '["family_counseling", "joint_parent_meetings"]', 'SIBLING_DISCOUNT',
    'Başvuru Yap', '/kardesh-indirim-basvuru',
    'Aile bağlarını güçlendiriyoruz! İkinci çocuğunuz için %15 kardeş indirimi. Aynı okula kayıtlı kardeşler için özel avantajlar ve aile danışmanlığı hizmeti dahil.',
    'PERCENTAGE', 'sibling_template', 'En az iki kardeşin aynı okulda kayıtlı olması gerekir.',
    'İndirim ikinci ve sonraki kardeşler için geçerlidir. Büyük kardeş tam ücret öder.',
    '["family_counseling_sessions"]', '["sibling_activity_book"]', 'Standart taksit seçenekleri',
    'Kardeş indirimi kampanyası ile ikinci çocuk %15 indirimli', 'kardeş indirimi, aile, okul',
    'Kardeş İndirimi - Aile Dostu Eğitim', 'Kardeş indirimi başvurularınızı bekliyoruz.',
    'KARDESH15', 'Kardeş ayrılması durumunda indirim iptal edilir.',
    'İkinci ve sonraki çocuklar için %15 kardeş indirimi', 'kardesh-indirimi-2025',
    'sibling_sms', 'ACTIVE', 'SIBLINGS', 'Tüm seviyeler',
    'Kardeş indirimi için en az iki çocuğun aynı okulda kayıtlı olması gerekmektedir. İndirim küçük kardeş/kardeşlere uygulanır.',
    'https://egitimara.com/images/campaigns/sibling-thumb.jpg',
    'Kardeş İndirimi - Aile Dostu Eğitim', null
),
-- Yaz okulu kampanyası
(
    22.1, 2500.00, null, null, '2025-06-30',
    '2025-06-15', '2025-04-01', null, true, true,
    true, 2500.00, 10000.00, 15, 50,
    1, 3, '2025-06-15', true, true,
    3, '2025-04-01', 12, 6, false,
    false, 78, 50, 94, '2025-03-28 13:20:00',
    3, 1847, 78, '2025-03-25 09:45:00', 3, 3,
    '2025-06-20 16:30:00', 3, 1847, '2025', '#F39C12', 'YAZ OKULU ÖZEL FİYAT',
    'https://egitimara.com/images/campaigns/summer-school-banner.jpg',
    '["swimming_lessons", "art_workshops", "stem_activities"]', 'SUMMER_SCHOOL',
    'Kayıt Ol', '/yaz-okulu-kayit',
    '8 haftalık yaz okulu programına özel fiyat! STEAM aktiviteleri, yüzme dersleri, sanat atölyeleri ve doğa gezileri ile unutulmaz bir yaz geçirin. Sınırlı kontenjan!',
    'FIXED_AMOUNT', 'summer_school_template', 'Sadece kayıtlı öğrenciler başvurabilir.',
    'Program minimum 20 öğrenci ile başlar. İptal durumunda 1 hafta öncesine kadar %80 iade.',
    '["swimming_lessons", "art_materials", "field_trips", "lunch"]',
    '["summer_t_shirt", "activity_book", "certificate"]', 'Peşin ödeme %5 ek indirim',
    'Yaz okulu programı özel kampanya fiyatı ile sınırlı süre', 'yaz okulu, yaz programı, etkinlik',
    'Yaz Okulu 2025 - Özel Kampanya', 'Yaz okulu kayıtları başladı, yerini ayırt!',
    'YAZOKULU25', 'Program başlamadan 1 hafta öncesine kadar %80 iade yapılır.',
    '8 haftalık yaz okulu programına özel indirimli fiyat', 'yaz-okulu-kampanyasi-2025',
    'summer_school_sms', 'ACTIVE', 'EXISTING_STUDENTS', '1-6. Sınıf',
    'Yaz okulu programına kayıt için son tarih 15 Haziran 2025. Minimum 20 öğrenci ile program başlar.',
    'https://egitimara.com/images/campaigns/summer-thumb.jpg',
    'Yaz Okulu 2025 - Eğlence Dolu 8 Hafta!', 'https://youtube.com/watch?v=yazokulu2025'
),
-- Deneme dersi kampanyası
(
    35.8, null, null, null, '2025-12-31',
    null, null, 7, true, false,
    true, null, null, null, null,
    1, 4, null, false, true,
    4, '2025-09-01', 8, 4, true,
    false, 124, 300, 198, '2025-08-28 10:15:00',
    1, 3456, 124, '2025-08-25 15:30:00', 1, 1,
    '2025-08-30 11:45:00', 1, 3456, '2025-2026', '#9B59B6', 'ÜCRETSİZ DENEME HAFTA',
    'https://egitimara.com/images/campaigns/free-trial-banner.jpg',
    '["trial_week", "assessment_report", "parent_consultation"]', 'FREE_TRIAL',
    'Deneme Haftasına Başla', '/ucretsiz-deneme',
    'Karar vermeden önce bizi tanıyın! 1 hafta ücretsiz deneme programı. Çocuğunuzun okula uyumunu gözlemleyin, öğretmenlerimizle tanışın. Hiçbir yükümlülük olmadan!',
    'NO_DISCOUNT', 'free_trial_template', 'Daha önce deneme programına katılanlar tekrar başvuramaz.',
    '1 haftalık deneme süresi sonunda kayıt zorunluluğu yoktur. Değerlendirme raporu ücretsiz verilir.',
    '["trial_classes", "assessment", "consultation"]', '["trial_kit", "evaluation_report"]',
    'Deneme sonrası kayıt durumunda normal taksit seçenekleri',
    'Ücretsiz 1 haftalık deneme programı ile okulu tanıyın', 'ücretsiz deneme, trial, okul tanıma',
    'Ücretsiz Deneme Haftası - Hiç Risk Yok!', 'Ücretsiz deneme haftanız sizi bekliyor!',
    'DENEME7', 'Deneme süresince hiçbir ücret talep edilmez.',
    'Hiçbir yükümlülük olmadan 1 hafta ücretsiz deneme', 'ucretsiz-deneme-haftasi',
    'free_trial_sms', 'ACTIVE', 'NEW_STUDENTS', 'Anaokulu, İlkokul',
    'Ücretsiz deneme haftası 7 gün sürer. Sonrasında kayıt zorunluluğu yoktur. Her aile sadece 1 kez faydalanabilir.',
    'https://egitimara.com/images/campaigns/trial-thumb.jpg',
    'Ücretsiz Deneme Haftası - Risk Almadan Tanıyın!', null
),
-- Burs kampanyası
(
    5.2, null, 50.0, null, '2025-07-15',
    null, null, null, true, true,
    false, 25000.00, 50000.00, null, 10,
    1, 5, '2025-07-01', true, false,
    5, '2025-05-01', 14, 10, true,
    false, 12, 10, 28, '2025-04-20 12:00:00',
    3, 234, 12, '2025-04-15 14:45:00', 3, 3,
    '2025-07-10 16:20:00', 3, 234, '2025-2026', '#E74C3C', 'BAŞARI BURSU %50',
    'https://egitimara.com/images/campaigns/scholarship-banner.jpg',
    '["academic_mentoring", "study_materials"]', 'SCHOLARSHIP',
    'Burs Başvurusu Yap', '/basari-bursu-basvuru',
    'Akademik başarı ödüllendiriliyor! Ortaokul öğrencileri için %50 başarı bursu. Sınav sonuçları ve portfolyo değerlendirmesi ile belirlenir. Sınırlı kontenjan!',
    'PERCENTAGE', 'scholarship_template', 'Sadece yeni kayıt yaptıran öğrenciler başvurabilir.',
    'Burs değerlendirmesi yapılır. Akademik performans korunmalıdır. Başarısızlık durumunda burs iptal edilir.',
    '["academic_mentoring", "extra_materials"]', '["certificate", "recognition_award"]',
    'Burs alan öğrenciler için özel ödeme planı',
    'Başarı bursu ile %50 indirim fırsatı akademik başarıyı ödüllendiriyor',
    'başarı bursu, scholarship, akademik başarı', 'Başarı Bursu - Akademik Mükemmellik',
    'Başarı bursu başvuruları için son günler!', 'BASARI50',
    'Burs koşulları sağlanmazsa normal ücret tarifesi uygulanır.',
    'Akademik başarı gösteren öğrenciler için %50 burs', 'basari-bursu-2025',
    'scholarship_sms', 'ACTIVE', 'NEW_STUDENTS', '5-8. Sınıf',
    'Başarı bursu için başvuru son tarihi 1 Temmuz 2025. Sınav ve mülakat sonucu belirlenir. Yılda 10 öğrenci alınır.',
    'https://egitimara.com/images/campaigns/scholarship-thumb.jpg',
    'Başarı Bursu - Akademik Mükemmelliğe Ödül', null
);

-- ======= CAMPAIGN CONTENTS =======
INSERT INTO campaign_contents (
    attribution_required, duration_seconds, engagement_rate, is_active, is_primary,
    is_test_variant, sort_order, approved_at, approved_by, campaign_id, click_count,
    created_at, created_by, download_count, file_size_bytes, share_count, updated_at,
    updated_by, view_count, alt_text, approval_status, attribution_text, caption,
    content, content_type, copyright_owner, dimensions, hashtags, language_code,
    license_type, media_url, mention_accounts, mime_type, rejection_reason,
    social_media_platforms, thumbnail_url, title, usage_context, usage_rights,
    variant_name
) VALUES
-- Erken kayıt kampanyası banner
(
    false, null, 12.5, true, true,
    false, 1, '2025-05-25 14:35:00', 1, 1, 1847,
    '2025-05-22 09:20:00', 1, 0, 2456789, 156, '2025-05-25 14:35:00',
    1, 12450, 'Erken kayıt kampanyası ana banner görsel', 'APPROVED', null,
    'Son günler! %20 erken kayıt indirimi kaçmaz',
    'Yeni akademik yıl için erken kayıt yapan aileler %20 indirimden faydalanıyor. Ücretsiz oryantasyon ve hoş geldin paketi hediye!',
    'BANNER_IMAGE', 'Eğitim Ara Tasarım Ekibi', '1920x600', '#ErkenKayıt #Eğitim #İndirim',
    'tr', 'Exclusive', 'https://egitimara.com/images/campaigns/early-bird-banner.jpg',
    null, 'image/jpeg', null, '["website", "social_media", "email"]',
    'https://egitimara.com/images/campaigns/early-bird-thumb.jpg', 'Erken Kayıt Banner',
    'WEBSITE_HOMEPAGE', 'Eğitim Ara platformunda kullanım hakkı saklıdır', 'Ana Banner'
),
-- Kardeş indirimi sosyal medya görseli
(
    false, null, 8.2, true, true,
    false, 1, '2025-08-10 16:50:00', 2, 2, 890,
    '2025-08-07 13:15:00', 2, 234, 1876543, 67, '2025-08-10 16:50:00',
    2, 3421, 'Kardeş indirimi sosyal medya paylaşım görseli', 'APPROVED', null,
    'Aile bağları güçlensin, eğitim masrafları azalsın! 💙',
    'İkinci çocuğunuz için %15 kardeş indirimi. Aynı kaliteli eğitim, daha uygun fiyat!',
    'SOCIAL_MEDIA_POST', 'Eğitim Ara Sosyal Medya Ekibi', '1080x1080', '#Kardeşİndirimi #AileDostu',
    'tr', 'Exclusive', 'https://egitimara.com/images/campaigns/sibling-social.jpg',
    '@egitimara', 'image/jpeg', null, '["instagram", "facebook", "twitter"]',
    'https://egitimara.com/images/campaigns/sibling-social-thumb.jpg', 'Kardeş İndirimi Post',
    'SOCIAL_MEDIA', 'Sosyal medya platformlarında kullanım izni', 'Instagram Post'
),
-- Yaz okulu tanıtım videosu
(
    true, 45, 18.7, true, true,
    false, 1, '2025-03-28 13:25:00', 3, 3, 2156,
    '2025-03-26 11:40:00', 3, 456, 67890123, 189, '2025-03-28 13:25:00',
    3, 5673, 'Yaz okulu programı tanıtım videosu', 'APPROVED', 'Video: Eğitim Ara Medya',
    'Unutulmaz bir yaz geçirmeye hazır mısınız? 🌞',
    '8 haftalık yaz okulu programımızda yüzme, sanat, STEAM ve doğa aktiviteleri!',
    'PROMOTIONAL_VIDEO', 'Eğitim Ara Video Prodüksiyon', '1920x1080', '#YazOkulu #Eğlence #Öğrenme',
    'tr', 'Exclusive', 'https://egitimara.com/videos/campaigns/summer-school-promo.mp4',
    '@egitimara @yazokulu2025', 'video/mp4', null, '["youtube", "instagram", "website"]',
    'https://egitimara.com/videos/campaigns/summer-school-thumb.jpg', 'Yaz Okulu Tanıtım',
    'CAMPAIGN_PAGE', 'Tüm medya kanallarında kullanım hakkı', 'Tanıtım Videosu'
),
-- Deneme haftası infografik
(
    false, null, 15.3, true, false,
    false, 2, '2025-08-28 10:20:00', 1, 4, 567,
    '2025-08-26 14:50:00', 1, 89, 3456789, 45, '2025-08-28 10:20:00',
    1, 1876, 'Ücretsiz deneme haftası süreç infografiği', 'APPROVED', null,
    '7 adımda ücretsiz deneme süreci 📋',
    'Hiçbir yükümlülük olmadan okulumuzı 1 hafta ücretsiz deneyimleyin!',
    'INFOGRAPHIC', 'Eğitim Ara Grafik Tasarım', '800x2000', '#ÜcretsizDeneme #SüreçRehberi',
    'tr', 'Creative Commons', 'https://egitimara.com/images/campaigns/trial-process-infographic.jpg',
    null, 'image/png', null, '["website", "email", "print"]',
    'https://egitimara.com/images/campaigns/trial-infographic-thumb.jpg', 'Deneme Süreci',
    'EMAIL_CAMPAIGN', 'Eğitimsel kullanım serbest', 'Süreç İnfografiği'
);

-- ======= CAMPAIGN SCHOOLS =======
INSERT INTO campaign_schools (
    application_count, approved_by_school, conversion_count, custom_discount_amount,
    custom_discount_percentage, custom_end_date, custom_start_date, custom_usage_limit,
    is_active, is_featured_on_school, priority, revenue_generated, show_on_homepage,
    show_on_pricing_page, usage_count, appointment_count, approved_by_school_at,
    approved_by_school_user_id, assigned_at, assigned_by_user_id, campaign_id,
    click_count, created_at, created_by, inquiry_count, school_id, updated_at,
    updated_by, view_count, custom_terms, school_notes, status
) VALUES
-- Erken kayıt - Eğitim Dünyası Maslak Anaokulu
(
    45, true, 34, null, null, null, null, null,
    true, true, 1, 612000.00, true, true, 34, 67,
    '2025-06-02 09:30:00', 1, '2025-06-01 08:15:00', 1, 1,
    1245, '2025-06-01 08:15:00', 1, 89, 1, '2025-08-15 16:20:00',
    1, 2847, 'Anaokulu için özel oryantasyon programı dahil',
    'Çok başarılı geçen kampanya, hedefi aştık', 'ACTIVE'
),
-- Kardeş indirimi - Bilim Sanat Kızılay Lise
(
    12, true, 8, null, 18.0, null, null, null,
    true, false, 2, 144000.00, false, true, 8, 23,
    '2025-08-12 14:45:00', 3, '2025-08-10 11:20:00', 2, 2,
    456, '2025-08-10 11:20:00', 2, 34, 4, '2025-08-20 10:15:00',
    2, 1256, 'Lise öğrencileri için %18 kardeş indirimi uygulanıyor',
    'Özel indirim oranı ile daha cazip', 'ACTIVE'
),
-- Yaz okulu - Tüm okullar
(
    18, true, 16, 2000.00, null, null, null, 20,
    true, true, 1, 320000.00, true, false, 16, 28,
    '2025-04-02 16:30:00', 3, '2025-04-01 10:00:00', 3, 3,
    892, '2025-04-01 10:00:00', 3, 45, 3, '2025-06-20 15:45:00',
    3, 1847, 'Bilim Sanat için özel 2000 TL indirim',
    'Kontenjan doldu, çok başarılı', 'COMPLETED'
),
-- Deneme haftası - Eğitim Dünyası Moda İlkokul
(
    56, true, 19, null, null, null, null, null,
    true, false, 3, 0.00, false, false, 19, 78,
    '2025-08-30 12:15:00', 2, '2025-08-28 09:45:00', 1, 4,
    2134, '2025-08-28 09:45:00', 1, 134, 6, '2025-08-30 17:30:00',
    1, 3456, 'Sanat ağırlıklı program için özel deneme haftası',
    'Çok ilgi gören program, kayıt oranı yüksek', 'ACTIVE'
);

-- ======= CAMPAIGN USAGES =======
INSERT INTO campaign_usages (
    discount_amount, final_amount, follow_up_completed, follow_up_required, is_active,
    is_validated, original_amount, student_age, appointment_id, approved_at, approved_by,
    campaign_id, created_at, created_by, enrollment_id, follow_up_date, invoice_id,
    processed_at, school_id, updated_at, updated_by, usage_date, user_id, validated_at,
    validation_expires_at, enrollment_year, grade_level, internal_notes, ip_address,
    notes, parent_email, parent_name, parent_phone, promo_code_used, referrer_url,
    status, student_name, usage_type, user_agent, utm_campaign, utm_medium, utm_source,
    validation_code
) VALUES
-- Erken kayıt kampanyası kullanımı
(
    10800.00, 43200.00, false, true, true,
    true, 54000.00, 4, 1, '2025-06-15 11:30:00', 1,
    1, '2025-06-10 14:20:00', null, null, '2025-07-01', null,
    '2025-06-15 11:30:00', 1, '2025-06-15 11:30:00', 1, '2025-06-10 14:20:00', 4,
    '2025-06-10 15:45:00', '2025-06-24 23:59:59', '2025-2026', 'Anaokulu',
    'Çok memnun aile, referans potansiyeli yüksek', '178.240.15.123',
    'Erken kayıt indirimi uygulandı, oryantasyon programına dahil edildi',
    'ayse.ozkan@email.com', 'Ayşe Özkan', '+905551234570', 'ERKENKAYIT20',
    'https://egitimara.com/anaokulu-bilgileri', 'COMPLETED', 'Elif Özkan', 'ENROLLMENT',
    'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'spring_enrollment', 'social', 'facebook',
    'VAL-ERK-001'
),
-- Kardeş indirimi kullanımı
(
    14400.00, 81600.00, true, false, true,
    true, 96000.00, 12, null, '2025-08-18 09:45:00', 2,
    2, '2025-08-15 16:30:00', null, null, null, null,
    '2025-08-18 09:45:00', 3, '2025-08-20 14:20:00', 2, '2025-08-15 16:30:00', 3,
    '2025-08-16 10:15:00', '2025-08-30 23:59:59', '2025-2026', '6. Sınıf',
    'İkinci kardeş için indirim uygulandı, aile çok memnun', '178.240.22.84',
    'Kardeş indirimi başarıyla uygulandı, ödeme planı düzenlendi',
    'zeynep.kaya@email.com', 'Zeynep Kaya', '+905551234568', 'KARDESH15',
    'https://egitimara.com/kardesh-indirim', 'COMPLETED', 'Emre Kaya', 'ENROLLMENT',
    'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0)', 'sibling_discount', 'email', 'newsletter',
    'VAL-KRD-002'
),
-- Yaz okulu kampanyası kullanımı
(
    2000.00, 10200.00, false, true, true,
    true, 12200.00, 8, null, '2025-04-15 13:20:00', 3,
    3, '2025-04-08 10:45:00', null, null, '2025-05-01', null,
    '2025-04-15 13:20:00', 3, '2025-04-15 13:20:00', 3, '2025-04-08 10:45:00', 3,
    '2025-04-08 11:30:00', '2025-04-22 23:59:59', '2025', '5. Sınıf',
    'Bilim Sanat için özel indirim uygulandı, programla uyumlu', '178.240.8.156',
    'Yaz okulu kayıt tamamlandı, özel indirim ile çok avantajlı',
    'mehmet.demir@email.com', 'Mehmet Demir', '+905551234569', 'YAZOKULU25',
    'https://egitimara.com/yaz-okulu-programi', 'COMPLETED', 'Ali Demir', 'ENROLLMENT',
    'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', 'summer_programs', 'organic', 'google',
    'VAL-YAZ-003'
),
-- Deneme haftası kullanımı
(
    null, null, false, true, true,
    true, null, 7, null, null, null,
    4, '2025-08-29 09:15:00', null, null, '2025-09-15', null,
    null, 6, '2025-08-29 09:15:00', 1, '2025-08-29 09:15:00', 1,
    '2025-08-29 09:30:00', '2025-09-12 23:59:59', '2025-2026', '2. Sınıf',
    'Sanat ağırlıklı programa çok uygun, kayıt potansiyeli yüksek', '178.240.45.67',
    'Deneme haftası başarıyla tamamlandı, veli çok memnun kaldı',
    'ahmet.yilmaz@email.com', 'Ahmet Yılmaz', '+905551234567', 'DENEME7',
    'https://egitimara.com/ucretsiz-deneme', 'COMPLETED', 'Ege Yılmaz', 'TRIAL_REQUEST',
    'Mozilla/5.0 (Linux; Android 11)', 'free_trial', 'direct', 'website',
    'VAL-DEN-004'
),
-- Burs kampanyası başvurusu
(
    null, null, false, true, true,
    false, null, 11, null, null, null,
    5, '2025-06-20 14:30:00', null, null, '2025-07-15', null,
    null, 4, '2025-06-20 14:30:00', null, '2025-06-20 14:30:00', null,
    null, '2025-07-04 23:59:59', '2025-2026', '5. Sınıf',
    'Başvuru değerlendirme aşamasında, akademik referanslar güçlü', '178.240.31.92',
    'Başarı bursu başvurusu alındı, değerlendirme süreci devam ediyor',
    'fatma.kara@email.com', 'Fatma Kara', '+905552223344', 'BASARI50',
    'https://egitimara.com/basari-bursu', 'PENDING', 'Selin Kara', 'FORM_SUBMISSION',
    'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'scholarship_2025', 'referral', 'friend',
    'VAL-BRS-005'
),
-- Erken kayıt randevu talebi
(
    null, null, true, false, true,
    true, null, 5, 2, null, null,
    1, '2025-06-25 11:45:00', null, null, null, null,
    null, 2, '2025-06-25 11:45:00', 2, '2025-06-25 11:45:00', 2,
    '2025-06-25 12:00:00', '2025-07-09 23:59:59', '2025-2026', 'İlkokul',
    'Kampanya etkisi ile randevu talebi, takip edildi', '178.240.19.75',
    'Erken kayıt kampanyası sayesinde randevu alındı, görüşme yapıldı',
    'sirket@egitimara.com', 'Zeynep Kaya', '+905551234568', 'ERKENKAYIT20',
    'https://egitimara.com/randevu-al', 'COMPLETED', 'Can Kaya', 'APPOINTMENT',
    'Mozilla/5.0 (Safari; iPhone)', 'spring_enrollment', 'social', 'instagram',
    'VAL-RND-006'
);




-- V9__Insert_Demo_Analytics_Performance_Data.sql
-- eğitimara.com için analytics ve performans izleme sistemi demo verileri

-- ======= PERFORMANCE METRICS =======
INSERT INTO performance_metrics (
    cache_hit, cache_ttl_seconds, conversion_event, cpu_time_ms, cpu_usage_percentage,
    db_connection_time_ms, db_query_count, db_query_time_ms, external_api_calls,
    external_api_errors, external_api_time_ms, file_io_time_ms, file_read_count,
    file_write_count, gzip_enabled, http_status_code, is_active, keep_alive,
    memory_total_mb, memory_usage_percentage, memory_used_mb, network_latency_ms,
    response_time_ms, success, threshold_exceeded, threshold_value, bytes_received,
    bytes_sent, created_at, created_by, request_size_bytes, response_size_bytes,
    timestamp, updated_at, updated_by, user_id, additional_metrics, application_version,
    business_operation, cache_key, endpoint_url, error_message, error_stack_trace,
    feature_name, http_method, ip_address, jvm_version, metric_category, server_instance,
    server_name, session_id, threshold_type, user_agent
) VALUES
-- Ana sayfa yüklenme performansı
(
    true, 3600, false, 45, 12.5,
    8, 3, 25, 0,
    0, 0, 5, 2,
    0, true, 200, true, true,
    2048.0, 18.7, 383.2, 12,
    187, true, false, 500.0, 2856,
    15483, CURRENT_TIMESTAMP - INTERVAL '2 hours', 1, 2856, 15483,
    CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP - INTERVAL '2 hours', 1, 4,
    '{"cache_provider": "Redis", "database_pool_size": 10, "active_sessions": 234}',
    'v2.1.3', 'HOMEPAGE_LOAD', 'homepage:schools:list', '/',
    null, null, 'Homepage School Listing', 'GET', '178.240.15.123',
    'OpenJDK 17.0.2', 'WEB_REQUEST', 'app-server-01', 'egitimara-prod-01',
    'sess_12345abc', 'RESPONSE_TIME', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)'
),
-- Veritabanı sorgu performansı
(
    false, null, false, 125, 8.3,
    15, 1, 89, 0,
    0, 0, 0, 0,
    0, false, null, true, false,
    2048.0, 18.7, 383.2, null,
    89, true, false, 100.0, 0,
    0, CURRENT_TIMESTAMP - INTERVAL '1 hour 30 minutes', 1, 0, 0,
    CURRENT_TIMESTAMP - INTERVAL '1 hour 30 minutes', CURRENT_TIMESTAMP - INTERVAL '1 hour 30 minutes', 1, null,
    '{"query_type": "SELECT", "table": "schools", "index_used": true, "rows_examined": 156}',
    'v2.1.3', 'SCHOOL_SEARCH', null, null,
    null, null, 'School Search Query', null, null,
    'OpenJDK 17.0.2', 'DATABASE', 'app-server-01', 'egitimara-prod-01',
    null, 'QUERY_TIME', null
),
-- Dış API çağrısı performansı
(
    null, null, false, 28, 5.2,
    null, 0, 0, 1,
    0, 234, 0, 0,
    0, false, 200, true, false,
    2048.0, 18.7, 383.2, 45,
    234, true, false, 300.0, 1456,
    687, CURRENT_TIMESTAMP - INTERVAL '45 minutes', 1, 1456, 687,
    CURRENT_TIMESTAMP - INTERVAL '45 minutes', CURRENT_TIMESTAMP - INTERVAL '45 minutes', 1, 3,
    '{"api_provider": "Google Maps", "endpoint": "geocoding", "retry_count": 0}',
    'v2.1.3', 'LOCATION_GEOCODING', null, '/api/geocode',
    null, null, 'Address Geocoding', null, null,
    'OpenJDK 17.0.2', 'EXTERNAL_API', 'app-server-02', 'egitimara-prod-02',
    'sess_67890def', 'API_RESPONSE_TIME', null
),
-- Cache miss durumu
(
    false, null, false, 67, 15.8,
    12, 5, 156, 0,
    0, 0, 8, 3,
    1, true, 200, true, true,
    2048.0, 20.3, 416.1, 18,
    298, true, true, 200.0, 3456,
    25678, CURRENT_TIMESTAMP - INTERVAL '30 minutes', 1, 3456, 25678,
    CURRENT_TIMESTAMP - INTERVAL '30 minutes', CURRENT_TIMESTAMP - INTERVAL '30 minutes', 1, 2,
    '{"cache_miss_reason": "TTL_EXPIRED", "cache_rebuild_time": 145}',
    'v2.1.3', 'SCHOOL_DETAILS', 'school:1:details', '/okul/egitim-dunyasi-maslak',
    null, null, 'School Detail Page', 'GET', '178.240.22.84',
    'OpenJDK 17.0.2', 'CACHE', 'app-server-01', 'egitimara-prod-01',
    'sess_abc123xy', 'CACHE_PERFORMANCE', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0)'
),
-- Yükleme hatası
(
    null, null, false, 890, 45.2,
    null, 0, 0, 1,
    1, 5000, 0, 0,
    0, false, 500, true, false,
    2048.0, 45.2, 925.8, 156,
    5000, false, true, 1000.0, 234,
    0, CURRENT_TIMESTAMP - INTERVAL '15 minutes', 1, 234, 0,
    CURRENT_TIMESTAMP - INTERVAL '15 minutes', CURRENT_TIMESTAMP - INTERVAL '15 minutes', 1, null,
    '{"error_type": "TIMEOUT", "timeout_duration": 5000}',
    'v2.1.3', 'FILE_UPLOAD', null, '/api/upload',
    'Connection timeout after 5000ms',
    'java.net.SocketTimeoutException: Read timed out\n\tat java.base/java.net.SocketInputStream.socketRead0(Native Method)',
    'Gallery Image Upload', 'POST', '178.240.8.156',
    'OpenJDK 17.0.2', 'UPLOAD', 'app-server-03', 'egitimara-prod-03',
    'sess_upload789', 'ERROR_THRESHOLD', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)'
);

-- ======= ANALYTICS =======
INSERT INTO analytics (
    appointment_conversion_rate, appointments_growth_rate, average_rating, average_session_duration_seconds,
    bounce_rate, churn_rate, conversion_rate, date, discount_amount_used, enrollment_conversion_rate,
    error_rate, inquiries_growth_rate, inquiry_conversion_rate, is_active, page_load_time_ms,
    pages_per_session, rating_change, revenue, search_to_appointment_rate, server_response_time_ms,
    survey_completion_rate, uptime_percentage, visitors_growth_rate, appointment_completions,
    appointment_confirmations, appointment_requests, brand_id, brochure_downloads, calculation_duration_ms,
    campaign_applications, campaign_clicks, campaign_conversions, campaign_views, campus_id,
    canceled_subscriptions, created_at, created_by, desktop_visitors, direct_traffic, direction_clicks,
    email_clicks, email_traffic, gallery_views, internal_searches, international_visitors,
    last_calculated_at, local_visitors, message_inquiries, mobile_visitors, national_visitors,
    new_subscriptions, new_visitors, organic_search_traffic, page_views, paid_search_traffic,
    phone_clicks, post_comments, post_likes, post_shares, post_views, promo_code_usage,
    referral_traffic, returning_visitors, school_id, social_media_clicks, social_media_traffic,
    subscription_renewals, survey_responses, tablet_visitors, total_ratings, unique_visitors,
    updated_at, updated_by, video_views, zero_result_searches, custom_metrics, data_source,
    metric_type, time_period
) VALUES
-- Günlük trafik analizi - Eğitim Dünyası Maslak
(
    15.2, 8.5, 4.3, 245,
    35.8, null, 12.4, CURRENT_DATE - INTERVAL '1 day', 25600.00, 8.9,
    0.8, 12.3, 18.7, true, 187,
    3.4, 0.2, 156000.00, 22.1, 89,
    67.5, 99.8, 15.6, 23,
    34, 45, 1, 67, 234,
    89, 1847, 23, 2456, 1,
    0, CURRENT_TIMESTAMP - INTERVAL '1 day', 1, 1456, 892, 156,
    23, 234, 567, 234, 12,
    CURRENT_TIMESTAMP - INTERVAL '8 hours', 2134, 89, 1234, 1876,
    3, 789, 1567, 3456, 123,
    78, 34, 67, 45, 892, 12,
    345, 567, 1, 89, 234,
    2, 34, 234, 89, 2345,
    CURRENT_TIMESTAMP - INTERVAL '1 day', 1, 234, 5,
    '{"top_search_terms": ["anaokulu", "montessori", "fiyat"], "popular_pages": ["/", "/anaokulu"]}',
    'Google Analytics', 'TRAFFIC', 'DAILY'
),
-- Haftalık dönüşüm analizi - Bilim Sanat Kızılay
(
    22.3, 15.2, 4.7, 312,
    28.4, 2.1, 18.9, CURRENT_DATE - INTERVAL '7 days', 45200.00, 12.6,
    0.3, 25.8, 28.4, true, 156,
    4.2, 0.4, 284000.00, 31.7, 76,
    78.2, 99.9, 22.1, 45,
    67, 89, 2, 123, 567,
    156, 2847, 45, 4567, 2,
    1, CURRENT_TIMESTAMP - INTERVAL '7 days', 3, 2345, 1456, 234,
    45, 567, 892, 456, 23,
    CURRENT_TIMESTAMP - INTERVAL '2 days', 3456, 156, 1876, 2987,
    8, 1234, 2345, 5678, 234,
    123, 67, 134, 89, 1456, 28,
    456, 892, 3, 134, 456,
    5, 89, 345, 156, 3789,
    CURRENT_TIMESTAMP - INTERVAL '7 days', 3, 456, 8,
    '{"conversion_funnel": {"homepage": 5678, "school_page": 2345, "appointment": 89}}',
    'Internal Analytics', 'CONVERSION', 'WEEKLY'
),
-- Aylık performans analizi - Gelişim Alsancak
(
    18.7, 5.3, 4.1, 287,
    42.1, 3.8, 14.2, CURRENT_DATE - INTERVAL '30 days', 18900.00, 9.4,
    1.2, 8.9, 15.6, true, 234,
    2.8, -0.1, 89000.00, 19.3, 123,
    56.8, 99.5, 8.9, 67,
    89, 134, 3, 89, 456,
    67, 1234, 23, 2134, 3,
    2, CURRENT_TIMESTAMP - INTERVAL '30 days', 4, 1234, 789, 89,
    34, 123, 345, 234, 8,
    CURRENT_TIMESTAMP - INTERVAL '7 days', 1567, 67, 892, 1345,
    2, 567, 1234, 2567, 89,
    56, 23, 78, 34, 567, 8,
    234, 456, 5, 67, 189,
    3, 45, 123, 67, 1801,
    CURRENT_TIMESTAMP - INTERVAL '30 days', 4, 189, 12,
    '{"retention_rate": 85.6, "customer_lifetime_value": 125000}',
    'Business Intelligence', 'ENGAGEMENT', 'MONTHLY'
),
-- Sistem performans ölçümleri
(
    null, null, null, null,
    null, null, null, CURRENT_DATE, null, null,
    0.2, null, null, true, 89,
    null, null, null, null, 89,
    null, 99.9, null, null,
    null, null, null, null, 145,
    null, null, null, null, null,
    null, CURRENT_TIMESTAMP - INTERVAL '1 hour', 1, null, null, null,
    null, null, null, null, null,
    CURRENT_TIMESTAMP - INTERVAL '30 minutes', null, null, null, null,
    null, null, null, 45678, null,
    null, null, null, null, null, null,
    null, null, null, null, null,
    null, null, null, null, null,
    CURRENT_TIMESTAMP - INTERVAL '1 hour', 1, null, null,
    '{"avg_response_time": 89, "error_count": 12, "active_connections": 145}',
    'System Monitoring', 'PERFORMANCE', 'HOURLY'
);

-- ======= SEARCH LOGS =======
INSERT INTO search_logs (
    abandoned_search, autocomplete_used, clicked_result_position, confidence_score, is_active,
    page_number, refined_search, response_time_ms, results_count, results_per_page,
    search_radius_km, time_to_click_seconds, zero_results, clicked_school_id, created_at,
    created_by, search_time, updated_at, updated_by, user_id, cleaned_query, device_type,
    experiment_id, filters_applied, ip_address, search_intent, search_query, search_type,
    session_id, sort_order, suggestion_selected, user_agent, user_location, variant
) VALUES
-- Anaokulu arama
(
    false, true, 1, 0.89, true,
    1, false, 145, 12, 10,
    null, 3, false, 1, CURRENT_TIMESTAMP - INTERVAL '2 hours',
    null, CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP - INTERVAL '2 hours', null, 4,
    'anaokulu istanbul', 'MOBILE', 'search_exp_001',
    '{"location": "İstanbul", "institution_type": "anaokulu", "age_group": "3-6"}',
    '178.240.15.123', 'LOCAL', 'anaokulu istanbul maslak', 'LOCATION',
    'sess_search_001', 'relevance', 'anaokulu',
    'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0)', 'İstanbul, Türkiye', 'A'
),
-- Özel okul fiyat araması
(
    false, false, 3, 0.76, true,
    1, true, 234, 24, 10,
    null, 8, false, 2, CURRENT_TIMESTAMP - INTERVAL '1 hour 30 minutes',
    null, CURRENT_TIMESTAMP - INTERVAL '1 hour 30 minutes', CURRENT_TIMESTAMP - INTERVAL '1 hour 30 minutes', null, 3,
    'özel okul fiyat ankara', 'DESKTOP', null,
    '{"location": "Ankara", "price_range": "50000-100000"}',
    '178.240.22.84', 'PRICE_COMPARISON', 'özel okul fiyatları ankara çankaya', 'PRICE_RANGE',
    'sess_search_002', 'price_low_to_high', null,
    'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'Ankara, Türkiye', 'B'
),
-- STEAM eğitimi arama
(
    false, true, 2, 0.92, true,
    1, false, 89, 8, 10,
    null, 5, false, 3, CURRENT_TIMESTAMP - INTERVAL '45 minutes',
    null, CURRENT_TIMESTAMP - INTERVAL '45 minutes', CURRENT_TIMESTAMP - INTERVAL '45 minutes', null, 2,
    'steam eğitim ortaokul', 'DESKTOP', 'search_exp_002',
    '{"curriculum": "STEAM", "grade_level": "ortaokul"}',
    '178.240.8.156', 'ACADEMIC', 'STEAM eğitimi veren ortaokullar', 'CURRICULUM',
    'sess_search_003', 'relevance', 'STEAM eğitimi',
    'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', 'İzmir, Türkiye', 'A'
),
-- Sonuç bulunamayan arama
(
    true, false, null, 0.15, true,
    1, true, 67, 0, 10,
    null, null, true, null, CURRENT_TIMESTAMP - INTERVAL '30 minutes',
    null, CURRENT_TIMESTAMP - INTERVAL '30 minutes', CURRENT_TIMESTAMP - INTERVAL '30 minutes', null, 1,
    'rus dil okul ankara', 'TABLET', null,
    '{"location": "Ankara", "language": "Rusça"}',
    '178.240.31.92', 'ACADEMIC', 'Rusça dil eğitimi veren okullar ankara', 'LANGUAGE',
    'sess_search_004', 'relevance', null,
    'Mozilla/5.0 (iPad; CPU OS 15_0)', 'Ankara, Türkiye', 'B'
),
-- İleri düzey arama
(
    false, false, 1, 0.94, true,
    1, false, 178, 6, 10,
    10, 2, false, 4, CURRENT_TIMESTAMP - INTERVAL '15 minutes',
    null, CURRENT_TIMESTAMP - INTERVAL '15 minutes', CURRENT_TIMESTAMP - INTERVAL '15 minutes', null, null,
    'ib program lise istanbul', 'MOBILE', 'search_exp_001',
    '{"curriculum": "IB", "grade_level": "lise", "location": "İstanbul", "radius": "10km"}',
    '178.240.45.67', 'ACADEMIC', 'IB programı olan liseler istanbul', 'ADVANCED',
    'sess_search_005', 'rating', 'IB programı',
    'Mozilla/5.0 (Linux; Android 11)', 'İstanbul, Türkiye', 'A'
);

-- ======= VISITOR LOGS =======
INSERT INTO visitor_logs (
    anonymized, clicked_directions, clicked_email, clicked_phone, clicked_social_media,
    consent_given, dom_content_loaded_ms, downloaded_brochure, exit_page, first_paint_ms,
    is_active, is_bot, is_bounce, is_new_visitor, largest_contentful_paint_ms, latitude,
    longitude, page_depth, page_load_time_ms, requested_appointment, scroll_depth_percentage,
    sent_message, time_on_page_seconds, viewed_gallery, brand_id, campus_id, created_at,
    created_by, school_id, updated_at, updated_by, user_id, visit_time, bot_name,
    browser_name, browser_version, city, country, device_type, ip_address, language,
    operating_system, page_title, page_url, referrer_url, region, screen_resolution,
    search_keywords, session_id, timezone, traffic_source, user_agent, utm_campaign,
    utm_content, utm_medium, utm_source, utm_term, visitor_id
) VALUES
-- Ana sayfa ziyareti - yeni kullanıcı
(
    false, false, false, false, false,
    true, 1245, false, false, 567,
    true, false, false, true, 1876, 41.0082,
    28.9784, 5, 1876, true, 85,
    true, 234, true, 1, 1, CURRENT_TIMESTAMP - INTERVAL '2 hours',
    null, 1, CURRENT_TIMESTAMP - INTERVAL '2 hours', null, 4, CURRENT_TIMESTAMP - INTERVAL '2 hours',
    null, 'Chrome', '91.0.4472.124', 'İstanbul', 'Turkey', 'MOBILE',
    '178.240.15.123', 'tr-TR', 'iOS 15.0', 'Eğitim Dünyası Maslak Kampüsü | Anaokulu',
    '/okul/egitim-dunyasi-maslak-anaokulu', 'https://google.com/search?q=anaokulu+istanbul',
    'İstanbul', '375x667', 'anaokulu istanbul', 'sess_visitor_001', 'Europe/Istanbul',
    'ORGANIC_SEARCH', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0)', 'spring_enrollment',
    'cta_button', 'organic', 'google', 'anaokulu', 'visitor_12345abc'
),
-- Okul detay sayfası - randevu talebi
(
    false, true, true, true, false,
    true, 892, false, false, 456,
    true, false, false, false, 1234, 39.9334,
    32.8597, 3, 1234, true, 95,
    false, 567, false, 2, 2, CURRENT_TIMESTAMP - INTERVAL '1 hour 30 minutes',
    null, 3, CURRENT_TIMESTAMP - INTERVAL '1 hour 30 minutes', null, 3, CURRENT_TIMESTAMP - INTERVAL '1 hour 30 minutes',
    null, 'Firefox', '89.0', 'Ankara', 'Turkey', 'DESKTOP',
    '178.240.22.84', 'tr-TR', 'Windows 10', 'Bilim Sanat Kızılay Kampüsü | Ortaokul',
    '/okul/bilim-sanat-kizilay-ortaokul', 'https://egitimara.com/',
    'Ankara', '1920x1080', null, 'sess_visitor_002', 'Europe/Istanbul',
    'DIRECT', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', null,
    null, 'direct', 'website', null, 'visitor_67890def'
),
-- Bot ziyareti
(
    true, false, false, false, false,
    false, null, false, true, null,
    true, true, true, null, null, null,
    null, 1, 89, false, null,
    false, 12, false, null, null, CURRENT_TIMESTAMP - INTERVAL '45 minutes',
    null, null, CURRENT_TIMESTAMP - INTERVAL '45 minutes', null, null, CURRENT_TIMESTAMP - INTERVAL '45 minutes',
    'Googlebot', 'Chrome', '91.0.4472.124', null, null, 'UNKNOWN',
    '66.249.66.1', null, 'Linux', 'Ana Sayfa | eğitimara.com',
    '/', null, null, null, null, 'sess_bot_001', null,
    'ORGANIC_SEARCH', 'Mozilla/5.0 (Linux; Android 6.0.1; Nexus 5X Build/MMB29P) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.90 Mobile Safari/537.36 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)',
    null, null, null, null, null, 'bot_google_001'
),
-- Sosyal medyadan gelen ziyaret
(
    false, false, false, false, true,
    true, 567, true, false, 234,
    true, false, false, true, 892, 38.4192,
    27.1287, 4, 892, false, 65,
    false, 456, true, 3, 3, CURRENT_TIMESTAMP - INTERVAL '30 minutes',
    null, 5, CURRENT_TIMESTAMP - INTERVAL '30 minutes', null, null, CURRENT_TIMESTAMP - INTERVAL '30 minutes',
    null, 'Safari', '14.1.2', 'İzmir', 'Turkey', 'MOBILE',
    '178.240.8.156', 'tr-TR', 'iOS 14.8', 'Gelişim Koleji Alsancak | Anaokulu',
    '/okul/gelisim-koleji-alsancak', 'https://instagram.com/',
    'İzmir', '414x896', null, 'sess_visitor_003', 'Europe/Istanbul',
    'SOCIAL_MEDIA', 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_8)', 'summer_programs',
    'instagram_post', 'social', 'instagram', null, 'visitor_abc123xy'
),
-- Bounce ziyareti
(
    false, false, false, false, false,
    false, 2345, false, true, 1234,
    true, false, true, true, 3456, 41.1069,
    28.9958, 1, 3456, false, 15,
    false, 8, false, 1, 4, CURRENT_TIMESTAMP - INTERVAL '15 minutes',
    null, 6, CURRENT_TIMESTAMP - INTERVAL '15 minutes', null, null, CURRENT_TIMESTAMP - INTERVAL '15 minutes',
    null, 'Edge', '91.0.864.59', 'İstanbul', 'Turkey', 'DESKTOP',
    '178.240.31.92', 'tr-TR', 'Windows 11', 'Eğitim Dünyası Moda İlkokul | Sanat Ağırlıklı',
    '/okul/egitim-dunyasi-moda-ilkokul', 'https://google.com/search?q=sanat+okulu+istanbul',
    'İstanbul', '1366x768', 'sanat okulu istanbul', 'sess_visitor_004', 'Europe/Istanbul',
    'PAID_SEARCH', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36 Edg/91.0.864.59',
    'art_schools', 'banner_ad', 'cpc', 'google_ads', 'sanat eğitimi', 'visitor_xyz789uv'
);
