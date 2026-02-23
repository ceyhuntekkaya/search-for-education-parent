package com.genixo.education.search.service.hr;

import com.genixo.education.search.common.exception.BusinessException;
import com.genixo.education.search.common.exception.ResourceNotFoundException;
import com.genixo.education.search.dto.hr.*;
import com.genixo.education.search.entity.hr.TeacherEducation;
import com.genixo.education.search.entity.hr.TeacherExperience;
import com.genixo.education.search.entity.hr.TeacherProfile;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.repository.hr.TeacherEducationRepository;
import com.genixo.education.search.repository.hr.TeacherExperienceRepository;
import com.genixo.education.search.repository.hr.TeacherProfileRepository;
import com.genixo.education.search.repository.location.ProvinceRepository;
import com.genixo.education.search.repository.user.UserRepository;
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
public class TeacherProfileService {

    private final TeacherProfileRepository teacherProfileRepository;
    private final UserRepository userRepository;
    private final ProvinceRepository provinceRepository;
    private final TeacherEducationRepository teacherEducationRepository;
    private final TeacherExperienceRepository teacherExperienceRepository;

    @Transactional
    public TeacherProfileDto create(TeacherProfileCreateDto dto, Long currentUserId) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", currentUserId));

        if (teacherProfileRepository.existsByUserId(currentUserId)) {
            throw BusinessException.duplicateResource("TeacherProfile", "userId", currentUserId);
        }
        if (teacherProfileRepository.existsByEmail(dto.getEmail())) {
            throw BusinessException.duplicateResource("TeacherProfile", "email", dto.getEmail());
        }

