package com.genixo.education.search.service.webinar;

import com.genixo.education.search.common.exception.BusinessException;
import com.genixo.education.search.common.exception.ResourceNotFoundException;
import com.genixo.education.search.common.util.SecurityUtils;
import com.genixo.education.search.dto.webinar.EventOrganizerCreateDto;
import com.genixo.education.search.dto.webinar.EventOrganizerDto;
import com.genixo.education.search.dto.webinar.EventOrganizerSummaryDto;
import com.genixo.education.search.dto.webinar.EventOrganizerUpdateDto;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.entity.webinar.EventOrganizer;
import com.genixo.education.search.repository.user.UserRepository;
import com.genixo.education.search.repository.webinar.EventOrganizerRepository;
import com.genixo.education.search.service.SlugGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EventOrganizerService {

    private final EventOrganizerRepository eventOrganizerRepository;
    private final UserRepository userRepository;
    private final SlugGeneratorService slugGeneratorService;

    @Transactional
    public EventOrganizerDto create(EventOrganizerCreateDto dto) {
        String slug = resolveSlug(dto.getSlug(), dto.getName());
        if (eventOrganizerRepository.existsBySlug(slug)) {
            throw BusinessException.duplicateResource("EventOrganizer", "slug", slug);
        }

        User createdBy = resolveCurrentUser();

        EventOrganizer organizer = new EventOrganizer();
        organizer.setName(dto.getName());
        organizer.setSlug(slug);
        organizer.setType(EventOrganizer.OrganizerType.valueOf(dto.getType()));
        organizer.setDescription(dto.getDescription());
        organizer.setLogoUrl(dto.getLogoUrl());
        organizer.setWebsite(dto.getWebsite());
        organizer.setEmail(dto.getEmail());
        organizer.setPhone(dto.getPhone());
        organizer.setAddress(dto.getAddress());
        organizer.setCity(dto.getCity());
        organizer.setSocialMediaLinks(dto.getSocialMediaLinks());
        organizer.setIsVerified(dto.getIsVerified() != null ? dto.getIsVerified() : false);
        organizer.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        organizer.setCreatedByUser(createdBy);

        EventOrganizer saved = eventOrganizerRepository.save(organizer);
        return mapToDto(saved);
    }

    public Page<EventOrganizerDto> search(String type, String searchTerm, Boolean isActive, Pageable pageable) {
        return eventOrganizerRepository.search(type, searchTerm, isActive, pageable)
                .map(this::mapToDto);
    }

    public EventOrganizerDto getById(Long id) {
        EventOrganizer organizer = eventOrganizerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EventOrganizer", id));
        return mapToDto(organizer);
    }

    public EventOrganizerDto getBySlug(String slug) {
        EventOrganizer organizer = eventOrganizerRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("EventOrganizer", slug));
        return mapToDto(organizer);
    }

    public Page<EventOrganizerDto> listActive(Pageable pageable) {
        return eventOrganizerRepository.findByIsActiveTrue(pageable).map(this::mapToDto);
    }

    @Transactional
    public EventOrganizerDto update(Long id, EventOrganizerUpdateDto dto) {
        EventOrganizer organizer = eventOrganizerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EventOrganizer", id));

        if (dto.getName() != null) organizer.setName(dto.getName());
        if (dto.getType() != null) organizer.setType(EventOrganizer.OrganizerType.valueOf(dto.getType()));
        if (dto.getDescription() != null) organizer.setDescription(dto.getDescription());
        if (dto.getLogoUrl() != null) organizer.setLogoUrl(dto.getLogoUrl());
        if (dto.getWebsite() != null) organizer.setWebsite(dto.getWebsite());
        if (dto.getEmail() != null) organizer.setEmail(dto.getEmail());
        if (dto.getPhone() != null) organizer.setPhone(dto.getPhone());
        if (dto.getAddress() != null) organizer.setAddress(dto.getAddress());
        if (dto.getCity() != null) organizer.setCity(dto.getCity());
        if (dto.getSocialMediaLinks() != null) organizer.setSocialMediaLinks(dto.getSocialMediaLinks());
        if (dto.getIsVerified() != null) organizer.setIsVerified(dto.getIsVerified());
        if (dto.getIsActive() != null) organizer.setIsActive(dto.getIsActive());
        if (StringUtils.hasText(dto.getSlug())) {
            if (!dto.getSlug().equals(organizer.getSlug()) && eventOrganizerRepository.existsBySlug(dto.getSlug())) {
                throw BusinessException.duplicateResource("EventOrganizer", "slug", dto.getSlug());
            }
            organizer.setSlug(dto.getSlug());
        }

        return mapToDto(eventOrganizerRepository.save(organizer));
    }

    @Transactional
    public void delete(Long id) {
        EventOrganizer organizer = eventOrganizerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EventOrganizer", id));
        if (organizer.getEvents() != null && !organizer.getEvents().isEmpty()) {
            throw BusinessException.resourceInUse("EventOrganizer", "etkinlikler");
        }
        eventOrganizerRepository.deleteById(id);
    }

    private String resolveSlug(String providedSlug, String name) {
        if (StringUtils.hasText(providedSlug)) {
            return slugGeneratorService.createSlugFromText(providedSlug);
        }
        String baseSlug = slugGeneratorService.createSlugFromText(name);
        String slug = baseSlug;
        int attempt = 0;
        while (eventOrganizerRepository.existsBySlug(slug) && attempt < 100) {
            slug = baseSlug + "-" + System.currentTimeMillis();
            attempt++;
        }
        return slug;
    }

    private User resolveCurrentUser() {
        String email = SecurityUtils.getCurrentUser();
        if (email == null) {
            throw new BusinessException("Oturum açmış kullanıcı gerekli");
        }
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));
    }

    private EventOrganizerDto mapToDto(EventOrganizer o) {
        return EventOrganizerDto.builder()
                .id(o.getId())
                .name(o.getName())
                .slug(o.getSlug())
                .type(o.getType().name())
                .description(o.getDescription())
                .logoUrl(o.getLogoUrl())
                .website(o.getWebsite())
                .email(o.getEmail())
                .phone(o.getPhone())
                .address(o.getAddress())
                .city(o.getCity())
                .socialMediaLinks(o.getSocialMediaLinks())
                .isVerified(o.getIsVerified())
                .isActive(o.getIsActive())
                .createdByUserId(o.getCreatedByUser() != null ? o.getCreatedByUser().getId() : null)
                .createdByUserEmail(o.getCreatedByUser() != null ? o.getCreatedByUser().getEmail() : null)
                .eventCount(o.getEvents() != null ? o.getEvents().size() : 0)
                .createdAt(o.getCreatedAt())
                .updatedAt(o.getUpdatedAt())
                .build();
    }
}
