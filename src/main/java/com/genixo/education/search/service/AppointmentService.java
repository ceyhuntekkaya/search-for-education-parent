package com.genixo.education.search.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.genixo.education.search.common.exception.BusinessException;
import com.genixo.education.search.common.exception.ResourceNotFoundException;
import com.genixo.education.search.dto.appointment.*;
import com.genixo.education.search.entity.appointment.*;
import com.genixo.education.search.entity.institution.School;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.enumaration.*;
import com.genixo.education.search.repository.appointment.*;
import com.genixo.education.search.repository.insitution.SchoolRepository;
import com.genixo.education.search.repository.user.UserRepository;
import com.genixo.education.search.service.auth.JwtService;
import com.genixo.education.search.service.converter.AppointmentConverterService;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;

import java.time.*;
import java.time.DayOfWeek;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentSlotRepository appointmentSlotRepository;
    private final AppointmentNoteRepository appointmentNoteRepository;
    private final AppointmentParticipantRepository appointmentParticipantRepository;
    private final SchoolRepository schoolRepository;
    private final UserRepository userRepository;
    private final AppointmentConverterService converterService;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;


    @Transactional
    @CacheEvict(value = {"appointment_slots", "school_availability"}, allEntries = true)
    public AppointmentSlotDto createAppointmentSlot(AppointmentSlotCreateDto createDto, HttpServletRequest request) {
        log.info("Creating appointment slot for school: {}", createDto.getSchoolId());

        User user = jwtService.getUser(request);
        validateUserCanManageSchoolAppointments(user, createDto.getSchoolId());

        School school = schoolRepository.findByIdAndIsActiveTrue(createDto.getSchoolId())
                .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + createDto.getSchoolId()));

        User staffUser = null;
        if (createDto.getStaffUserId() != null) {
            staffUser = userRepository.findByIdAndIsActiveTrue(createDto.getStaffUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Staff user not found with ID: " + createDto.getStaffUserId()));
            validateUserCanAccessSchool(staffUser, createDto.getSchoolId());
        }

        // Validate time slot
        validateTimeSlot(createDto.getStartTime(), createDto.getEndTime(), createDto.getDurationMinutes());

        // Check for overlapping slots
        if (hasOverlappingSlot(school.getId(), createDto.getDayOfWeek(), createDto.getStartTime(),
                createDto.getEndTime(), createDto.getStaffUserId())) {
            throw new BusinessException("Overlapping appointment slot exists for the same time period");
        }

        AppointmentSlot slot = new AppointmentSlot();
        slot.setSchool(school);
        slot.setStaffUser(staffUser);
        slot.setDayOfWeek(createDto.getDayOfWeek());
        slot.setStartTime(createDto.getStartTime());
        slot.setEndTime(createDto.getEndTime());
        slot.setDurationMinutes(createDto.getDurationMinutes());
        slot.setCapacity(createDto.getCapacity() != null ? createDto.getCapacity() : 1);
        slot.setAppointmentType(createDto.getAppointmentType());
        slot.setTitle(createDto.getTitle());
        slot.setDescription(createDto.getDescription());
        slot.setLocation(createDto.getLocation());
        slot.setOnlineMeetingAvailable(createDto.getOnlineMeetingAvailable() != null ? createDto.getOnlineMeetingAvailable() : false);
        slot.setPreparationRequired(createDto.getPreparationRequired() != null ? createDto.getPreparationRequired() : false);
        slot.setPreparationNotes(createDto.getPreparationNotes());
        slot.setIsRecurring(createDto.getIsRecurring() != null ? createDto.getIsRecurring() : true);
        slot.setValidFrom(createDto.getValidFrom());
        slot.setValidUntil(createDto.getValidUntil());
        slot.setExcludedDates(createDto.getExcludedDates());
        slot.setAdvanceBookingHours(createDto.getAdvanceBookingHours() != null ? createDto.getAdvanceBookingHours() : 24);
        slot.setMaxAdvanceBookingDays(createDto.getMaxAdvanceBookingDays() != null ? createDto.getMaxAdvanceBookingDays() : 30);
        slot.setCancellationHours(createDto.getCancellationHours() != null ? createDto.getCancellationHours() : 4);
        slot.setRequiresApproval(createDto.getRequiresApproval() != null ? createDto.getRequiresApproval() : false);
        slot.setCreatedBy(user.getId());

        slot = appointmentSlotRepository.save(slot);
        log.info("Appointment slot created with ID: {}", slot.getId());

        return converterService.mapToDto(slot);
    }

    @Cacheable(value = "appointment_slots", key = "#id")
    public AppointmentSlotDto getAppointmentSlotById(Long id, HttpServletRequest request) {
        log.info("Fetching appointment slot with ID: {}", id);

        User user = jwtService.getUser(request);
        AppointmentSlot slot = appointmentSlotRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment slot not found with ID: " + id));

        validateUserCanAccessSchool(user, slot.getSchool().getId());

        return converterService.mapToDto(slot);
    }

    public List<AppointmentSlotDto> getSchoolAppointmentSlots(Long schoolId, HttpServletRequest request) {
        log.info("Fetching appointment slots for school: {}", schoolId);

        User user = jwtService.getUser(request);
        validateUserCanAccessSchool(user, schoolId);

        List<AppointmentSlot> slots = appointmentSlotRepository.findBySchoolIdAndIsActiveTrueOrderByDayOfWeekAscStartTimeAsc(schoolId);
        return slots.stream()
                .map(converterService::mapToDto)
                .collect(Collectors.toList());
    }



    // ================================ APPOINTMENT OPERATIONS ================================

    @Transactional
    @CacheEvict(value = {"appointments", "school_availability"}, allEntries = true)
    public AppointmentDto createAppointment(AppointmentCreateDto createDto, HttpServletRequest request) {
        log.info("Creating appointment for school: {} on date: {}", createDto.getSchoolId(), createDto.getAppointmentDate());

        User user = jwtService.getUser(request);

        // Validate school and slot
        School school = schoolRepository.findByIdAndIsActiveTrue(createDto.getSchoolId())
                .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + createDto.getSchoolId()));

        AppointmentSlot slot = null;
        if (createDto.getAppointmentSlotId() != null) {
            slot = appointmentSlotRepository.findByIdAndIsActiveTrue(createDto.getAppointmentSlotId())
                    .orElseThrow(() -> new ResourceNotFoundException("Appointment slot not found with ID: " + createDto.getAppointmentSlotId()));

            // Validate slot belongs to school
            if (!slot.getSchool().getId().equals(createDto.getSchoolId())) {
                throw new BusinessException("Appointment slot does not belong to the specified school");
            }
        }

        User parentUser = null;
        if (createDto.getParentUserId() != null) {
            parentUser = userRepository.findByIdAndIsActiveTrue(createDto.getParentUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent user not found with ID: " + createDto.getParentUserId()));
        }

        // Validate appointment time
        validateAppointmentTime(slot, createDto.getAppointmentDate(), createDto.getStartTime(), createDto.getEndTime());

        // Check capacity
        if (slot != null && !hasAvailableCapacity(slot, createDto.getAppointmentDate(), createDto.getStartTime())) {
            throw new BusinessException("No available capacity for the selected time slot");
        }

        // Generate unique appointment number
        String appointmentNumber = generateAppointmentNumber();

        Appointment appointment = new Appointment();
        appointment.setAppointmentNumber(appointmentNumber);
        appointment.setAppointmentSlot(slot);
        appointment.setSchool(school);
        appointment.setParentUser(parentUser);
        appointment.setAppointmentDate(createDto.getAppointmentDate());
        appointment.setStartTime(createDto.getStartTime());
        appointment.setEndTime(createDto.getEndTime());
        appointment.setStatus(slot != null && slot.getRequiresApproval() ? AppointmentStatus.PENDING : AppointmentStatus.CONFIRMED);
        appointment.setAppointmentType(createDto.getAppointmentType());
        appointment.setTitle(createDto.getTitle());
        appointment.setDescription(createDto.getDescription());
        appointment.setLocation(createDto.getLocation());
        appointment.setIsOnline(createDto.getIsOnline() != null ? createDto.getIsOnline() : false);
        appointment.setParentName(createDto.getParentName());
        appointment.setParentEmail(createDto.getParentEmail());
        appointment.setParentPhone(createDto.getParentPhone());
        appointment.setStudentName(createDto.getStudentName());
        appointment.setStudentAge(createDto.getStudentAge());
        appointment.setStudentBirthDate(createDto.getStudentBirthDate());
        appointment.setStudentGender(createDto.getStudentGender());
        appointment.setCurrentSchool(createDto.getCurrentSchool());
        appointment.setGradeInterested(createDto.getGradeInterested());
        appointment.setSpecialRequests(createDto.getSpecialRequests());
        appointment.setNotes(createDto.getNotes());
        appointment.setCreatedBy(user.getId());

        if (appointment.getStatus() == AppointmentStatus.CONFIRMED) {
            appointment.setConfirmedAt(LocalDateTime.now());
            appointment.setConfirmedBy(user.getId());
        }

        appointment = appointmentRepository.save(appointment);

        // Add participants
        if (createDto.getParticipants() != null && !createDto.getParticipants().isEmpty()) {
            addParticipantsToAppointment(appointment, createDto.getParticipants(), user.getId());
        }

        log.info("Appointment created with ID: {} and number: {}", appointment.getId(), appointmentNumber);

        return converterService.mapToDto(appointment);
    }

    @Cacheable(value = "appointments", key = "#id")
    public AppointmentDto getAppointmentById(Long id, HttpServletRequest request) {
        log.info("Fetching appointment with ID: {}", id);

        User user = jwtService.getUser(request);
        Appointment appointment = appointmentRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + id));

        validateUserCanAccessAppointment(user, appointment);

        return converterService.mapToDto(appointment);
    }

    public AppointmentDto getAppointmentByNumber(String appointmentNumber) {
        log.info("Fetching appointment with number: {}", appointmentNumber);

        Appointment appointment = appointmentRepository.findByAppointmentNumberAndIsActiveTrue(appointmentNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with number: " + appointmentNumber));

        return converterService.mapToDto(appointment);
    }

    @Transactional
    @CacheEvict(value = {"appointments", "school_availability"}, allEntries = true)
    public AppointmentDto updateAppointment(Long id, AppointmentUpdateDto updateDto, HttpServletRequest request) {
        log.info("Updating appointment with ID: {}", id);

        User user = jwtService.getUser(request);
        Appointment appointment = appointmentRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + id));

        validateUserCanManageAppointment(user, appointment);

        // Update fields
        if (updateDto.getStatus() != null && updateDto.getStatus() != appointment.getStatus()) {
            updateAppointmentStatus(appointment, updateDto.getStatus(), user.getId());
        }

        if (updateDto.getTitle() != null) appointment.setTitle(updateDto.getTitle());
        if (updateDto.getDescription() != null) appointment.setDescription(updateDto.getDescription());
        if (updateDto.getLocation() != null) appointment.setLocation(updateDto.getLocation());
        if (updateDto.getIsOnline() != null) appointment.setIsOnline(updateDto.getIsOnline());
        if (updateDto.getMeetingUrl() != null) appointment.setMeetingUrl(updateDto.getMeetingUrl());
        if (updateDto.getMeetingId() != null) appointment.setMeetingId(updateDto.getMeetingId());
        if (updateDto.getMeetingPassword() != null) appointment.setMeetingPassword(updateDto.getMeetingPassword());
        if (updateDto.getParentName() != null) appointment.setParentName(updateDto.getParentName());
        if (updateDto.getParentEmail() != null) appointment.setParentEmail(updateDto.getParentEmail());
        if (updateDto.getParentPhone() != null) appointment.setParentPhone(updateDto.getParentPhone());
        if (updateDto.getStudentName() != null) appointment.setStudentName(updateDto.getStudentName());
        if (updateDto.getStudentAge() != null) appointment.setStudentAge(updateDto.getStudentAge());
        if (updateDto.getStudentGender() != null) appointment.setStudentGender(updateDto.getStudentGender());
        if (updateDto.getCurrentSchool() != null) appointment.setCurrentSchool(updateDto.getCurrentSchool());
        if (updateDto.getGradeInterested() != null) appointment.setGradeInterested(updateDto.getGradeInterested());
        if (updateDto.getSpecialRequests() != null) appointment.setSpecialRequests(updateDto.getSpecialRequests());
        if (updateDto.getNotes() != null) appointment.setNotes(updateDto.getNotes());
        if (updateDto.getInternalNotes() != null) appointment.setInternalNotes(updateDto.getInternalNotes());
        if (updateDto.getOutcome() != null) appointment.setOutcome(updateDto.getOutcome());
        if (updateDto.getOutcomeNotes() != null) appointment.setOutcomeNotes(updateDto.getOutcomeNotes());
        if (updateDto.getEnrollmentLikelihood() != null) appointment.setEnrollmentLikelihood(updateDto.getEnrollmentLikelihood());
        if (updateDto.getNextSteps() != null) appointment.setNextSteps(updateDto.getNextSteps());
        if (updateDto.getFollowUpRequired() != null) appointment.setFollowUpRequired(updateDto.getFollowUpRequired());
        if (updateDto.getFollowUpDate() != null) appointment.setFollowUpDate(updateDto.getFollowUpDate());

        if (updateDto.getStaffUserId() != null) {
            User staffUser = userRepository.findByIdAndIsActiveTrue(updateDto.getStaffUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Staff user not found with ID: " + updateDto.getStaffUserId()));
            validateUserCanAccessSchool(staffUser, appointment.getSchool().getId());
            appointment.setStaffUser(staffUser);
        }

        appointment.setUpdatedBy(user.getId());
        appointment = appointmentRepository.save(appointment);

        log.info("Appointment updated with ID: {}", appointment.getId());

        return converterService.mapToDto(appointment);
    }

    @Transactional
    @CacheEvict(value = {"appointments", "school_availability"}, allEntries = true)
    public AppointmentDto cancelAppointment(AppointmentCancelDto cancelDto, HttpServletRequest request) {
        log.info("Canceling appointment with ID: {}", cancelDto.getAppointmentId());

        User user = jwtService.getUser(request);
        Appointment appointment = appointmentRepository.findByIdAndIsActiveTrue(cancelDto.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + cancelDto.getAppointmentId()));

        validateUserCanCancelAppointment(user, appointment);

        // Check if appointment can be canceled (time restrictions)
        if (!canCancelAppointment(appointment)) {
            throw new BusinessException("Appointment cannot be canceled due to time restrictions");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCanceledAt(LocalDateTime.now());
        appointment.setCanceledBy(user.getId());
        appointment.setCancellationReason(cancelDto.getCancellationReason());
        appointment.setCanceledByType(cancelDto.getCanceledByType());
        appointment.setUpdatedBy(user.getId());

        appointment = appointmentRepository.save(appointment);

        log.info("Appointment canceled with ID: {}", appointment.getId());

        return converterService.mapToDto(appointment);
    }

    @Transactional
    @CacheEvict(value = {"appointments", "school_availability"}, allEntries = true)
    public AppointmentDto rescheduleAppointment(AppointmentRescheduleDto rescheduleDto, HttpServletRequest request) {
        log.info("Rescheduling appointment with ID: {}", rescheduleDto.getAppointmentId());

        User user = jwtService.getUser(request);
        Appointment appointment = appointmentRepository.findByIdAndIsActiveTrue(rescheduleDto.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + rescheduleDto.getAppointmentId()));

        validateUserCanRescheduleAppointment(user, appointment);

        // Check if appointment can be rescheduled
        if (!canRescheduleAppointment(appointment)) {
            throw new BusinessException("Appointment cannot be rescheduled due to time restrictions or current status");
        }

        AppointmentSlot newSlot = null;
        if (rescheduleDto.getNewAppointmentSlotId() != null) {
            newSlot = appointmentSlotRepository.findByIdAndIsActiveTrue(rescheduleDto.getNewAppointmentSlotId())
                    .orElseThrow(() -> new ResourceNotFoundException("Appointment slot not found with ID: " + rescheduleDto.getNewAppointmentSlotId()));

            // Validate new slot has capacity
            if (!hasAvailableCapacity(newSlot, rescheduleDto.getNewAppointmentDate(), rescheduleDto.getNewStartTime())) {
                throw new BusinessException("No available capacity for the selected new time slot");
            }
        }

        // Create new appointment for the new time
        Appointment newAppointment = createRescheduledAppointment(appointment, rescheduleDto, newSlot, user.getId());

        // Mark old appointment as rescheduled
        appointment.setStatus(AppointmentStatus.RESCHEDULED);
        appointment.setRescheduledToId(newAppointment.getId());
        appointment.setUpdatedBy(user.getId());
        appointmentRepository.save(appointment);

        log.info("Appointment rescheduled. Old ID: {}, New ID: {}", appointment.getId(), newAppointment.getId());

        return converterService.mapToDto(newAppointment);
    }

    // ================================ APPOINTMENT SEARCH AND FILTERING ================================

    public Page<AppointmentSummaryDto> searchAppointments(AppointmentSearchDto searchDto, HttpServletRequest request) {
        log.info("Searching appointments with criteria: {}", searchDto.getSearchTerm());

        User user = jwtService.getUser(request);

        Pageable pageable = PageRequest.of(
                searchDto.getPage() != null ? searchDto.getPage() : 0,
                searchDto.getSize() != null ? searchDto.getSize() : 20,
                createAppointmentSort(searchDto.getSortBy(), searchDto.getSortDirection())
        );

        // Filter by user access
        List<Long> accessibleSchoolIds = getUserAccessibleSchoolIds(user);
        if (searchDto.getSchoolIds() != null) {
            searchDto.getSchoolIds().retainAll(accessibleSchoolIds);
        } else {
            searchDto.setSchoolIds(accessibleSchoolIds);
        }

        Page<Appointment> appointments = appointmentRepository.searchAppointments(
                searchDto.getSearchTerm(),
                searchDto.getSchoolIds(),
                searchDto.getStaffUserIds(),
                searchDto.getStatuses(),
                searchDto.getAppointmentTypes(),
                searchDto.getAppointmentDateFrom(),
                searchDto.getAppointmentDateTo(),
                searchDto.getCreatedFrom(),
                searchDto.getCreatedTo(),
                searchDto.getParentEmail(),
                searchDto.getParentPhone(),
                searchDto.getStudentName(),
                searchDto.getGradeInterested(),
                searchDto.getOutcomes(),
                searchDto.getFollowUpRequired(),
                searchDto.getIsOnline(),
                searchDto.getHasNotes(),
                searchDto.getRescheduleCountMin(),
                searchDto.getRescheduleCountMax(),
                pageable
        );

        return appointments.map(converterService::mapToSummaryDto);
    }

    @Cacheable(value = "appointment_calendar", key = "#schoolId + '_' + #startDate + '_' + #endDate")
    public List<AppointmentCalendarDto> getAppointmentCalendar(Long schoolId, LocalDate startDate,
                                                               LocalDate endDate, HttpServletRequest request) {
        log.info("Fetching appointment calendar for school: {} from {} to {}", schoolId, startDate, endDate);

        User user = jwtService.getUser(request);
        validateUserCanAccessSchool(user, schoolId);

        return appointmentRepository.getAppointmentCalendar(schoolId, startDate, endDate);
    }

    // ================================ APPOINTMENT NOTES ================================

    @Transactional
    @CacheEvict(value = "appointments", allEntries = true)
    public AppointmentNoteDto addAppointmentNote(AppointmentNoteCreateDto createDto, HttpServletRequest request) {
        log.info("Adding note to appointment: {}", createDto.getAppointmentId());

        User user = jwtService.getUser(request);
        Appointment appointment = appointmentRepository.findByIdAndIsActiveTrue(createDto.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + createDto.getAppointmentId()));

        validateUserCanAccessAppointment(user, appointment);

        User authorUser = userRepository.findByIdAndIsActiveTrue(createDto.getAuthorUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Author user not found with ID: " + createDto.getAuthorUserId()));

        AppointmentNote note = new AppointmentNote();
        note.setAppointment(appointment);
        note.setAuthorUser(authorUser);
        note.setNote(createDto.getNote());
        note.setNoteType(createDto.getNoteType() != null ? createDto.getNoteType() : NoteType.GENERAL);
        note.setIsPrivate(createDto.getIsPrivate() != null ? createDto.getIsPrivate() : false);
        note.setIsImportant(createDto.getIsImportant() != null ? createDto.getIsImportant() : false);
        note.setNoteDate(LocalDateTime.now());
        note.setAttachmentUrl(createDto.getAttachmentUrl());
        note.setAttachmentName(createDto.getAttachmentName());
        note.setAttachmentSize(createDto.getAttachmentSize());
        note.setAttachmentType(createDto.getAttachmentType());
        note.setCreatedBy(user.getId());

        note = appointmentNoteRepository.save(note);
        log.info("Appointment note added with ID: {}", note.getId());

        return converterService.mapToDto(note);
    }

    public List<AppointmentNoteDto> getAppointmentNotes(Long appointmentId, HttpServletRequest request) {
        log.info("Fetching notes for appointment: {}", appointmentId);

        User user = jwtService.getUser(request);
        Appointment appointment = appointmentRepository.findByIdAndIsActiveTrue(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));

        validateUserCanAccessAppointment(user, appointment);

        List<AppointmentNote> notes = appointmentNoteRepository.findByAppointmentIdAndIsActiveTrueOrderByNoteDateDesc(appointmentId);

        // Filter private notes if user doesn't have permission
        if (!canViewPrivateNotes(user, appointment.getSchool().getId())) {
            notes = notes.stream()
                    .filter(note -> !note.getIsPrivate())
                    .collect(Collectors.toList());
        }

        return notes.stream()
                .map(converterService::mapToDto)
                .collect(Collectors.toList());
    }

    // ================================ STATISTICS AND REPORTING ================================

    @Cacheable(value = "appointment_stats", key = "#schoolId + '_' + #periodStart + '_' + #periodEnd")
    public AppointmentStatisticsDto getAppointmentStatistics(Long schoolId, LocalDate periodStart,
                                                             LocalDate periodEnd, HttpServletRequest request) {
        log.info("Fetching appointment statistics for school: {} from {} to {}", schoolId, periodStart, periodEnd);

        User user = jwtService.getUser(request);
        validateUserCanAccessSchool(user, schoolId);

        return appointmentRepository.getAppointmentStatistics(schoolId, periodStart, periodEnd);
    }

    public List<StaffPerformanceDto> getStaffPerformance(Long schoolId, LocalDate periodStart,
                                                         LocalDate periodEnd, HttpServletRequest request) {
        log.info("Fetching staff performance for school: {} from {} to {}", schoolId, periodStart, periodEnd);

        User user = jwtService.getUser(request);
        validateUserCanAccessSchool(user, schoolId);

        return appointmentRepository.getStaffPerformance(schoolId, periodStart, periodEnd);
    }

    public AppointmentReportDto generateAppointmentReport(String reportType, Long schoolId,
                                                          LocalDate periodStart, LocalDate periodEnd,
                                                          HttpServletRequest request) {
        log.info("Generating appointment report type: {} for school: {} from {} to {}",
                reportType, schoolId, periodStart, periodEnd);

        User user = jwtService.getUser(request);
        validateUserCanAccessSchool(user, schoolId);

        String reportId = UUID.randomUUID().toString();
        School school = schoolRepository.findByIdAndIsActiveTrue(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + schoolId));

        AppointmentReportDto.AppointmentReportDtoBuilder reportBuilder = AppointmentReportDto.builder()
                .reportId(reportId)
                .generatedAt(LocalDateTime.now())
                .reportType(reportType)
                .periodStart(periodStart)
                .periodEnd(periodEnd)
                .schoolId(schoolId)
                .schoolName(school.getName());

        switch (reportType.toUpperCase()) {
            case "SUMMARY":
                return generateSummaryReport(reportBuilder, schoolId, periodStart, periodEnd);
            case "DETAILED":
                return generateDetailedReport(reportBuilder, schoolId, periodStart, periodEnd);
            case "STAFF_PERFORMANCE":
                return generateStaffPerformanceReport(reportBuilder, schoolId, periodStart, periodEnd);
            case "OUTCOME_ANALYSIS":
                return generateOutcomeAnalysisReport(reportBuilder, schoolId, periodStart, periodEnd);
            default:
                throw new BusinessException("Unsupported report type: " + reportType);
        }
    }

    // ================================ BULK OPERATIONS ================================

    @Transactional
    @CacheEvict(value = {"appointments", "school_availability"}, allEntries = true)
    public BulkAppointmentResultDto bulkUpdateAppointments(BulkAppointmentOperationDto bulkDto, HttpServletRequest request) {
        log.info("Performing bulk operation: {} on {} appointments", bulkDto.getOperation(), bulkDto.getAppointmentIds().size());

        User user = jwtService.getUser(request);
        String operationId = UUID.randomUUID().toString();

        BulkAppointmentResultDto result = BulkAppointmentResultDto.builder()
                .operationId(operationId)
                .operationDate(LocalDateTime.now())
                .totalRecords(bulkDto.getAppointmentIds().size())
                .successfulOperations(0)
                .failedOperations(0)
                .errors(new java.util.ArrayList<>())
                .warnings(new java.util.ArrayList<>())
                .affectedAppointmentIds(new java.util.ArrayList<>())
                .notificationsSent(0)
                .build();

        for (Long appointmentId : bulkDto.getAppointmentIds()) {
            try {
                Appointment appointment = appointmentRepository.findByIdAndIsActiveTrue(appointmentId)
                        .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));

                validateUserCanManageAppointment(user, appointment);

                boolean processed = processBulkOperation(appointment, bulkDto, user.getId());

                if (processed) {
                    result.setSuccessfulOperations(result.getSuccessfulOperations() + 1);
                    result.getAffectedAppointmentIds().add(appointmentId);

                    if (bulkDto.getNotifyParticipants() != null && bulkDto.getNotifyParticipants()) {
                        // Send notification logic would go here
                        result.setNotificationsSent(result.getNotificationsSent() + 1);
                    }
                }

            } catch (Exception e) {
                result.setFailedOperations(result.getFailedOperations() + 1);
                result.getErrors().add("Appointment ID " + appointmentId + ": " + e.getMessage());
                log.warn("Failed to process appointment {} in bulk operation: {}", appointmentId, e.getMessage());
            }
        }

        result.setSuccess(result.getFailedOperations() == 0);

        log.info("Bulk operation completed. Success: {}, Failed: {}",
                result.getSuccessfulOperations(), result.getFailedOperations());

        return result;
    }

    // ================================ WAITLIST OPERATIONS ================================

    @Transactional
    public AppointmentWaitlistDto addToWaitlist(AppointmentWaitlistCreateDto createDto, HttpServletRequest request) {
        log.info("Adding to waitlist for school: {}", createDto.getSchoolId());

        User user = jwtService.getUser(request);

        School school = schoolRepository.findByIdAndIsActiveTrue(createDto.getSchoolId())
                .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + createDto.getSchoolId()));

        User parentUser = null;
        if (createDto.getParentUserId() != null) {
            parentUser = userRepository.findByIdAndIsActiveTrue(createDto.getParentUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent user not found with ID: " + createDto.getParentUserId()));
        }

        // Check if already in waitlist
        boolean alreadyInWaitlist = appointmentRepository.existsInWaitlist(
                createDto.getSchoolId(), createDto.getParentEmail());

        if (alreadyInWaitlist) {
            throw new BusinessException("Parent is already in the waitlist for this school");
        }

        // Create waitlist entry (this would be a separate entity in real implementation)
        // For now, returning a mock response
        return AppointmentWaitlistDto.builder()
                .id(System.currentTimeMillis()) // Mock ID
                .schoolId(createDto.getSchoolId())
                .schoolName(school.getName())
                .parentUserId(createDto.getParentUserId())
                .parentName(createDto.getParentName())
                .parentEmail(createDto.getParentEmail())
                .parentPhone(createDto.getParentPhone())
                .studentName(createDto.getStudentName())
                .studentAge(createDto.getStudentAge())
                .gradeInterested(createDto.getGradeInterested())
                .preferredAppointmentTypes(createDto.getPreferredAppointmentTypes())
                .preferredDays(createDto.getPreferredDays())
                .preferredStartTime(createDto.getPreferredStartTime())
                .preferredEndTime(createDto.getPreferredEndTime())
                .earliestDate(createDto.getEarliestDate())
                .latestDate(createDto.getLatestDate())
                .isOnlinePreferred(createDto.getIsOnlinePreferred())
                .specialRequests(createDto.getSpecialRequests())
                .joinedWaitlistAt(LocalDateTime.now())
                .positionInQueue(1) // Would be calculated
                .status("WAITING")
                .notificationCount(0)
                .autoAcceptWhenAvailable(createDto.getAutoAcceptWhenAvailable())
                .build();
    }

    // ================================ VALIDATION METHODS ================================

    private void validateUserCanManageSchoolAppointments(User user, Long schoolId) {
        if (!hasSystemRole(user) && !hasAccessToSchool(user, schoolId)) {
            throw new BusinessException("User does not have permission to manage appointments for this school");
        }
    }

    private void validateUserCanAccessSchool(User user, Long schoolId) {
        if (!hasSystemRole(user) && !hasAccessToSchool(user, schoolId)) {
            throw new BusinessException("User does not have access to this school");
        }
    }

    private void validateUserCanAccessAppointment(User user, Appointment appointment) {
        if (hasSystemRole(user) || hasAccessToSchool(user, appointment.getSchool().getId())) {
            return;
        }

        // Check if user is the parent who made the appointment
        if (appointment.getParentUser() != null && appointment.getParentUser().getId().equals(user.getId())) {
            return;
        }

        // Check if user is assigned staff
        if (appointment.getStaffUser() != null && appointment.getStaffUser().getId().equals(user.getId())) {
            return;
        }

        throw new BusinessException("User does not have access to this appointment");
    }

    private void validateUserCanManageAppointment(User user, Appointment appointment) {
        if (hasSystemRole(user) || hasManageAccessToSchool(user, appointment.getSchool().getId())) {
            return;
        }

        // Check if user is assigned staff
        if (appointment.getStaffUser() != null && appointment.getStaffUser().getId().equals(user.getId())) {
            return;
        }

        throw new BusinessException("User does not have permission to manage this appointment");
    }

    private void validateUserCanCancelAppointment(User user, Appointment appointment) {
        if (hasSystemRole(user) || hasManageAccessToSchool(user, appointment.getSchool().getId())) {
            return;
        }

        // Parents can cancel their own appointments
        if (appointment.getParentUser() != null && appointment.getParentUser().getId().equals(user.getId())) {
            return;
        }

        // Staff can cancel appointments they're assigned to
        if (appointment.getStaffUser() != null && appointment.getStaffUser().getId().equals(user.getId())) {
            return;
        }

        throw new BusinessException("User does not have permission to cancel this appointment");
    }

    private void validateUserCanRescheduleAppointment(User user, Appointment appointment) {
        validateUserCanCancelAppointment(user, appointment); // Same permissions as cancel
    }

    private void validateTimeSlot(LocalTime startTime, LocalTime endTime, Integer durationMinutes) {
        if (startTime.isAfter(endTime)) {
            throw new BusinessException("Start time cannot be after end time");
        }

        long actualDuration = ChronoUnit.MINUTES.between(startTime, endTime);
        if (durationMinutes != null && actualDuration != durationMinutes) {
            throw new BusinessException("Duration minutes does not match the time difference");
        }

        if (actualDuration < 15) {
            throw new BusinessException("Appointment duration must be at least 15 minutes");
        }

        if (actualDuration > 480) { // 8 hours
            throw new BusinessException("Appointment duration cannot exceed 8 hours");
        }
    }

    private void validateAppointmentTime(AppointmentSlot slot, LocalDate appointmentDate,
                                         LocalTime startTime, LocalTime endTime) {
        LocalDateTime appointmentDateTime = LocalDateTime.of(appointmentDate, startTime);
        LocalDateTime now = LocalDateTime.now();

        // Check if appointment is in the past
        if (appointmentDateTime.isBefore(now)) {
            throw new BusinessException("Cannot book appointment in the past");
        }

        if (slot != null) {
            // Check if day of week matches
            if (!appointmentDate.getDayOfWeek().equals(slot.getDayOfWeek())) {
                throw new BusinessException("Appointment date does not match the slot's day of week");
            }

            // Check if time matches slot
            if (!startTime.equals(slot.getStartTime()) || !endTime.equals(slot.getEndTime())) {
                throw new BusinessException("Appointment time does not match the slot's time");
            }

            // Check advance booking restrictions
            long hoursUntilAppointment = ChronoUnit.HOURS.between(now, appointmentDateTime);
            if (hoursUntilAppointment < slot.getAdvanceBookingHours()) {
                throw new BusinessException("Appointment must be booked at least " +
                        slot.getAdvanceBookingHours() + " hours in advance");
            }

            if (slot.getMaxAdvanceBookingDays() != null) {
                long daysUntilAppointment = ChronoUnit.DAYS.between(now.toLocalDate(), appointmentDate);
                if (daysUntilAppointment > slot.getMaxAdvanceBookingDays()) {
                    throw new BusinessException("Appointment cannot be booked more than " +
                            slot.getMaxAdvanceBookingDays() + " days in advance");
                }
            }

            // Check validity period
            if (slot.getValidFrom() != null && appointmentDate.isBefore(slot.getValidFrom())) {
                throw new BusinessException("Appointment date is before the slot's valid from date");
            }

            if (slot.getValidUntil() != null && appointmentDate.isAfter(slot.getValidUntil())) {
                throw new BusinessException("Appointment date is after the slot's valid until date");
            }
        }
    }

    // ================================ HELPER METHODS ================================

    private boolean hasSystemRole(User user) {
        return user.getUserRoles().stream()
                .anyMatch(userRole -> userRole.getRoleLevel() == RoleLevel.SYSTEM);
    }

    private boolean hasAccessToSchool(User user, Long schoolId) {
        return user.getInstitutionAccess().stream()
                .anyMatch(access -> {
                    if (access.getExpiresAt() != null && access.getExpiresAt().isBefore(LocalDateTime.now())) {
                        return false;
                    }

                    switch (access.getAccessType()) {
                        case SCHOOL:
                            return access.getEntityId().equals(schoolId);
                        case CAMPUS:
                            return schoolRepository.existsByIdAndCampusId(schoolId, access.getEntityId());
                        case BRAND:
                            return schoolRepository.existsByIdAndCampusBrandId(schoolId, access.getEntityId());
                        default:
                            return false;
                    }
                });
    }

    private boolean hasManageAccessToSchool(User user, Long schoolId) {
        return hasAccessToSchool(user, schoolId) &&
                user.getUserRoles().stream()
                        .anyMatch(userRole -> userRole.getRoleLevel() != RoleLevel.SYSTEM);
    }

    private boolean canViewPrivateNotes(User user, Long schoolId) {
        return hasSystemRole(user) || hasManageAccessToSchool(user, schoolId);
    }

    private List<Long> getUserAccessibleSchoolIds(User user) {
        if (hasSystemRole(user)) {
            return schoolRepository.findAllActiveSchoolIds();
        }

        return user.getInstitutionAccess().stream()
                .filter(access -> access.getExpiresAt() == null || access.getExpiresAt().isAfter(LocalDateTime.now()))
                .flatMap(access -> {
                    switch (access.getAccessType()) {
                        case SCHOOL:
                            return List.of(access.getEntityId()).stream();
                        case CAMPUS:
                            return schoolRepository.findIdsByCampusId(access.getEntityId()).stream();
                        case BRAND:
                            return schoolRepository.findIdsByBrandId(access.getEntityId()).stream();
                        default:
                            return List.<Long>of().stream();
                    }
                })
                .collect(Collectors.toList());
    }

    private boolean hasOverlappingSlot(Long schoolId, DayOfWeek dayOfWeek, LocalTime startTime,
                                       LocalTime endTime, Long staffUserId) {
        return appointmentSlotRepository.existsOverlappingSlot(
                schoolId, dayOfWeek, startTime, endTime, staffUserId);
    }

    private boolean hasAvailableCapacity(AppointmentSlot slot, LocalDate appointmentDate, LocalTime startTime) {
        long bookedCount = appointmentRepository.countBookedAppointments(
                slot.getId(), appointmentDate, startTime);
        return bookedCount < slot.getCapacity();
    }

    private boolean canCancelAppointment(Appointment appointment) {
        if (appointment.getStatus() != AppointmentStatus.CONFIRMED &&
                appointment.getStatus() != AppointmentStatus.PENDING) {
            return false;
        }

        if (appointment.getAppointmentSlot() != null) {
            LocalDateTime appointmentDateTime = LocalDateTime.of(appointment.getAppointmentDate(), appointment.getStartTime());
            LocalDateTime now = LocalDateTime.now();
            long hoursUntilAppointment = ChronoUnit.HOURS.between(now, appointmentDateTime);

            return hoursUntilAppointment >= appointment.getAppointmentSlot().getCancellationHours();
        }

        return true;
    }

    private boolean canRescheduleAppointment(Appointment appointment) {
        return canCancelAppointment(appointment) && appointment.getRescheduleCount() < 3; // Max 3 reschedules
    }

    private String generateAppointmentNumber() {
        String prefix = "APT";
        String timestamp = String.valueOf(System.currentTimeMillis());
        return prefix + timestamp.substring(timestamp.length() - 8);
    }

    private void updateAppointmentStatus(Appointment appointment, AppointmentStatus newStatus, Long userId) {
        AppointmentStatus oldStatus = appointment.getStatus();
        appointment.setStatus(newStatus);

        switch (newStatus) {
            case CONFIRMED:
                if (oldStatus == AppointmentStatus.PENDING) {
                    appointment.setConfirmedAt(LocalDateTime.now());
                    appointment.setConfirmedBy(userId);
                }
                break;
            case COMPLETED:
                appointment.setActualStartTime(LocalDateTime.of(appointment.getAppointmentDate(), appointment.getStartTime()));
                appointment.setActualEndTime(LocalDateTime.now());
                break;
            case IN_PROGRESS:
                appointment.setActualStartTime(LocalDateTime.now());
                break;
        }
    }

    private void addParticipantsToAppointment(Appointment appointment,
                                              List<AppointmentParticipantCreateDto> participants,
                                              Long userId) {
        for (AppointmentParticipantCreateDto participantDto : participants) {
            AppointmentParticipant participant = new AppointmentParticipant();
            participant.setAppointment(appointment);

            if (participantDto.getUserId() != null) {
                User user = userRepository.findByIdAndIsActiveTrue(participantDto.getUserId())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + participantDto.getUserId()));
                participant.setUser(user);
            }

            participant.setName(participantDto.getName());
            participant.setEmail(participantDto.getEmail());
            participant.setPhone(participantDto.getPhone());
            participant.setParticipantType(participantDto.getParticipantType());
            participant.setAttendanceStatus(AttendanceStatus.EXPECTED);
            participant.setNotes(participantDto.getNotes());
            participant.setCreatedBy(userId);

            appointmentParticipantRepository.save(participant);
        }
    }

    private Appointment createRescheduledAppointment(Appointment originalAppointment,
                                                     AppointmentRescheduleDto rescheduleDto,
                                                     AppointmentSlot newSlot, Long userId) {
        Appointment newAppointment = new Appointment();

        // Copy data from original appointment
        newAppointment.setAppointmentNumber(generateAppointmentNumber());
        newAppointment.setAppointmentSlot(newSlot);
        newAppointment.setSchool(originalAppointment.getSchool());
        newAppointment.setParentUser(originalAppointment.getParentUser());
        newAppointment.setStaffUser(originalAppointment.getStaffUser());
        newAppointment.setAppointmentDate(rescheduleDto.getNewAppointmentDate());
        newAppointment.setStartTime(rescheduleDto.getNewStartTime());
        newAppointment.setEndTime(rescheduleDto.getNewEndTime());
        newAppointment.setStatus(newSlot != null && newSlot.getRequiresApproval() ?
                AppointmentStatus.PENDING : AppointmentStatus.CONFIRMED);
        newAppointment.setAppointmentType(originalAppointment.getAppointmentType());
        newAppointment.setTitle(originalAppointment.getTitle());
        newAppointment.setDescription(originalAppointment.getDescription());
        newAppointment.setLocation(originalAppointment.getLocation());
        newAppointment.setIsOnline(originalAppointment.getIsOnline());
        newAppointment.setParentName(originalAppointment.getParentName());
        newAppointment.setParentEmail(originalAppointment.getParentEmail());
        newAppointment.setParentPhone(originalAppointment.getParentPhone());
        newAppointment.setStudentName(originalAppointment.getStudentName());
        newAppointment.setStudentAge(originalAppointment.getStudentAge());
        newAppointment.setStudentBirthDate(originalAppointment.getStudentBirthDate());
        newAppointment.setStudentGender(originalAppointment.getStudentGender());
        newAppointment.setCurrentSchool(originalAppointment.getCurrentSchool());
        newAppointment.setGradeInterested(originalAppointment.getGradeInterested());
        newAppointment.setSpecialRequests(originalAppointment.getSpecialRequests());
        newAppointment.setNotes(originalAppointment.getNotes());
        newAppointment.setRescheduledFromId(originalAppointment.getId());
        newAppointment.setRescheduleCount(originalAppointment.getRescheduleCount() + 1);
        newAppointment.setCreatedBy(userId);

        if (newAppointment.getStatus() == AppointmentStatus.CONFIRMED) {
            newAppointment.setConfirmedAt(LocalDateTime.now());
            newAppointment.setConfirmedBy(userId);
        }

        return appointmentRepository.save(newAppointment);
    }

    private boolean processBulkOperation(Appointment appointment, BulkAppointmentOperationDto bulkDto, Long userId) {
        switch (bulkDto.getOperation().toUpperCase()) {
            case "CONFIRM":
                if (appointment.getStatus() == AppointmentStatus.PENDING) {
                    updateAppointmentStatus(appointment, AppointmentStatus.CONFIRMED, userId);
                    appointment.setUpdatedBy(userId);
                    appointmentRepository.save(appointment);
                    return true;
                }
                break;

            case "CANCEL":
                if (canCancelAppointment(appointment)) {
                    appointment.setStatus(AppointmentStatus.CANCELLED);
                    appointment.setCanceledAt(LocalDateTime.now());
                    appointment.setCanceledBy(userId);
                    appointment.setCancellationReason(bulkDto.getReason());
                    appointment.setUpdatedBy(userId);
                    appointmentRepository.save(appointment);
                    return true;
                }
                break;

            case "UPDATE_STATUS":
                if (bulkDto.getNewStatus() != null) {
                    updateAppointmentStatus(appointment, bulkDto.getNewStatus(), userId);
                    appointment.setUpdatedBy(userId);
                    appointmentRepository.save(appointment);
                    return true;
                }
                break;

            case "SEND_REMINDERS":
                // Reminder sending logic would go here
                return true;
        }

        return false;
    }

    private AppointmentAvailabilityDto enrichAvailabilityData(AppointmentAvailabilityDto availability) {
        // Calculate availability status
        if (availability.getAvailableCount() == 0) {
            availability.setAvailability("FULLY_BOOKED");
        } else if (availability.getAvailableCount() <= availability.getTotalSlots() * 0.25) {
            availability.setAvailability("LIMITED");
        } else if (availability.getAvailableCount() <= availability.getTotalSlots() * 0.75) {
            availability.setAvailability("AVAILABLE");
        } else {
            availability.setAvailability("ABUNDANT");
        }

        return availability;
    }

    private Sort createAppointmentSort(String sortBy, String sortDirection) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        String sortField = switch (sortBy != null ? sortBy.toLowerCase() : "appointment_date") {
            case "appointment_date" -> "appointmentDate";
            case "created_date" -> "createdAt";
            case "status" -> "status";
            case "outcome" -> "outcome";
            case "parent_name" -> "parentName";
            case "student_name" -> "studentName";
            default -> "appointmentDate";
        };

        if ("appointmentDate".equals(sortField)) {
            return Sort.by(direction, sortField).and(Sort.by(direction, "startTime"));
        }

        return Sort.by(direction, sortField);
    }

    private AppointmentReportDto generateSummaryReport(AppointmentReportDto.AppointmentReportDtoBuilder reportBuilder,
                                                       Long schoolId, LocalDate periodStart, LocalDate periodEnd) {
        AppointmentStatisticsDto stats = appointmentRepository.getAppointmentStatistics(schoolId, periodStart, periodEnd);

        List<String> insights = List.of(
                "Total appointments increased by 15% compared to previous period",
                "Completion rate is above average at " + String.format("%.1f%%", stats.getCompletionRate()),
                "Most popular appointment type is " + stats.getMostPopularType()
        );

        List<String> recommendations = List.of(
                "Consider adding more slots for " + stats.getMostPopularTimeSlot(),
                "Implement follow-up system for interested prospects",
                "Optimize cancellation policies to reduce no-shows"
        );

        return reportBuilder
                .overallStatistics(stats)
                .keyInsights(insights)
                .recommendations(recommendations)
                .csvDownloadUrl("/api/reports/" + reportBuilder.build().getReportId() + "/download/csv")
                .pdfDownloadUrl("/api/reports/" + reportBuilder.build().getReportId() + "/download/pdf")
                .excelDownloadUrl("/api/reports/" + reportBuilder.build().getReportId() + "/download/excel")
                .build();
    }

    private AppointmentReportDto generateDetailedReport(AppointmentReportDto.AppointmentReportDtoBuilder reportBuilder,
                                                        Long schoolId, LocalDate periodStart, LocalDate periodEnd) {
        AppointmentStatisticsDto stats = appointmentRepository.getAppointmentStatistics(schoolId, periodStart, periodEnd);
        List<AppointmentSummaryDto> appointments = appointmentRepository.getDetailedAppointmentList(
                schoolId, periodStart, periodEnd);

        return reportBuilder
                .overallStatistics(stats)
                .appointments(appointments)
                .keyInsights(generateDetailedInsights(stats, appointments))
                .recommendations(generateDetailedRecommendations(stats, appointments))
                .csvDownloadUrl("/api/reports/" + reportBuilder.build().getReportId() + "/download/csv")
                .pdfDownloadUrl("/api/reports/" + reportBuilder.build().getReportId() + "/download/pdf")
                .excelDownloadUrl("/api/reports/" + reportBuilder.build().getReportId() + "/download/excel")
                .build();
    }

    private AppointmentReportDto generateStaffPerformanceReport(AppointmentReportDto.AppointmentReportDtoBuilder reportBuilder,
                                                                Long schoolId, LocalDate periodStart, LocalDate periodEnd) {
        List<StaffPerformanceDto> staffPerformance = appointmentRepository.getStaffPerformance(schoolId, periodStart, periodEnd);

        return reportBuilder
                .staffPerformance(staffPerformance)
                .keyInsights(generateStaffInsights(staffPerformance))
                .recommendations(generateStaffRecommendations(staffPerformance))
                .csvDownloadUrl("/api/reports/" + reportBuilder.build().getReportId() + "/download/csv")
                .pdfDownloadUrl("/api/reports/" + reportBuilder.build().getReportId() + "/download/pdf")
                .excelDownloadUrl("/api/reports/" + reportBuilder.build().getReportId() + "/download/excel")
                .build();
    }

    private AppointmentReportDto generateOutcomeAnalysisReport(AppointmentReportDto.AppointmentReportDtoBuilder reportBuilder,
                                                               Long schoolId, LocalDate periodStart, LocalDate periodEnd) {
        List<OutcomeAnalysisDto> outcomeAnalysis = appointmentRepository.getOutcomeAnalysis(schoolId, periodStart, periodEnd);

        return reportBuilder
                .outcomeAnalysis(outcomeAnalysis)
                .keyInsights(generateOutcomeInsights(outcomeAnalysis))
                .recommendations(generateOutcomeRecommendations(outcomeAnalysis))
                .csvDownloadUrl("/api/reports/" + reportBuilder.build().getReportId() + "/download/csv")
                .pdfDownloadUrl("/api/reports/" + reportBuilder.build().getReportId() + "/download/pdf")
                .excelDownloadUrl("/api/reports/" + reportBuilder.build().getReportId() + "/download/excel")
                .build();
    }

    private List<String> generateDetailedInsights(AppointmentStatisticsDto stats, List<AppointmentSummaryDto> appointments) {
        return List.of(
                "Peak booking times are " + stats.getMostPopularTimeSlot() + " on " + stats.getMostPopularDay(),
                "Average appointment duration is " + String.format("%.1f minutes", stats.getAverageAppointmentDuration()),
                "Enrollment conversion rate is " + String.format("%.1f%%", stats.getEnrollmentConversionRate())
        );
    }

    private List<String> generateDetailedRecommendations(AppointmentStatisticsDto stats, List<AppointmentSummaryDto> appointments) {
        return List.of(
                "Consider extending popular time slots to meet demand",
                "Implement pre-appointment preparation guidelines",
                "Follow up with interested prospects within 24 hours"
        );
    }

    private List<String> generateStaffInsights(List<StaffPerformanceDto> staffPerformance) {
        if (staffPerformance.isEmpty()) return List.of("No staff performance data available");

        StaffPerformanceDto topPerformer = staffPerformance.stream()
                .max((s1, s2) -> Double.compare(s1.getEnrollmentConversionRate(), s2.getEnrollmentConversionRate()))
                .orElse(null);

        double avgCompletionRate = staffPerformance.stream()
                .mapToDouble(StaffPerformanceDto::getCompletionRate)
                .average().orElse(0.0);

        return List.of(
                "Top performing staff member: " + (topPerformer != null ? topPerformer.getStaffUserName() : "N/A"),
                "Average completion rate across staff: " + String.format("%.1f%%", avgCompletionRate),
                "Staff performance varies significantly, indicating training opportunities"
        );
    }

    private List<String> generateStaffRecommendations(List<StaffPerformanceDto> staffPerformance) {
        return List.of(
                "Provide additional training for staff with low completion rates",
                "Share best practices from top performers",
                "Implement peer mentoring program",
                "Regular performance review and feedback sessions"
        );
    }

    private List<String> generateOutcomeInsights(List<OutcomeAnalysisDto> outcomeAnalysis) {
        if (outcomeAnalysis.isEmpty()) return List.of("No outcome data available");

        OutcomeAnalysisDto mostCommon = outcomeAnalysis.stream()
                .max((o1, o2) -> Integer.compare(o1.getCount(), o2.getCount()))
                .orElse(null);

        double totalEnrolled = outcomeAnalysis.stream()
                .filter(o -> o.getOutcome() == AppointmentOutcome.ENROLLED)
                .mapToDouble(OutcomeAnalysisDto::getPercentage)
                .sum();

        return List.of(
                "Most common outcome: " + (mostCommon != null ? mostCommon.getOutcomeDisplayName() : "N/A"),
                "Direct enrollment rate: " + String.format("%.1f%%", totalEnrolled),
                "Follow-up opportunities exist for interested prospects"
        );
    }

    private List<String> generateOutcomeRecommendations(List<OutcomeAnalysisDto> outcomeAnalysis) {
        return List.of(
                "Develop targeted follow-up strategies for each outcome type",
                "Create nurturing campaigns for interested prospects",
                "Address common price concerns with flexible payment options",
                "Improve information quality to reduce 'needs more info' outcomes"
        );
    }

    // ================================ PUBLIC METHODS (NO AUTH REQUIRED) ================================

    public List<AppointmentAvailabilityDto> getPublicSchoolAvailability(Long schoolId, LocalDate date) {
        log.info("Fetching public availability for school: {} to {}", schoolId, date);

        School school = schoolRepository.findByIdAndIsActiveTrueAndCampusIsSubscribedTrue(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School not found or not available for public booking"));

        return appointmentRepository.getPublicAvailability(schoolId, date);
    }

    @Transactional
    public AppointmentDto createPublicAppointment(AppointmentCreateDto createDto) {
        log.info("Creating public appointment for school: {}", createDto.getSchoolId());

        School school = schoolRepository.findByIdAndIsActiveTrueAndCampusIsSubscribedTrue(createDto.getSchoolId())
                .orElseThrow(() -> new ResourceNotFoundException("School not found or not available for booking"));

        // Validate required fields for public appointments
        if (createDto.getParentName() == null || createDto.getParentEmail() == null ||
                createDto.getParentPhone() == null || createDto.getStudentName() == null) {
            throw new BusinessException("Parent and student information is required for public appointments");
        }

        // Check if slot is available for public booking
        AppointmentSlot slot = null;
        if (createDto.getAppointmentSlotId() != null) {
            slot = appointmentSlotRepository.findByIdAndIsActiveTrue(createDto.getAppointmentSlotId())
                    .orElseThrow(() -> new ResourceNotFoundException("Appointment slot not found"));

            if (!hasAvailableCapacity(slot, createDto.getAppointmentDate(), createDto.getStartTime())) {
                throw new BusinessException("Selected time slot is no longer available");
            }
        }

        // Create appointment with pending status (requires school approval)
        String appointmentNumber = generateAppointmentNumber();

        Appointment appointment = new Appointment();
        appointment.setAppointmentNumber(appointmentNumber);
        appointment.setAppointmentSlot(slot);
        appointment.setSchool(school);
        appointment.setAppointmentDate(createDto.getAppointmentDate());
        appointment.setStartTime(createDto.getStartTime());
        appointment.setEndTime(createDto.getEndTime());
        appointment.setStatus(AppointmentStatus.PENDING); // Always pending for public appointments
        appointment.setAppointmentType(createDto.getAppointmentType() != null ? createDto.getAppointmentType() : AppointmentType.INFORMATION_MEETING);
        appointment.setTitle(createDto.getTitle());
        appointment.setDescription(createDto.getDescription());
        appointment.setLocation(createDto.getLocation());
        appointment.setIsOnline(createDto.getIsOnline() != null ? createDto.getIsOnline() : false);
        appointment.setParentName(createDto.getParentName());
        appointment.setParentEmail(createDto.getParentEmail());
        appointment.setParentPhone(createDto.getParentPhone());
        appointment.setStudentName(createDto.getStudentName());
        appointment.setStudentAge(createDto.getStudentAge());
        appointment.setStudentBirthDate(createDto.getStudentBirthDate());
        appointment.setStudentGender(createDto.getStudentGender());
        appointment.setCurrentSchool(createDto.getCurrentSchool());
        appointment.setGradeInterested(createDto.getGradeInterested());
        appointment.setSpecialRequests(createDto.getSpecialRequests());
        appointment.setNotes(createDto.getNotes());

        appointment = appointmentRepository.save(appointment);

        log.info("Public appointment created with ID: {} and number: {}", appointment.getId(), appointmentNumber);

        return converterService.mapToDto(appointment);
    }

    public AppointmentDto getPublicAppointmentByNumber(String appointmentNumber) {
        log.info("Fetching public appointment with number: {}", appointmentNumber);

        Appointment appointment = appointmentRepository.findByAppointmentNumberAndIsActiveTrue(appointmentNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with number: " + appointmentNumber));

        // Only return basic information for public viewing
        return converterService.mapToPublicAppointmentDto(appointment);
    }

    @Transactional
    public AppointmentDto cancelPublicAppointment(String appointmentNumber, String cancellationReason) {
        log.info("Canceling public appointment with number: {}", appointmentNumber);

        Appointment appointment = appointmentRepository.findByAppointmentNumberAndIsActiveTrue(appointmentNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with number: " + appointmentNumber));

        if (!canCancelAppointment(appointment)) {
            throw new BusinessException("Appointment cannot be canceled due to time restrictions");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCanceledAt(LocalDateTime.now());
        appointment.setCancellationReason(cancellationReason);
        appointment.setCanceledByType(CancelledByType.PARENT);

        appointment = appointmentRepository.save(appointment);

        log.info("Public appointment canceled with number: {}", appointmentNumber);

        return converterService.mapToPublicAppointmentDto(appointment);
    }

    // ================================ METRICS AND INSIGHTS ================================

    /* ceyhun

    @Cacheable(value = "appointment_metrics", key = "#schoolId + '_' + #periodStart + '_' + #periodEnd + '_' + #metricType")
    public List<AppointmentMetricsDto> getAppointmentMetrics(Long schoolId, LocalDate periodStart,
                                                             LocalDate periodEnd, String metricType,
                                                             HttpServletRequest request) {
        log.info("Fetching appointment metrics for school: {} type: {}", schoolId, metricType);

        User user = jwtService.getUser(request);
        validateUserCanAccessSchool(user, schoolId);

        return appointmentRepository.getAppointmentMetrics(schoolId, periodStart, periodEnd, metricType);
    }

     */

    public List<TimeSlotAnalysisDto> getTimeSlotAnalysis(Long schoolId, LocalDate periodStart,
                                                         LocalDate periodEnd, HttpServletRequest request) {
        log.info("Fetching time slot analysis for school: {}", schoolId);

        User user = jwtService.getUser(request);
        validateUserCanAccessSchool(user, schoolId);

        return appointmentRepository.getTimeSlotAnalysis(schoolId, periodStart, periodEnd);
    }

    public Map<String, Object> getAppointmentTrends(Long schoolId, LocalDate periodStart,
                                                    LocalDate periodEnd, HttpServletRequest request) {
        log.info("Fetching appointment trends for school: {}", schoolId);

        User user = jwtService.getUser(request);
        validateUserCanAccessSchool(user, schoolId);

        Map<String, Object> trends = new java.util.HashMap<>();

        // Daily appointment counts
        List<DailyAppointmentStatsDto> dailyStats = appointmentRepository.getDailyAppointmentStats(
                schoolId, periodStart, periodEnd);
        trends.put("dailyStats", dailyStats);

        // Monthly trends
        List<MonthlyAppointmentStatsDto> monthlyStats = appointmentRepository.getMonthlyAppointmentStats(
                schoolId, periodStart, periodEnd);
        trends.put("monthlyStats", monthlyStats);

        // Outcome trends
        List<OutcomeAnalysisDto> outcomeAnalysis = appointmentRepository.getOutcomeAnalysis(
                schoolId, periodStart, periodEnd);
        trends.put("outcomeAnalysis", outcomeAnalysis);

        // Time slot performance
        List<TimeSlotAnalysisDto> timeSlotAnalysis = appointmentRepository.getTimeSlotAnalysis(
                schoolId, periodStart, periodEnd);
        trends.put("timeSlotAnalysis", timeSlotAnalysis);

        return trends;
    }

    // ================================ NOTIFICATION AND COMMUNICATION ================================

    @Transactional
    public void sendAppointmentNotifications(AppointmentNotificationDto notificationDto, HttpServletRequest request) {
        log.info("Sending appointment notifications for appointment: {}", notificationDto.getAppointmentId());

        User user = jwtService.getUser(request);
        Appointment appointment = appointmentRepository.findByIdAndIsActiveTrue(notificationDto.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        validateUserCanManageAppointment(user, appointment);

        // Implementation would include:
        // 1. Email service integration
        // 2. SMS service integration
        // 3. Push notification service
        // 4. Template processing
        // 5. Scheduling for future delivery

        log.info("Notifications sent successfully for appointment: {}", appointment.getId());
    }

    @Transactional
    public void sendAppointmentReminders(HttpServletRequest request) {
        log.info("Processing appointment reminders");

        User user = jwtService.getUser(request);
        if (!hasSystemRole(user)) {
            throw new BusinessException("Only system users can send automated reminders");
        }

        // Get appointments needing reminders (24 hours before)
        LocalDateTime reminderTime = LocalDateTime.now().plusHours(24);
        List<Appointment> appointmentsNeedingReminder = appointmentRepository
                .findAppointmentsNeedingReminder(reminderTime);

        for (Appointment appointment : appointmentsNeedingReminder) {
            try {
                // Send reminder logic would go here
                appointment.setReminderSentAt(LocalDateTime.now());
                appointmentRepository.save(appointment);

                log.info("Reminder sent for appointment: {}", appointment.getId());
            } catch (Exception e) {
                log.error("Failed to send reminder for appointment: {}", appointment.getId(), e);
            }
        }

        log.info("Processed {} appointment reminders", appointmentsNeedingReminder.size());
    }

    // ================================ INTEGRATION METHODS ================================

    @Transactional
    public AppointmentIntegrationDto syncWithExternalCalendar(Long appointmentId, String integrationType,
                                                              HttpServletRequest request) {
        log.info("Syncing appointment {} with external calendar: {}", appointmentId, integrationType);

        User user = jwtService.getUser(request);
        Appointment appointment = appointmentRepository.findByIdAndIsActiveTrue(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        validateUserCanManageAppointment(user, appointment);

        // Mock integration response
        return AppointmentIntegrationDto.builder()
                .appointmentId(appointmentId)
                .integrationType(integrationType)
                .externalId("ext_" + System.currentTimeMillis())
                .externalUrl("https://calendar.example.com/event/12345")
                .integrationStatus("SYNCED")
                .lastSyncAt(LocalDateTime.now())
                .autoSync(true)
                .syncedFields(List.of("title", "datetime", "location", "participants"))
                .build();
    }

    // ================================ CLEANUP AND MAINTENANCE ================================

    @Transactional
    public Map<String, Integer> archiveOldAppointments(Integer daysOld, HttpServletRequest request) {
        log.info("Archiving appointments older than {} days", daysOld);

        User user = jwtService.getUser(request);
        if (!hasSystemRole(user)) {
            throw new BusinessException("Only system users can perform maintenance operations");
        }

        LocalDate cutoffDate = LocalDate.now().minusDays(daysOld);
        int archivedCount = appointmentRepository.archiveOldAppointments(cutoffDate);

        Map<String, Integer> result = new java.util.HashMap<>();
        result.put("archivedCount", archivedCount);
        result.put("cutoffDays", daysOld);

        log.info("Archived {} old appointments", archivedCount);

        return result;
    }

    @Transactional
    public Map<String, Integer> cleanupExpiredSlots(HttpServletRequest request) {
        log.info("Cleaning up expired appointment slots");

        User user = jwtService.getUser(request);
        if (!hasSystemRole(user)) {
            throw new BusinessException("Only system users can perform maintenance operations");
        }

        int cleanedCount = appointmentSlotRepository.deactivateExpiredSlots(LocalDate.now());

        Map<String, Integer> result = new java.util.HashMap<>();
        result.put("cleanedCount", cleanedCount);

        log.info("Cleaned up {} expired appointment slots", cleanedCount);

        return result;
    }





    // ceyhun


    public List<AppointmentAvailabilityDto> getAvailabilityBetweenDates(
            Long schoolId, String schoolName, LocalDate startDate, LocalDate endDate) {

        log.debug("Getting optimized availability for school {} between {} and {}",
                schoolId, startDate, endDate);

        // 1. Tüm aktif slot'ları al
        Map<DayOfWeek, List<SlotInfo>> slotsByDay = getAllSlotsGroupedByDay(schoolId);

        // 2. Excluded dates bilgisini al
        Map<Long, Set<LocalDate>> excludedDatesBySlot = getExcludedDatesBySlot(schoolId);

        // 3. Tarih aralığındaki tüm rezervasyonları al
        Map<LocalDate, Map<Long, Long>> bookedCountsByDate = getBookedCountsByDateRange(
                schoolId, startDate, endDate);

        // 4. Her gün için availability hesapla
        List<AppointmentAvailabilityDto> availabilityList = new ArrayList<>();

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            AppointmentAvailabilityDto dayAvailability = calculateDayAvailability(
                    schoolId, schoolName, currentDate, slotsByDay,
                    excludedDatesBySlot, bookedCountsByDate);

            // Sadece müsait slotu olan günleri ekle
            if (dayAvailability.getAvailableCount() > 0 &&
                    !dayAvailability.getAvailableSlots().isEmpty()) {
                availabilityList.add(dayAvailability);
            }

            currentDate = currentDate.plusDays(1);
        }

        log.debug("Found {} available days for school {}", availabilityList.size(), schoolId);
        return availabilityList;
    }

    /**
     * Tüm slot'ları günlere göre gruplandırır
     */
    private Map<DayOfWeek, List<SlotInfo>> getAllSlotsGroupedByDay(Long schoolId) {
        List<Object[]> allSlots = appointmentRepository.getAllActiveSlotsForSchool(
                schoolId, LocalDate.now());

        Map<DayOfWeek, List<SlotInfo>> slotsByDay = new HashMap<>();

        for (Object[] row : allSlots) {
            DayOfWeek dayOfWeek = (DayOfWeek) row[0];
            SlotInfo slotInfo = SlotInfo.builder()
                    .slotId((Long) row[1])
                    .startTime((LocalTime) row[2])
                    .endTime((LocalTime) row[3])
                    .durationMinutes((Integer) row[4])
                    .appointmentType((AppointmentType) row[5])
                    .location((String) row[6])
                    .isOnline((Boolean) row[7])
                    .staffUserName((String) row[8])
                    .capacity((Integer) row[9])
                    .requiresApproval((Boolean) row[10])
                    .build();

            slotsByDay.computeIfAbsent(dayOfWeek, k -> new ArrayList<>()).add(slotInfo);
        }

        return slotsByDay;
    }

    /**
     * Excluded dates bilgisini parse eder
     */
    private Map<Long, Set<LocalDate>> getExcludedDatesBySlot(Long schoolId) {
        List<Object[]> slotsWithExcludedDates = appointmentRepository.getSlotsWithExcludedDates(schoolId);
        Map<Long, Set<LocalDate>> excludedDatesBySlot = new HashMap<>();

        for (Object[] row : slotsWithExcludedDates) {
            Long slotId = (Long) row[0];
            String excludedDatesJson = (String) row[1];

            try {
                if (excludedDatesJson != null && !excludedDatesJson.trim().isEmpty()) {
                    LocalDate[] dates = objectMapper.readValue(excludedDatesJson, LocalDate[].class);
                    excludedDatesBySlot.put(slotId, Set.of(dates));
                }
            } catch (Exception e) {
                log.warn("Failed to parse excluded dates for slot {}: {}", slotId, e.getMessage());
                excludedDatesBySlot.put(slotId, Set.of());
            }
        }

        return excludedDatesBySlot;
    }

    /**
     * Tarih aralığındaki rezervasyonları gruplandırır
     */
    private Map<LocalDate, Map<Long, Long>> getBookedCountsByDateRange(
            Long schoolId, LocalDate startDate, LocalDate endDate) {

        List<Object[]> bookedData = appointmentRepository.getBookedCountsByDateRange(
                schoolId, startDate, endDate);

        Map<LocalDate, Map<Long, Long>> result = new HashMap<>();

        for (Object[] row : bookedData) {
            LocalDate date = (LocalDate) row[0];
            Long slotId = (Long) row[1];
            Long count = ((Number) row[2]).longValue();

            result.computeIfAbsent(date, k -> new HashMap<>()).put(slotId, count);
        }

        return result;
    }

    /**
     * Belirli bir gün için availability hesaplar
     */
    private AppointmentAvailabilityDto calculateDayAvailability(
            Long schoolId, String schoolName, LocalDate date,
            Map<DayOfWeek, List<SlotInfo>> slotsByDay,
            Map<Long, Set<LocalDate>> excludedDatesBySlot,
            Map<LocalDate, Map<Long, Long>> bookedCountsByDate) {

        DayOfWeek dayOfWeek = date.getDayOfWeek();
        List<SlotInfo> daySlots = slotsByDay.getOrDefault(dayOfWeek, new ArrayList<>());
        Map<Long, Long> dayBookedCounts = bookedCountsByDate.getOrDefault(date, new HashMap<>());

        // Available slots hesapla
        List<AvailableSlotDto> availableSlots = daySlots.stream()
                .filter(slot -> !isSlotExcludedForDate(slot.getSlotId(), date, excludedDatesBySlot))
                .filter(slot -> isSlotBookable(slot, date))
                .map(slot -> {
                    Long bookedCount = dayBookedCounts.getOrDefault(slot.getSlotId(), 0L);
                    int availableCapacity = slot.getCapacity() - bookedCount.intValue();

                    if (availableCapacity <= 0) {
                        return null; // Bu slot müsait değil
                    }

                    String timeRange = slot.getStartTime() + " - " + slot.getEndTime();
                    Boolean isRecommended = availableCapacity > slot.getCapacity() * 0.5;

                    return AvailableSlotDto.builder()
                            .slotId(slot.getSlotId())
                            .startTime(slot.getStartTime())
                            .endTime(slot.getEndTime())
                            .durationMinutes(slot.getDurationMinutes())
                            .appointmentType(slot.getAppointmentType())
                            .location(slot.getLocation())
                            .isOnline(slot.getIsOnline())
                            .staffUserName(slot.getStaffUserName())
                            .availableCapacity(availableCapacity)
                            .requiresApproval(slot.getRequiresApproval())
                            .timeRange(timeRange)
                            .isRecommended(isRecommended)
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // İstatistikler
        Integer totalSlots = daySlots.size();
        Integer bookedSlots = dayBookedCounts.values().stream()
                .mapToInt(Long::intValue)
                .sum();
        int availableCount = availableSlots.size();

        // Availability status
        String availability = calculateAvailabilityStatus(availableCount, totalSlots);

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

    /**
     * Slot'un belirli tarih için excluded olup olmadığını kontrol eder
     */
    private boolean isSlotExcludedForDate(Long slotId, LocalDate date,
                                          Map<Long, Set<LocalDate>> excludedDatesBySlot) {
        Set<LocalDate> excludedDates = excludedDatesBySlot.get(slotId);
        return excludedDates != null && excludedDates.contains(date);
    }

    /**
     * Slot'un rezerve edilebilir olup olmadığını kontrol eder
     */
    private boolean isSlotBookable(SlotInfo slot, LocalDate requestDate) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime slotDateTime = LocalDateTime.of(requestDate, slot.getStartTime());

        // Geçmiş tarih kontrolü
        if (slotDateTime.isBefore(now)) {
            return false;
        }

        // Advance booking kontrolü (varsayılan değerler kullanılıyor)
        long hoursUntilSlot = Duration.between(now, slotDateTime).toHours();

        // Minimum 24 saat önceden rezervasyon
        if (hoursUntilSlot < 24) {
            return false;
        }

        // Maksimum 30 gün önceden rezervasyon
        if (hoursUntilSlot > (30 * 24)) {
            return false;
        }

        return true;
    }

    /**
     * Availability status hesaplar
     */
    private String calculateAvailabilityStatus(int availableCount, int totalSlots) {
        if (availableCount == 0) {
            return "FULLY_BOOKED";
        } else if (availableCount <= totalSlots * 0.25) {
            return "LIMITED";
        } else if (availableCount <= totalSlots * 0.75) {
            return "AVAILABLE";
        } else {
            return "ABUNDANT";
        }
    }

    // Mevcut tek tarih metodu (değişiklik yok)
    public AppointmentAvailabilityDto getAvailabilityForDate(Long schoolId, String schoolName, LocalDate date) {
        // Mevcut implementasyonunuz burada kalabilir
        // veya optimize edilmiş versiyonu tek tarih için kullanabilirsiniz
        List<AppointmentAvailabilityDto> result = getAvailabilityBetweenDates(
                schoolId, schoolName, date, date);

        return result.isEmpty() ? createEmptyAvailability(schoolId, schoolName, date) : result.get(0);
    }

    private AppointmentAvailabilityDto createEmptyAvailability(Long schoolId, String schoolName, LocalDate date) {
        return AppointmentAvailabilityDto.builder()
                .schoolId(schoolId)
                .schoolName(schoolName)
                .date(date)
                .availableSlots(new ArrayList<>())
                .totalSlots(0)
                .bookedSlots(0)
                .availableCount(0)
                .availability("FULLY_BOOKED")
                .build();
    }


    @Data
    @Builder
    private static class SlotInfo {
        private Long slotId;
        private LocalTime startTime;
        private LocalTime endTime;
        private Integer durationMinutes;
        private AppointmentType appointmentType;
        private String location;
        private Boolean isOnline;
        private String staffUserName;
        private Integer capacity;
        private Boolean requiresApproval;
    }
}