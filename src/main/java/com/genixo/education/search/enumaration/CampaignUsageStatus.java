package com.genixo.education.search.enumaration;

public enum CampaignUsageStatus {
    PENDING,            // Beklemede
    VALIDATED,          // Doğrulandı
    APPROVED,           // Onaylandı
    PROCESSED,          // İşlendi
    COMPLETED,          // Tamamlandı
    REJECTED,           // Reddedildi
    EXPIRED,            // Süresi doldu
    CANCELLED,          // İptal edildi
    DUPLICATE,          // Tekrar
    FRAUD_SUSPECTED     // Dolandırıcılık şüphesi
}