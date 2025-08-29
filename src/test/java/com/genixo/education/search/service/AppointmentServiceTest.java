package com.genixo.education.search.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.genixo.education.search.common.exception.BusinessException;
import com.genixo.education.search.common.exception.ResourceNotFoundException;
import com.genixo.education.search.dto.appointment.*;
import com.genixo.education.search.entity.appointment.*;
import com.genixo.education.search.entity.institution.School;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.entity.user.UserRole;
import com.genixo.education.search.enumaration.*;
import com.genixo.education.search.repository.appointment.*;
import com.genixo.education.search.repository.insitution.SchoolRepository;
import com.genixo.education.search.repository.user.UserRepository;
import com.genixo.education.search.service.auth.JwtService;
import com.genixo.education.search.service.converter.AppointmentConverterService;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.*;
import java.time.DayOfWeek;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppointmentService Tests")
class AppointmentServiceTest {

    @Mock private AppointmentRepository appointmentRepository;
    @Mock private AppointmentSlotRepository appointmentSlotRepository;
    @Mock private AppointmentNoteRepository appointmentNoteRepository;
    @Mock private SchoolRepository schoolRepository;
    @Mock private UserRepository userRepository;
    @Mock private AppointmentConverterService converterService;
    @Mock private JwtService jwtService;
    @Mock private ObjectMapper objectMapper;
    @Mock private HttpServletRequest request;

    @InjectMocks
    private AppointmentService appointmentService;

    private User systemUser;
    private User regularUser;
    private User parentUser;
    private User staffUser;
    private School validSchool;
    private AppointmentSlot validSlot;
    private Appointment validAppointment;

    // DTOs
    private AppointmentSlotCreateDto validSlotCreateDto;
    private AppointmentCreateDto validAppointmentCreateDto;
    private AppointmentSlotDto expectedSlotDto;
    private AppointmentDto expectedAppointmentDto;

    @BeforeEach
    void setUp() {
        // Users
        systemUser = createUser(1L, RoleLevel.SYSTEM);
        regularUser = createUser(2L, RoleLevel.BRAND);
        parentUser = createUser(3L, RoleLevel.BRAND);
        staffUser = createUser(4L, RoleLevel.SCHOOL);

        // School
        validSchool = new School();
        validSchool.setId(1L);
        validSchool.setName("Test School");
        validSchool.setIsActive(true);

        // Appointment Slot
        validSlot = new AppointmentSlot();
        validSlot.setId(1L);
        validSlot.setSchool(validSchool);
        validSlot.setStaffUser(staffUser);
        validSlot.setDayOfWeek(DayOfWeek.MONDAY);
        validSlot.setStartTime(LocalTime.of(9, 0));
        validSlot.setEndTime(LocalTime.of(10, 0));
        validSlot.setCapacity(1);
        validSlot.setAppointmentType(AppointmentType.INFORMATION_MEETING);
        validSlot.setIsActive(true);
        validSlot.setAdvanceBookingHours(24);
        validSlot.setCancellationHours(4);
        validSlot.setRequiresApproval(false);

        // Appointment
        validAppointment = new Appointment();
        validAppointment.setId(1L);
        validAppointment.setAppointmentNumber("APT12345678");
        validAppointment.setAppointmentSlot(validSlot);
        validAppointment.setSchool(validSchool);
        validAppointment.setParentUser(parentUser);
        validAppointment.setStaffUser(staffUser);
        validAppointment.setAppointmentDate(LocalDate.now().plusDays(2));
        validAppointment.setStartTime(LocalTime.of(9, 0));
        validAppointment.setEndTime(LocalTime.of(10, 0));
        validAppointment.setStatus(AppointmentStatus.CONFIRMED);
        validAppointment.setAppointmentType(AppointmentType.INFORMATION_MEETING);
        validAppointment.setParentName("John Doe");
        validAppointment.setParentEmail("john@example.com");
        validAppointment.setParentPhone("+90 555 123 4567");
        validAppointment.setStudentName("Jane Doe");
        validAppointment.setStudentAge(8);
        validAppointment.setIsActive(true);

        // Create DTOs
        validSlotCreateDto = AppointmentSlotCreateDto.builder()
                .schoolId(1L)
                .staffUserId(4L)
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(10, 0))
                .durationMinutes(60)
                .capacity(1)
                .appointmentType(AppointmentType.INFORMATION_MEETING)
                .title("Information Meeting")
                .description("School information meeting")
                .location("Main Office")
                .isRecurring(true)
                .advanceBookingHours(24)
                .cancellationHours(4)
                .requiresApproval(false)
                .build();

