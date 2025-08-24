package com.genixo.education.search.entity.appointment;

import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.enumaration.AppointmentOutcome;
import com.genixo.education.search.enumaration.AppointmentStatus;
import com.genixo.education.search.enumaration.AppointmentType;
import com.genixo.education.search.enumaration.CancelledByType;
import com.genixo.education.search.entity.institution.School;
import com.genixo.education.search.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "appointments")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Appointment extends BaseEntity {

    @Column(name = "appointment_number", unique = true, nullable = false)
    private String appointmentNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_slot_id", nullable = false)
    private AppointmentSlot appointmentSlot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_user_id", nullable = false)
    private User parentUser; // Randevuyu alan veli

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_user_id")
    private User staffUser; // Görüşmeyi yapacak personel

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "actual_start_time")
    private LocalDateTime actualStartTime;

    @Column(name = "actual_end_time")
    private LocalDateTime actualEndTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AppointmentStatus status = AppointmentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "appointment_type", nullable = false)
    private AppointmentType appointmentType;

    @Column(name = "title")
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "location")
    private String location;

    @Column(name = "is_online")
    private Boolean isOnline = false;

    @Column(name = "meeting_url")
    private String meetingUrl;

    @Column(name = "meeting_id")
    private String meetingId;

    @Column(name = "meeting_password")
    private String meetingPassword;

    // Parent information
    @Column(name = "parent_name", nullable = false)
    private String parentName;

    @Column(name = "parent_email", nullable = false)
    private String parentEmail;

    @Column(name = "parent_phone", nullable = false)
    private String parentPhone;

    // Student information
    @Column(name = "student_name")
    private String studentName;

    @Column(name = "student_age")
    private Integer studentAge;

    @Column(name = "student_birth_date")
    private LocalDate studentBirthDate;

    @Column(name = "student_gender")
    private String studentGender;

    @Column(name = "current_school")
    private String currentSchool;

    @Column(name = "grade_interested")
    private String gradeInterested;

    // Appointment details
    @Column(name = "special_requests", columnDefinition = "TEXT")
    private String specialRequests;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "internal_notes", columnDefinition = "TEXT")
    private String internalNotes;

    // Confirmation and reminders
    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "confirmed_by")
    private Long confirmedBy;

    @Column(name = "reminder_sent_at")
    private LocalDateTime reminderSentAt;

    @Column(name = "follow_up_required")
    private Boolean followUpRequired = false;

    @Column(name = "follow_up_date")
    private LocalDate followUpDate;

    // Cancellation
    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @Column(name = "canceled_by")
    private Long canceledBy;

    @Column(name = "cancellation_reason")
    private String cancellationReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "canceled_by_type")
    private CancelledByType canceledByType;

    // Rescheduling
    @Column(name = "rescheduled_from_id")
    private Long rescheduledFromId;

    @Column(name = "rescheduled_to_id")
    private Long rescheduledToId;

    @Column(name = "reschedule_count")
    private Integer rescheduleCount = 0;

    // Outcome and results
    @Enumerated(EnumType.STRING)
    @Column(name = "outcome")
    private AppointmentOutcome outcome;

    @Column(name = "outcome_notes", columnDefinition = "TEXT")
    private String outcomeNotes;

    @Column(name = "enrollment_likelihood")
    private Integer enrollmentLikelihood; // 1-10 scale

    @Column(name = "next_steps", columnDefinition = "TEXT")
    private String nextSteps;

    // Survey
    @Column(name = "survey_sent_at")
    private LocalDateTime surveySentAt;

    @Column(name = "survey_completed_at")
    private LocalDateTime surveyCompletedAt;

    // Relationships
    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<AppointmentParticipant> participants = new HashSet<>();

    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<AppointmentNote> appointmentNotes = new HashSet<>();
}