        TeacherProfile profile = new TeacherProfile();
        profile.setUser(user);
        profile.setFullName(dto.getFullName());
        profile.setEmail(dto.getEmail());
        profile.setPhone(dto.getPhone());
        profile.setCity(dto.getCity());
        profile.setBranch(dto.getBranch());
        profile.setBio(dto.getBio());
        profile.setProfilePhotoUrl(dto.getProfilePhotoUrl());
        profile.setVideoUrl(dto.getVideoUrl());
        profile.setCvUrl(dto.getCvUrl());
        profile.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);

        if (dto.getProvinceIds() != null && !dto.getProvinceIds().isEmpty()) {
            profile.setProvinces(provinceRepository.findAllById(dto.getProvinceIds()));
        } else {
            profile.setProvinces(new ArrayList<>());
        }

        TeacherProfile saved = teacherProfileRepository.save(profile);
        return mapToDto(saved, true);
    }

    public Page<TeacherProfileDto> search(String branch, String searchTerm, Pageable pageable) {
        return teacherProfileRepository.search(branch, searchTerm, pageable)
                .map(p -> mapToDto(p, false));
    }

    public TeacherProfileDto getById(Long id) {
        TeacherProfile profile = teacherProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TeacherProfile", id));
        return mapToDto(profile, true);
    }

    public TeacherProfileDto getByUserId(Long userId) {
        TeacherProfile profile = teacherProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("TeacherProfile", "userId", userId));
        return mapToDto(profile, true);
    }

    @Transactional
    public TeacherProfileDto update(Long id, TeacherProfileUpdateDto dto) {
        TeacherProfile profile = teacherProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TeacherProfile", id));

        if (dto.getFullName() != null) profile.setFullName(dto.getFullName());
        if (dto.getEmail() != null) {
            if (teacherProfileRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
                throw BusinessException.duplicateResource("TeacherProfile", "email", dto.getEmail());
            }
            profile.setEmail(dto.getEmail());
        }
        if (dto.getPhone() != null) profile.setPhone(dto.getPhone());
        if (dto.getCity() != null) profile.setCity(dto.getCity());
        if (dto.getBranch() != null) profile.setBranch(dto.getBranch());
        if (dto.getBio() != null) profile.setBio(dto.getBio());
        if (dto.getProfilePhotoUrl() != null) profile.setProfilePhotoUrl(dto.getProfilePhotoUrl());
        if (dto.getVideoUrl() != null) profile.setVideoUrl(dto.getVideoUrl());
        if (dto.getCvUrl() != null) profile.setCvUrl(dto.getCvUrl());
        if (dto.getIsActive() != null) profile.setIsActive(dto.getIsActive());
        if (dto.getProvinceIds() != null) {
            profile.setProvinces(dto.getProvinceIds().isEmpty()
                    ? new ArrayList<>()
                    : provinceRepository.findAllById(dto.getProvinceIds()));
        }

        return mapToDto(teacherProfileRepository.save(profile), true);
    }

    @Transactional
    public void delete(Long id) {
        if (!teacherProfileRepository.existsById(id)) {
            throw new ResourceNotFoundException("TeacherProfile", id);
        }
        teacherProfileRepository.deleteById(id);
    }

    private TeacherProfileDto mapToDto(TeacherProfile t, boolean includeEducationAndExperience) {
        List<TeacherEducationDto> educations = Collections.emptyList();
        List<TeacherExperienceDto> experiences = Collections.emptyList();
        if (includeEducationAndExperience && t.getEducations() != null) {
            educations = t.getEducations().stream()
                    .sorted((a, b) -> {
                        int o = Integer.compare(a.getDisplayOrder() != null ? a.getDisplayOrder() : 0,
                                b.getDisplayOrder() != null ? b.getDisplayOrder() : 0);
                        if (o != 0) return o;
                        Integer ay = a.getEndYear();
                        Integer by = b.getEndYear();
                        if (ay == null && by == null) return 0;
                        if (ay == null) return 1;
                        if (by == null) return -1;
                        return Integer.compare(by, ay);
                    })
                    .map(this::mapToEducationDto)
                    .collect(Collectors.toList());
        }
        if (includeEducationAndExperience && t.getExperiences() != null) {
            experiences = t.getExperiences().stream()
                    .sorted((a, b) -> {
                        int o = Integer.compare(a.getDisplayOrder() != null ? a.getDisplayOrder() : 0,
                                b.getDisplayOrder() != null ? b.getDisplayOrder() : 0);
                        if (o != 0) return o;
                        int d = (b.getEndDate() != null ? b.getEndDate() : java.time.LocalDate.MAX)
                                .compareTo(a.getEndDate() != null ? a.getEndDate() : java.time.LocalDate.MAX);
                        return d;
                    })
                    .map(this::mapToExperienceDto)
                    .collect(Collectors.toList());
        }
        return TeacherProfileDto.builder()
                .id(t.getId())
                .userId(t.getUser().getId())
                .fullName(t.getFullName())
                .email(t.getEmail())
                .phone(t.getPhone())
                .city(t.getCity())
                .branch(t.getBranch())
                .bio(t.getBio())
                .profilePhotoUrl(t.getProfilePhotoUrl())
                .videoUrl(t.getVideoUrl())
                .cvUrl(t.getCvUrl())
                .isActive(t.getIsActive())
                .provinces(t.getProvinces() != null ? t.getProvinces().stream()
                        .map(p -> ProvinceSummaryDto.builder().id(p.getId()).name(p.getName()).code(p.getCode()).build())
                        .collect(Collectors.toList()) : Collections.emptyList())
                .educations(educations)
                .experiences(experiences)
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .build();
    }

    private TeacherEducationDto mapToEducationDto(TeacherEducation e) {
        return TeacherEducationDto.builder()
                .id(e.getId())
                .teacherProfileId(e.getTeacherProfile().getId())
                .educationLevel(e.getEducationLevel())
                .institution(e.getInstitution())
                .department(e.getDepartment())
                .startYear(e.getStartYear())
                .endYear(e.getEndYear())
                .displayOrder(e.getDisplayOrder())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    private TeacherExperienceDto mapToExperienceDto(TeacherExperience e) {
        return TeacherExperienceDto.builder()
                .id(e.getId())
                .teacherProfileId(e.getTeacherProfile().getId())
                .institution(e.getInstitution())
                .roleTitle(e.getRoleTitle())
                .startDate(e.getStartDate())
                .endDate(e.getEndDate())
                .description(e.getDescription())
                .displayOrder(e.getDisplayOrder())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    // --- Eğitim CRUD ---

    @Transactional
    public TeacherEducationDto addEducation(Long profileId, TeacherEducationCreateDto dto) {
        TeacherProfile profile = teacherProfileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("TeacherProfile", profileId));
        TeacherEducation entity = new TeacherEducation();
        entity.setTeacherProfile(profile);
        entity.setEducationLevel(dto.getEducationLevel());
        entity.setInstitution(dto.getInstitution());
        entity.setDepartment(dto.getDepartment());
        entity.setStartYear(dto.getStartYear());
        entity.setEndYear(dto.getEndYear());
        entity.setDisplayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0);
        TeacherEducation saved = teacherEducationRepository.save(entity);
        return mapToEducationDto(saved);
    }

    public List<TeacherEducationDto> getEducations(Long profileId) {
        return teacherEducationRepository.findByTeacherProfileIdOrderByDisplayOrderAscEndYearDesc(profileId)
                .stream()
                .map(this::mapToEducationDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public TeacherEducationDto updateEducation(Long profileId, Long educationId, TeacherEducationUpdateDto dto) {
        TeacherEducation entity = teacherEducationRepository.findById(educationId)
                .orElseThrow(() -> new ResourceNotFoundException("TeacherEducation", educationId));
        if (!entity.getTeacherProfile().getId().equals(profileId)) {
            throw new ResourceNotFoundException("TeacherEducation", educationId);
        }
        if (dto.getEducationLevel() != null) entity.setEducationLevel(dto.getEducationLevel());
        if (dto.getInstitution() != null) entity.setInstitution(dto.getInstitution());
        if (dto.getDepartment() != null) entity.setDepartment(dto.getDepartment());
        if (dto.getStartYear() != null) entity.setStartYear(dto.getStartYear());
        if (dto.getEndYear() != null) entity.setEndYear(dto.getEndYear());
        if (dto.getDisplayOrder() != null) entity.setDisplayOrder(dto.getDisplayOrder());
        TeacherEducation saved = teacherEducationRepository.save(entity);
        return mapToEducationDto(saved);
    }

    @Transactional
    public void deleteEducation(Long profileId, Long educationId) {
        TeacherEducation entity = teacherEducationRepository.findById(educationId)
                .orElseThrow(() -> new ResourceNotFoundException("TeacherEducation", educationId));
        if (!entity.getTeacherProfile().getId().equals(profileId)) {
            throw new ResourceNotFoundException("TeacherEducation", educationId);
        }
        teacherEducationRepository.delete(entity);
    }

    // --- Tecrübe CRUD ---

    @Transactional
    public TeacherExperienceDto addExperience(Long profileId, TeacherExperienceCreateDto dto) {
        TeacherProfile profile = teacherProfileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("TeacherProfile", profileId));
        TeacherExperience entity = new TeacherExperience();
        entity.setTeacherProfile(profile);
        entity.setInstitution(dto.getInstitution());
        entity.setRoleTitle(dto.getRoleTitle());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setDescription(dto.getDescription());
        entity.setDisplayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0);
        TeacherExperience saved = teacherExperienceRepository.save(entity);
        return mapToExperienceDto(saved);
    }

    public List<TeacherExperienceDto> getExperiences(Long profileId) {
        return teacherExperienceRepository.findByTeacherProfileIdOrderByDisplayOrderAscEndDateDesc(profileId)
                .stream()
                .map(this::mapToExperienceDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public TeacherExperienceDto updateExperience(Long profileId, Long experienceId, TeacherExperienceUpdateDto dto) {
        TeacherExperience entity = teacherExperienceRepository.findById(experienceId)
                .orElseThrow(() -> new ResourceNotFoundException("TeacherExperience", experienceId));
        if (!entity.getTeacherProfile().getId().equals(profileId)) {
            throw new ResourceNotFoundException("TeacherExperience", experienceId);
        }
        if (dto.getInstitution() != null) entity.setInstitution(dto.getInstitution());
        if (dto.getRoleTitle() != null) entity.setRoleTitle(dto.getRoleTitle());
        if (dto.getStartDate() != null) entity.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) entity.setEndDate(dto.getEndDate());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getDisplayOrder() != null) entity.setDisplayOrder(dto.getDisplayOrder());
        TeacherExperience saved = teacherExperienceRepository.save(entity);
        return mapToExperienceDto(saved);
    }

    @Transactional
    public void deleteExperience(Long profileId, Long experienceId) {
        TeacherExperience entity = teacherExperienceRepository.findById(experienceId)
                .orElseThrow(() -> new ResourceNotFoundException("TeacherExperience", experienceId));
        if (!entity.getTeacherProfile().getId().equals(profileId)) {
            throw new ResourceNotFoundException("TeacherExperience", experienceId);
        }
        teacherExperienceRepository.delete(entity);
    }
}
