package com.genixo.education.search.dto.appointment;

import com.genixo.education.search.enumaration.AppointmentOutcome;
import com.genixo.education.search.enumaration.AppointmentStatus;
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
    private AppointmentStatus status;
    private String title;
    private String description;
    private String location;
    private Boolean isOnline;
    private String meetingUrl;
    private String meetingId;
    private String meetingPassword;
    private String parentName;
    private String parentEmail;
    private String parentPhone;
    private String studentName;
    private Integer studentAge;
    private String studentGender;
    private String currentSchool;
    private String gradeInterested;
    private String specialRequests;
    private String notes;
    private String internalNotes;
    private AppointmentOutcome outcome;
    private String outcomeNotes;
    private Integer enrollmentLikelihood;
    private String nextSteps;
    private Boolean followUpRequired;
    private LocalDate followUpDate;
    private Long staffUserId;
}