        validAppointmentCreateDto = AppointmentCreateDto.builder()
                .appointmentSlotId(1L)
                .schoolId(1L)
                .parentUserId(3L)
                .appointmentDate(LocalDate.now().plusDays(2))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(10, 0))
                .appointmentType(AppointmentType.INFORMATION_MEETING)
                .title("Information Meeting")
                .description("Meeting to discuss school programs")
                .location("Main Office")
                .isOnline(false)
                .parentName("John Doe")
                .parentEmail("john@example.com")
                .parentPhone("+90 555 123 4567")
                .studentName("Jane Doe")
                .studentAge(8)
                .currentSchool("Current School")
                .gradeInterested("Grade 3")
                .specialRequests("No special requests")
                .notes("Looking forward to the meeting")
                .build();

        // Expected DTOs
        expectedSlotDto = AppointmentSlotDto.builder()
                .id(1L)
                .schoolId(1L)
                .schoolName("Test School")
                .staffUserId(4L)
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(10, 0))
                .capacity(1)
                .build();

        expectedAppointmentDto = AppointmentDto.builder()
                .id(1L)
                .appointmentNumber("APT12345678")
                .schoolId(1L)
                .schoolName("Test School")
                .parentName("John Doe")
                .studentName("Jane Doe")
                .status(AppointmentStatus.CONFIRMED)
                .build();
    }

    // ================================ APPOINTMENT SLOT TESTS ================================

    @Nested
    @DisplayName("createAppointmentSlot() Tests")
    class CreateAppointmentSlotTests {

        @Test
        @DisplayName("Should create appointment slot successfully with valid data")
        void shouldCreateAppointmentSlotSuccessfully() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validSchool));
            when(userRepository.findByIdAndIsActiveTrue(4L)).thenReturn(Optional.of(staffUser));
            when(appointmentSlotRepository.existsOverlappingSlot(eq(1L), eq(DayOfWeek.MONDAY),
                    any(LocalTime.class), any(LocalTime.class), eq(4L))).thenReturn(false);
            when(appointmentSlotRepository.save(any(AppointmentSlot.class))).thenReturn(validSlot);
            when(converterService.mapToDto(validSlot)).thenReturn(expectedSlotDto);

            // When
            AppointmentSlotDto result = appointmentService.createAppointmentSlot(validSlotCreateDto, request);

            // Then
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals(DayOfWeek.MONDAY, result.getDayOfWeek());

            verify(jwtService).getUser(request);
            verify(schoolRepository).findByIdAndIsActiveTrue(1L);
            verify(userRepository).findByIdAndIsActiveTrue(4L);
            verify(appointmentSlotRepository).save(argThat(slot ->
                    slot.getSchool().getId().equals(1L) &&
                            slot.getStaffUser().getId().equals(4L) &&
                            slot.getDayOfWeek().equals(DayOfWeek.MONDAY) &&
                            slot.getStartTime().equals(LocalTime.of(9, 0)) &&
                            slot.getCapacity().equals(1)
            ));
        }

        @Test
        @DisplayName("Should throw BusinessException when user cannot manage school appointments")
        void shouldThrowExceptionWhenUserCannotManageAppointments() {
            // Given
            when(jwtService.getUser(request)).thenReturn(regularUser);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> appointmentService.createAppointmentSlot(validSlotCreateDto, request));

            assertEquals("User does not have permission to manage appointments for this school", exception.getMessage());

            verify(jwtService).getUser(request);
            verifyNoInteractions(schoolRepository, appointmentSlotRepository);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when school not found")
        void shouldThrowExceptionWhenSchoolNotFound() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.empty());

            // When & Then
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> appointmentService.createAppointmentSlot(validSlotCreateDto, request));

            assertEquals("School not found with ID: 1", exception.getMessage());

            verify(schoolRepository).findByIdAndIsActiveTrue(1L);
            verifyNoInteractions(appointmentSlotRepository);
        }

        @Test
        @DisplayName("Should throw BusinessException when overlapping slot exists")
        void shouldThrowExceptionWhenOverlappingSlotExists() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validSchool));
            when(userRepository.findByIdAndIsActiveTrue(4L)).thenReturn(Optional.of(staffUser));
            when(appointmentSlotRepository.existsOverlappingSlot(eq(1L), eq(DayOfWeek.MONDAY),
                    any(LocalTime.class), any(LocalTime.class), eq(4L))).thenReturn(true);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> appointmentService.createAppointmentSlot(validSlotCreateDto, request));

            assertEquals("Overlapping appointment slot exists for the same time period", exception.getMessage());

            verify(appointmentSlotRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should validate time slot properly")
        void shouldValidateTimeSlotProperly() {
            // Given - Invalid time slot (start after end)
            AppointmentSlotCreateDto invalidTimeDto = AppointmentSlotCreateDto.builder()
                    .schoolId(1L)
                    .dayOfWeek(DayOfWeek.MONDAY)
                    .startTime(LocalTime.of(10, 0))
                    .endTime(LocalTime.of(9, 0)) // End before start
                    .durationMinutes(60)
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validSchool));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> appointmentService.createAppointmentSlot(invalidTimeDto, request));

            assertEquals("Start time cannot be after end time", exception.getMessage());
        }

        @Test
        @DisplayName("Should create slot without staff user")
        void shouldCreateSlotWithoutStaffUser() {
            // Given
            AppointmentSlotCreateDto noStaffDto = AppointmentSlotCreateDto.builder()
                    .schoolId(1L)
                    .staffUserId(null) // No staff assigned
                    .dayOfWeek(DayOfWeek.MONDAY)
                    .startTime(LocalTime.of(9, 0))
                    .endTime(LocalTime.of(10, 0))
                    .durationMinutes(60)
                    .capacity(1)
                    .appointmentType(AppointmentType.INFORMATION_MEETING)
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validSchool));
            when(appointmentSlotRepository.existsOverlappingSlot(eq(1L), eq(DayOfWeek.MONDAY),
                    any(LocalTime.class), any(LocalTime.class), isNull())).thenReturn(false);
            when(appointmentSlotRepository.save(any(AppointmentSlot.class))).thenReturn(validSlot);
            when(converterService.mapToDto(any(AppointmentSlot.class))).thenReturn(expectedSlotDto);

            // When
            AppointmentSlotDto result = appointmentService.createAppointmentSlot(noStaffDto, request);

            // Then
            assertNotNull(result);
            verify(appointmentSlotRepository).save(argThat(slot -> slot.getStaffUser() == null));
        }
    }

    @Nested
    @DisplayName("getAppointmentSlotById() Tests")
    class GetAppointmentSlotByIdTests {

        @Test
        @DisplayName("Should return appointment slot successfully when user has access")
        void shouldReturnAppointmentSlotSuccessfully() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(appointmentSlotRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validSlot));
            when(converterService.mapToDto(validSlot)).thenReturn(expectedSlotDto);

            // When
            AppointmentSlotDto result = appointmentService.getAppointmentSlotById(1L, request);

            // Then
            assertNotNull(result);
            assertEquals(1L, result.getId());

            verify(jwtService).getUser(request);
            verify(appointmentSlotRepository).findByIdAndIsActiveTrue(1L);
            verify(converterService).mapToDto(validSlot);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when slot not found")
        void shouldThrowExceptionWhenSlotNotFound() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(appointmentSlotRepository.findByIdAndIsActiveTrue(999L)).thenReturn(Optional.empty());

            // When & Then
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> appointmentService.getAppointmentSlotById(999L, request));

            assertEquals("Appointment slot not found with ID: 999", exception.getMessage());

            verifyNoInteractions(converterService);
        }

        @Test
        @DisplayName("Should throw BusinessException when user has no access to school")
        void shouldThrowExceptionWhenUserHasNoAccess() {
            // Given
            when(jwtService.getUser(request)).thenReturn(regularUser);
            when(appointmentSlotRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validSlot));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> appointmentService.getAppointmentSlotById(1L, request));

            assertEquals("User does not have access to this school", exception.getMessage());

            verifyNoInteractions(converterService);
        }
    }

    // ================================ APPOINTMENT TESTS ================================

    @Nested
    @DisplayName("createAppointment() Tests")
    class CreateAppointmentTests {

        @Test
        @DisplayName("Should create appointment successfully with valid data")
        void shouldCreateAppointmentSuccessfully() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validSchool));
            when(appointmentSlotRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validSlot));
            when(userRepository.findByIdAndIsActiveTrue(3L)).thenReturn(Optional.of(parentUser));
            when(appointmentRepository.countBookedAppointments(eq(1L), any(LocalDate.class), any(LocalTime.class)))
                    .thenReturn(0L);
            when(appointmentRepository.save(any(Appointment.class))).thenReturn(validAppointment);
            when(converterService.mapToDto(validAppointment)).thenReturn(expectedAppointmentDto);

            // When
            AppointmentDto result = appointmentService.createAppointment(validAppointmentCreateDto, request);

            // Then
            assertNotNull(result);
            assertEquals("APT12345678", result.getAppointmentNumber());
            assertEquals(AppointmentStatus.CONFIRMED, result.getStatus());

            verify(appointmentRepository).save(argThat(appointment ->
                    appointment.getSchool().getId().equals(1L) &&
                            appointment.getParentUser().getId().equals(3L) &&
                            appointment.getAppointmentSlot().getId().equals(1L) &&
                            appointment.getStatus() == AppointmentStatus.CONFIRMED &&
                            appointment.getParentName().equals("John Doe") &&
                            appointment.getStudentName().equals("Jane Doe")
            ));
        }

        @Test
        @DisplayName("Should create appointment with pending status when approval required")
        void shouldCreateAppointmentWithPendingStatusWhenApprovalRequired() {
            // Given
            AppointmentSlot approvalSlot = new AppointmentSlot();
            approvalSlot.setId(1L);
            approvalSlot.setSchool(validSchool);
            approvalSlot.setRequiresApproval(true); // Requires approval

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validSchool));
            when(appointmentSlotRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(approvalSlot));
            when(userRepository.findByIdAndIsActiveTrue(3L)).thenReturn(Optional.of(parentUser));
            when(appointmentRepository.countBookedAppointments(any(), any(), any())).thenReturn(0L);
            when(appointmentRepository.save(any(Appointment.class))).thenReturn(validAppointment);
            when(converterService.mapToDto(any(Appointment.class))).thenReturn(expectedAppointmentDto);

            // When
            appointmentService.createAppointment(validAppointmentCreateDto, request);

            // Then
            verify(appointmentRepository).save(argThat(appointment ->
                    appointment.getStatus() == AppointmentStatus.PENDING &&
                            appointment.getConfirmedAt() == null
            ));
        }

        @Test
        @DisplayName("Should throw BusinessException when slot belongs to different school")
        void shouldThrowExceptionWhenSlotBelongsToDifferentSchool() {
            // Given
            School differentSchool = new School();
            differentSchool.setId(2L);
            differentSchool.setName("Different School");

            AppointmentSlot differentSlot = new AppointmentSlot();
            differentSlot.setId(1L);
            differentSlot.setSchool(differentSchool); // Different school

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validSchool));
            when(appointmentSlotRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(differentSlot));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> appointmentService.createAppointment(validAppointmentCreateDto, request));

            assertEquals("Appointment slot does not belong to the specified school", exception.getMessage());

            verify(appointmentRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw BusinessException when no capacity available")
        void shouldThrowExceptionWhenNoCapacityAvailable() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validSchool));
            when(appointmentSlotRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validSlot));
            when(userRepository.findByIdAndIsActiveTrue(3L)).thenReturn(Optional.of(parentUser));
            when(appointmentRepository.countBookedAppointments(eq(1L), any(LocalDate.class), any(LocalTime.class)))
                    .thenReturn(1L); // Capacity full

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> appointmentService.createAppointment(validAppointmentCreateDto, request));

            assertEquals("No available capacity for the selected time slot", exception.getMessage());

            verify(appointmentRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should create appointment without slot")
        void shouldCreateAppointmentWithoutSlot() {
            // Given
            AppointmentCreateDto noSlotDto = AppointmentCreateDto.builder()
                    .appointmentSlotId(null) // No slot
                    .schoolId(1L)
                    .appointmentDate(LocalDate.now().plusDays(2))
                    .startTime(LocalTime.of(9, 0))
                    .endTime(LocalTime.of(10, 0))
                    .parentName("John Doe")
                    .parentEmail("john@example.com")
                    .parentPhone("+90 555 123 4567")
                    .studentName("Jane Doe")
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validSchool));
            when(appointmentRepository.save(any(Appointment.class))).thenReturn(validAppointment);
            when(converterService.mapToDto(any(Appointment.class))).thenReturn(expectedAppointmentDto);

            // When
            AppointmentDto result = appointmentService.createAppointment(noSlotDto, request);

            // Then
            assertNotNull(result);
            verify(appointmentRepository).save(argThat(appointment ->
                    appointment.getAppointmentSlot() == null &&
                            appointment.getStatus() == AppointmentStatus.CONFIRMED
            ));
        }

        @Test
        @DisplayName("Should validate appointment time in past")
        void shouldValidateAppointmentTimeInPast() {
            // Given
            AppointmentCreateDto pastTimeDto = AppointmentCreateDto.builder()
                    .schoolId(1L)
                    .appointmentDate(LocalDate.now().minusDays(1)) // Past date
                    .startTime(LocalTime.of(9, 0))
                    .endTime(LocalTime.of(10, 0))
                    .parentName("John Doe")
                    .studentName("Jane Doe")
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validSchool));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> appointmentService.createAppointment(pastTimeDto, request));

            assertEquals("Cannot book appointment in the past", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("getAppointmentById() Tests")
    class GetAppointmentByIdTests {

        @Test
        @DisplayName("Should return appointment when user has access")
        void shouldReturnAppointmentWhenUserHasAccess() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(appointmentRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validAppointment));
            when(converterService.mapToDto(validAppointment)).thenReturn(expectedAppointmentDto);

            // When
            AppointmentDto result = appointmentService.getAppointmentById(1L, request);

            // Then
            assertNotNull(result);
            assertEquals(1L, result.getId());

            verify(appointmentRepository).findByIdAndIsActiveTrue(1L);
            verify(converterService).mapToDto(validAppointment);
        }

        @Test
        @DisplayName("Should allow parent to access their own appointment")
        void shouldAllowParentToAccessOwnAppointment() {
            // Given
            when(jwtService.getUser(request)).thenReturn(parentUser);
            when(appointmentRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validAppointment));
            when(converterService.mapToDto(validAppointment)).thenReturn(expectedAppointmentDto);

            // When
            AppointmentDto result = appointmentService.getAppointmentById(1L, request);

            // Then
            assertNotNull(result);
            verify(converterService).mapToDto(validAppointment);
        }

        @Test
        @DisplayName("Should allow assigned staff to access appointment")
        void shouldAllowAssignedStaffToAccessAppointment() {
            // Given
            when(jwtService.getUser(request)).thenReturn(staffUser);
            when(appointmentRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validAppointment));
            when(converterService.mapToDto(validAppointment)).thenReturn(expectedAppointmentDto);

            // When
            AppointmentDto result = appointmentService.getAppointmentById(1L, request);

            // Then
            assertNotNull(result);
            verify(converterService).mapToDto(validAppointment);
        }

        @Test
        @DisplayName("Should throw exception when user has no access")
        void shouldThrowExceptionWhenUserHasNoAccess() {
            // Given
            User unauthorizedUser = createUser(99L, RoleLevel.BRAND);
            when(jwtService.getUser(request)).thenReturn(unauthorizedUser);
            when(appointmentRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validAppointment));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> appointmentService.getAppointmentById(1L, request));

            assertEquals("User does not have access to this appointment", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("cancelAppointment() Tests")
    class CancelAppointmentTests {

        @Test
        @DisplayName("Should cancel appointment successfully")
        void shouldCancelAppointmentSuccessfully() {
            // Given
            AppointmentCancelDto cancelDto = AppointmentCancelDto.builder()
                    .appointmentId(1L)
                    .cancellationReason("Last minute change")
                    .build();

            when(jwtService.getUser(request)).thenReturn(parentUser);
            when(appointmentRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validAppointment));
            //when(appointmentRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(soonAppointment));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> appointmentService.cancelAppointment(cancelDto, request));

            assertEquals("Appointment cannot be canceled due to time restrictions", exception.getMessage());

            verify(appointmentRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when appointment is not in cancellable status")
        void shouldThrowExceptionWhenNotCancellableStatus() {
            // Given
            Appointment completedAppointment = new Appointment();
            completedAppointment.setId(1L);
            completedAppointment.setSchool(validSchool);
            completedAppointment.setParentUser(parentUser);
            completedAppointment.setStatus(AppointmentStatus.COMPLETED); // Cannot cancel completed

            AppointmentCancelDto cancelDto = AppointmentCancelDto.builder()
                    .appointmentId(1L)
                    .cancellationReason("Change of mind")
                    .build();

            when(jwtService.getUser(request)).thenReturn(parentUser);
            when(appointmentRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(completedAppointment));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> appointmentService.cancelAppointment(cancelDto, request));

            assertEquals("Appointment cannot be canceled due to time restrictions", exception.getMessage());
        }

        @Test
        @DisplayName("Should allow staff to cancel appointment")
        void shouldAllowStaffToCancelAppointment() {
            // Given
            AppointmentCancelDto cancelDto = AppointmentCancelDto.builder()
                    .appointmentId(1L)
                    .cancellationReason("Staff unavailable")
                    .canceledByType(CancelledByType.SCHOOL)
                    .build();

            when(jwtService.getUser(request)).thenReturn(staffUser);
            when(appointmentRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validAppointment));
            when(appointmentRepository.save(any(Appointment.class))).thenReturn(validAppointment);
            when(converterService.mapToDto(any(Appointment.class))).thenReturn(expectedAppointmentDto);

            // When
            AppointmentDto result = appointmentService.cancelAppointment(cancelDto, request);

            // Then
            assertNotNull(result);
            verify(appointmentRepository).save(argThat(appointment ->
                    appointment.getCanceledByType() == CancelledByType.SCHOOL &&
                            appointment.getCanceledBy().equals(4L)
            ));
        }
    }

    @Nested
    @DisplayName("rescheduleAppointment() Tests")
    class RescheduleAppointmentTests {

        @Test
        @DisplayName("Should reschedule appointment successfully")
        void shouldRescheduleAppointmentSuccessfully() {
            // Given
            AppointmentSlot newSlot = new AppointmentSlot();
            newSlot.setId(2L);
            newSlot.setSchool(validSchool);
            newSlot.setCapacity(1);

            Appointment newAppointment = new Appointment();
            newAppointment.setId(2L);
            newAppointment.setAppointmentNumber("APT87654321");
            newAppointment.setStatus(AppointmentStatus.CONFIRMED);

            AppointmentRescheduleDto rescheduleDto = AppointmentRescheduleDto.builder()
                    .appointmentId(1L)
                    .newAppointmentSlotId(2L)
                    .newAppointmentDate(LocalDate.now().plusDays(3))
                    .newStartTime(LocalTime.of(14, 0))
                    .newEndTime(LocalTime.of(15, 0))
                    .rescheduleReason("Better timing")
                    .build();

            when(jwtService.getUser(request)).thenReturn(parentUser);
            when(appointmentRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validAppointment));
            when(appointmentSlotRepository.findByIdAndIsActiveTrue(2L)).thenReturn(Optional.of(newSlot));
            when(appointmentRepository.countBookedAppointments(eq(2L), any(LocalDate.class), any(LocalTime.class)))
                    .thenReturn(0L);
            when(appointmentRepository.save(any(Appointment.class)))
                    .thenReturn(validAppointment) // First save (original)
                    .thenReturn(newAppointment); // Second save (new)
            when(converterService.mapToDto(newAppointment)).thenReturn(expectedAppointmentDto);

            // When
            AppointmentDto result = appointmentService.rescheduleAppointment(rescheduleDto, request);

            // Then
            assertNotNull(result);

            // Verify original appointment marked as rescheduled
            verify(appointmentRepository, times(2)).save(any(Appointment.class));

            // Verify new appointment created
            verify(appointmentRepository).save(argThat(appointment ->
                    appointment.getRescheduledFromId() != null &&
                            appointment.getRescheduleCount() == 1
            ));
        }

        @Test
        @DisplayName("Should throw exception when appointment cannot be rescheduled")
        void shouldThrowExceptionWhenCannotReschedule() {
            // Given
            Appointment maxRescheduledAppointment = new Appointment();
            maxRescheduledAppointment.setId(1L);
            maxRescheduledAppointment.setSchool(validSchool);
            maxRescheduledAppointment.setParentUser(parentUser);
            maxRescheduledAppointment.setStatus(AppointmentStatus.CONFIRMED);
            maxRescheduledAppointment.setRescheduleCount(3); // Max reschedules reached

            AppointmentRescheduleDto rescheduleDto = AppointmentRescheduleDto.builder()
                    .appointmentId(1L)
                    .newAppointmentDate(LocalDate.now().plusDays(3))
                    .build();

            when(jwtService.getUser(request)).thenReturn(parentUser);
            when(appointmentRepository.findByIdAndIsActiveTrue(1L))
                    .thenReturn(Optional.of(maxRescheduledAppointment));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> appointmentService.rescheduleAppointment(rescheduleDto, request));

            assertEquals("Appointment cannot be rescheduled due to time restrictions or current status",
                    exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when new slot has no capacity")
        void shouldThrowExceptionWhenNewSlotHasNoCapacity() {
            // Given
            AppointmentSlot fullSlot = new AppointmentSlot();
            fullSlot.setId(2L);
            fullSlot.setSchool(validSchool);
            fullSlot.setCapacity(1);

            AppointmentRescheduleDto rescheduleDto = AppointmentRescheduleDto.builder()
                    .appointmentId(1L)
                    .newAppointmentSlotId(2L)
                    .newAppointmentDate(LocalDate.now().plusDays(3))
                    .newStartTime(LocalTime.of(14, 0))
                    .newEndTime(LocalTime.of(15, 0))
                    .build();

            when(jwtService.getUser(request)).thenReturn(parentUser);
            when(appointmentRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validAppointment));
            when(appointmentSlotRepository.findByIdAndIsActiveTrue(2L)).thenReturn(Optional.of(fullSlot));
            when(appointmentRepository.countBookedAppointments(eq(2L), any(LocalDate.class), any(LocalTime.class)))
                    .thenReturn(1L); // Full capacity

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> appointmentService.rescheduleAppointment(rescheduleDto, request));

            assertEquals("No available capacity for the selected new time slot", exception.getMessage());
        }
    }

    // ================================ SEARCH AND FILTERING TESTS ================================

    @Nested
    @DisplayName("searchAppointments() Tests")
    class SearchAppointmentsTests {

        @Test
        @DisplayName("Should search appointments with all filters")
        void shouldSearchAppointmentsWithAllFilters() {
            // Given
            AppointmentSearchDto searchDto = AppointmentSearchDto.builder()
                    .searchTerm("John Doe")
                    .schoolIds(List.of(1L))
                    .statuses(List.of(AppointmentStatus.CONFIRMED))
                    .appointmentTypes(List.of(AppointmentType.INFORMATION_MEETING))
                    .appointmentDateFrom(LocalDate.now())
                    .appointmentDateTo(LocalDate.now().plusDays(7))
                    .parentEmail("john@example.com")
                    .studentName("Jane Doe")
                    .isOnline(false)
                    .sortBy("appointment_date")
                    .sortDirection("asc")
                    .page(0)
                    .size(10)
                    .build();

            List<Appointment> mockAppointments = List.of(validAppointment);
            Page<Appointment> mockPage = new PageImpl<>(mockAppointments);
            List<AppointmentSummaryDto> expectedSummaries = List.of(
                    AppointmentSummaryDto.builder()
                            .id(1L)
                            .appointmentNumber("APT12345678")
                            .parentName("John Doe")
                            .studentName("Jane Doe")
                            .build()
            );

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(appointmentRepository.searchAppointments(
                    eq("John Doe"), any(), any(), eq(List.of(AppointmentStatus.CONFIRMED)),
                    eq(List.of(AppointmentType.INFORMATION_MEETING)), any(), any(), any(), any(),
                    eq("john@example.com"), any(), eq("Jane Doe"), any(), any(), any(), eq(false),
                    any(), any(), any(), any(Pageable.class)
            )).thenReturn(mockPage);

            when(converterService.mapToSummaryDto(validAppointment))
                    .thenReturn(expectedSummaries.get(0));

            // When
            Page<AppointmentSummaryDto> result = appointmentService.searchAppointments(searchDto, request);

            // Then
            assertNotNull(result);
            assertEquals(1, result.getContent().size());
            assertEquals("APT12345678", result.getContent().get(0).getAppointmentNumber());

            verify(appointmentRepository).searchAppointments(
                    eq("John Doe"), any(), any(), eq(List.of(AppointmentStatus.CONFIRMED)),
                    eq(List.of(AppointmentType.INFORMATION_MEETING)), any(), any(), any(), any(),
                    eq("john@example.com"), any(), eq("Jane Doe"), any(), any(), any(), eq(false),
                    any(), any(), any(), any(Pageable.class)
            );
        }

        @Test
        @DisplayName("Should use default pagination when not specified")
        void shouldUseDefaultPaginationWhenNotSpecified() {
            // Given
            AppointmentSearchDto searchDto = AppointmentSearchDto.builder()
                    .searchTerm("test")
                    .build(); // No pagination specified

            Page<Appointment> emptyPage = new PageImpl<>(Collections.emptyList());

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(appointmentRepository.searchAppointments(
                    anyString(), any(), any(), any(), any(), any(), any(), any(), any(),
                    any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
                    any(Pageable.class)
            )).thenReturn(emptyPage);

            // When
            appointmentService.searchAppointments(searchDto, request);

            // Then
            verify(appointmentRepository).searchAppointments(
                    anyString(), any(), any(), any(), any(), any(), any(), any(), any(),
                    any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
                    argThat(pageable ->
                            pageable.getPageNumber() == 0 && // Default page
                                    pageable.getPageSize() == 20     // Default size
                    )
            );
        }

        @Test
        @DisplayName("Should filter by user accessible school IDs")
        void shouldFilterByUserAccessibleSchoolIds() {
            // Given
            AppointmentSearchDto searchDto = AppointmentSearchDto.builder()
                    .searchTerm("test")
                    .schoolIds(List.of(1L, 2L, 3L)) // User provides these
                    .build();

            Page<Appointment> emptyPage = new PageImpl<>(Collections.emptyList());

            when(jwtService.getUser(request)).thenReturn(regularUser);
            when(schoolRepository.findAllActiveSchoolIds()).thenReturn(List.of(1L)); // User only has access to school 1
            when(appointmentRepository.searchAppointments(
                    anyString(), eq(List.of(1L)), any(), any(), any(), any(), any(), any(), any(),
                    any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
                    any(Pageable.class)
            )).thenReturn(emptyPage);

            // When
            appointmentService.searchAppointments(searchDto, request);

            // Then
            // Should filter to only accessible schools
            verify(appointmentRepository).searchAppointments(
                    anyString(), eq(List.of(1L)), any(), any(), any(), any(), any(), any(), any(),
                    any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
                    any(Pageable.class)
            );
        }
    }

    // ================================ APPOINTMENT NOTES TESTS ================================

    @Nested
    @DisplayName("addAppointmentNote() Tests")
    class AddAppointmentNoteTests {

        @Test
        @DisplayName("Should add appointment note successfully")
        void shouldAddAppointmentNoteSuccessfully() {
            // Given
            AppointmentNoteCreateDto createDto = AppointmentNoteCreateDto.builder()
                    .appointmentId(1L)
                    .authorUserId(4L)
                    .note("Parent was very interested in the programs")
                    .noteType(NoteType.GENERAL)
                    .isPrivate(false)
                    .isImportant(true)
                    .build();

            AppointmentNote savedNote = new AppointmentNote();
            savedNote.setId(1L);
            savedNote.setAppointment(validAppointment);
            savedNote.setAuthorUser(staffUser);
            savedNote.setNote("Parent was very interested in the programs");

            AppointmentNoteDto expectedNoteDto = AppointmentNoteDto.builder()
                    .id(1L)
                    .appointmentId(1L)
                    .note("Parent was very interested in the programs")
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(appointmentRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validAppointment));
            when(userRepository.findByIdAndIsActiveTrue(4L)).thenReturn(Optional.of(staffUser));
            when(appointmentNoteRepository.save(any(AppointmentNote.class))).thenReturn(savedNote);
            when(converterService.mapToDto(savedNote)).thenReturn(expectedNoteDto);

            // When
            AppointmentNoteDto result = appointmentService.addAppointmentNote(createDto, request);

            // Then
            assertNotNull(result);
            assertEquals("Parent was very interested in the programs", result.getNote());

            verify(appointmentNoteRepository).save(argThat(note ->
                    note.getAppointment().getId().equals(1L) &&
                            note.getAuthorUser().getId().equals(4L) &&
                            note.getNote().equals("Parent was very interested in the programs") &&
                            note.getNoteType() == NoteType.GENERAL &&
                            note.getIsPrivate().equals(false) &&
                            note.getIsImportant().equals(true) &&
                            note.getNoteDate() != null
            ));
        }

        @Test
        @DisplayName("Should set default values for optional fields")
        void shouldSetDefaultValuesForOptionalFields() {
            // Given
            AppointmentNoteCreateDto minimalDto = AppointmentNoteCreateDto.builder()
                    .appointmentId(1L)
                    .authorUserId(4L)
                    .note("Simple note")
                    .build(); // No optional fields

            AppointmentNote savedNote = new AppointmentNote();
            savedNote.setId(1L);

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(appointmentRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validAppointment));
            when(userRepository.findByIdAndIsActiveTrue(4L)).thenReturn(Optional.of(staffUser));
            when(appointmentNoteRepository.save(any(AppointmentNote.class))).thenReturn(savedNote);
            when(converterService.mapToDto(any(AppointmentNote.class))).thenReturn(AppointmentNoteDto.builder().build());

            // When
            appointmentService.addAppointmentNote(minimalDto, request);

            // Then
            verify(appointmentNoteRepository).save(argThat(note ->
                    note.getNoteType() == NoteType.GENERAL && // Default
                            note.getIsPrivate().equals(false) &&      // Default
                            note.getIsImportant().equals(false)       // Default
            ));
        }

        @Test
        @DisplayName("Should throw exception when appointment not found")
        void shouldThrowExceptionWhenAppointmentNotFound() {
            // Given
            AppointmentNoteCreateDto createDto = AppointmentNoteCreateDto.builder()
                    .appointmentId(999L)
                    .authorUserId(4L)
                    .note("Test note")
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(appointmentRepository.findByIdAndIsActiveTrue(999L)).thenReturn(Optional.empty());

            // When & Then
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> appointmentService.addAppointmentNote(createDto, request));

            assertEquals("Appointment not found with ID: 999", exception.getMessage());

            verifyNoInteractions(appointmentNoteRepository);
        }

        @Test
        @DisplayName("Should handle note with attachments")
        void shouldHandleNoteWithAttachments() {
            // Given
            AppointmentNoteCreateDto createDto = AppointmentNoteCreateDto.builder()
                    .appointmentId(1L)
                    .authorUserId(4L)
                    .note("Note with attachment")
                    .attachmentUrl("https://example.com/document.pdf")
                    .attachmentName("document.pdf")
                    .attachmentSize(1024L)
                    .attachmentType("application/pdf")
                    .build();

            AppointmentNote savedNote = new AppointmentNote();
            savedNote.setId(1L);

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(appointmentRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validAppointment));
            when(userRepository.findByIdAndIsActiveTrue(4L)).thenReturn(Optional.of(staffUser));
            when(appointmentNoteRepository.save(any(AppointmentNote.class))).thenReturn(savedNote);
            when(converterService.mapToDto(any(AppointmentNote.class))).thenReturn(AppointmentNoteDto.builder().build());

            // When
            appointmentService.addAppointmentNote(createDto, request);

            // Then
            verify(appointmentNoteRepository).save(argThat(note ->
                    note.getAttachmentUrl().equals("https://example.com/document.pdf") &&
                            note.getAttachmentName().equals("document.pdf") &&
                            note.getAttachmentSize().equals(1024L) &&
                            note.getAttachmentType().equals("application/pdf")
            ));
        }
    }

    @Nested
    @DisplayName("getAppointmentNotes() Tests")
    class GetAppointmentNotesTests {

        @Test
        @DisplayName("Should return all notes when user can view private notes")
        void shouldReturnAllNotesWhenUserCanViewPrivateNotes() {
            // Given
            AppointmentNote publicNote = new AppointmentNote();
            publicNote.setId(1L);
            publicNote.setIsPrivate(false);
            publicNote.setNote("Public note");

            AppointmentNote privateNote = new AppointmentNote();
            privateNote.setId(2L);
            privateNote.setIsPrivate(true);
            privateNote.setNote("Private note");

            List<AppointmentNote> allNotes = List.of(publicNote, privateNote);
            List<AppointmentNoteDto> expectedDtos = List.of(
                    AppointmentNoteDto.builder().id(1L).note("Public note").build(),
                    AppointmentNoteDto.builder().id(2L).note("Private note").build()
            );

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(appointmentRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validAppointment));
            when(appointmentNoteRepository.findByAppointmentIdAndIsActiveTrueOrderByNoteDateDesc(1L))
                    .thenReturn(allNotes);
            when(converterService.mapToDto(publicNote)).thenReturn(expectedDtos.get(0));
            when(converterService.mapToDto(privateNote)).thenReturn(expectedDtos.get(1));

            // When
            List<AppointmentNoteDto> result = appointmentService.getAppointmentNotes(1L, request);

            // Then
            assertNotNull(result);
            assertEquals(2, result.size());

            verify(converterService, times(2)).mapToDto(any(AppointmentNote.class));
        }

        @Test
        @DisplayName("Should filter private notes when user cannot view them")
        void shouldFilterPrivateNotesWhenUserCannotView() {
            // Given
            AppointmentNote publicNote = new AppointmentNote();
            publicNote.setId(1L);
            publicNote.setIsPrivate(false);
            publicNote.setNote("Public note");

            AppointmentNote privateNote = new AppointmentNote();
            privateNote.setId(2L);
            privateNote.setIsPrivate(true);
            privateNote.setNote("Private note");

            List<AppointmentNote> allNotes = List.of(publicNote, privateNote);
            AppointmentNoteDto expectedDto = AppointmentNoteDto.builder()
                    .id(1L)
                    .note("Public note")
                    .build();

            when(jwtService.getUser(request)).thenReturn(parentUser); // Parent cannot view private notes
            when(appointmentRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validAppointment));
            when(appointmentNoteRepository.findByAppointmentIdAndIsActiveTrueOrderByNoteDateDesc(1L))
                    .thenReturn(allNotes);
            when(converterService.mapToDto(publicNote)).thenReturn(expectedDto);

            // When
            List<AppointmentNoteDto> result = appointmentService.getAppointmentNotes(1L, request);

            // Then
            assertNotNull(result);
            assertEquals(1, result.size()); // Only public note
            assertEquals("Public note", result.get(0).getNote());

            verify(converterService, times(1)).mapToDto(any(AppointmentNote.class));
        }
    }

    // ================================ BULK OPERATIONS TESTS ================================

    @Nested
    @DisplayName("bulkUpdateAppointments() Tests")
    class BulkUpdateAppointmentsTests {

        @Test
        @DisplayName("Should perform bulk confirm operation successfully")
        void shouldPerformBulkConfirmSuccessfully() {
            // Given
            List<Long> appointmentIds = List.of(1L, 2L);
            BulkAppointmentOperationDto bulkDto = BulkAppointmentOperationDto.builder()
                    .operation("CONFIRM")
                    .appointmentIds(appointmentIds)
                    .notifyParticipants(true)
                    .build();

            Appointment pendingAppointment1 = new Appointment();
            pendingAppointment1.setId(1L);
            pendingAppointment1.setSchool(validSchool);
            pendingAppointment1.setStatus(AppointmentStatus.PENDING);

            Appointment pendingAppointment2 = new Appointment();
            pendingAppointment2.setId(2L);
            pendingAppointment2.setSchool(validSchool);
            pendingAppointment2.setStatus(AppointmentStatus.PENDING);

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(appointmentRepository.findByIdAndIsActiveTrue(1L))
                    .thenReturn(Optional.of(pendingAppointment1));
            when(appointmentRepository.findByIdAndIsActiveTrue(2L))
                    .thenReturn(Optional.of(pendingAppointment2));

            // When
            BulkAppointmentResultDto result = appointmentService.bulkUpdateAppointments(bulkDto, request);

            // Then
            assertNotNull(result);
            assertEquals(2, result.getTotalRecords());
            assertEquals(2, result.getSuccessfulOperations());
            assertEquals(0, result.getFailedOperations());
            assertEquals(2, result.getNotificationsSent());
            assertTrue(result.getSuccess());
            assertEquals(2, result.getAffectedAppointmentIds().size());

            verify(appointmentRepository, times(2)).save(any(Appointment.class));
        }

        @Test
        @DisplayName("Should handle partial failures in bulk operation")
        void shouldHandlePartialFailuresInBulkOperation() {
            // Given
            List<Long> appointmentIds = List.of(1L, 999L); // 999 doesn't exist
            BulkAppointmentOperationDto bulkDto = BulkAppointmentOperationDto.builder()
                    .operation("CONFIRM")
                    .appointmentIds(appointmentIds)
                    .build();

            Appointment validAppointment = new Appointment();
            validAppointment.setId(1L);
            validAppointment.setSchool(validSchool);
            validAppointment.setStatus(AppointmentStatus.PENDING);

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(appointmentRepository.findByIdAndIsActiveTrue(1L))
                    .thenReturn(Optional.of(validAppointment));
            when(appointmentRepository.findByIdAndIsActiveTrue(999L))
                    .thenReturn(Optional.empty());

            // When
            BulkAppointmentResultDto result = appointmentService.bulkUpdateAppointments(bulkDto, request);

            // Then
            assertNotNull(result);
            assertEquals(2, result.getTotalRecords());
            assertEquals(1, result.getSuccessfulOperations());
            assertEquals(1, result.getFailedOperations());
            assertFalse(result.getSuccess());
            assertEquals(1, result.getErrors().size());
            assertTrue(result.getErrors().get(0).contains("999"));
        }

        @Test
        @DisplayName("Should perform bulk cancel operation")
        void shouldPerformBulkCancelOperation() {
            // Given
            List<Long> appointmentIds = List.of(1L);
            BulkAppointmentOperationDto bulkDto = BulkAppointmentOperationDto.builder()
                    .operation("CANCEL")
                    .appointmentIds(appointmentIds)
                    .reason("Bulk cancellation due to facility maintenance")
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(appointmentRepository.findByIdAndIsActiveTrue(1L))
                    .thenReturn(Optional.of(validAppointment));

            // When
            BulkAppointmentResultDto result = appointmentService.bulkUpdateAppointments(bulkDto, request);

            // Then
            assertNotNull(result);
            assertEquals(1, result.getSuccessfulOperations());

            verify(appointmentRepository).save(argThat(appointment ->
                    appointment.getStatus() == AppointmentStatus.CANCELLED &&
                            appointment.getCancellationReason().equals("Bulk cancellation due to facility maintenance")
            ));
        }

        @Test
        @DisplayName("Should throw exception for unsupported bulk operation")
        void shouldThrowExceptionForUnsupportedOperation() {
            // Given
            BulkAppointmentOperationDto bulkDto = BulkAppointmentOperationDto.builder()
                    .operation("INVALID_OPERATION")
                    .appointmentIds(List.of(1L))
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(appointmentRepository.findByIdAndIsActiveTrue(1L))
                    .thenReturn(Optional.of(validAppointment));

            // When
            BulkAppointmentResultDto result = appointmentService.bulkUpdateAppointments(bulkDto, request);

            // Then
            assertEquals(1, result.getFailedOperations());
            assertEquals(0, result.getSuccessfulOperations());
            assertFalse(result.getSuccess());
        }
    }

    // ================================ PUBLIC APPOINTMENT TESTS ================================

    @Nested
    @DisplayName("Public Appointment Tests")
    class PublicAppointmentTests {

        @Test
        @DisplayName("Should create public appointment successfully")
        void shouldCreatePublicAppointmentSuccessfully() {
            // Given
            School subscribedSchool = new School();
            subscribedSchool.setId(1L);
            subscribedSchool.setName("Subscribed School");
            subscribedSchool.setIsActive(true);

            AppointmentCreateDto publicDto = AppointmentCreateDto.builder()
                    .schoolId(1L)
                    .appointmentDate(LocalDate.now().plusDays(2))
                    .startTime(LocalTime.of(9, 0))
                    .endTime(LocalTime.of(10, 0))
                    .parentName("John Doe")
                    .parentEmail("john@example.com")
                    .parentPhone("+90 555 123 4567")
                    .studentName("Jane Doe")
                    .appointmentType(AppointmentType.INFORMATION_MEETING)
                    .build();

            Appointment publicAppointment = new Appointment();
            publicAppointment.setId(1L);
            publicAppointment.setAppointmentNumber("APT12345678");
            publicAppointment.setStatus(AppointmentStatus.PENDING); // Always pending for public

            when(schoolRepository.findByIdAndIsActiveTrueAndCampusIsSubscribedTrue(1L))
                    .thenReturn(Optional.of(subscribedSchool));
            when(appointmentRepository.save(any(Appointment.class))).thenReturn(publicAppointment);
            when(converterService.mapToDto(publicAppointment)).thenReturn(expectedAppointmentDto);

            // When
            AppointmentDto result = appointmentService.createPublicAppointment(publicDto);

            // Then
            assertNotNull(result);

            verify(appointmentRepository).save(argThat(appointment ->
                    appointment.getStatus() == AppointmentStatus.PENDING &&
                            appointment.getParentName().equals("John Doe") &&
                            appointment.getParentEmail().equals("john@example.com") &&
                            appointment.getStudentName().equals("Jane Doe")
            ));
        }

        @Test
        @DisplayName("Should throw exception when school not subscribed for public booking")
        void shouldThrowExceptionWhenSchoolNotSubscribed() {
            // Given
            AppointmentCreateDto publicDto = AppointmentCreateDto.builder()
                    .schoolId(999L)
                    .parentName("John Doe")
                    .parentEmail("john@example.com")
                    .studentName("Jane Doe")
                    .build();

            when(schoolRepository.findByIdAndIsActiveTrueAndCampusIsSubscribedTrue(999L))
                    .thenReturn(Optional.empty());

            // When & Then
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> appointmentService.createPublicAppointment(publicDto));

            assertEquals("School not found or not available for booking", exception.getMessage());

            verifyNoInteractions(appointmentRepository);
        }

        @Test
        @DisplayName("Should validate required fields for public appointments")
        void shouldValidateRequiredFieldsForPublicAppointments() {
            // Given
            AppointmentCreateDto incompleteDto = AppointmentCreateDto.builder()
                    .schoolId(1L)
                    .parentName("John Doe")
                    // Missing required fields: email, phone, student name
                    .build();

            School subscribedSchool = new School();
            subscribedSchool.setId(1L);

            when(schoolRepository.findByIdAndIsActiveTrueAndCampusIsSubscribedTrue(1L))
                    .thenReturn(Optional.of(subscribedSchool));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> appointmentService.createPublicAppointment(incompleteDto));

            assertEquals("Parent and student information is required for public appointments",
                    exception.getMessage());
        }

        @Test
        @DisplayName("Should get public appointment by number without authentication")
        void shouldGetPublicAppointmentByNumberWithoutAuth() {
            // Given
            String appointmentNumber = "APT12345678";
            when(appointmentRepository.findByAppointmentNumberAndIsActiveTrue(appointmentNumber))
                    .thenReturn(Optional.of(validAppointment));
            when(converterService.mapToPublicAppointmentDto(validAppointment))
                    .thenReturn(expectedAppointmentDto);

            // When
            AppointmentDto result = appointmentService.getPublicAppointmentByNumber(appointmentNumber);

            // Then
            assertNotNull(result);
            verify(converterService).mapToPublicAppointmentDto(validAppointment);
            verifyNoInteractions(jwtService); // No authentication
        }

        @Test
        @DisplayName("Should cancel public appointment successfully")
        void shouldCancelPublicAppointmentSuccessfully() {
            // Given
            String appointmentNumber = "APT12345678";
            String cancellationReason = "Change of plans";

            when(appointmentRepository.findByAppointmentNumberAndIsActiveTrue(appointmentNumber))
                    .thenReturn(Optional.of(validAppointment));
            when(appointmentRepository.save(any(Appointment.class))).thenReturn(validAppointment);
            when(converterService.mapToPublicAppointmentDto(any(Appointment.class)))
                    .thenReturn(expectedAppointmentDto);

            // When
            AppointmentDto result = appointmentService.cancelPublicAppointment(
                    appointmentNumber, cancellationReason);

            // Then
            assertNotNull(result);

            verify(appointmentRepository).save(argThat(appointment ->
                    appointment.getStatus() == AppointmentStatus.CANCELLED &&
                            appointment.getCancellationReason().equals("Change of plans") &&
                            appointment.getCanceledByType() == CancelledByType.PARENT &&
                            appointment.getCanceledAt() != null
            ));

            verifyNoInteractions(jwtService); // No authentication required
        }
    }

    // ================================ AVAILABILITY TESTS ================================

    @Nested
    @DisplayName("getAvailabilityBetweenDates() Tests")
    class GetAvailabilityBetweenDatesTests {

        @Test
        @DisplayName("Should return availability for date range successfully")
        void shouldReturnAvailabilityForDateRangeSuccessfully() {
            // Given
            Long schoolId = 1L;
            String schoolName = "Test School";
            LocalDate startDate = LocalDate.now().plusDays(1);
            LocalDate endDate = LocalDate.now().plusDays(3);

            // Mock slots data
            List<Object[]> slotsData = Collections.singletonList(
                    new Object[]{DayOfWeek.MONDAY, 1L, LocalTime.of(9, 0), LocalTime.of(10, 0),
                            60, AppointmentType.INFORMATION_MEETING, "Main Office", false, "Staff User", 1, false}
            );

            // Mock excluded dates (empty)
            List<Object[]> excludedDates = Collections.emptyList();

            // Mock booked counts (no bookings)
            List<Object[]> bookedCounts = Collections.emptyList();

            when(appointmentRepository.getAllActiveSlotsForSchool(eq(schoolId), any(LocalDate.class)))
                    .thenReturn(slotsData);
            when(appointmentRepository.getSlotsWithExcludedDates(schoolId))
                    .thenReturn(excludedDates);
            when(appointmentRepository.getBookedCountsByDateRange(schoolId, startDate, endDate))
                    .thenReturn(bookedCounts);

            // When
            List<AppointmentAvailabilityDto> result = appointmentService
                    .getAvailabilityBetweenDates(schoolId, schoolName, startDate, endDate);

            // Then
            assertNotNull(result);
            // Should only return available days (only Monday in this case)
            assertTrue(result.size() <= 3); // Max 3 days in range

            verify(appointmentRepository).getAllActiveSlotsForSchool(eq(schoolId), any(LocalDate.class));
            verify(appointmentRepository).getSlotsWithExcludedDates(schoolId);
            verify(appointmentRepository).getBookedCountsByDateRange(schoolId, startDate, endDate);
        }

        @Test
        @DisplayName("Should return empty list when no available slots")
        void shouldReturnEmptyListWhenNoAvailableSlots() {
            // Given
            Long schoolId = 1L;
            String schoolName = "Test School";
            LocalDate startDate = LocalDate.now().plusDays(1);
            LocalDate endDate = LocalDate.now().plusDays(3);

            // No slots available
            when(appointmentRepository.getAllActiveSlotsForSchool(eq(schoolId), any(LocalDate.class)))
                    .thenReturn(Collections.emptyList());
            when(appointmentRepository.getSlotsWithExcludedDates(schoolId))
                    .thenReturn(Collections.emptyList());
            when(appointmentRepository.getBookedCountsByDateRange(schoolId, startDate, endDate))
                    .thenReturn(Collections.emptyList());

            // When
            List<AppointmentAvailabilityDto> result = appointmentService
                    .getAvailabilityBetweenDates(schoolId, schoolName, startDate, endDate);

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should handle excluded dates properly")
        void shouldHandleExcludedDatesProperly() throws Exception {
            // Given
            Long schoolId = 1L;
            String schoolName = "Test School";
            LocalDate startDate = LocalDate.now().plusDays(1);
            LocalDate endDate = LocalDate.now().plusDays(3);
            LocalDate excludedDate = LocalDate.now().plusDays(2);

            // Mock slots
            List<Object[]> slotsData = new ArrayList<>();
            slotsData.add(new Object[]{DayOfWeek.MONDAY, 1L, LocalTime.of(9, 0), LocalTime.of(10, 0),
                    60, AppointmentType.INFORMATION_MEETING, "Main Office", false, "Staff User", 1, false});

            // Mock excluded dates
            List<Object[]> excludedDates = new ArrayList<>();
            excludedDates.add(new Object[]{1L, "[\"" + excludedDate + "\"]"});

            when(appointmentRepository.getAllActiveSlotsForSchool(eq(schoolId), any(LocalDate.class)))
                    .thenReturn(slotsData);
            when(appointmentRepository.getSlotsWithExcludedDates(schoolId))
                    .thenReturn(excludedDates);
            when(appointmentRepository.getBookedCountsByDateRange(schoolId, startDate, endDate))
                    .thenReturn(Collections.emptyList());
            when(objectMapper.readValue(anyString(), eq(LocalDate[].class)))
                    .thenReturn(new LocalDate[]{excludedDate});

            // When
            List<AppointmentAvailabilityDto> result = appointmentService
                    .getAvailabilityBetweenDates(schoolId, schoolName, startDate, endDate);

            // Then
            assertNotNull(result);
            // Should exclude the date that's in excluded dates
            verify(objectMapper).readValue(anyString(), eq(LocalDate[].class));
        }

        @Test
        @DisplayName("Should calculate availability status correctly")
        void shouldCalculateAvailabilityStatusCorrectly() {
            // Given
            Long schoolId = 1L;
            String schoolName = "Test School";
            LocalDate startDate = LocalDate.now().plusDays(1);
            LocalDate endDate = LocalDate.now().plusDays(1);

            // Mock multiple slots with different capacities
            List<Object[]> slotsData = List.of(
                    new Object[]{startDate.getDayOfWeek(), 1L, LocalTime.of(9, 0), LocalTime.of(10, 0),
                            60, AppointmentType.INFORMATION_MEETING, "Office 1", false, "Staff 1", 2, false},
                    new Object[]{startDate.getDayOfWeek(), 2L, LocalTime.of(10, 0), LocalTime.of(11, 0),
                            60, AppointmentType.SCHOOL_TOUR, "Office 2", false, "Staff 2", 1, false}
            );

            // One slot fully booked
            List<Object[]> bookedCounts = Collections.singletonList(
                    new Object[]{startDate, 2L, 1L}
            );

            when(appointmentRepository.getAllActiveSlotsForSchool(eq(schoolId), any(LocalDate.class)))
                    .thenReturn(slotsData);
            when(appointmentRepository.getSlotsWithExcludedDates(schoolId))
                    .thenReturn(Collections.emptyList());
            when(appointmentRepository.getBookedCountsByDateRange(schoolId, startDate, endDate))
                    .thenReturn(bookedCounts);

            // When
            List<AppointmentAvailabilityDto> result = appointmentService
                    .getAvailabilityBetweenDates(schoolId, schoolName, startDate, endDate);

            // Then
            assertNotNull(result);
            assertFalse(result.isEmpty());

            AppointmentAvailabilityDto dayAvailability = result.get(0);
            assertEquals(1, dayAvailability.getAvailableCount()); // Only slot 1 available
            assertEquals(2, dayAvailability.getTotalSlots());
            assertEquals(1, dayAvailability.getBookedSlots());
        }
    }

    // ================================ STATISTICS TESTS ================================

    @Nested
    @DisplayName("getAppointmentStatistics() Tests")
    class GetAppointmentStatisticsTests {

        @Test
        @DisplayName("Should return appointment statistics successfully")
        void shouldReturnAppointmentStatisticsSuccessfully() {
            // Given
            Long schoolId = 1L;
            LocalDate periodStart = LocalDate.now().minusDays(30);
            LocalDate periodEnd = LocalDate.now();

            AppointmentStatisticsDto expectedStats = new AppointmentStatisticsDto(
                    schoolId, "Test School", periodStart, periodEnd,
                    100L, 80L, 10L, 5L, 5L,
                    80.0, 10.0, 5.0, 5.0, 25.0,
                    20L, 40L, 20L, 75.5, 45.0,
                    3, 2.5, "09:00-10:00", "MONDAY",
                    AppointmentType.INFORMATION_MEETING, "John Staff"
            );

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(appointmentRepository.getAppointmentStatistics(schoolId, periodStart, periodEnd))
                    .thenReturn(expectedStats);

            // When
            AppointmentStatisticsDto result = appointmentService.getAppointmentStatistics(
                    schoolId, periodStart, periodEnd, request);

            // Then
            assertNotNull(result);
            assertEquals(schoolId, result.getSchoolId());
            assertEquals("Test School", result.getSchoolName());
            assertEquals(100L, result.getTotalAppointments());
            assertEquals(80.0, result.getCompletionRate());

            verify(appointmentRepository).getAppointmentStatistics(schoolId, periodStart, periodEnd);
        }

        @Test
        @DisplayName("Should throw exception when user has no access to school")
        void shouldThrowExceptionWhenUserHasNoAccessToSchool() {
            // Given
            Long schoolId = 1L;
            LocalDate periodStart = LocalDate.now().minusDays(30);
            LocalDate periodEnd = LocalDate.now();

            when(jwtService.getUser(request)).thenReturn(regularUser);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> appointmentService.getAppointmentStatistics(schoolId, periodStart, periodEnd, request));

            assertEquals("User does not have access to this school", exception.getMessage());

            verifyNoInteractions(appointmentRepository);
        }
    }

    @Nested
    @DisplayName("generateAppointmentReport() Tests")
    class GenerateAppointmentReportTests {

        @Test
        @DisplayName("Should generate summary report successfully")
        void shouldGenerateSummaryReportSuccessfully() {
            // Given
            String reportType = "SUMMARY";
            Long schoolId = 1L;
            LocalDate periodStart = LocalDate.now().minusDays(30);
            LocalDate periodEnd = LocalDate.now();

            AppointmentStatisticsDto mockStats = new AppointmentStatisticsDto(
                    schoolId, "Test School", periodStart, periodEnd,
                    50L, 40L, 5L, 3L, 2L,
                    80.0, 10.0, 6.0, 4.0, 30.0,
                    15L, 20L, 10L, 75.0, 50.0,
                    2, 2.0, "10:00-11:00", "TUESDAY",
                    AppointmentType.SCHOOL_TOUR, "Jane Staff"
            );

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolRepository.findByIdAndIsActiveTrue(schoolId))
                    .thenReturn(Optional.of(validSchool));
            when(appointmentRepository.getAppointmentStatistics(schoolId, periodStart, periodEnd))
                    .thenReturn(mockStats);

            // When
            AppointmentReportDto result = appointmentService.generateAppointmentReport(
                    reportType, schoolId, periodStart, periodEnd, request);

            // Then
            assertNotNull(result);
            assertEquals("SUMMARY", result.getReportType());
            assertEquals(schoolId, result.getSchoolId());
            assertEquals("Test School", result.getSchoolName());
            assertNotNull(result.getOverallStatistics());
            assertNotNull(result.getKeyInsights());
            assertNotNull(result.getRecommendations());
            assertNotNull(result.getCsvDownloadUrl());
            assertNotNull(result.getPdfDownloadUrl());
            assertNotNull(result.getExcelDownloadUrl());

            verify(appointmentRepository).getAppointmentStatistics(schoolId, periodStart, periodEnd);
        }

        @Test
        @DisplayName("Should generate staff performance report successfully")
        void shouldGenerateStaffPerformanceReportSuccessfully() {
            // Given
            String reportType = "STAFF_PERFORMANCE";
            Long schoolId = 1L;
            LocalDate periodStart = LocalDate.now().minusDays(30);
            LocalDate periodEnd = LocalDate.now();

            List<StaffPerformanceDto> mockStaffPerformance = List.of(
                    StaffPerformanceDto.builder()
                            .staffUserId(4L)
                            .staffUserName("John Staff")
                            .totalAppointments(20)
                            .completedAppointments(18)
                            .completionRate(90.0)
                            .enrollmentConversionRate(35.0)
                            .build()
            );

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolRepository.findByIdAndIsActiveTrue(schoolId))
                    .thenReturn(Optional.of(validSchool));
            when(appointmentRepository.getStaffPerformance(schoolId, periodStart, periodEnd))
                    .thenReturn(mockStaffPerformance);

            // When
            AppointmentReportDto result = appointmentService.generateAppointmentReport(
                    reportType, schoolId, periodStart, periodEnd, request);

            // Then
            assertNotNull(result);
            assertEquals("STAFF_PERFORMANCE", result.getReportType());
            assertNotNull(result.getStaffPerformance());
            assertEquals(1, result.getStaffPerformance().size());
            assertEquals("John Staff", result.getStaffPerformance().get(0).getStaffUserName());

            verify(appointmentRepository).getStaffPerformance(schoolId, periodStart, periodEnd);
        }

        @Test
        @DisplayName("Should throw exception for unsupported report type")
        void shouldThrowExceptionForUnsupportedReportType() {
            // Given
            String reportType = "INVALID_REPORT_TYPE";
            Long schoolId = 1L;
            LocalDate periodStart = LocalDate.now().minusDays(30);
            LocalDate periodEnd = LocalDate.now();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolRepository.findByIdAndIsActiveTrue(schoolId))
                    .thenReturn(Optional.of(validSchool));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> appointmentService.generateAppointmentReport(
                            reportType, schoolId, periodStart, periodEnd, request));

            assertEquals("Unsupported report type: INVALID_REPORT_TYPE", exception.getMessage());
        }
    }

    // ================================ WAITLIST TESTS ================================

    @Nested
    @DisplayName("addToWaitlist() Tests")
    class AddToWaitlistTests {

        @Test
        @DisplayName("Should add to waitlist successfully")
        void shouldAddToWaitlistSuccessfully() {
            // Given
            AppointmentWaitlistCreateDto createDto = AppointmentWaitlistCreateDto.builder()
                    .schoolId(1L)
                    .parentUserId(3L)
                    .parentName("John Doe")
                    .parentEmail("john@example.com")
                    .parentPhone("+90 555 123 4567")
                    .studentName("Jane Doe")
                    .studentAge(8)
                    .gradeInterested("Grade 3")
                    .preferredAppointmentTypes(List.of(AppointmentType.INFORMATION_MEETING))
                    .preferredDays(List.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY))
                    .preferredStartTime(LocalTime.of(9, 0))
                    .preferredEndTime(LocalTime.of(17, 0))
                    .earliestDate(LocalDate.now().plusDays(1))
                    .latestDate(LocalDate.now().plusDays(30))
                    .isOnlinePreferred(false)
                    .autoAcceptWhenAvailable(true)
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validSchool));
            when(userRepository.findByIdAndIsActiveTrue(3L)).thenReturn(Optional.of(parentUser));
            when(appointmentRepository.existsInWaitlist(1L, "john@example.com")).thenReturn(false);

            // When
            AppointmentWaitlistDto result = appointmentService.addToWaitlist(createDto, request);

            // Then
            assertNotNull(result);
            assertEquals(1L, result.getSchoolId());
            assertEquals("Test School", result.getSchoolName());
            assertEquals("John Doe", result.getParentName());
            assertEquals("jane Doe", result.getStudentName());
            assertEquals("WAITING", result.getStatus());
            assertEquals(1, result.getPositionInQueue());
            assertTrue(result.getAutoAcceptWhenAvailable());

            verify(appointmentRepository).existsInWaitlist(1L, "john@example.com");
        }

        @Test
        @DisplayName("Should throw exception when already in waitlist")
        void shouldThrowExceptionWhenAlreadyInWaitlist() {
            // Given
            AppointmentWaitlistCreateDto createDto = AppointmentWaitlistCreateDto.builder()
                    .schoolId(1L)
                    .parentEmail("john@example.com")
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validSchool));
            when(appointmentRepository.existsInWaitlist(1L, "john@example.com")).thenReturn(true);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> appointmentService.addToWaitlist(createDto, request));

            assertEquals("Parent is already in the waitlist for this school", exception.getMessage());
        }
    }

    // ================================ HELPER METHODS ================================

    private UserRole createUserRole(RoleLevel roleLevel) {
        UserRole mockRole = new UserRole();
        mockRole.setRoleLevel(roleLevel);
        return mockRole;
    }
    private User createUser(Long id, RoleLevel roleLevel) {
        User user = new User();
        user.setId(id);
        user.setUserRoles(Set.of(createUserRole(roleLevel)));
       // user.setUserRoles(createMockUserRoles(roleLevel));
        user.setInstitutionAccess(Collections.emptySet());
        return user;
    }

}




