package com.genixo.education.search.api.rest;

import com.genixo.education.search.dto.appointment.*;
import com.genixo.education.search.service.AppointmentService;
import com.genixo.education.search.service.auth.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Appointment Management", description = "APIs for managing appointment slots, bookings, and scheduling")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final JwtService jwtService;

    // ================================ APPOINTMENT SLOT OPERATIONS ================================

    @PostMapping("/slots")
    @Operation(summary = "Create appointment slot", description = "Create a new recurring appointment slot for a school")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Appointment slot created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid slot data or overlapping slot exists"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School or staff user not found")
    })
    public ResponseEntity<ApiResponse<AppointmentSlotDto>> createAppointmentSlot(
            @Valid @RequestBody AppointmentSlotCreateDto createDto,
            HttpServletRequest request) {

        AppointmentSlotDto slotDto = appointmentService.createAppointmentSlot(createDto, request);

        ApiResponse<AppointmentSlotDto> response = ApiResponse.success(slotDto, "Appointment slot created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }



    @PostMapping("/slots/search/date")
    @Operation(summary = "Create appointment slot", description = "Create a new recurring appointment slot for a school")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Appointment slot created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid slot data or overlapping slot exists"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School or staff user not found")
    })
    public ResponseEntity<ApiResponse<List<AppointmentSlotDto>>> searchSlotsWithDate(
            @Valid @RequestBody AppointmentSlotSearchDto searchDto,
            HttpServletRequest request) {


        List<AppointmentSlotDto> slotDto = appointmentService.searchSlotsWithDate(searchDto);

        ApiResponse<List<AppointmentSlotDto>> response = ApiResponse.success(slotDto, "Appointment slot created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }



    @GetMapping("/slots/search/user/{userId}")
    @Operation(summary = "Create appointment slot", description = "Create a new recurring appointment slot for a school")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Appointment slot created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid slot data or overlapping slot exists"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School or staff user not found")
    })
    public ResponseEntity<ApiResponse<List<AppointmentSlotDto>>> searchSlotsWithUser(
            @Parameter(description = "Appointment slot ID") @PathVariable Long userId,
            HttpServletRequest request) {


        List<AppointmentSlotDto> slotDto = appointmentService.searchSlotsWithUser(userId);

        ApiResponse<List<AppointmentSlotDto>> response = ApiResponse.success(slotDto, "Appointment slot created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/slots/search/user/{userId}/school/{schoolId}")
    @Operation(summary = "Create appointment slot", description = "Create a new recurring appointment slot for a school")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Appointment slot created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid slot data or overlapping slot exists"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School or staff user not found")
    })
    public ResponseEntity<ApiResponse<List<AppointmentSlotDto>>> searchSlotsWithUser(
            @Parameter(description = "Appointment slot ID") @PathVariable Long userId,
            @Parameter(description = "Appointment slot ID") @PathVariable Long schoolId,
            HttpServletRequest request) {


        List<AppointmentSlotDto> slotDto = appointmentService.searchSlotsWithUser(userId, schoolId);

        ApiResponse<List<AppointmentSlotDto>> response = ApiResponse.success(slotDto, "Appointment slot created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }



    @GetMapping("/slots/{id}")
    @Operation(summary = "Get appointment slot by ID", description = "Get appointment slot details by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Appointment slot retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Appointment slot not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<AppointmentSlotDto>> getAppointmentSlotById(
            @Parameter(description = "Appointment slot ID") @PathVariable Long id,
            HttpServletRequest request) {

        log.debug("Get appointment slot request: {}", id);

        AppointmentSlotDto slotDto = appointmentService.getAppointmentSlotById(id, request);

        ApiResponse<AppointmentSlotDto> response = ApiResponse.success(slotDto, "Appointment slot retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/schools/{schoolId}/slots")
    @Operation(summary = "Get school appointment slots", description = "Get all appointment slots for a school")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Appointment slots retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School not found")
    })
    public ResponseEntity<ApiResponse<List<AppointmentSlotDto>>> getSchoolAppointmentSlots(
            @Parameter(description = "School ID") @PathVariable Long schoolId,
            HttpServletRequest request) {

        log.debug("Get school appointment slots request: {}", schoolId);

        List<AppointmentSlotDto> slots = appointmentService.getSchoolAppointmentSlots(schoolId, request);

        ApiResponse<List<AppointmentSlotDto>> response = ApiResponse.success(slots, "Appointment slots retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ APPOINTMENT OPERATIONS ================================

    @PostMapping
    @Operation(summary = "Create appointment", description = "Book a new appointment")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Appointment created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid appointment data or no available capacity"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School or appointment slot not found")
    })
    public ResponseEntity<ApiResponse<AppointmentDto>> createAppointment(
            @Valid @RequestBody AppointmentCreateDto createDto,
            HttpServletRequest request) {


        AppointmentDto appointmentDto = appointmentService.createAppointment(createDto, request);

        ApiResponse<AppointmentDto> response = ApiResponse.success(appointmentDto, "Appointment created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }






    @PostMapping("/confirm")
    @Operation(summary = "Create appointment", description = "Book a new appointment")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Appointment created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid appointment data or no available capacity"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School or appointment slot not found")
    })
    public ResponseEntity<ApiResponse<AppointmentDto>> confirmAppointment(
            @Valid @RequestBody AppointmentConfirmDto confirmDto,
            HttpServletRequest request) {


        AppointmentDto appointmentDto = appointmentService.confirmAppointment(confirmDto, request);

        ApiResponse<AppointmentDto> response = ApiResponse.success(appointmentDto, "Appointment created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/reschedule")
    @Operation(summary = "Reschedule appointment", description = "Book a new appointment")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Appointment created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid appointment data or no available capacity"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School or appointment slot not found")
    })
    public ResponseEntity<ApiResponse<AppointmentDto>> rescheduleAppointment(
            @Valid @RequestBody AppointmentRescheduleDto rescheduleDto,
            HttpServletRequest request) {


        AppointmentDto appointmentDto = appointmentService.rescheduleAppointment(rescheduleDto, request);

        ApiResponse<AppointmentDto> response = ApiResponse.success(appointmentDto, "Appointment created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }





    @GetMapping("/{id}")
    @Operation(summary = "Get appointment by ID", description = "Get appointment details by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Appointment retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Appointment not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<AppointmentDto>> getAppointmentById(
            @Parameter(description = "Appointment ID") @PathVariable Long id,
            HttpServletRequest request) {

        log.debug("Get appointment request: {}", id);

        AppointmentDto appointmentDto = appointmentService.getAppointmentById(id, request);

        ApiResponse<AppointmentDto> response = ApiResponse.success(appointmentDto, "Appointment retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/number/{appointmentNumber}")
    @Operation(summary = "Get appointment by number", description = "Get appointment details by appointment number")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Appointment retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    public ResponseEntity<ApiResponse<AppointmentDto>> getAppointmentByNumber(
            @Parameter(description = "Appointment number") @PathVariable String appointmentNumber,
            HttpServletRequest request) {

        log.debug("Get appointment by number request: {}", appointmentNumber);

        AppointmentDto appointmentDto = appointmentService.getAppointmentByNumber(appointmentNumber);

        ApiResponse<AppointmentDto> response = ApiResponse.success(appointmentDto, "Appointment retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update appointment", description = "Update an existing appointment")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Appointment updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Appointment not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid update data")
    })
    public ResponseEntity<ApiResponse<AppointmentDto>> updateAppointment(
            @Parameter(description = "Appointment ID") @PathVariable Long id,
            @Valid @RequestBody AppointmentUpdateDto updateDto,
            HttpServletRequest request) {

        AppointmentDto appointmentDto = appointmentService.updateAppointment(id, updateDto, request);

        ApiResponse<AppointmentDto> response = ApiResponse.success(appointmentDto, "Appointment updated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/cancel")
    @Operation(summary = "Cancel appointment", description = "Cancel an existing appointment")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Appointment canceled successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Appointment not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cannot cancel due to time restrictions")
    })
    public ResponseEntity<ApiResponse<AppointmentDto>> cancelAppointment(
            @Valid @RequestBody AppointmentCancelDto cancelDto,
            HttpServletRequest request) {

        AppointmentDto appointmentDto = appointmentService.cancelAppointment(cancelDto, request);

        ApiResponse<AppointmentDto> response = ApiResponse.success(appointmentDto, "Appointment canceled successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }



    // ================================ APPOINTMENT SEARCH AND FILTERING ================================

    @PostMapping("/search")
    @Operation(summary = "Search appointments", description = "Search appointments with advanced filters")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Search completed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<Page<AppointmentSummaryDto>>> searchAppointments(
            @Valid @RequestBody AppointmentSearchDto searchDto,
            HttpServletRequest request) {

        log.debug("Search appointments request");

        Page<AppointmentSummaryDto> appointments = appointmentService.searchAppointments(searchDto, request);

        ApiResponse<Page<AppointmentSummaryDto>> response = ApiResponse.success(appointments, "Search completed successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/schools/{schoolId}/calendar")
    @Operation(summary = "Get appointment calendar", description = "Get appointment calendar for a school")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Calendar retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School not found")
    })
    public ResponseEntity<ApiResponse<List<AppointmentCalendarDto>>> getAppointmentCalendar(
            @Parameter(description = "School ID") @PathVariable Long schoolId,
            @Parameter(description = "Start date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpServletRequest request) {

        log.debug("Get appointment calendar request for school: {} from {} to {}", schoolId, startDate, endDate);

        List<AppointmentCalendarDto> calendar = appointmentService.getAppointmentCalendar(schoolId, startDate, endDate, request);

        ApiResponse<List<AppointmentCalendarDto>> response = ApiResponse.success(calendar, "Calendar retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ APPOINTMENT AVAILABILITY ================================

    @GetMapping("/schools/{schoolId}/availability")
    @Operation(summary = "Get school availability", description = "Get appointment availability for a school on a specific date")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Availability retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School not found")
    })
    public ResponseEntity<ApiResponse<AppointmentAvailabilityDto>> getSchoolAvailability(
            @Parameter(description = "School ID") @PathVariable Long schoolId,
            @Parameter(description = "Date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "School name") @RequestParam(required = false) String schoolName,
            HttpServletRequest request) {

        log.debug("Get school availability request: {} for date: {}", schoolId, date);

        AppointmentAvailabilityDto availability = appointmentService.getAvailabilityForDate(schoolId, schoolName, date);

        ApiResponse<AppointmentAvailabilityDto> response = ApiResponse.success(availability, "Availability retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/schools/{schoolId}/availability-range")
    @Operation(summary = "Get school availability range", description = "Get appointment availability for a school across a date range")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Availability range retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School not found")
    })
    public ResponseEntity<ApiResponse<List<AppointmentAvailabilityDto>>> getSchoolAvailabilityRange(
            @Parameter(description = "School ID") @PathVariable Long schoolId,
            @Parameter(description = "Start date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "School name") @RequestParam(required = false) String schoolName,
            HttpServletRequest request) {

        log.debug("Get school availability range request: {} from {} to {}", schoolId, startDate, endDate);

        List<AppointmentAvailabilityDto> availability = appointmentService.getAvailabilityBetweenDates(schoolId, schoolName, startDate, endDate);

        ApiResponse<List<AppointmentAvailabilityDto>> response = ApiResponse.success(availability, "Availability range retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ APPOINTMENT NOTES ================================

    @PostMapping("/{appointmentId}/notes")
    @Operation(summary = "Add appointment note", description = "Add a note to an appointment")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Note added successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Appointment not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid note data")
    })
    public ResponseEntity<ApiResponse<AppointmentNoteDto>> addAppointmentNote(
            @Parameter(description = "Appointment ID") @PathVariable Long appointmentId,
            @Valid @RequestBody AppointmentNoteCreateDto createDto,
            HttpServletRequest request) {

        // Set appointment ID from path
        createDto.setAppointmentId(appointmentId);

        AppointmentNoteDto noteDto = appointmentService.addAppointmentNote(createDto, request);

        ApiResponse<AppointmentNoteDto> response = ApiResponse.success(noteDto, "Note added successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{appointmentId}/notes")
    @Operation(summary = "Get appointment notes", description = "Get all notes for an appointment")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Notes retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Appointment not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<List<AppointmentNoteDto>>> getAppointmentNotes(
            @Parameter(description = "Appointment ID") @PathVariable Long appointmentId,
            HttpServletRequest request) {

        log.debug("Get appointment notes request: {}", appointmentId);

        List<AppointmentNoteDto> notes = appointmentService.getAppointmentNotes(appointmentId, request);

        ApiResponse<List<AppointmentNoteDto>> response = ApiResponse.success(notes, "Notes retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ STATISTICS AND REPORTING ================================

    @GetMapping("/schools/{schoolId}/statistics")
    @Operation(summary = "Get appointment statistics", description = "Get appointment statistics for a school")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School not found")
    })
    public ResponseEntity<ApiResponse<AppointmentStatisticsDto>> getAppointmentStatistics(
            @Parameter(description = "School ID") @PathVariable Long schoolId,
            @Parameter(description = "Period start date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodStart,
            @Parameter(description = "Period end date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodEnd,
            HttpServletRequest request) {

        log.debug("Get appointment statistics request for school: {}", schoolId);

        AppointmentStatisticsDto statistics = appointmentService.getAppointmentStatistics(schoolId, periodStart, periodEnd, request);

        ApiResponse<AppointmentStatisticsDto> response = ApiResponse.success(statistics, "Statistics retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/schools/{schoolId}/staff-performance")
    @Operation(summary = "Get staff performance", description = "Get staff performance metrics for appointments")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Staff performance retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School not found")
    })
    public ResponseEntity<ApiResponse<List<StaffPerformanceDto>>> getStaffPerformance(
            @Parameter(description = "School ID") @PathVariable Long schoolId,
            @Parameter(description = "Period start date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodStart,
            @Parameter(description = "Period end date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodEnd,
            HttpServletRequest request) {

        log.debug("Get staff performance request for school: {}", schoolId);

        List<StaffPerformanceDto> performance = appointmentService.getStaffPerformance(schoolId, periodStart, periodEnd, request);

        ApiResponse<List<StaffPerformanceDto>> response = ApiResponse.success(performance, "Staff performance retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reports/generate")
    @Operation(summary = "Generate appointment report", description = "Generate comprehensive appointment report")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Report generated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid report parameters")
    })
    public ResponseEntity<ApiResponse<AppointmentReportDto>> generateAppointmentReport(
            @Valid @RequestBody ReportGenerationRequestDto reportRequest,
            HttpServletRequest request) {

        AppointmentReportDto report = appointmentService.generateAppointmentReport(
                reportRequest.getReportType(),
                reportRequest.getSchoolId(),
                reportRequest.getPeriodStart(),
                reportRequest.getPeriodEnd(),
                request
        );

        ApiResponse<AppointmentReportDto> response = ApiResponse.success(report, "Report generated successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ BULK OPERATIONS ================================

    @PostMapping("/bulk")
    @Operation(summary = "Bulk update appointments", description = "Perform bulk operations on multiple appointments")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Bulk operation completed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid bulk operation data")
    })
    public ResponseEntity<ApiResponse<BulkAppointmentResultDto>> bulkUpdateAppointments(
            @Valid @RequestBody BulkAppointmentOperationDto bulkDto,
            HttpServletRequest request) {


        BulkAppointmentResultDto result = appointmentService.bulkUpdateAppointments(bulkDto, request);

        ApiResponse<BulkAppointmentResultDto> response = ApiResponse.success(result, "Bulk operation completed");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ WAITLIST OPERATIONS ================================

    @PostMapping("/waitlist")
    @Operation(summary = "Add to waitlist", description = "Add parent to appointment waitlist")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Added to waitlist successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid waitlist data or already in waitlist"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School not found")
    })
    public ResponseEntity<ApiResponse<AppointmentWaitlistDto>> addToWaitlist(
            @Valid @RequestBody AppointmentWaitlistCreateDto createDto,
            HttpServletRequest request) {


        AppointmentWaitlistDto waitlistDto = appointmentService.addToWaitlist(createDto, request);

        ApiResponse<AppointmentWaitlistDto> response = ApiResponse.success(waitlistDto, "Added to waitlist successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ================================ METRICS AND INSIGHTS ================================

    @GetMapping("/schools/{schoolId}/metrics")
    @Operation(summary = "Get appointment metrics", description = "Get appointment metrics and KPIs")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Metrics retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<List<AppointmentMetricsDto>>> getAppointmentMetrics(
            @Parameter(description = "School ID") @PathVariable Long schoolId,
            @Parameter(description = "Period start date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodStart,
            @Parameter(description = "Period end date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodEnd,
            @Parameter(description = "Metric type") @RequestParam(defaultValue = "SUMMARY") String metricType,
            HttpServletRequest request) {

        log.debug("Get appointment metrics request for school: {}", schoolId);

        List<AppointmentMetricsDto> metrics = appointmentService.getAppointmentMetrics(schoolId, periodStart, periodEnd, metricType, request);

        ApiResponse<List<AppointmentMetricsDto>> response = ApiResponse.success(metrics, "Metrics retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/schools/{schoolId}/time-slot-analysis")
    @Operation(summary = "Get time slot analysis", description = "Get time slot performance analysis")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Time slot analysis retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<List<TimeSlotAnalysisDto>>> getTimeSlotAnalysis(
            @Parameter(description = "School ID") @PathVariable Long schoolId,
            @Parameter(description = "Period start date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodStart,
            @Parameter(description = "Period end date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodEnd,
            HttpServletRequest request) {

        log.debug("Get time slot analysis request for school: {}", schoolId);

        List<TimeSlotAnalysisDto> analysis = appointmentService.getTimeSlotAnalysis(schoolId, periodStart, periodEnd, request);

        ApiResponse<List<TimeSlotAnalysisDto>> response = ApiResponse.success(analysis, "Time slot analysis retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/schools/{schoolId}/trends")
    @Operation(summary = "Get appointment trends", description = "Get comprehensive appointment trends and patterns")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Trends retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAppointmentTrends(
            @Parameter(description = "School ID") @PathVariable Long schoolId,
            @Parameter(description = "Period start date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodStart,
            @Parameter(description = "Period end date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodEnd,
            HttpServletRequest request) {

        log.debug("Get appointment trends request for school: {}", schoolId);

        Map<String, Object> trends = appointmentService.getAppointmentTrends(schoolId, periodStart, periodEnd, request);

        ApiResponse<Map<String, Object>> response = ApiResponse.success(trends, "Trends retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ NOTIFICATION AND COMMUNICATION ================================

    @PostMapping("/notifications/send")
    @Operation(summary = "Send appointment notifications", description = "Send notifications for appointment events")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Notifications sent successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    public ResponseEntity<ApiResponse<Void>> sendAppointmentNotifications(
            @Valid @RequestBody AppointmentNotificationDto notificationDto,
            HttpServletRequest request) {


        appointmentService.sendAppointmentNotifications(notificationDto, request);

        ApiResponse<Void> response = ApiResponse.success(null, "Notifications sent successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reminders/send")
    @Operation(summary = "Send appointment reminders", description = "Send automated appointment reminders (system only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reminders processed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "System access required")
    })
    public ResponseEntity<ApiResponse<Void>> sendAppointmentReminders(
            HttpServletRequest request) {


        appointmentService.sendAppointmentReminders(request);

        ApiResponse<Void> response = ApiResponse.success(null, "Reminders processed successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ INTEGRATION ENDPOINTS ================================

    @PostMapping("/{appointmentId}/sync-calendar")
    @Operation(summary = "Sync with external calendar", description = "Sync appointment with external calendar system")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Calendar sync completed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    public ResponseEntity<ApiResponse<AppointmentIntegrationDto>> syncWithExternalCalendar(
            @Parameter(description = "Appointment ID") @PathVariable Long appointmentId,
            @Parameter(description = "Integration type (google, outlook, apple)") @RequestParam String integrationType,
            HttpServletRequest request) {


        AppointmentIntegrationDto integration = appointmentService.syncWithExternalCalendar(appointmentId, integrationType, request);

        ApiResponse<AppointmentIntegrationDto> response = ApiResponse.success(integration, "Calendar sync completed successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ MAINTENANCE OPERATIONS ================================

    @PostMapping("/maintenance/archive-old")
    @Operation(summary = "Archive old appointments", description = "Archive appointments older than specified days (system only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Archiving completed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "System access required")
    })
    public ResponseEntity<ApiResponse<Map<String, Integer>>> archiveOldAppointments(
            @Parameter(description = "Days old threshold") @RequestParam(defaultValue = "365") Integer daysOld,
            HttpServletRequest request) {


        Map<String, Integer> result = appointmentService.archiveOldAppointments(daysOld, request);

        ApiResponse<Map<String, Integer>> response = ApiResponse.success(result, "Archiving completed successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/maintenance/cleanup-expired-slots")
    @Operation(summary = "Cleanup expired slots", description = "Cleanup expired appointment slots (system only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cleanup completed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "System access required")
    })
    public ResponseEntity<ApiResponse<Map<String, Integer>>> cleanupExpiredSlots(
            HttpServletRequest request) {


        Map<String, Integer> result = appointmentService.cleanupExpiredSlots(request);

        ApiResponse<Map<String, Integer>> response = ApiResponse.success(result, "Cleanup completed successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ PUBLIC ENDPOINTS (NO AUTH REQUIRED) ================================

    @GetMapping("/public/schools/{schoolId}/availability")
    @Operation(summary = "Get public school availability", description = "Get public appointment availability (no authentication required)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Public availability retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School not found or not available for public booking")
    })
    public ResponseEntity<ApiResponse<List<AppointmentAvailabilityDto>>> getPublicSchoolAvailability(
            @Parameter(description = "School ID") @PathVariable Long schoolId,
            @Parameter(description = "Date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            HttpServletRequest request) {

        log.debug("Get public school availability request: {} for date: {}", schoolId, date);

        List<AppointmentAvailabilityDto> availability = appointmentService.getPublicSchoolAvailability(schoolId, date);

        ApiResponse<List<AppointmentAvailabilityDto>> response = ApiResponse.success(availability, "Public availability retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/public")
    @Operation(summary = "Create public appointment", description = "Book appointment from public website (no authentication required)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Public appointment created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid appointment data or time slot not available"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School not found or not available for public booking")
    })
    public ResponseEntity<ApiResponse<AppointmentDto>> createPublicAppointment(
            @Valid @RequestBody AppointmentCreateDto createDto,
            HttpServletRequest request) {


        AppointmentDto appointmentDto = appointmentService.createPublicAppointment(createDto);

        ApiResponse<AppointmentDto> response = ApiResponse.success(appointmentDto, "Public appointment created successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/public/number/{appointmentNumber}")
    @Operation(summary = "Get public appointment by number", description = "Get public appointment details by appointment number")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Public appointment retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    public ResponseEntity<ApiResponse<AppointmentDto>> getPublicAppointmentByNumber(
            @Parameter(description = "Appointment number") @PathVariable String appointmentNumber,
            HttpServletRequest request) {

        log.debug("Get public appointment by number request: {}", appointmentNumber);

        AppointmentDto appointmentDto = appointmentService.getPublicAppointmentByNumber(appointmentNumber);

        ApiResponse<AppointmentDto> response = ApiResponse.success(appointmentDto, "Public appointment retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/public/cancel/{appointmentNumber}")
    @Operation(summary = "Cancel public appointment", description = "Cancel appointment from public website using appointment number")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Public appointment canceled successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cannot cancel due to time restrictions"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    public ResponseEntity<ApiResponse<AppointmentDto>> cancelPublicAppointment(
            @Parameter(description = "Appointment number") @PathVariable String appointmentNumber,
            @Valid @RequestBody PublicCancellationRequestDto cancellationRequest,
            HttpServletRequest request) {


        AppointmentDto appointmentDto = appointmentService.cancelPublicAppointment(
                appointmentNumber, cancellationRequest.getCancellationReason());

        ApiResponse<AppointmentDto> response = ApiResponse.success(appointmentDto, "Public appointment canceled successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ HELPER REQUEST DTOs ================================

    public static class ReportGenerationRequestDto {
        private String reportType;
        private Long schoolId;
        private LocalDate periodStart;
        private LocalDate periodEnd;

        public String getReportType() { return reportType; }
        public void setReportType(String reportType) { this.reportType = reportType; }

        public Long getSchoolId() { return schoolId; }
        public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }

        public LocalDate getPeriodStart() { return periodStart; }
        public void setPeriodStart(LocalDate periodStart) { this.periodStart = periodStart; }

        public LocalDate getPeriodEnd() { return periodEnd; }
        public void setPeriodEnd(LocalDate periodEnd) { this.periodEnd = periodEnd; }
    }

    public static class PublicCancellationRequestDto {
        private String cancellationReason;

        public String getCancellationReason() { return cancellationReason; }
        public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }
    }
}