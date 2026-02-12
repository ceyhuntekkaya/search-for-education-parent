package com.genixo.education.search.api.rest;

import com.genixo.education.search.dto.hr.*;
import com.genixo.education.search.service.auth.JwtService;
import com.genixo.education.search.service.hr.ApplicationService;
import com.genixo.education.search.entity.user.User;
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
import java.util.List;

@RestController
@RequestMapping("/hr/applications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "HR - Başvurular", description = "Başvuru yönetimi")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final JwtService jwtService;

    @PostMapping
    @Operation(summary = "Yeni başvuru oluştur")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Başvuru oluşturuldu"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Zaten başvurulmuş veya ilan kapalı"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "İlan veya öğretmen profili bulunamadı"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Validasyon hatası")
    })
    public ResponseEntity<ApiResponse<ApplicationDto>> create(
            @Valid @RequestBody ApplicationCreateDto dto,
            HttpServletRequest request) {
        User user = jwtService.getUser(request);
        ApplicationDto result = applicationService.create(dto, user.getId());
        ApiResponse<ApplicationDto> response = ApiResponse.success(result, "Başvuru oluşturuldu");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Başvuruları listele/ara")
    public ResponseEntity<ApiResponse<Page<ApplicationDto>>> search(
            @Parameter(description = "İlan ID") @RequestParam(required = false) Long jobPostingId,
            @Parameter(description = "Öğretmen ID") @RequestParam(required = false) Long teacherId,
            @Parameter(description = "Durum") @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            HttpServletRequest request) {
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ApplicationDto> result = applicationService.search(jobPostingId, teacherId, status, pageable);
        ApiResponse<Page<ApplicationDto>> response = ApiResponse.success(result, "Başvurular listelendi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Başvuru detayı")
    public ResponseEntity<ApiResponse<ApplicationDto>> getById(
            @PathVariable Long id,
            HttpServletRequest request) {
        ApplicationDto result = applicationService.getById(id);
        ApiResponse<ApplicationDto> response = ApiResponse.success(result, "Başvuru getirildi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-job-posting/{jobPostingId}")
    @Operation(summary = "İlanın başvuruları")
    public ResponseEntity<ApiResponse<Page<ApplicationDto>>> getByJobPostingId(
            @PathVariable Long jobPostingId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ApplicationDto> result = applicationService.getByJobPostingId(jobPostingId, pageable);
        ApiResponse<Page<ApplicationDto>> response = ApiResponse.success(result, "Başvurular listelendi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-teacher/{teacherId}")
    @Operation(summary = "Öğretmenin başvuruları")
    public ResponseEntity<ApiResponse<Page<ApplicationDto>>> getByTeacherId(
            @PathVariable Long teacherId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ApplicationDto> result = applicationService.getByTeacherId(teacherId, pageable);
        ApiResponse<Page<ApplicationDto>> response = ApiResponse.success(result, "Başvurular listelendi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-campus/{campusId}")
    @Operation(summary = "Okulun tüm başvuruları")
    public ResponseEntity<ApiResponse<Page<ApplicationDto>>> getByCampusId(
            @PathVariable Long campusId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ApplicationDto> result = applicationService.getByCampusId(campusId, pageable);
        ApiResponse<Page<ApplicationDto>> response = ApiResponse.success(result, "Başvurular listelendi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Başvuru durumunu güncelle")
    public ResponseEntity<ApiResponse<ApplicationDto>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody ApplicationStatusUpdateDto dto,
            HttpServletRequest request) {
        User user = jwtService.getUser(request);
        ApplicationDto result = applicationService.updateStatus(id, dto, user.getId());
        ApiResponse<ApplicationDto> response = ApiResponse.success(result, "Durum güncellendi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/withdraw")
    @Operation(summary = "Başvuruyu geri çek")
    public ResponseEntity<ApiResponse<ApplicationDto>> withdraw(
            @PathVariable Long id,
            HttpServletRequest request) {
        User user = jwtService.getUser(request);
        ApplicationDto result = applicationService.withdraw(id, user.getId());
        ApiResponse<ApplicationDto> response = ApiResponse.success(result, "Başvuru geri çekildi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/notes")
    @Operation(summary = "Başvuru notları")
    public ResponseEntity<ApiResponse<List<ApplicationNoteDto>>> getNotes(
            @PathVariable Long id,
            HttpServletRequest request) {
        List<ApplicationNoteDto> result = applicationService.getNotes(id);
        ApiResponse<List<ApplicationNoteDto>> response = ApiResponse.success(result, "Notlar getirildi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/notes")
    @Operation(summary = "Başvuruya not ekle")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Not eklendi")
    })
    public ResponseEntity<ApiResponse<ApplicationNoteDto>> addNote(
            @PathVariable Long id,
            @Valid @RequestBody ApplicationNoteCreateDto dto,
            HttpServletRequest request) {
        User user = jwtService.getUser(request);
        ApplicationNoteCreateDto noteDto = ApplicationNoteCreateDto.builder()
                .applicationId(id)
                .noteText(dto.getNoteText())
                .build();
        ApplicationNoteDto result = applicationService.addNote(id, noteDto, user.getId());
        ApiResponse<ApplicationNoteDto> response = ApiResponse.success(result, "Not eklendi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/documents")
    @Operation(summary = "Başvuru belgeleri")
    public ResponseEntity<ApiResponse<List<ApplicationDocumentDto>>> getDocuments(
            @PathVariable Long id,
            HttpServletRequest request) {
        List<ApplicationDocumentDto> result = applicationService.getDocuments(id);
        ApiResponse<List<ApplicationDocumentDto>> response = ApiResponse.success(result, "Belgeler getirildi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/documents")
    @Operation(summary = "Başvuruya belge ekle")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Belge eklendi")
    })
    public ResponseEntity<ApiResponse<ApplicationDocumentDto>> addDocument(
            @PathVariable Long id,
            @Valid @RequestBody ApplicationDocumentCreateDto dto,
            HttpServletRequest request) {
        ApplicationDocumentCreateDto docDto = ApplicationDocumentCreateDto.builder()
                .documentName(dto.getDocumentName())
                .documentUrl(dto.getDocumentUrl())
                .documentType(dto.getDocumentType())
                .fileSize(dto.getFileSize())
                .build();
        ApplicationDocumentDto result = applicationService.addDocument(id, docDto);
        ApiResponse<ApplicationDocumentDto> response = ApiResponse.success(result, "Belge eklendi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}/documents/{documentId}")
    @Operation(summary = "Başvuru belgesini sil")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(
            @PathVariable Long id,
            @PathVariable Long documentId,
            HttpServletRequest request) {
        applicationService.deleteDocument(id, documentId);
        ApiResponse<Void> response = ApiResponse.success(null, "Belge silindi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }
}
