package com.genixo.education.search.enumaration;

public enum SurveyTriggerEvent {
    APPOINTMENT_COMPLETED,  // Randevu tamamlandıktan sonra
    ENROLLMENT_COMPLETED,   // Kayıt tamamlandıktan sonra
    MANUAL_SEND,           // Manuel gönderim
    PERIODIC,              // Periyodik gönderim
    EVENT_BASED            // Olay bazlı
}