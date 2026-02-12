package com.genixo.education.search.service.hr;

import com.genixo.education.search.common.exception.BusinessException;
import com.genixo.education.search.common.exception.ResourceNotFoundException;
import com.genixo.education.search.dto.hr.*;
import com.genixo.education.search.entity.hr.TeacherProfile;
import com.genixo.education.search.entity.location.Province;
import com.genixo.education.search.entity.user.User;
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
        profile.setEducationLevel(dto.getEducationLevel());
        profile.setExperienceYears(dto.getExperienceYears());
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
        return mapToDto(saved);
    }

    public Page<TeacherProfileDto> search(String branch, String searchTerm, Pageable pageable) {
        return teacherProfileRepository.search(branch, searchTerm, pageable).map(this::mapToDto);
    }

    public TeacherProfileDto getById(Long id) {
        TeacherProfile profile = teacherProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TeacherProfile", id));
        return mapToDto(profile);
    }

    public TeacherProfileDto getByUserId(Long userId) {
        TeacherProfile profile = teacherProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("TeacherProfile", "userId", userId));
        return mapToDto(profile);
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
        if (dto.getEducationLevel() != null) profile.setEducationLevel(dto.getEducationLevel());
        if (dto.getExperienceYears() != null) profile.setExperienceYears(dto.getExperienceYears());
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

        return mapToDto(teacherProfileRepository.save(profile));
    }

    @Transactional
    public void delete(Long id) {
        if (!teacherProfileRepository.existsById(id)) {
            throw new ResourceNotFoundException("TeacherProfile", id);
        }
        teacherProfileRepository.deleteById(id);
    }

    private TeacherProfileDto mapToDto(TeacherProfile t) {
        return TeacherProfileDto.builder()
                .id(t.getId())
                .userId(t.getUser().getId())
                .fullName(t.getFullName())
                .email(t.getEmail())
                .phone(t.getPhone())
                .city(t.getCity())
                .branch(t.getBranch())
                .educationLevel(t.getEducationLevel())
                .experienceYears(t.getExperienceYears())
                .bio(t.getBio())
                .profilePhotoUrl(t.getProfilePhotoUrl())
                .videoUrl(t.getVideoUrl())
                .cvUrl(t.getCvUrl())
                .isActive(t.getIsActive())
                .provinces(t.getProvinces() != null ? t.getProvinces().stream()
                        .map(p -> ProvinceSummaryDto.builder().id(p.getId()).name(p.getName()).code(p.getCode()).build())
                        .collect(Collectors.toList()) : Collections.emptyList())
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .build();
    }
}
