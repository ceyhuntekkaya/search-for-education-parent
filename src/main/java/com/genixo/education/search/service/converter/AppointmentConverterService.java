package com.genixo.education.search.service.converter;


import com.genixo.education.search.dto.appointment.*;
import com.genixo.education.search.entity.appointment.*;
import com.genixo.education.search.util.ConversionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AppointmentConverterService {

    private final UserConverterService userConverterService;

    private final InstitutionConverterService institutionConverterService;


    public AppointmentSlotDto mapToDto(AppointmentSlot entity) {
        if (entity == null) {
            return null;
        }
        AppointmentDto appointmentDto = null;
        if(entity.getAppointments() != null) {
            if(!entity.getAppointments().isEmpty()) {
                Appointment appointment = entity.getAppointments().stream().findFirst().orElse(null);
                if(appointment != null) {
                    appointmentDto = mapToDto(appointment);
                }
            }
        }


        return AppointmentSlotDto.builder()
                .id(entity.getId())
                .schoolId(entity.getSchool() != null ? entity.getSchool().getId() : null)
                .schoolName(entity.getSchool() != null ? entity.getSchool().getName() : null)
                .staffUserId(entity.getStaffUser() != null ? entity.getStaffUser().getId() : null)
                .appointment(appointmentDto)
                .slotDate(entity.getSlotDate())
                .staffUserName(entity.getStaffUser() != null ?
                        (entity.getStaffUser().getFirstName() + " " + entity.getStaffUser().getLastName()).trim() : null)
                .durationMinutes(entity.getDurationMinutes())
                .appointmentType(entity.getAppointmentType())
                .onlineMeetingAvailable(ConversionUtils.defaultIfNull(entity.getOnlineMeetingAvailable(), false))
                .advanceBookingHours(ConversionUtils.defaultIfNull(entity.getAdvanceBookingHours(), 24))
                .maxAdvanceBookingDays(ConversionUtils.defaultIfNull(entity.getMaxAdvanceBookingDays(), 30))
                .cancellationHours(ConversionUtils.defaultIfNull(entity.getCancellationHours(), 4))
                .requiresApproval(ConversionUtils.defaultIfNull(entity.getRequiresApproval(), false))
                // Calculated fields
                .dayOfWeekName(entity.getDayOfWeek() != null ?
                        ConversionUtils.getDisplayName(entity.getDayOfWeek()) : null)
                .isAvailable(calculateSlotAvailability(entity))
                .isActive(ConversionUtils.defaultIfNull(entity.getIsActive(), true))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();



        /*
        return AppointmentSlotDto.builder()
                .id(entity.getId())
                .schoolId(entity.getSchool() != null ? entity.getSchool().getId() : null)
                .schoolName(entity.getSchool() != null ? entity.getSchool().getName() : null)
                .staffUserId(entity.getStaffUser() != null ? entity.getStaffUser().getId() : null)
                .slotDate(entity.getSlotDate())
                .staffUserName(entity.getStaffUser() != null ?
                        (entity.getStaffUser().getFirstName() + " " + entity.getStaffUser().getLastName()).trim() : null)
                .dayOfWeek(entity.getDayOfWeek())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .durationMinutes(entity.getDurationMinutes())
                .capacity(entity.getCapacity())
                .appointmentType(entity.getAppointmentType())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .location(entity.getLocation())
                .onlineMeetingAvailable(ConversionUtils.defaultIfNull(entity.getOnlineMeetingAvailable(), false))
                .preparationRequired(ConversionUtils.defaultIfNull(entity.getPreparationRequired(), false))
                .preparationNotes(entity.getPreparationNotes())
                .isRecurring(ConversionUtils.defaultIfNull(entity.getIsRecurring(), true))
                .validFrom(entity.getValidFrom())
                .validUntil(entity.getValidUntil())
                .excludedDates(entity.getExcludedDates())
                .advanceBookingHours(ConversionUtils.defaultIfNull(entity.getAdvanceBookingHours(), 24))
                .maxAdvanceBookingDays(ConversionUtils.defaultIfNull(entity.getMaxAdvanceBookingDays(), 30))
                .cancellationHours(ConversionUtils.defaultIfNull(entity.getCancellationHours(), 4))
                .requiresApproval(ConversionUtils.defaultIfNull(entity.getRequiresApproval(), false))
                // Calculated fields
                .timeRange(ConversionUtils.formatTimeRange(entity.getStartTime(), entity.getEndTime()))
                .dayOfWeekName(entity.getDayOfWeek() != null ?
                        ConversionUtils.getDisplayName(entity.getDayOfWeek()) : null)
                .isAvailable(calculateSlotAvailability(entity))
                .availableCapacity(calculateAvailableCapacity(entity))
                .bookedCount(calculateBookedCount(entity))
                .nextAvailableDates(calculateNextAvailableDates(entity, 7)) // Next 7 dates
                .isActive(ConversionUtils.defaultIfNull(entity.getIsActive(), true))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
         */
    }



    public List<AppointmentSlotDto> mapSlotToDto(List<AppointmentSlot> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return new ArrayList<>();
        }
        return entities.stream()
                .map(this::mapToDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // ================== APPOINTMENT CONVERSIONS ==================

    public AppointmentDto mapToDto(Appointment entity) {
        if (entity == null) {
            return null;
        }

        return AppointmentDto.builder()
                .id(entity.getId())
                .appointmentNumber(entity.getAppointmentNumber())
                .appointmentSlotId(entity.getAppointmentSlot() != null ? entity.getAppointmentSlot().getId() : null)
                .schoolId(entity.getSchool() != null ? entity.getSchool().getId() : null)
                .schoolName(entity.getSchool() != null ? entity.getSchool().getName() : null)
                .campusName(entity.getSchool() != null && entity.getSchool().getCampus() != null ?
                        entity.getSchool().getCampus().getName() : null)
                .parentUserId(entity.getParentUser() != null ? entity.getParentUser().getId() : null)
                .parentUserName(entity.getParentUser() != null ?
                        (entity.getParentUser().getFirstName() + " " + entity.getParentUser().getLastName()).trim() : null)
                .staffUserId(entity.getStaffUser() != null ? entity.getStaffUser().getId() : null)
                .staffUserName(entity.getStaffUser() != null ?
                        (entity.getStaffUser().getFirstName() + " " + entity.getStaffUser().getLastName()).trim() : null)

                // Timing
                .appointmentDate(entity.getAppointmentDate())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .actualStartTime(entity.getActualStartTime())
                .actualEndTime(entity.getActualEndTime())
                .durationMinutes(calculateDuration(entity.getStartTime(), entity.getEndTime()))

                // Status and type
                .status(entity.getStatus())
                .appointmentType(entity.getAppointmentType())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .location(entity.getLocation())
                .isOnline(ConversionUtils.defaultIfNull(entity.getIsOnline(), false))
                .meetingUrl(entity.getMeetingUrl())
                .meetingId(entity.getMeetingId())
                .meetingPassword(entity.getMeetingPassword())

                // Parent and student info
                .parentName(entity.getParentName())
                .parentEmail(entity.getParentEmail())
                .parentPhone(entity.getParentPhone())
                .studentName(entity.getStudentName())
                .studentAge(entity.getStudentAge())
                .studentBirthDate(entity.getStudentBirthDate())
                .studentGender(entity.getStudentGender())
                .currentSchool(entity.getCurrentSchool())
                .gradeInterested(entity.getGradeInterested())

                // Notes and requests
                .specialRequests(entity.getSpecialRequests())
                .notes(entity.getNotes())
                .internalNotes(entity.getInternalNotes())

                // Confirmation and reminders
                .confirmedAt(entity.getConfirmedAt())
                .confirmedByUserName(getUserNameById(entity.getConfirmedBy()))
                .reminderSentAt(entity.getReminderSentAt())
                .followUpRequired(ConversionUtils.defaultIfNull(entity.getFollowUpRequired(), false))
                .followUpDate(entity.getFollowUpDate())

                // Cancellation
                .canceledAt(entity.getCanceledAt())
                .canceledByUserName(getUserNameById(entity.getCanceledBy()))
                .cancellationReason(entity.getCancellationReason())
                .canceledByType(entity.getCanceledByType())

                // Rescheduling
                .rescheduledFromId(entity.getRescheduledFromId())
                .rescheduledToId(entity.getRescheduledToId())
                .rescheduleCount(ConversionUtils.defaultIfNull(entity.getRescheduleCount(), 0))

                // Outcome
                .outcome(entity.getOutcome())
                .outcomeNotes(entity.getOutcomeNotes())
                .enrollmentLikelihood(entity.getEnrollmentLikelihood())
                .nextSteps(entity.getNextSteps())

                // Survey
                .surveySentAt(entity.getSurveySentAt())
                .surveyCompletedAt(entity.getSurveyCompletedAt())

                // Calculated fields
                .formattedDate(ConversionUtils.formatDate(entity.getAppointmentDate()))
                .formattedTime(ConversionUtils.formatTimeRange(entity.getStartTime(), entity.getEndTime()))
                .statusDisplayName(ConversionUtils.getDisplayName(entity.getStatus()))
                .outcomeDisplayName(entity.getOutcome() != null ? ConversionUtils.getDisplayName(entity.getOutcome()) : null)
                .canCancel(canCancelAppointment(entity))
                .canReschedule(canRescheduleAppointment(entity))
                .canComplete(canCompleteAppointment(entity))
                .hoursUntilAppointment(calculateHoursUntilAppointment(entity))
                .appointmentSummary(generateAppointmentSummary(entity))

                // Relationships
                .participants(mapParticipantsToDto(entity.getParticipants()))
                .appointmentNotes(mapNotesToDto(entity.getAppointmentNotes()))

                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public AppointmentSummaryDto mapToSummaryDto(Appointment entity) {
        if (entity == null) {
            return null;
        }

        return AppointmentSummaryDto.builder()
                .id(entity.getId())
                .appointmentNumber(entity.getAppointmentNumber())
                .schoolName(entity.getSchool() != null ? entity.getSchool().getName() : null)
                .parentName(entity.getParentName())
                .studentName(entity.getStudentName())
                .appointmentDate(entity.getAppointmentDate())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .status(entity.getStatus())
                .appointmentType(entity.getAppointmentType())
                .location(entity.getLocation())
                .isOnline(ConversionUtils.defaultIfNull(entity.getIsOnline(), false))
                .staffUserName(entity.getStaffUser() != null ?
                        (entity.getStaffUser().getFirstName() + " " + entity.getStaffUser().getLastName()).trim() : null)
                .outcome(entity.getOutcome())
                .followUpRequired(ConversionUtils.defaultIfNull(entity.getFollowUpRequired(), false))
                .statusDisplayName(ConversionUtils.getDisplayName(entity.getStatus()))
                .formattedDateTime(ConversionUtils.formatDateTime(
                        LocalDateTime.of(entity.getAppointmentDate(), entity.getStartTime())))
                .timeUntilAppointment(calculateTimeUntilAppointment(entity))
                .build();
    }


/*
    public void updateEntity(AppointmentUpdateDto dto, Appointment entity) {
        if (dto == null || entity == null) {
            return;
        }

        // Status and basic info
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
        if (StringUtils.hasText(dto.getTitle())) {
            entity.setTitle(dto.getTitle());
        }
        if (StringUtils.hasText(dto.getDescription())) {
            entity.setDescription(dto.getDescription());
        }
        if (StringUtils.hasText(dto.getLocation())) {
            entity.setLocation(dto.getLocation());
        }
        if (dto.getIsOnline() != null) {
            entity.setIsOnline(dto.getIsOnline());
        }
        if (StringUtils.hasText(dto.getMeetingUrl())) {
            entity.setMeetingUrl(dto.getMeetingUrl());
        }
        if (StringUtils.hasText(dto.getMeetingId())) {
            entity.setMeetingId(dto.getMeetingId());
        }
        if (StringUtils.hasText(dto.getMeetingPassword())) {
            entity.setMeetingPassword(dto.getMeetingPassword());
        }

        // Parent and student info
        if (StringUtils.hasText(dto.getParentName())) {
            entity.setParentName(dto.getParentName());
        }
        if (StringUtils.hasText(dto.getParentEmail())) {
            entity.setParentEmail(dto.getParentEmail());
        }
        if (StringUtils.hasText(dto.getParentPhone())) {
            entity.setParentPhone(dto.getParentPhone());
        }
        if (StringUtils.hasText(dto.getStudentName())) {
            entity.setStudentName(dto.getStudentName());
        }
        if (dto.getStudentAge() != null) {
            entity.setStudentAge(dto.getStudentAge());
        }
        if (StringUtils.hasText(dto.getStudentGender())) {
            entity.setStudentGender(dto.getStudentGender());
        }
        if (StringUtils.hasText(dto.getCurrentSchool())) {
            entity.setCurrentSchool(dto.getCurrentSchool());
        }
        if (StringUtils.hasText(dto.getGradeInterested())) {
            entity.setGradeInterested(dto.getGradeInterested());
        }

        // Notes and requests
        if (dto.getSpecialRequests() != null) {
            entity.setSpecialRequests(dto.getSpecialRequests());
        }
        if (dto.getNotes() != null) {
            entity.setNotes(dto.getNotes());
        }
        if (dto.getInternalNotes() != null) {
            entity.setInternalNotes(dto.getInternalNotes());
        }

        // Outcome
        if (dto.getOutcome() != null) {
            entity.setOutcome(dto.getOutcome());
        }
        if (dto.getOutcomeNotes() != null) {
            entity.setOutcomeNotes(dto.getOutcomeNotes());
        }
        if (dto.getEnrollmentLikelihood() != null) {
            entity.setEnrollmentLikelihood(dto.getEnrollmentLikelihood());
        }
        if (dto.getNextSteps() != null) {
            entity.setNextSteps(dto.getNextSteps());
        }

        // Follow-up
        if (dto.getFollowUpRequired() != null) {
            entity.setFollowUpRequired(dto.getFollowUpRequired());
        }
        if (dto.getFollowUpDate() != null) {
            entity.setFollowUpDate(dto.getFollowUpDate());
        }
    }

 */

    public List<AppointmentDto> mapToDto(List<Appointment> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return new ArrayList<>();
        }
        return entities.stream()
                .map(this::mapToDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<AppointmentSummaryDto> mapToSummaryDto(List<Appointment> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return new ArrayList<>();
        }
        return entities.stream()
                .map(this::mapToSummaryDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // ================== APPOINTMENT PARTICIPANT CONVERSIONS ==================

    public AppointmentParticipantDto mapToDto(AppointmentParticipant entity) {
        if (entity == null) {
            return null;
        }

        return AppointmentParticipantDto.builder()
                .id(entity.getId())
                .appointmentId(entity.getAppointment() != null ? entity.getAppointment().getId() : null)
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .name(entity.getName())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .participantType(entity.getParticipantType())
                .attendanceStatus(entity.getAttendanceStatus())
                .notes(entity.getNotes())
                .arrivalTime(entity.getArrivalTime())
                .departureTime(entity.getDepartureTime())
                .participantTypeDisplayName(ConversionUtils.getDisplayName(entity.getParticipantType()))
                .attendanceStatusDisplayName(ConversionUtils.getDisplayName(entity.getAttendanceStatus()))
                .attendanceDurationMinutes(calculateAttendanceDuration(entity.getArrivalTime(), entity.getDepartureTime()))
                .build();
    }

    public AppointmentParticipant mapToEntity(AppointmentParticipantCreateDto dto) {
        if (dto == null) {
            return null;
        }

        AppointmentParticipant entity = new AppointmentParticipant();
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setParticipantType(dto.getParticipantType());
        entity.setNotes(dto.getNotes());

        return entity;
    }

    private List<AppointmentParticipantDto> mapParticipantsToDto(Set<AppointmentParticipant> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return new ArrayList<>();
        }
        return entities.stream()
                .map(this::mapToDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // ================== APPOINTMENT NOTE CONVERSIONS ==================

    public AppointmentNoteDto mapToDto(AppointmentNote entity) {
        if (entity == null) {
            return null;
        }

        return AppointmentNoteDto.builder()
                .id(entity.getId())
                .appointmentId(entity.getAppointment() != null ? entity.getAppointment().getId() : null)
                .authorUserId(entity.getAuthorUser() != null ? entity.getAuthorUser().getId() : null)
                .authorUserName(entity.getAuthorUser() != null ?
                        (entity.getAuthorUser().getFirstName() + " " + entity.getAuthorUser().getLastName()).trim() : null)
                .note(entity.getNote())
                .noteType(entity.getNoteType())
                .isPrivate(ConversionUtils.defaultIfNull(entity.getIsPrivate(), false))
                .isImportant(ConversionUtils.defaultIfNull(entity.getIsImportant(), false))
                .noteDate(entity.getNoteDate())
                .attachmentUrl(entity.getAttachmentUrl())
                .attachmentName(entity.getAttachmentName())
                .attachmentSize(entity.getAttachmentSize())
                .attachmentType(entity.getAttachmentType())
                .noteTypeDisplayName(ConversionUtils.getDisplayName(entity.getNoteType()))
                .formattedNoteDate(ConversionUtils.formatDateTime(entity.getNoteDate()))
                .canEdit(canEditNote(entity))
                .canDelete(canDeleteNote(entity))
                .build();
    }

    public AppointmentNote mapToEntity(AppointmentNoteCreateDto dto) {
        if (dto == null) {
            return null;
        }

        AppointmentNote entity = new AppointmentNote();
        entity.setNote(dto.getNote());
        entity.setNoteType(ConversionUtils.defaultIfNull(dto.getNoteType(),
                com.genixo.education.search.enumaration.NoteType.GENERAL));
        entity.setIsPrivate(ConversionUtils.defaultIfNull(dto.getIsPrivate(), false));
        entity.setIsImportant(ConversionUtils.defaultIfNull(dto.getIsImportant(), false));
        entity.setNoteDate(LocalDateTime.now());
        entity.setAttachmentUrl(dto.getAttachmentUrl());
        entity.setAttachmentName(dto.getAttachmentName());
        entity.setAttachmentSize(dto.getAttachmentSize());
        entity.setAttachmentType(dto.getAttachmentType());

        return entity;
    }

    private List<AppointmentNoteDto> mapNotesToDto(Set<AppointmentNote> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return new ArrayList<>();
        }
        return entities.stream()
                .map(this::mapToDto)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(AppointmentNoteDto::getNoteDate).reversed())
                .collect(Collectors.toList());
    }

    // ================== STATISTICS CONVERSIONS ==================

    public AppointmentStatisticsDto mapToStatisticsDto(Long schoolId, String schoolName,
                                                       LocalDate periodStart, LocalDate periodEnd,
                                                       Map<String, Object> statisticsData) {
        if (statisticsData == null) {
            return null;
        }

        return AppointmentStatisticsDto.builder()
                .schoolId(schoolId)
                .schoolName(schoolName)
                .periodStart(periodStart)
                .periodEnd(periodEnd)

                // Overall metrics
                .totalAppointments(getLongValue(statisticsData, "totalAppointments"))
                .completedAppointments(getLongValue(statisticsData, "completedAppointments"))
                .canceledAppointments(getLongValue(statisticsData, "canceledAppointments"))
                .noShowAppointments(getLongValue(statisticsData, "noShowAppointments"))
                .rescheduledAppointments(getLongValue(statisticsData, "rescheduledAppointments"))

                // Conversion metrics
                .completionRate(getDoubleValue(statisticsData, "completionRate"))
                .cancellationRate(getDoubleValue(statisticsData, "cancellationRate"))
                .noShowRate(getDoubleValue(statisticsData, "noShowRate"))
                .rescheduleRate(getDoubleValue(statisticsData, "rescheduleRate"))
                .enrollmentConversionRate(getDoubleValue(statisticsData, "enrollmentConversionRate"))

                // Outcome metrics
                .enrolledCount(getLongValue(statisticsData, "enrolledCount"))
                .interestedCount(getLongValue(statisticsData, "interestedCount"))
                .notInterestedCount(getLongValue(statisticsData, "notInterestedCount"))
                .averageEnrollmentLikelihood(getDoubleValue(statisticsData, "averageEnrollmentLikelihood"))

                // Time metrics
                .averageAppointmentDuration(getDoubleValue(statisticsData, "averageAppointmentDuration"))
                .averageRescheduleCount(getIntegerValue(statisticsData, "averageRescheduleCount"))
                .averageResponseTime(getDoubleValue(statisticsData, "averageResponseTime"))

                // Popular choices
                .mostPopularTimeSlot(getStringValue(statisticsData, "mostPopularTimeSlot"))
                .mostPopularDay(getStringValue(statisticsData, "mostPopularDay"))
                .mostPopularType((com.genixo.education.search.enumaration.AppointmentType)
                        statisticsData.get("mostPopularType"))
                .mostSuccessfulStaff(getStringValue(statisticsData, "mostSuccessfulStaff"))

                .build();
    }

    // ================== HELPER METHODS ==================

    private Boolean calculateSlotAvailability(AppointmentSlot slot) {
        if (slot == null) {
            return false;
        }

        // Check if slot is active
        if (!ConversionUtils.defaultIfNull(slot.getIsActive(), true)) {
            return false;
        }

        // Check validity period
        LocalDate now = LocalDate.now();
        if (slot.getValidFrom() != null && now.isBefore(slot.getValidFrom())) {
            return false;
        }
        if (slot.getValidUntil() != null && now.isAfter(slot.getValidUntil())) {
            return false;
        }

        return true;
    }

    private Integer calculateAvailableCapacity(AppointmentSlot slot) {
        if (slot == null || slot.getCapacity() == null) {
            return 0;
        }

        int bookedCount = calculateBookedCount(slot);
        return Math.max(0, slot.getCapacity() - bookedCount);
    }

    private Integer calculateBookedCount(AppointmentSlot slot) {
        if (slot == null || CollectionUtils.isEmpty(slot.getAppointments())) {
            return 0;
        }

        // Count active appointments (not cancelled or completed)
        return (int) slot.getAppointments().stream()
                .filter(appointment ->
                        appointment.getStatus() != com.genixo.education.search.enumaration.AppointmentStatus.CANCELLED)
                .count();
    }

    private List<LocalDate> calculateNextAvailableDates(AppointmentSlot slot, int numberOfDates) {
        if (slot == null || !ConversionUtils.defaultIfNull(slot.getIsRecurring(), true)) {
            return new ArrayList<>();
        }

        List<LocalDate> availableDates = new ArrayList<>();
        LocalDate startDate = LocalDate.now().plusDays(1); // Start from tomorrow
        LocalDate endDate = startDate.plusDays(90); // Look ahead 90 days

        for (LocalDate date = startDate; date.isBefore(endDate) && availableDates.size() < numberOfDates;
             date = date.plusDays(1)) {

            if (date.getDayOfWeek() == slot.getDayOfWeek()) {
                // Check if date is not excluded
                // This would require parsing excludedDates JSON
                availableDates.add(date);
            }
        }

        return availableDates;
    }

    private Integer calculateDuration(java.time.LocalTime startTime, java.time.LocalTime endTime) {
        if (startTime == null || endTime == null) {
            return null;
        }

        return (int) ChronoUnit.MINUTES.between(startTime, endTime);
    }

    private String getUserNameById(Long userId) {
        if (userId == null) {
            return null;
        }
        // This would typically be a service call to get user name
        // For now, return a placeholder
        return "User " + userId;
    }

    private Boolean canCancelAppointment(Appointment appointment) {
        if (appointment == null) {
            return false;
        }

        // Can't cancel if already cancelled or completed
        if (appointment.getStatus() == com.genixo.education.search.enumaration.AppointmentStatus.CANCELLED ||
                appointment.getStatus() == com.genixo.education.search.enumaration.AppointmentStatus.COMPLETED) {
            return false;
        }

        // Check if within cancellation window
        if (appointment.getAppointmentSlot() != null) {
            Integer cancellationHours = appointment.getAppointmentSlot().getCancellationHours();
            if (cancellationHours != null) {
                LocalDateTime appointmentDateTime = LocalDateTime.of(
                        appointment.getAppointmentDate(), appointment.getStartTime());
                LocalDateTime cutoffTime = appointmentDateTime.minusHours(cancellationHours);

                return LocalDateTime.now().isBefore(cutoffTime);
            }
        }

        return true;
    }

    private Boolean canRescheduleAppointment(Appointment appointment) {
        if (appointment == null) {
            return false;
        }

        // Similar logic to cancellation
        return canCancelAppointment(appointment) &&
                (appointment.getRescheduleCount() == null || appointment.getRescheduleCount() < 3);
    }

    private Boolean canCompleteAppointment(Appointment appointment) {
        if (appointment == null) {
            return false;
        }

        return appointment.getStatus() == com.genixo.education.search.enumaration.AppointmentStatus.CONFIRMED &&
                appointment.getAppointmentDate() != null &&
                !appointment.getAppointmentDate().isAfter(LocalDate.now());
    }

    private Integer calculateHoursUntilAppointment(Appointment appointment) {
        if (appointment == null || appointment.getAppointmentDate() == null ||
                appointment.getStartTime() == null) {
            return null;
        }

        LocalDateTime appointmentDateTime = LocalDateTime.of(
                appointment.getAppointmentDate(), appointment.getStartTime());
        LocalDateTime now = LocalDateTime.now();

        if (appointmentDateTime.isBefore(now)) {
            return 0; // Past appointment
        }

        return (int) ChronoUnit.HOURS.between(now, appointmentDateTime);
    }

    private String generateAppointmentSummary(Appointment appointment) {
        if (appointment == null) {
            return null;
        }

        StringBuilder summary = new StringBuilder();

        if (StringUtils.hasText(appointment.getStudentName())) {
            summary.append(appointment.getStudentName()).append(" - ");
        }

        if (StringUtils.hasText(appointment.getGradeInterested())) {
            summary.append(appointment.getGradeInterested()).append(" - ");
        }

        if (appointment.getAppointmentDate() != null) {
            summary.append(ConversionUtils.formatDate(appointment.getAppointmentDate()));
        }

        if (appointment.getStartTime() != null) {
            summary.append(" ").append(ConversionUtils.formatTime(appointment.getStartTime()));
        }

        return summary.toString();
    }

    private String calculateTimeUntilAppointment(Appointment appointment) {
        if (appointment == null || appointment.getAppointmentDate() == null ||
                appointment.getStartTime() == null) {
            return null;
        }

        LocalDateTime appointmentDateTime = LocalDateTime.of(
                appointment.getAppointmentDate(), appointment.getStartTime());
        LocalDateTime now = LocalDateTime.now();

        if (appointmentDateTime.isBefore(now)) {
            return "Geçmiş";
        }

        long hours = ChronoUnit.HOURS.between(now, appointmentDateTime);
        long days = ChronoUnit.DAYS.between(now, appointmentDateTime);

        if (days > 0) {
            return days + " gün";
        } else if (hours > 0) {
            return hours + " saat";
        } else {
            long minutes = ChronoUnit.MINUTES.between(now, appointmentDateTime);
            return minutes + " dakika";
        }
    }

    private Integer calculateAttendanceDuration(LocalDateTime arrivalTime, LocalDateTime departureTime) {
        if (arrivalTime == null || departureTime == null) {
            return null;
        }

        return (int) ChronoUnit.MINUTES.between(arrivalTime, departureTime);
    }

    private Boolean canEditNote(AppointmentNote note) {
        if (note == null) {
            return false;
        }

        // Can edit within 24 hours of creation
        if (note.getCreatedAt() != null) {
            LocalDateTime cutoff = note.getCreatedAt().plusHours(24);
            return LocalDateTime.now().isBefore(cutoff);
        }

        return true;
    }

    private Boolean canDeleteNote(AppointmentNote note) {
        if (note == null) {
            return false;
        }

        // Can delete within 1 hour of creation, unless it's important
        if (ConversionUtils.defaultIfNull(note.getIsImportant(), false)) {
            return false;
        }

        if (note.getCreatedAt() != null) {
            LocalDateTime cutoff = note.getCreatedAt().plusHours(1);
            return LocalDateTime.now().isBefore(cutoff);
        }

        return true;
    }

    private String generateAppointmentNumber() {
        // Generate unique appointment number
        // Format: APT-YYYYMMDD-XXXXX
        LocalDate now = LocalDate.now();
        String datePart = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = String.format("%05d", new Random().nextInt(100000));

        return "APT-" + datePart + "-" + randomPart;
    }

    // ================== AVAILABLE SLOT CONVERSIONS ==================

    public AvailableSlotDto mapToAvailableSlotDto(AppointmentSlot slot) {
        if (slot == null) {
            return null;
        }

        return AvailableSlotDto.builder()
                .slotId(slot.getId())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .durationMinutes(slot.getDurationMinutes())
                .appointmentType(slot.getAppointmentType())
                .location(slot.getLocation())
                .isOnline(ConversionUtils.defaultIfNull(slot.getOnlineMeetingAvailable(), false))
                .staffUserName(slot.getStaffUser() != null ?
                        (slot.getStaffUser().getFirstName() + " " + slot.getStaffUser().getLastName()).trim() : null)
                .availableCapacity(calculateAvailableCapacity(slot))
                .requiresApproval(ConversionUtils.defaultIfNull(slot.getRequiresApproval(), false))
                .timeRange(ConversionUtils.formatTimeRange(slot.getStartTime(), slot.getEndTime()))
                .isRecommended(isRecommendedSlot(slot))
                .build();
    }

    public AppointmentAvailabilityDto mapToAvailabilityDto(Long schoolId, String schoolName,
                                                           LocalDate date, List<AppointmentSlot> slots) {
        if (slots == null) {
            slots = new ArrayList<>();
        }

        List<AvailableSlotDto> availableSlots = slots.stream()
                .filter(slot -> calculateSlotAvailability(slot))
                .map(this::mapToAvailableSlotDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        int totalSlots = slots.size();
        int bookedSlots = slots.stream().mapToInt(this::calculateBookedCount).sum();
        int availableCount = totalSlots - bookedSlots;

        String availability;
        if (availableCount == 0) {
            availability = "FULLY_BOOKED";
        } else if (availableCount < totalSlots * 0.2) {
            availability = "LIMITED";
        } else if (availableCount < totalSlots * 0.5) {
            availability = "AVAILABLE";
        } else {
            availability = "ABUNDANT";
        }

        return AppointmentAvailabilityDto.builder()
                .schoolId(schoolId)
                .schoolName(schoolName)
                .date(date)
                .availableSlots(availableSlots)
                .totalSlots(totalSlots)
                .bookedSlots(bookedSlots)
                .availableCount(availableCount)
                .availability(availability)
                .build();
    }

    // ================== CALENDAR CONVERSIONS ==================

    public AppointmentCalendarEventDto mapToCalendarEventDto(Appointment appointment) {
        if (appointment == null) {
            return null;
        }

        String statusColor = getStatusColor(appointment.getStatus());
        String tooltip = generateTooltip(appointment);

        return AppointmentCalendarEventDto.builder()
                .appointmentId(appointment.getId())
                .title(generateCalendarTitle(appointment))
                .startTime(appointment.getStartTime())
                .endTime(appointment.getEndTime())
                .status(appointment.getStatus())
                .statusColor(statusColor)
                .participantName(appointment.getParentName())
                .location(appointment.getLocation())
                .isOnline(ConversionUtils.defaultIfNull(appointment.getIsOnline(), false))
                .isUrgent(isUrgentAppointment(appointment))
                .tooltip(tooltip)
                .build();
    }

    public AppointmentCalendarDto mapToCalendarDto(LocalDate date, List<Appointment> appointments) {
        if (appointments == null) {
            appointments = new ArrayList<>();
        }

        List<AppointmentCalendarEventDto> events = appointments.stream()
                .map(this::mapToCalendarEventDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return AppointmentCalendarDto.builder()
                .date(date)
                .events(events)
                .totalAppointments(appointments.size())
                .availableSlots(calculateAvailableSlots(date, appointments))
                .hasConflicts(checkForConflicts(events))
                .build();
    }

    // ================== BULK OPERATIONS ==================

    public BulkAppointmentResultDto mapToBulkResultDto(String operationId, List<Long> affectedIds,
                                                       Map<String, Object> operationResults) {
        if (operationResults == null) {
            operationResults = new HashMap<>();
        }

        return BulkAppointmentResultDto.builder()
                .success(getBooleanValue(operationResults, "success"))
                .totalRecords(getIntegerValue(operationResults, "totalRecords"))
                .successfulOperations(getIntegerValue(operationResults, "successfulOperations"))
                .failedOperations(getIntegerValue(operationResults, "failedOperations"))
                .errors((List<String>) operationResults.get("errors"))
                .warnings((List<String>) operationResults.get("warnings"))
                .operationId(operationId)
                .operationDate(LocalDateTime.now())
                .affectedAppointmentIds(affectedIds)
                .notificationsSent(getIntegerValue(operationResults, "notificationsSent"))
                .build();
    }

    // ================== HELPER METHODS CONTINUED ==================

    private Boolean isRecommendedSlot(AppointmentSlot slot) {
        if (slot == null) {
            return false;
        }

        // Recommend slots that are popular times (10-12, 14-16)
        if (slot.getStartTime() != null) {
            int hour = slot.getStartTime().getHour();
            return (hour >= 10 && hour < 12) || (hour >= 14 && hour < 16);
        }

        return false;
    }

    private String getStatusColor(com.genixo.education.search.enumaration.AppointmentStatus status) {
        if (status == null) {
            return "#6c757d"; // Default gray
        }

        switch (status) {
            case PENDING: return "#ffc107"; // Yellow
            case CONFIRMED: return "#28a745"; // Green
            case APPROVED: return "#17a2b8"; // Info blue
            case REJECTED: return "#dc3545"; // Red
            case CANCELLED: return "#dc3545"; // Red
            case COMPLETED: return "#007bff"; // Blue
            case NO_SHOW: return "#fd7e14"; // Orange
            case RESCHEDULED: return "#6f42c1"; // Purple
            case IN_PROGRESS: return "#20c997"; // Teal
            default: return "#6c757d"; // Gray
        }
    }

    private String generateTooltip(Appointment appointment) {
        if (appointment == null) {
            return null;
        }

        StringBuilder tooltip = new StringBuilder();

        if (StringUtils.hasText(appointment.getStudentName())) {
            tooltip.append("Öğrenci: ").append(appointment.getStudentName()).append("\n");
        }

        if (StringUtils.hasText(appointment.getParentName())) {
            tooltip.append("Veli: ").append(appointment.getParentName()).append("\n");
        }

        if (appointment.getStartTime() != null && appointment.getEndTime() != null) {
            tooltip.append("Saat: ").append(ConversionUtils.formatTimeRange(
                    appointment.getStartTime(), appointment.getEndTime())).append("\n");
        }

        if (StringUtils.hasText(appointment.getLocation())) {
            tooltip.append("Yer: ").append(appointment.getLocation());
        }

        return tooltip.toString().trim();
    }

    private String generateCalendarTitle(Appointment appointment) {
        if (appointment == null) {
            return "Randevu";
        }

        if (StringUtils.hasText(appointment.getStudentName())) {
            return appointment.getStudentName();
        }

        if (StringUtils.hasText(appointment.getParentName())) {
            return appointment.getParentName();
        }

        return "Randevu";
    }

    private Boolean isUrgentAppointment(Appointment appointment) {
        if (appointment == null) {
            return false;
        }

        // Urgent if appointment is within next 2 hours
        Integer hoursUntil = calculateHoursUntilAppointment(appointment);
        return hoursUntil != null && hoursUntil <= 2;
    }

    private Integer calculateAvailableSlots(LocalDate date, List<Appointment> appointments) {
        // This would require slot information
        // For now, return a placeholder calculation
        return Math.max(0, 10 - appointments.size());
    }

    private Boolean checkForConflicts(List<AppointmentCalendarEventDto> events) {
        if (ConversionUtils.isEmpty(events)) {
            return false;
        }

        // Check for time overlaps
        for (int i = 0; i < events.size(); i++) {
            for (int j = i + 1; j < events.size(); j++) {
                AppointmentCalendarEventDto event1 = events.get(i);
                AppointmentCalendarEventDto event2 = events.get(j);

                if (timesOverlap(event1.getStartTime(), event1.getEndTime(),
                        event2.getStartTime(), event2.getEndTime())) {
                    return true;
                }
            }
        }

        return false;
    }

    private Boolean timesOverlap(java.time.LocalTime start1, java.time.LocalTime end1,
                                 java.time.LocalTime start2, java.time.LocalTime end2) {
        if (start1 == null || end1 == null || start2 == null || end2 == null) {
            return false;
        }

        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    // ================== UTILITY METHODS FOR MAP ACCESS ==================

    private Long getLongValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof Integer) {
            return ((Integer) value).longValue();
        } else if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return 0L;
    }

    private Double getDoubleValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return 0.0;
    }

    private Integer getIntegerValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return 0;
    }

    private String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }

    private Boolean getBooleanValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return false;
    }

    public AppointmentDto mapToPublicAppointmentDto(Appointment appointment) {
        if (appointment == null) {
            return null;
        }

        return AppointmentDto.builder()
                .id(appointment.getId())
                .appointmentNumber(appointment.getAppointmentNumber())
                .appointmentDate(appointment.getAppointmentDate())
                .startTime(appointment.getStartTime())
                .endTime(appointment.getEndTime())
                .status(appointment.getStatus())
                .statusDisplayName(ConversionUtils.getDisplayName(appointment.getStatus()))
                .appointmentType(appointment.getAppointmentType())
                .location(appointment.getLocation())
                .isOnline(ConversionUtils.defaultIfNull(appointment.getIsOnline(), false))
                .meetingUrl(appointment.getMeetingUrl())
                .meetingId(appointment.getMeetingId())
                .meetingPassword(appointment.getMeetingPassword())
                .parentName(appointment.getParentName())
                .parentEmail(appointment.getParentEmail())
                .parentPhone(appointment.getParentPhone())
                .studentName(appointment.getStudentName())
                .studentAge(appointment.getStudentAge())
                .studentGender(appointment.getStudentGender())
                .currentSchool(appointment.getCurrentSchool())
                .gradeInterested(appointment.getGradeInterested())
                .specialRequests(appointment.getSpecialRequests())
                .notes(appointment.getNotes()) // Hide internal notes
                .internalNotes(appointment.getInternalNotes()) // Hide internal notes
                .outcome(appointment.getOutcome()) // Hide outcome
                .outcomeNotes(appointment.getOutcomeNotes()) // Hide outcome notes
                .enrollmentLikelihood(appointment.getEnrollmentLikelihood()) // Hide likelihood
                .nextSteps(null) // Hide next steps
                .followUpRequired(null) // Hide follow-up
                .followUpDate(null) // Hide follow-up date
                .createdAt(null) // Hide creation date
                .updatedAt(null) // Hide update date
                .canCancel(false) // No public cancellation
                .canReschedule(false) // No public rescheduling
                .canComplete(false) // No public completion
                .hoursUntilAppointment(calculateHoursUntilAppointment(appointment))
                .participants(new ArrayList<>()) // Hide participants
                .build();
    }
}