package com.genixo.education.search.dto.appointment;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParentSchoolListItemResponse {

    private Long id;
    private Long schoolId;
    private String schoolName;
    private String schoolSlug;
    private String schoolLogoUrl;
    private String schoolPhone;
    private String schoolEmail;
    private Double schoolRatingAverage;
    private Long schoolRatingCount;

    // List item specific fields
    private Integer starRating;
    private Boolean isFavorite;
    private Boolean isBlocked;
    private Integer priorityOrder;
    private String personalNotes;
    private String pros;
    private String cons;
    private String tags;
    private LocalDateTime visitPlannedDate;
    private LocalDateTime visitCompletedDate;
    private String addedFromSearch;

    // Appointment info
    private Integer appointmentCount;
    private LocalDateTime lastAppointmentDate;
    private LocalDateTime nextAppointmentDate;

    // Note counts
    private Integer noteCount;
    private Integer importantNoteCount;

    // Timestamps
    private LocalDateTime addedAt;
    private LocalDateTime lastUpdatedAt;
}
