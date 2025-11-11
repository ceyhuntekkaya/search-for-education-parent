package com.genixo.education.search.dto.appointment;

import com.genixo.education.search.enumaration.AppointmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentWaitlistCreateDto {
    private Long schoolId;
    private Long parentUserId;
    private String parentName;
    private String parentEmail;
    private String parentPhone;
    private String studentName;
    private Integer studentAge;
    private String gradeInterested;
    private List<AppointmentType> preferredAppointmentTypes;
    private List<DayOfWeek> preferredDays;
    private LocalTime preferredStartTime;
    private LocalTime preferredEndTime;
    private LocalDate earliestDate;
    private LocalDate latestDate;
    private Boolean isOnlinePreferred;
    private String specialRequests;
    private Boolean autoAcceptWhenAvailable;
}