package com.genixo.education.search.enumaration;

public enum QuestionType {
    SINGLE_CHOICE,      // Tek seçim (radio button)
    MULTIPLE_CHOICE,    // Çoklu seçim (checkbox)
    DROPDOWN,           // Açılır liste
    TEXT_SHORT,         // Kısa metin
    TEXT_LONG,          // Uzun metin (textarea)
    EMAIL,              // E-posta
    PHONE,              // Telefon
    NUMBER,             // Sayı
    DATE,               // Tarih
    TIME,               // Saat
    RATING_STAR,        // Yıldız değerlendirme
    RATING_SCALE,       // Ölçek değerlendirme (1-10)
    YES_NO,             // Evet/Hayır
    LIKERT_SCALE,       // Likert ölçeği
    MATRIX,             // Matris sorusu
    FILE_UPLOAD,        // Dosya yükleme
    SIGNATURE,          // İmza
    SECTION_HEADER      // Bölüm başlığı
}