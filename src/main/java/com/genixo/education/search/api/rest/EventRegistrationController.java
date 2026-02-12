package com.genixo.education.search.api.rest;

import com.genixo.education.search.dto.webinar.EventRegistrationCreateDto;
import com.genixo.education.search.dto.webinar.EventRegistrationDto;
import com.genixo.education.search.dto.webinar.EventRegistrationStatusUpdateDto;
import com.genixo.education.search.service.webinar.EventRegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/webinar/registrations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Webinar - Etkinlik Kayıtları", description = "Etkinlik kayıt yönetimi")
public class EventRegistrationController {

    private final EventRegistrationService eventRegistrationService;

    @PostMapping
    @Operation(summary = "Etkinliğe kayıt ol")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Kayıt oluşturuldu"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Etkinlik veya öğretmen bulunamadı"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Validasyon hatası")
    })
    public ResponseEntity<ApiResponse<EventRegistrationDto>> create(
            @Valid @RequestBody EventRegistrationCreateDto dto,
            HttpServletRequest request) {
        EventRegistrationDto result = eventRegistrationService.create(dto);
        ApiResponse<EventRegistrationDto> response = ApiResponse.success(result, "Kayıt oluşturuldu");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Kayıtları listele/ara")
    public ResponseEntity<ApiResponse<Page<EventRegistrationDto>>> search(
            @Parameter(description = "Etkinlik ID") @RequestParam(required = false) Long eventId,
            @Parameter(description = "Öğretmen ID") @RequestParam(required = false) Long teacherId,
            @Parameter(description = "Kayıt durumu") @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            HttpServletRequest request) {
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<EventRegistrationDto> result = eventRegistrationService.search(eventId, teacherId, status, pageable);
        ApiResponse<Page<EventRegistrationDto>> response = ApiResponse.success(result, "Kayıtlar listelendi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Kayıt detayı")
    public ResponseEntity<ApiResponse<EventRegistrationDto>> getById(
            @PathVariable Long id,
            HttpServletRequest request) {
        EventRegistrationDto result = eventRegistrationService.getById(id);
        ApiResponse<EventRegistrationDto> response = ApiResponse.success(result, "Kayıt getirildi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-event/{eventId}")
    @Operation(summary = "Etkinliğin kayıtları")
    public ResponseEntity<ApiResponse<Page<EventRegistrationDto>>> getByEventId(
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page, size);
        Page<EventRegistrationDto> result = eventRegistrationService.getByEventId(eventId, pageable);
        ApiResponse<Page<EventRegistrationDto>> response = ApiResponse.success(result, "Kayıtlar listelendi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-teacher/{teacherId}")
    @Operation(summary = "Öğretmenin kayıtları")
    public ResponseEntity<ApiResponse<Page<EventRegistrationDto>>> getByTeacherId(
            @PathVariable Long teacherId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page, size);
        Page<EventRegistrationDto> result = eventRegistrationService.getByTeacherId(teacherId, pageable);
        ApiResponse<Page<EventRegistrationDto>> response = ApiResponse.success(result, "Kayıtlar listelendi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Kayıt durumunu güncelle")
    public ResponseEntity<ApiResponse<EventRegistrationDto>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody EventRegistrationStatusUpdateDto dto,
            HttpServletRequest request) {
        EventRegistrationDto result = eventRegistrationService.updateStatus(id, dto);
        ApiResponse<EventRegistrationDto> response = ApiResponse.success(result, "Kayıt durumu güncellendi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/attendance")
    @Operation(summary = "Katılım işaretle")
    public ResponseEntity<ApiResponse<EventRegistrationDto>> markAttendance(
            @PathVariable Long id,
            @RequestParam boolean attended,
            HttpServletRequest request) {
        EventRegistrationDto result = eventRegistrationService.markAttendance(id, attended);
        ApiResponse<EventRegistrationDto> response = ApiResponse.success(result, "Katılım işaretlendi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Kayıt sil")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            HttpServletRequest request) {
        eventRegistrationService.delete(id);
        ApiResponse<Void> response = ApiResponse.success(null, "Kayıt silindi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }
}
