package com.genixo.education.search.entity.appointment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.enumaration.AppointmentType;
import com.genixo.education.search.entity.institution.School;
import com.genixo.education.search.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

// ========================= APPOINTMENT SLOT =========================
@Entity
@Table(name = "appointment_slots")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentSlot extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_user_id")
    private User staffUser; // Hangi personel ile randevu

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes = 30;

    @Column(name = "capacity")
    private Integer capacity = 1; // Aynı saatte kaç randevu alınabilir

    @Enumerated(EnumType.STRING)
    @Column(name = "appointment_type")
    private AppointmentType appointmentType;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "location")
    private String location;

    @Column(name = "online_meeting_available")
    private Boolean onlineMeetingAvailable = false;

    @Column(name = "preparation_required")
    private Boolean preparationRequired = false;

    @Column(name = "preparation_notes", columnDefinition = "TEXT")
    private String preparationNotes;

    // Slot availability
    @Column(name = "is_recurring")
    private Boolean isRecurring = true;

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_until")
    private LocalDate validUntil;

    // Excluded dates (JSON array of dates when this slot is not available)
    @Column(name = "excluded_dates", columnDefinition = "JSON")
    private String excludedDates;

    // Booking rules
    @Column(name = "advance_booking_hours")
    private Integer advanceBookingHours = 24; // Kaç saat önceden randevu alınabilir

    @Column(name = "max_advance_booking_days")
    private Integer maxAdvanceBookingDays = 30; // En fazla kaç gün önceden

    @Column(name = "cancellation_hours")
    private Integer cancellationHours = 4; // Kaç saat öncesine kadar iptal edilebilir

    @Column(name = "requires_approval")
    private Boolean requiresApproval = false;

    // Relationships
    @OneToMany(mappedBy = "appointmentSlot", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<Appointment> appointments = new HashSet<>();
}