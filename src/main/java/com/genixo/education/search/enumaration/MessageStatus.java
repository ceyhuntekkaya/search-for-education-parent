package com.genixo.education.search.enumaration;

public enum MessageStatus {
    NEW,                // Yeni
    READ,               // Okundu
    IN_PROGRESS,        // İşlemde
    WAITING_RESPONSE,   // Yanıt bekleniyor
    RESPONDED,          // Yanıtlandı
    RESOLVED,           // Çözüldü
    CLOSED,             // Kapatıldı
    ESCALATED,          // Üst makama iletildi
    SPAM,               // Spam
    ARCHIVED            // Arşivlendi
}