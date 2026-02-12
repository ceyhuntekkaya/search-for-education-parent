package com.genixo.education.search.api.rest;

import com.genixo.education.search.dto.webinar.EventOrganizerCreateDto;
import com.genixo.education.search.dto.webinar.EventOrganizerDto;
import com.genixo.education.search.dto.webinar.EventOrganizerUpdateDto;
import com.genixo.education.search.service.webinar.EventOrganizerService;
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
@RequestMapping("/webinar/organizers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Webinar - Etkinlik Düzenleyenler", description = "Etkinlik düzenleyen kurum/kişi yönetimi")
public class EventOrganizerController {

    private final EventOrganizerService eventOrganizerService;

    @PostMapping
    @Operation(summary = "Yeni düzenleyen oluştur")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Düzenleyen oluşturuldu"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Kullanıcı bulunamadı"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Validasyon hatası")
    })
    public ResponseEntity<ApiResponse<EventOrganizerDto>> create(
            @Valid @RequestBody EventOrganizerCreateDto dto,
            HttpServletRequest request) {
        EventOrganizerDto result = eventOrganizerService.create(dto);
        ApiResponse<EventOrganizerDto> response = ApiResponse.success(result, "Düzenleyen oluşturuldu");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Düzenleyenleri listele/ara")
    public ResponseEntity<ApiResponse<Page<EventOrganizerDto>>> search(
            @Parameter(description = "Düzenleyen tipi") @RequestParam(required = false) String type,
            @Parameter(description = "Arama terimi") @RequestParam(required = false) String searchTerm,
            @Parameter(description = "Aktiflik filtresi") @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            HttpServletRequest request) {
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<EventOrganizerDto> result = eventOrganizerService.search(type, searchTerm, isActive, pageable);
        ApiResponse<Page<EventOrganizerDto>> response = ApiResponse.success(result, "Düzenleyenler listelendi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    @Operation(summary = "Aktif düzenleyenler")
    public ResponseEntity<ApiResponse<Page<EventOrganizerDto>>> listActive(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page, size);
        Page<EventOrganizerDto> result = eventOrganizerService.listActive(pageable);
        ApiResponse<Page<EventOrganizerDto>> response = ApiResponse.success(result, "Aktif düzenleyenler listelendi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Düzenleyen detayı")
    public ResponseEntity<ApiResponse<EventOrganizerDto>> getById(
            @PathVariable Long id,
            HttpServletRequest request) {
        EventOrganizerDto result = eventOrganizerService.getById(id);
        ApiResponse<EventOrganizerDto> response = ApiResponse.success(result, "Düzenleyen getirildi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-slug/{slug}")
    @Operation(summary = "Düzenleyen detayı (slug ile)")
    public ResponseEntity<ApiResponse<EventOrganizerDto>> getBySlug(
            @PathVariable String slug,
            HttpServletRequest request) {
        EventOrganizerDto result = eventOrganizerService.getBySlug(slug);
        ApiResponse<EventOrganizerDto> response = ApiResponse.success(result, "Düzenleyen getirildi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Düzenleyen güncelle")
    public ResponseEntity<ApiResponse<EventOrganizerDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody EventOrganizerUpdateDto dto,
            HttpServletRequest request) {
        EventOrganizerDto result = eventOrganizerService.update(id, dto);
        ApiResponse<EventOrganizerDto> response = ApiResponse.success(result, "Düzenleyen güncellendi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Düzenleyen sil")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            HttpServletRequest request) {
        eventOrganizerService.delete(id);
        ApiResponse<Void> response = ApiResponse.success(null, "Düzenleyen silindi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }
}
