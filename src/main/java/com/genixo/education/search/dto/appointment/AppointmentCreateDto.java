package com.genixo.education.search.dto.appointment;

import com.genixo.education.search.enumaration.AppointmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentCreateDto {
    private Long appointmentSlotId;
    private Long schoolId;
    private Long parentUserId;
    private LocalDate appointmentDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private AppointmentType appointmentType;
    private String title;
    private String description;
    private String location;
    private Boolean isOnline;

    // Parent information (for non-registered users)
    private String parentName;
    private String parentEmail;
    private String parentPhone;

    // Student information
    private String studentName;
    private Integer studentAge;
    private LocalDate studentBirthDate;
    private String studentGender;
    private String currentSchool;
    private String gradeInterested;

    // Additional details
    private String specialRequests;
    private String notes;

    // Participants
    private List<AppointmentParticipantCreateDto> participants;
}