/*
.cancellationReason("Schedule conflict")
                    .canceledByType(CancelledByType.PARENT)
                    .build();

when(jwtService.getUser(request)).thenReturn(parentUser);
when(appointmentRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validAppointment));
when(appointmentRepository.save(any(Appointment.class))).thenReturn(validAppointment);
when(converterService.mapToDto(any(Appointment.class))).thenReturn(expectedAppointmentDto);

// When
AppointmentDto result = appointmentService.cancelAppointment(cancelDto, request);

// Then
assertNotNull(result);
verify(appointmentRepository).save(argThat(appointment ->
        appointment.getStatus() == AppointmentStatus.CANCELLED &&
        appointment.getCancellationReason().equals("Schedule conflict") &&
        appointment.getCanceledByType() == CancelledByType.PARENT &&
        appointment.getCanceledAt() != null &&
        appointment.getCanceledBy().equals(3L)
            ));
                    }

@Test
@DisplayName("Should throw exception when appointment cannot be canceled due to time restrictions")
void shouldThrowExceptionWhenCannotCancelDueToTime() {
    // Given - Appointment too close to cancel (less than cancellation hours)
    Appointment soonAppointment = new Appointment();
    soonAppointment.setId(1L);
    soonAppointment.setAppointmentSlot(validSlot);
    soonAppointment.setSchool(validSchool);
    soonAppointment.setParentUser(parentUser);
    soonAppointment.setAppointmentDate(LocalDate.now());
    soonAppointment.setStartTime(LocalTime.now().plusHours(2)); // Only 2 hours ahead
    soonAppointment.setStatus(AppointmentStatus.CONFIRMED);

    AppointmentCancelDto cancelDto = AppointmentCancelDto.builder()
            .appointmentId(1L)
 */