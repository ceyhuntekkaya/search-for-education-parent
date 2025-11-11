package com.genixo.education.search.dto.appointment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentIntegrationDto {
    private Long appointmentId;
    private String integrationType; // GOOGLE_CALENDAR, OUTLOOK, ZOOM, TEAMS, WHATSAPP
    private String externalId;
    private String externalUrl;
    private String integrationStatus; // SYNCED, PENDING, FAILED, NOT_SYNCED
    private LocalDateTime lastSyncAt;
    private String syncError;
    private Map<String, String> integrationData;
    private Boolean autoSync;
    private List<String> syncedFields;
}

