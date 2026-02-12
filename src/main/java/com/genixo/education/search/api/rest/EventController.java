package com.genixo.education.search.api.rest;

import com.genixo.education.search.dto.webinar.EventCreateDto;
import com.genixo.education.search.dto.webinar.EventDto;
import com.genixo.education.search.dto.webinar.EventUpdateDto;
import com.genixo.education.search.service.webinar.EventService;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/webinar/events")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Webinar - Etkinlikler", description = "Etkinlik yönetimi")
public class EventController {

    private final EventService eventService;

    @PostMapping
    @Operation(summary = "Yeni etkinlik oluştur")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Etkinlik oluşturuldu"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Düzenleyen bulunamadı"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Validasyon hatası")
    })
    public ResponseEntity<ApiResponse<EventDto>> create(
            @Valid @RequestBody EventCreateDto dto,
            HttpServletRequest request) {
        EventDto result = eventService.create(dto);
        ApiResponse<EventDto> response = ApiResponse.success(result, "Etkinlik oluşturuldu");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Etkinlikleri listele/ara")
    public ResponseEntity<ApiResponse<Page<EventDto>>> search(
            @Parameter(description = "Düzenleyen ID") @RequestParam(required = false) Long organizerId,
            @Parameter(description = "Kategori ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "Etkinlik tipi") @RequestParam(required = false) String eventType,
            @Parameter(description = "Durum") @RequestParam(required = false) String status,
            @Parameter(description = "Arama terimi") @RequestParam(required = false) String searchTerm,
            @Parameter(description = "Başlangıç tarihinden") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateFrom,
            @Parameter(description = "Başlangıç tarihine") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "startDateTime") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir,
            HttpServletRequest request) {
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<EventDto> result = eventService.search(organizerId, categoryId, eventType, status, searchTerm, startDateFrom, startDateTo, pageable);
        ApiResponse<Page<EventDto>> response = ApiResponse.success(result, "Etkinlikler listelendi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/published")
    @Operation(summary = "Yayındaki etkinlikler")
    public ResponseEntity<ApiResponse<Page<EventDto>>> getPublished(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("startDateTime").ascending());
        Page<EventDto> result = eventService.getPublishedEvents(pageable);
        ApiResponse<Page<EventDto>> response = ApiResponse.success(result, "Yayındaki etkinlikler listelendi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Etkinlik detayı")
    public ResponseEntity<ApiResponse<EventDto>> getById(
            @PathVariable Long id,
            HttpServletRequest request) {
        EventDto result = eventService.getById(id);
        ApiResponse<EventDto> response = ApiResponse.success(result, "Etkinlik getirildi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-organizer/{organizerId}")
    @Operation(summary = "Düzenleyenin etkinlikleri")
    public ResponseEntity<ApiResponse<Page<EventDto>>> getByOrganizerId(
            @PathVariable Long organizerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page, size);
        Page<EventDto> result = eventService.getByOrganizerId(organizerId, pageable);
        ApiResponse<Page<EventDto>> response = ApiResponse.success(result, "Etkinlikler listelendi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Etkinlik güncelle")
    public ResponseEntity<ApiResponse<EventDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody EventUpdateDto dto,
            HttpServletRequest request) {
        EventDto result = eventService.update(id, dto);
        ApiResponse<EventDto> response = ApiResponse.success(result, "Etkinlik güncellendi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Etkinlik sil")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            HttpServletRequest request) {
        eventService.delete(id);
        ApiResponse<Void> response = ApiResponse.success(null, "Etkinlik silindi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }
}
