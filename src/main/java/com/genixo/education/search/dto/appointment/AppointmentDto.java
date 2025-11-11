package com.genixo.education.search.dto.appointment;

import com.genixo.education.search.enumaration.AppointmentOutcome;
import com.genixo.education.search.enumaration.AppointmentStatus;
import com.genixo.education.search.enumaration.AppointmentType;
import com.genixo.education.search.enumaration.CancelledByType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentDto {
    private Long id;
    private String appointmentNumber;
    private Long appointmentSlotId;
    private Long schoolId;
    private String schoolName;
    private String campusName;
    private Long parentUserId;
    private String parentUserName;
    private Long staffUserId;
    private String staffUserName;

    // Appointment timing
    private LocalDate appointmentDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDateTime actualStartTime;
    private LocalDateTime actualEndTime;
    private Integer durationMinutes;

    // Status and type
    private AppointmentStatus status;
    private AppointmentType appointmentType;
    private String title;
    private String description;
    private String location;
    private Boolean isOnline;
    private String meetingUrl;
    private String meetingId;
    private String meetingPassword;

    // Parent information
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

    // Appointment details
    private String specialRequests;
    private String notes;
    private String internalNotes;

    // Confirmation and reminders
    private LocalDateTime confirmedAt;
    private String confirmedByUserName;
    private LocalDateTime reminderSentAt;
    private Boolean followUpRequired;
    private LocalDate followUpDate;

    // Cancellation
    private LocalDateTime canceledAt;
    private String canceledByUserName;
    private String cancellationReason;
    private CancelledByType canceledByType;

    // Rescheduling
    private Long rescheduledFromId;
    private Long rescheduledToId;
    private Integer rescheduleCount;

    // Outcome and results
    private AppointmentOutcome outcome;
    private String outcomeNotes;
    private Integer enrollmentLikelihood;
    private String nextSteps;

    // Survey
    private LocalDateTime surveySentAt;
    private LocalDateTime surveyCompletedAt;

    // Calculated fields
    private String formattedDate;
    private String formattedTime;
    private String statusDisplayName;
    private String outcomeDisplayName;
    private Boolean canCancel;
    private Boolean canReschedule;
    private Boolean canComplete;
    private Integer hoursUntilAppointment;
    private String appointmentSummary;

    // Relationships
    private List<AppointmentParticipantDto> participants;
    private List<AppointmentNoteDto> appointmentNotes;

    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}