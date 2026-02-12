package com.genixo.education.search.service.hr;

import com.genixo.education.search.common.exception.BusinessException;
import com.genixo.education.search.common.exception.ResourceNotFoundException;
import com.genixo.education.search.dto.hr.*;
import com.genixo.education.search.entity.hr.JobPosting;
import com.genixo.education.search.entity.institution.Campus;
import com.genixo.education.search.entity.location.Province;
import com.genixo.education.search.repository.hr.JobPostingRepository;
import com.genixo.education.search.repository.insitution.CampusRepository;
import com.genixo.education.search.repository.location.ProvinceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class JobPostingService {

    private final JobPostingRepository jobPostingRepository;
    private final CampusRepository campusRepository;
    private final ProvinceRepository provinceRepository;

    @Transactional
    public JobPostingDto create(JobPostingCreateDto dto) {
        Campus campus = campusRepository.findById(dto.getSchoolId())
                .orElseThrow(() -> new ResourceNotFoundException("Campus", dto.getSchoolId()));

        JobPosting jobPosting = new JobPosting();
        jobPosting.setCampus(campus);
        jobPosting.setPositionTitle(dto.getPositionTitle());
        jobPosting.setBranch(dto.getBranch());
        jobPosting.setEmploymentType(dto.getEmploymentType());
        jobPosting.setStartDate(dto.getStartDate());
        jobPosting.setContractDuration(dto.getContractDuration());
        jobPosting.setRequiredExperienceYears(dto.getRequiredExperienceYears());
        jobPosting.setRequiredEducationLevel(dto.getRequiredEducationLevel());
        jobPosting.setSalaryMin(dto.getSalaryMin());
        jobPosting.setSalaryMax(dto.getSalaryMax());
        jobPosting.setShowSalary(dto.getShowSalary() != null ? dto.getShowSalary() : false);
        jobPosting.setDescription(dto.getDescription());
        jobPosting.setApplicationDeadline(dto.getApplicationDeadline());
        jobPosting.setStatus(dto.getStatus() != null ? JobPosting.JobPostingStatus.valueOf(dto.getStatus()) : JobPosting.JobPostingStatus.DRAFT);
        jobPosting.setIsPublic(dto.getIsPublic() != null ? dto.getIsPublic() : true);

        if (dto.getProvinceIds() != null && !dto.getProvinceIds().isEmpty()) {
            List<Province> provinces = provinceRepository.findAllById(dto.getProvinceIds());
            jobPosting.setProvinces(provinces);
        } else {
            jobPosting.setProvinces(new ArrayList<>());
        }

        JobPosting saved = jobPostingRepository.save(jobPosting);
        return mapToDto(saved);
    }

    public Page<JobPostingDto> search(Long schoolId, String branch, String status, String searchTerm, Pageable pageable) {
        return jobPostingRepository.search(schoolId, branch, status, searchTerm, pageable)
                .map(this::mapToDto);
    }

    public JobPostingDto getById(Long id) {
        JobPosting jobPosting = jobPostingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("JobPosting", id));
        return mapToDto(jobPosting);
    }

    public Page<JobPostingDto> getBySchoolId(Long schoolId, Pageable pageable) {
        if (!campusRepository.existsById(schoolId)) {
            throw new ResourceNotFoundException("Campus", schoolId);
        }
        return jobPostingRepository.findByCampusId(schoolId, pageable).map(this::mapToDto);
    }

    @Transactional
    public JobPostingDto update(Long id, JobPostingUpdateDto dto) {
        JobPosting jobPosting = jobPostingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("JobPosting", id));

        if (dto.getPositionTitle() != null) jobPosting.setPositionTitle(dto.getPositionTitle());
        if (dto.getBranch() != null) jobPosting.setBranch(dto.getBranch());
        if (dto.getEmploymentType() != null) jobPosting.setEmploymentType(dto.getEmploymentType());
        if (dto.getStartDate() != null) jobPosting.setStartDate(dto.getStartDate());
        if (dto.getContractDuration() != null) jobPosting.setContractDuration(dto.getContractDuration());
        if (dto.getRequiredExperienceYears() != null) jobPosting.setRequiredExperienceYears(dto.getRequiredExperienceYears());
        if (dto.getRequiredEducationLevel() != null) jobPosting.setRequiredEducationLevel(dto.getRequiredEducationLevel());
        if (dto.getSalaryMin() != null) jobPosting.setSalaryMin(dto.getSalaryMin());
        if (dto.getSalaryMax() != null) jobPosting.setSalaryMax(dto.getSalaryMax());
        if (dto.getShowSalary() != null) jobPosting.setShowSalary(dto.getShowSalary());
        if (dto.getDescription() != null) jobPosting.setDescription(dto.getDescription());
        if (dto.getApplicationDeadline() != null) jobPosting.setApplicationDeadline(dto.getApplicationDeadline());
        if (dto.getStatus() != null) jobPosting.setStatus(JobPosting.JobPostingStatus.valueOf(dto.getStatus()));
        if (dto.getIsPublic() != null) jobPosting.setIsPublic(dto.getIsPublic());
        if (dto.getProvinceIds() != null) {
            jobPosting.setProvinces(dto.getProvinceIds().isEmpty()
                    ? new ArrayList<>()
                    : provinceRepository.findAllById(dto.getProvinceIds()));
        }

        return mapToDto(jobPostingRepository.save(jobPosting));
    }

    @Transactional
    public void delete(Long id) {
        if (!jobPostingRepository.existsById(id)) {
            throw new ResourceNotFoundException("JobPosting", id);
        }
        jobPostingRepository.deleteById(id);
    }

    private JobPostingDto mapToDto(JobPosting j) {
        return JobPostingDto.builder()
                .id(j.getId())
                .schoolId(j.getCampus().getId())
                .campus(CampusSummaryDto.builder()
                        .id(j.getCampus().getId())
                        .name(j.getCampus().getName())
                        .slug(j.getCampus().getSlug())
                        .email(j.getCampus().getEmail())
                        .build())
                .positionTitle(j.getPositionTitle())
                .branch(j.getBranch())
                .employmentType(j.getEmploymentType())
                .startDate(j.getStartDate())
                .contractDuration(j.getContractDuration())
                .requiredExperienceYears(j.getRequiredExperienceYears())
                .requiredEducationLevel(j.getRequiredEducationLevel())
                .salaryMin(j.getSalaryMin())
                .salaryMax(j.getSalaryMax())
                .showSalary(j.getShowSalary())
                .description(j.getDescription())
                .applicationDeadline(j.getApplicationDeadline())
                .status(j.getStatus().name())
                .isPublic(j.getIsPublic())
                .provinces(j.getProvinces() != null ? j.getProvinces().stream()
                        .map(p -> ProvinceSummaryDto.builder().id(p.getId()).name(p.getName()).code(p.getCode()).build())
                        .collect(Collectors.toList()) : Collections.emptyList())
                .applicationCount(j.getApplications() != null ? j.getApplications().size() : 0)
                .createdAt(j.getCreatedAt())
                .updatedAt(j.getUpdatedAt())
                .build();
    }
}
