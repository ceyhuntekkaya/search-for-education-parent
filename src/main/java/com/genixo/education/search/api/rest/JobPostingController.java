package com.genixo.education.search.api.rest;

import com.genixo.education.search.dto.hr.JobPostingCreateDto;
import com.genixo.education.search.dto.hr.JobPostingDto;
import com.genixo.education.search.dto.hr.JobPostingUpdateDto;
import com.genixo.education.search.service.hr.JobPostingService;
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
@RequestMapping("/hr/job-postings")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "HR - İş İlanları", description = "İş ilanları yönetimi")
public class JobPostingController {

    private final JobPostingService jobPostingService;

    @PostMapping
    @Operation(summary = "Yeni ilan oluştur")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "İlan oluşturuldu"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Okul bulunamadı"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Validasyon hatası")
    })
    public ResponseEntity<ApiResponse<JobPostingDto>> create(
            @Valid @RequestBody JobPostingCreateDto dto,
            HttpServletRequest request) {
        JobPostingDto result = jobPostingService.create(dto);
        ApiResponse<JobPostingDto> response = ApiResponse.success(result, "İlan oluşturuldu");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "İlanları listele/ara")
    public ResponseEntity<ApiResponse<Page<JobPostingDto>>> search(
            @Parameter(description = "Okul ID") @RequestParam(required = false) Long schoolId,
            @Parameter(description = "Branş") @RequestParam(required = false) String branch,
            @Parameter(description = "Durum") @RequestParam(required = false) String status,
            @Parameter(description = "Arama terimi") @RequestParam(required = false) String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            HttpServletRequest request) {
        // Native query uses DB column names; map entity property names to column names
        String sortColumn = mapJobPostingSortToColumn(sortBy);
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortColumn).ascending() : Sort.by(sortColumn).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<JobPostingDto> result = jobPostingService.search(schoolId, branch, status, searchTerm, pageable);
        ApiResponse<Page<JobPostingDto>> response = ApiResponse.success(result, "İlanlar listelendi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    /**
     * Maps JobPosting entity property names to job_postings table column names
     * for native query sort (ORDER BY jp.column_name).
     */
    private static String mapJobPostingSortToColumn(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return "created_at";
        }
        return switch (sortBy) {
            case "createdAt" -> "created_at";
            case "updatedAt" -> "updated_at";
            case "positionTitle" -> "position_title";
            case "employmentType" -> "employment_type";
            case "startDate" -> "start_date";
            case "contractDuration" -> "contract_duration";
            case "requiredExperienceYears" -> "required_experience_years";
            case "requiredEducationLevel" -> "required_education_level";
            case "salaryMin" -> "salary_min";
            case "salaryMax" -> "salary_max";
            case "showSalary" -> "show_salary";
            case "applicationDeadline" -> "application_deadline";
            case "isPublic" -> "is_public";
            case "createdBy" -> "created_by";
            case "updatedBy" -> "updated_by";
            case "isActive" -> "is_active";
            default -> sortBy; // already snake_case or unknown, use as-is
        };
    }

    @GetMapping("/{id}")
    @Operation(summary = "İlan detayı")
    public ResponseEntity<ApiResponse<JobPostingDto>> getById(
            @PathVariable Long id,
            HttpServletRequest request) {
        JobPostingDto result = jobPostingService.getById(id);
        ApiResponse<JobPostingDto> response = ApiResponse.success(result, "İlan getirildi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-school/{schoolId}")
    @Operation(summary = "Okulun ilanları")
    public ResponseEntity<ApiResponse<Page<JobPostingDto>>> getBySchoolId(
            @PathVariable Long schoolId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page, size);
        Page<JobPostingDto> result = jobPostingService.getBySchoolId(schoolId, pageable);
        ApiResponse<Page<JobPostingDto>> response = ApiResponse.success(result, "İlanlar listelendi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "İlan güncelle")
    public ResponseEntity<ApiResponse<JobPostingDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody JobPostingUpdateDto dto,
            HttpServletRequest request) {
        JobPostingDto result = jobPostingService.update(id, dto);
        ApiResponse<JobPostingDto> response = ApiResponse.success(result, "İlan güncellendi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "İlan sil")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            HttpServletRequest request) {
        jobPostingService.delete(id);
        ApiResponse<Void> response = ApiResponse.success(null, "İlan silindi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }
}
