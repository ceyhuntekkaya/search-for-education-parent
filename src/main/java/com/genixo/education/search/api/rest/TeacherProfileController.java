package com.genixo.education.search.api.rest;

import com.genixo.education.search.common.exception.BusinessException;
import com.genixo.education.search.dto.hr.*;
import com.genixo.education.search.entity.user.Role;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.service.auth.JwtService;
import com.genixo.education.search.service.hr.TeacherProfileService;
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
@RequestMapping("/hr/teacher-profiles")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "HR - Öğretmen Profilleri", description = "Öğretmen profili yönetimi")
public class TeacherProfileController {

    private final TeacherProfileService teacherProfileService;
    private final JwtService jwtService;

    @PostMapping
    @Operation(summary = "Yeni öğretmen profili oluştur (sadece TEACHER rolü)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Profil oluşturuldu"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Sadece öğretmenler profil oluşturabilir"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Kullanıcı zaten profil sahibi"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Validasyon hatası")
    })
    public ResponseEntity<ApiResponse<TeacherProfileDto>> create(
            @Valid @RequestBody TeacherProfileCreateDto dto,
            HttpServletRequest request) {
        User user = jwtService.getUser(request);
        boolean isTeacher = user.getUserRoles().stream()
                .anyMatch(ur -> ur.getRole() == Role.TEACHER);
        if (!isTeacher) {
            throw BusinessException.accessDenied("Öğretmen profili oluşturmak için TEACHER rolü gerekli");
        }
        TeacherProfileDto result = teacherProfileService.create(dto, user.getId());
        ApiResponse<TeacherProfileDto> response = ApiResponse.success(result, "Öğretmen profili oluşturuldu");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Öğretmen profillerini listele/ara")
    public ResponseEntity<ApiResponse<Page<TeacherProfileDto>>> search(
            @Parameter(description = "Branş") @RequestParam(required = false) String branch,
            @Parameter(description = "Arama terimi") @RequestParam(required = false) String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            HttpServletRequest request) {
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TeacherProfileDto> result = teacherProfileService.search(branch, searchTerm, pageable);
        ApiResponse<Page<TeacherProfileDto>> response = ApiResponse.success(result, "Profiller listelendi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Profil detayı")
    public ResponseEntity<ApiResponse<TeacherProfileDto>> getById(
            @PathVariable Long id,
            HttpServletRequest request) {
        TeacherProfileDto result = teacherProfileService.getById(id);
        ApiResponse<TeacherProfileDto> response = ApiResponse.success(result, "Profil getirildi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-user/{userId}")
    @Operation(summary = "Kullanıcı ID ile profil getir")
    public ResponseEntity<ApiResponse<TeacherProfileDto>> getByUserId(
            @PathVariable Long userId,
            HttpServletRequest request) {
        TeacherProfileDto result = teacherProfileService.getByUserId(userId);
        ApiResponse<TeacherProfileDto> response = ApiResponse.success(result, "Profil getirildi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Profil güncelle")
    public ResponseEntity<ApiResponse<TeacherProfileDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody TeacherProfileUpdateDto dto,
            HttpServletRequest request) {
        TeacherProfileDto result = teacherProfileService.update(id, dto);
        ApiResponse<TeacherProfileDto> response = ApiResponse.success(result, "Profil güncellendi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Profil sil")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            HttpServletRequest request) {
        teacherProfileService.delete(id);
        ApiResponse<Void> response = ApiResponse.success(null, "Profil silindi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    // --- Eğitim bilgileri (sadece profil sahibi yazabilir) ---

    @PostMapping("/{profileId}/educations")
    @Operation(summary = "Profiline eğitim bilgisi ekle")
    public ResponseEntity<ApiResponse<TeacherEducationDto>> addEducation(
            @PathVariable Long profileId,
            @Valid @RequestBody TeacherEducationCreateDto dto,
            HttpServletRequest request) {
        ensureProfileOwnedBy(profileId, request);
        TeacherEducationDto result = teacherProfileService.addEducation(profileId, dto);
        ApiResponse<TeacherEducationDto> response = ApiResponse.success(result, "Eğitim bilgisi eklendi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{profileId}/educations")
    @Operation(summary = "Profil eğitim listesi")
    public ResponseEntity<ApiResponse<List<TeacherEducationDto>>> getEducations(
            @PathVariable Long profileId,
            HttpServletRequest request) {
        List<TeacherEducationDto> result = teacherProfileService.getEducations(profileId);
        ApiResponse<List<TeacherEducationDto>> response = ApiResponse.success(result, "Eğitim listesi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{profileId}/educations/{educationId}")
    @Operation(summary = "Eğitim bilgisini güncelle")
    public ResponseEntity<ApiResponse<TeacherEducationDto>> updateEducation(
            @PathVariable Long profileId,
            @PathVariable Long educationId,
            @Valid @RequestBody TeacherEducationUpdateDto dto,
            HttpServletRequest request) {
        ensureProfileOwnedBy(profileId, request);
        TeacherEducationDto result = teacherProfileService.updateEducation(profileId, educationId, dto);
        ApiResponse<TeacherEducationDto> response = ApiResponse.success(result, "Eğitim bilgisi güncellendi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{profileId}/educations/{educationId}")
    @Operation(summary = "Eğitim bilgisini sil")
    public ResponseEntity<ApiResponse<Void>> deleteEducation(
            @PathVariable Long profileId,
            @PathVariable Long educationId,
            HttpServletRequest request) {
        ensureProfileOwnedBy(profileId, request);
        teacherProfileService.deleteEducation(profileId, educationId);
        ApiResponse<Void> response = ApiResponse.success(null, "Eğitim bilgisi silindi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    // --- Tecrübe (sadece profil sahibi yazabilir) ---

    @PostMapping("/{profileId}/experiences")
    @Operation(summary = "Profiline tecrübe ekle")
    public ResponseEntity<ApiResponse<TeacherExperienceDto>> addExperience(
            @PathVariable Long profileId,
            @Valid @RequestBody TeacherExperienceCreateDto dto,
            HttpServletRequest request) {
        ensureProfileOwnedBy(profileId, request);
        TeacherExperienceDto result = teacherProfileService.addExperience(profileId, dto);
        ApiResponse<TeacherExperienceDto> response = ApiResponse.success(result, "Tecrübe eklendi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{profileId}/experiences")
    @Operation(summary = "Profil tecrübe listesi")
    public ResponseEntity<ApiResponse<List<TeacherExperienceDto>>> getExperiences(
            @PathVariable Long profileId,
            HttpServletRequest request) {
        List<TeacherExperienceDto> result = teacherProfileService.getExperiences(profileId);
        ApiResponse<List<TeacherExperienceDto>> response = ApiResponse.success(result, "Tecrübe listesi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{profileId}/experiences/{experienceId}")
    @Operation(summary = "Tecrübe güncelle")
    public ResponseEntity<ApiResponse<TeacherExperienceDto>> updateExperience(
            @PathVariable Long profileId,
            @PathVariable Long experienceId,
            @Valid @RequestBody TeacherExperienceUpdateDto dto,
            HttpServletRequest request) {
        ensureProfileOwnedBy(profileId, request);
        TeacherExperienceDto result = teacherProfileService.updateExperience(profileId, experienceId, dto);
        ApiResponse<TeacherExperienceDto> response = ApiResponse.success(result, "Tecrübe güncellendi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{profileId}/experiences/{experienceId}")
    @Operation(summary = "Tecrübe sil")
    public ResponseEntity<ApiResponse<Void>> deleteExperience(
            @PathVariable Long profileId,
            @PathVariable Long experienceId,
            HttpServletRequest request) {
        ensureProfileOwnedBy(profileId, request);
        teacherProfileService.deleteExperience(profileId, experienceId);
        ApiResponse<Void> response = ApiResponse.success(null, "Tecrübe silindi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    private void ensureProfileOwnedBy(Long profileId, HttpServletRequest request) {
        User user = jwtService.getUser(request);
        TeacherProfileDto profile = teacherProfileService.getById(profileId);
        if (!profile.getUserId().equals(user.getId())) {
            throw BusinessException.accessDenied("Bu profile ait eğitim/tecrübe yalnızca profil sahibi tarafından düzenlenebilir");
        }
    }
}
