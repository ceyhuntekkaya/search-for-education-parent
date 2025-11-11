package com.genixo.education.search.dto.appointment;

import com.genixo.education.search.enumaration.AppointmentOutcome;
import com.genixo.education.search.enumaration.AppointmentStatus;
import com.genixo.education.search.enumaration.AppointmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentUpdateDto {
    private Long appointmentSlotId;
    private Long schoolId;
    private Long parentUserId;

    private AppointmentType appointmentType;
    private Boolean isOnline;

    private String studentName;
    private Integer studentAge;
    private LocalDate studentBirthDate;
    private String studentGender;
    private String currentSchool;
    private String gradeInterested;

    private String specialRequests;
    private String notes;
}