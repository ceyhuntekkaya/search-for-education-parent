package com.genixo.education.search.dto.appointment;

import com.genixo.education.search.enumaration.AppointmentOutcome;
import com.genixo.education.search.enumaration.AppointmentStatus;
import com.genixo.education.search.enumaration.AppointmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentSearchDto {
    private String searchTerm;
    private List<Long> schoolIds;
    private List<Long> staffUserIds;
    private List<AppointmentStatus> statuses;
    private List<AppointmentType> appointmentTypes;
    private LocalDate appointmentDateFrom;
    private LocalDate appointmentDateTo;
    private LocalDateTime createdFrom;
    private LocalDateTime createdTo;
    private String parentEmail;
    private String parentPhone;
    private String studentName;
    private String gradeInterested;
    private List<AppointmentOutcome> outcomes;
    private Boolean followUpRequired;
    private Boolean isOnline;
    private Boolean hasNotes;
    private Integer rescheduleCountMin;
    private Integer rescheduleCountMax;

    // Sorting
    private String sortBy; // APPOINTMENT_DATE, CREATED_DATE, STATUS, OUTCOME
    private String sortDirection;

    // Pagination
    private Integer page;
    private Integer size;
}
