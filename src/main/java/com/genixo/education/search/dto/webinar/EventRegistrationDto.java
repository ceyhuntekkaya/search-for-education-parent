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
public class EventRegistrationDto {
    private Long id;
    private Long eventId;
    private EventSummaryDto event;
    private Long teacherId;
    private String teacherEmail;
    private String teacherName;
    private String registrationNote;
    private String status;
    private Boolean attended;
    private LocalDateTime attendanceMarkedAt;
    private Long attendanceMarkedByUserId;
    private String certificateUrl;
    private LocalDateTime certificateGeneratedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
