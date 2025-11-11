package com.genixo.education.search.enumaration;

public enum PaymentStatus {
    PENDING,        // Beklemede
    PROCESSING,     // İşleniyor
    COMPLETED,      // Tamamlandı
    FAILED,         // Başarısız
    CANCELED,       // İptal edildi
    REFUNDED,       // İade edildi
    PARTIALLY_REFUNDED, // Kısmi iade
    DISPUTED,       // Anlaşmazlık
    EXPIRED         // Süresi doldu
}