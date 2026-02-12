package com.genixo.education.search.dto.webinar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventSummaryDto {
    private Long id;
    private String title;
    private String eventType;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String status;
    private EventOrganizerSummaryDto organizer;
}
