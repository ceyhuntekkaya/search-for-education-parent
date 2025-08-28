
-- Sample parent school list
INSERT INTO parent_school_lists (parent_user_id, list_name, description, is_default, color_code, icon)
VALUES
    (1, 'Favorilerim', 'İlgilendiğim okulların listesi', TRUE, '#2563eb', 'heart'),
    (1, 'Araştırdıklarım', 'Detaylı araştırma yaptığım okullar', FALSE, '#059669', 'search'),
    (2, 'Favorilerim', 'Beğendiğim okullar', TRUE, '#dc2626', 'star');

-- Sample list items (assuming schools with IDs 1, 2, 3 exist)
INSERT INTO parent_school_list_items (parent_school_list_id, school_id, star_rating, is_favorite, personal_notes)
VALUES
    (1, 1, 5, TRUE, 'Çok beğendiğim bir okul, kesinlikle ziyaret edilmeli'),
    (1, 2, 4, TRUE, 'İyi bir seçenek, fiyat biraz yüksek'),
    (2, 3, 3, FALSE, 'Orta seviye, daha fazla araştırma gerekli');

-- Sample school notes
INSERT INTO parent_school_notes (parent_school_list_item_id, school_id, note_title, note_content, category, is_important)
VALUES
    (1, 1, 'Okul Ziyareti', 'Okulu ziyaret ettim, çok beğendim. Özellikle fen laboratuvarları çok iyi donanımlı.', 'VISIT_NOTES', TRUE),
    (2, 2, 'Ücret Bilgileri', 'Yıllık ücret 50.000 TL, kayıt ücreti 5.000 TL', 'FEES', FALSE);

-- Sample list notes
INSERT INTO parent_list_notes (parent_school_list_id, note_title, note_content, is_important)
VALUES
    (1, 'Genel Not', 'Bu listedeki okulları öncelik sırasına göre ziyaret etmeyi planlıyorum.', FALSE),
    (2, 'Araştırma Planı', 'Her okul için en az 2 saat araştırma yapacağım.', TRUE);