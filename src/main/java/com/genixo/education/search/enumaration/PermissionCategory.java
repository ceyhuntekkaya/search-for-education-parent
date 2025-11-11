package com.genixo.education.search.enumaration;

public enum PermissionCategory {
    USER_MANAGEMENT,        // kullanici_olustur, kullanici_duzenle vs
    INSTITUTION_MANAGEMENT, // kampus_duzenle, okul_olustur vs
    APPOINTMENT_MANAGEMENT, // randevu_olustur, randevu_iptal_et vs
    CONTENT_MANAGEMENT,     // galeri_duzenle, post_olustur vs
    SURVEY_MANAGEMENT,      // anket_goruntule, anket_duzenle vs
    SUBSCRIPTION_MANAGEMENT,// abonelik_goruntule, odeme_yap vs
    ANALYTICS              // istatistik_goruntule, rapor_olustur vs
}
