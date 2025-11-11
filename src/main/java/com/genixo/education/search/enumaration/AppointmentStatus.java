package com.genixo.education.search.enumaration;

public enum AppointmentStatus {
    PENDING,        // Beklemede
    CONFIRMED,      // Onaylandı
    APPROVED,       // Onaylandı (manuel onay gerektiren durumlarda)
    REJECTED,       // Reddedildi
    CANCELLED,      // İptal edildi
    COMPLETED,      // Tamamlandı
    NO_SHOW,        // Gelmedi
    RESCHEDULED,    // Ertelendi
    IN_PROGRESS     // Devam ediyor
}